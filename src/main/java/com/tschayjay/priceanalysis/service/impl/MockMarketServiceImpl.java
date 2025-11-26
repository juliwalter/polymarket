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
import java.util.*;
import java.util.random.RandomGenerator;

/**
 * @author julianwalter
 */
@Service
@ConditionalOnProperty(value = "polymarket.market.service.mock.enabled", havingValue = "true")
public class MockMarketServiceImpl extends MarketServiceImpl {

    public MockMarketServiceImpl(MarketRepository repo) {
        super(repo);
    }

    @Override
    public List<Market> getMarkets() {
        Instant now = Instant.now();

        List<MarketSnapshot> snapshots = new ArrayList<>();
        double seed = RandomGenerator.getDefault().nextDouble(0, 1);
        for (int i = 0; i < 240; i++) {
            Instant timestamp = now.minus(i, ChronoUnit.HOURS);
            double randomizer = RandomGenerator.getDefault().nextDouble(-0.15, 0.15) * seed;

            if (0 <= seed + randomizer && seed + randomizer <= 1) {
                seed += randomizer;
            } else {
                seed -= randomizer;
            }

            snapshots.add(
                    MarketSnapshot.builder()
                            .timestamp(timestamp)
                            .priceFirstOutcome(seed)
                            .priceSecondOutcome(1 - seed)
                            .build()
            );
        }
        return List.of(
                Market.builder()
                        .id(UUID.randomUUID())
                        .conditionId("aasj2b3j2nasaiducca")
                        .slug("test")
                        .startDate(Instant.parse("2025-01-01T00:00:00.000Z"))
                        .endDate(Instant.parse("2026-01-01T00:00:00.000Z"))
                        .question("Will something happen?")
                        .firstOutcome("Yes")
                        .secondOutcome("No")
                        .snapshots(snapshots)
                        .build());
    }
}
