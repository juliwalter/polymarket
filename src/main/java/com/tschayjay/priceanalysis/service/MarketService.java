package com.tschayjay.priceanalysis.service;

import com.tschayjay.priceanalysis.dto.MarketDto;
import com.tschayjay.priceanalysis.model.Market;

import java.util.List;

/**
 * @author julianwalter
 */
public interface MarketService {

    /**
     *
     * @return
     */
    List<Market> getMarkets();

    /**
     *
     * @param dtos
     */
    void processApiCall(List<MarketDto> dtos);

    /**
     *
     * @param market
     * @return
     */
    double calculateTrend(Market market);
}
