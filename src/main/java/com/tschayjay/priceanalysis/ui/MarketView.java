package com.tschayjay.priceanalysis.ui;

import com.tschayjay.base.util.InstantFormatter;
import com.tschayjay.priceanalysis.model.Market;
import com.tschayjay.priceanalysis.service.impl.MarketServiceImpl;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;

import java.util.Comparator;
import java.util.List;

/**
 * @author julianwalter
 */
@Route("markets")
@PageTitle("Markets")
@Menu(order = 0, icon = "vaadin:line-chart", title = "Markets")
class MarketView extends VerticalLayout {

    private final String marketUrl;
    private final MarketServiceImpl marketService;

    public MarketView(MarketServiceImpl marketService, @Value("${polymarket.market.url}") String marketUrl) {
        this.marketService = marketService;
        this.marketUrl = marketUrl;
        setup();
    }

    void setup() {
        // setup grid
        Grid<Market> marketGrid = new Grid<>(Market.class, false);
        marketGrid.setSizeFull();

        marketGrid.addColumn(Market::getQuestion)
                .setHeader("Question")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(market -> market.getQuestion().toLowerCase()));

        marketGrid.addColumn(market -> InstantFormatter.format(market.getEndDate()))
                .setHeader("End")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortable(true);

        marketGrid.addColumn(market -> "%.3f".formatted(marketService.calculateTrend(market)))
                .setHeader("Trend")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortable(true);

        // setup detail view
        MarketDetail detail = new MarketDetail(marketUrl);

        // setup master detail view
        MasterDetailLayout masterDetailLayout = new MasterDetailLayout();
        masterDetailLayout.setSizeFull();
        masterDetailLayout.setOrientation(MasterDetailLayout.Orientation.VERTICAL);
        masterDetailLayout.setMaster(marketGrid);

        marketGrid.asSingleSelect().addValueChangeListener(event -> {
            Market market = event.getValue();
            detail.setMarket(market);
            if (market != null) {
                masterDetailLayout.setDetail(detail);
            } else {
                masterDetailLayout.setDetail(null);
            }
        });
        detail.addCloseListener(event -> masterDetailLayout.setDetail(null));

        // insert data to grid
        List<Market> markets = marketService.getMarkets();
        marketGrid.setItems(markets);

        // put everything together
        setSizeFull();
        add(masterDetailLayout);
    }
}
