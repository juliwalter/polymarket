package com.tschayjay.priceanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * @author julianwalter
 */
@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MarketSnapshot {
    private Instant timestamp;

    private double priceFirstOutcome;

    private double priceSecondOutcome;

    private boolean active;

    private boolean enableOrderBook;

    private double volumeNum;

    private double volume24hr;

    private boolean acceptingOrders;
}
