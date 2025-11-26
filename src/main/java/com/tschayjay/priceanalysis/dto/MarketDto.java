package com.tschayjay.priceanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * @author julianwalter
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto {
    private String question;
    private String conditionId;
    private String slug;
    private Instant startDate;
    private Instant endDate;
    private List<String> outcomes;
    private List<Double> outcomePrices;
    private boolean active;
    private boolean enableOrderBook;
    private double volumeNum;
    private double volume24hr;
    private boolean acceptingOrders;
}
