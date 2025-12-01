package com.tschayjay.priceanalysis.controller;

import com.tschayjay.priceanalysis.dto.MarketDto;
import com.tschayjay.priceanalysis.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author julianwalter
 */
@Slf4j
@RestController
@RequiredArgsConstructor
class MarketController {

    private final MarketService marketService;

    @PostMapping("/market")
    ResponseEntity<String> createMarkets(@RequestBody List<MarketDto> dto) {
        marketService.processApiCall(dto);
        log.info("Processed {} market information", dto.size());
        return ResponseEntity.ok("success");
    }
}
