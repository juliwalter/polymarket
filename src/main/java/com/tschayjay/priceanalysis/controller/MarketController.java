package com.tschayjay.priceanalysis.controller;

import com.tschayjay.priceanalysis.dto.MarketDto;
import com.tschayjay.priceanalysis.model.Market;
import com.tschayjay.priceanalysis.service.MarketService;
import com.tschayjay.priceanalysis.service.impl.MarketServiceImpl;
import com.vaadin.flow.i18n.I18NProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

/**
 * @author julianwalter
 */
@RestController
@RequiredArgsConstructor
class MarketController implements I18NProvider {

    private final MarketService marketService;
    
    @GetMapping("/market")
    ResponseEntity<String> getMarkets() {
        List<Market> markets = marketService.getMarkets();
        return ResponseEntity.ok("success");
    }

    @PostMapping("/market")
    ResponseEntity<String> createMarkets(@RequestBody List<MarketDto> dto) {
        marketService.processApiCall(dto);
        return ResponseEntity.ok("success");
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of();
    }

    @Override
    public String getTranslation(String s, Locale locale, Object... objects) {
        return "";
    }
}
