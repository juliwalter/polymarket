package com.tschayjay.priceanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author julianwalter
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String conditionId;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private String firstOutcome;

    @Column(nullable = false)
    private String secondOutcome;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    List<MarketSnapshot> snapshots = new ArrayList<>();
}
