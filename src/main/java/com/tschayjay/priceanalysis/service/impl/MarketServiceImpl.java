package com.tschayjay.priceanalysis.service.impl;

import com.tschayjay.priceanalysis.dto.MarketDto;
import com.tschayjay.priceanalysis.model.Market;
import com.tschayjay.priceanalysis.model.MarketSnapshot;
import com.tschayjay.priceanalysis.repository.MarketRepository;
import com.tschayjay.priceanalysis.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author julianwalter
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "polymarket.market.service.mock.enabled", havingValue = "false", matchIfMissing = true)
public class MarketServiceImpl implements MarketService {
    private final MarketRepository repository;

    public List<Market> getMarkets() {
        return repository.findAll();
    }

    public void processApiCall(List<MarketDto> dtos) {
        dtos.forEach(this::processSingleDto);
    }

    private void processSingleDto(MarketDto dto) {
        Market market;
        if (repository.existsByConditionId(dto.getConditionId())) {
            market = repository.findByConditionId(dto.getConditionId());
        } else {
            market = Market.builder()
                    .conditionId(dto.getConditionId())
                    .question(dto.getQuestion())
                    .slug(dto.getSlug())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .firstOutcome(dto.getOutcomes().get(0))
                    .secondOutcome(dto.getOutcomes().get(1))
                    .build();
        }
        MarketSnapshot snapshot = MarketSnapshot.builder()
                .timestamp(Instant.now())
                .priceFirstOutcome(mapPriceForOutcome(market.getFirstOutcome(), dto))
                .priceSecondOutcome(mapPriceForOutcome(market.getSecondOutcome(), dto))
                .active(dto.isActive())
                .acceptingOrders(dto.isAcceptingOrders())
                .enableOrderBook(dto.isEnableOrderBook())
                .volume24hr(dto.getVolume24hr())
                .volumeNum(dto.getVolumeNum())
                .build();
        market.getSnapshots().add(snapshot);
        repository.save(market);
    }

    private double mapPriceForOutcome(String outcome, MarketDto dto) {
        int index = dto.getOutcomes().indexOf(outcome);
        return dto.getOutcomePrices().get(index);
    }

    public double calculateTrend(Market market) {
        Instant end = Instant.now();
        Instant start = end.minus(100, ChronoUnit.DAYS);

        List<MarketSnapshot> timeframe = market.getSnapshots()
                .stream()
                .filter(snapshot -> start.isBefore(snapshot.getTimestamp()) && end.isAfter(snapshot.getTimestamp()))
                .toList();

        Optional<MarketSnapshot> minEntry = timeframe.stream().min(Comparator.comparing(MarketSnapshot::getTimestamp));
        Optional<MarketSnapshot> maxEntry = timeframe.stream().max(Comparator.comparing(MarketSnapshot::getTimestamp));

        return minEntry.map(marketSnapshot ->
                        (maxEntry.get().getPriceFirstOutcome() - marketSnapshot.getPriceFirstOutcome()) / marketSnapshot.getPriceFirstOutcome())
                .orElse(0.0);

    }
}
