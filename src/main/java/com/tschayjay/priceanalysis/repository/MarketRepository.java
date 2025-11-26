package com.tschayjay.priceanalysis.repository;

import com.tschayjay.priceanalysis.model.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarketRepository extends JpaRepository<Market, UUID> {
    Market findByConditionId(String conditionId);
    boolean existsByConditionId(String conditionId);
}
