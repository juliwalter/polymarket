package com.tschayjay.priceanalysis.ui;

import com.flowingcode.vaadin.addons.gridhelpers.GridHelper;
import com.tschayjay.base.export.ListViewDataExtractor;
import com.tschayjay.base.util.InstantFormatter;
import com.tschayjay.priceanalysis.model.Market;
import com.tschayjay.priceanalysis.service.impl.MarketServiceImpl;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import software.xdev.vaadin.grid_exporter.GridExporter;
import software.xdev.vaadin.gridfilter.GridFilter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
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

        Grid.Column<Market> questionColumn = marketGrid.addColumn(Market::getQuestion)
                .setHeader("Question")
                .setKey("question")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(market -> market.getQuestion().toLowerCase()));

        Grid.Column<Market> endDateColumn = marketGrid.addColumn(market -> InstantFormatter.format(market.getEndDate()))
                .setHeader("End")
                .setKey("end")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortable(true);

        Grid.Column<Market> trendColumn = marketGrid.addColumn(market -> BigDecimal.valueOf(marketService.calculateTrend(market))
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .setHeader("Trend")
                .setKey("trend")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortable(true);

        GridHelper.setHidingToggleCaption(questionColumn, "Question");
        GridHelper.setHidingToggleCaption(endDateColumn, "End");
        GridHelper.setHidingToggleCaption(trendColumn, "Trend");

        GridHelper.setColumnToggleVisible(marketGrid, isVisible());

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

        // add a grid filter
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidthFull();

        HorizontalLayout itemCountLayout = new HorizontalLayout();
        Span itemCount = new Span("%d of %d items".formatted(marketGrid.getListDataView().getItemCount(),
                marketGrid.getDataProvider().size(new Query<>())));
        itemCountLayout.setWidthFull();
        itemCountLayout.addToEnd(itemCount);

        GridFilter<Market> filter = GridFilter.createDefault(marketGrid)
                .withFilterableField("Question", Market::getQuestion, String.class)
                .withFilterableField("End", Market::getEndDate, Instant.class)
                .withFilterableField("Trend", marketService::calculateTrend, Double.class);
        filter.addFilterChangedListener(event ->
                itemCount.setText("%d of %d items".formatted(marketGrid.getListDataView().getItemCount(),
                        marketGrid.getDataProvider().size(new Query<>())))
        );
        filterLayout.add(filter, itemCountLayout);

        // export
        Button exportButton = new Button(
                "Export",
                VaadinIcon.DOWNLOAD.create(),
                e -> GridExporter.newWithDefaults(marketGrid)
                        .withGridDataExtractorSupplier(ListViewDataExtractor::new)
                        .open());

        // put everything together
        setSizeFull();
        add(filterLayout, masterDetailLayout, exportButton);
    }
}
