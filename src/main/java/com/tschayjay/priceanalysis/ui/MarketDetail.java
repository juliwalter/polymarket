package com.tschayjay.priceanalysis.ui;

import com.storedobject.chart.*;
import com.tschayjay.base.util.InstantFormatter;
import com.tschayjay.priceanalysis.model.Market;
import com.tschayjay.priceanalysis.model.MarketSnapshot;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author julianwalter
 */
public class MarketDetail extends VerticalLayout {

    private final String marketUrl;

    private final GeneralTabLayout generalLayout;
    private final TrendTabLayout trendLayout;

    /**
     * Constructor for the {@link MarketDetail} component
     */
    public MarketDetail(String marketUrl) {
        this.marketUrl = marketUrl;
        this.generalLayout = new GeneralTabLayout(marketUrl);
        this.trendLayout = new TrendTabLayout();

        setSizeFull();

        // init data layout
        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.setHeight("97%");

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add("General", generalLayout);
        tabSheet.add("Trend", trendLayout);
        dataLayout.add(tabSheet);

        // init button row
        VerticalLayout headerLayout = new VerticalLayout();
        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.setTooltipText("Close");
        closeButton.addClickListener(event -> fireEvent(new CloseEvent(this, false)));
        headerLayout.setPadding(false);
        headerLayout.setAlignItems(Alignment.END);
        headerLayout.setHeight("3%");
        headerLayout.add(closeButton);

        add(headerLayout, dataLayout);
    }

    /**
     * Binds a market to the {@link MarketDetail} component
     *
     * @param market the market to bind to the detail view
     */
    public void setMarket(Market market) {
        generalLayout.setMarket(market);
        trendLayout.setMarket(market);
    }

    /**
     * Registers a close event listener to the component
     *
     * @param listener defines what do to on event listening
     */
    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }

    /**
     * Event when {@link MarketDetail} gets closed
     */
    public static class CloseEvent extends ComponentEvent<MarketDetail> {
        public CloseEvent(MarketDetail source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    @RequiredArgsConstructor
    private static class GeneralTabLayout extends VerticalLayout {

        private final String marketUrl;

        private void addRow(String key, String value) {
            addRow(key, new Text(value));
        }

        private void addRow(String key, Component value) {
            VerticalLayout keyLayout = new VerticalLayout();
            keyLayout.setPadding(false);
            keyLayout.setWidth("20%");
            keyLayout.add(key);
            keyLayout.getStyle().set("fontWeight", "bold");

            VerticalLayout valueLayout = new VerticalLayout();
            valueLayout.setPadding(false);
            valueLayout.setWidth("80%");
            valueLayout.add(value);

            HorizontalLayout rowLayout = new HorizontalLayout(keyLayout, valueLayout);
            rowLayout.setPadding(false);
            rowLayout.setWidthFull();
            add(rowLayout);
        }

        public void setMarket(Market market) {
            removeAll();
            if (market != null) {
                // add question
                addRow("Question", market.getQuestion());

                // add link
                HorizontalLayout linkLayout = new HorizontalLayout(VaadinIcon.LINK.create(), new Text("Go to polymarket"));
                linkLayout.addClassName(LumoUtility.Gap.SMALL);
                Anchor anchor = new Anchor(marketUrl + market.getSlug(), linkLayout);
                addRow("Link", anchor);

                // add start and end dates
                addRow("Start", InstantFormatter.format(market.getStartDate()));
                addRow("End", InstantFormatter.format(market.getEndDate()));

                // add outcomes
                HorizontalLayout outcomeLayout = new HorizontalLayout();
                outcomeLayout.addClassName(LumoUtility.Gap.SMALL);

                Span firstOutcomeBadge = new Span(market.getFirstOutcome());
                firstOutcomeBadge.getElement().getThemeList().add("badge success");

                Span secondOutcomeBadge = new Span(market.getSecondOutcome());
                secondOutcomeBadge.getElement().getThemeList().add("badge error");
                outcomeLayout.add(firstOutcomeBadge, secondOutcomeBadge);

                Optional<MarketSnapshot> latestSnapshot = market.getSnapshots().stream()
                        .max(Comparator.comparing(MarketSnapshot::getTimestamp));
                if (latestSnapshot.isPresent()) {
                    Tooltip.forComponent(firstOutcomeBadge).setText("%.2f%%".formatted(latestSnapshot.get().getPriceFirstOutcome() * 100));
                    Tooltip.forComponent(secondOutcomeBadge).setText("%.2f%%".formatted(latestSnapshot.get().getPriceSecondOutcome() * 100));
                }
                addRow("Outcomes", outcomeLayout);
            }
        }
    }

    private static class TrendTabLayout1 extends VerticalLayout {

        private final Grid<MarketSnapshot> grid;
        private final Grid.Column<MarketSnapshot> firstOutcomeColumn;
        private final Grid.Column<MarketSnapshot> secondOutcomeColumn;

        public TrendTabLayout1() {
            grid = new Grid<>(MarketSnapshot.class, false);
            grid.addColumn(MarketSnapshot::getTimestamp)
                    .setAutoWidth(true)
                    .setResizable(true)
                    .setHeader("Timestamp");

            firstOutcomeColumn = grid.addColumn(MarketSnapshot::getPriceFirstOutcome)
                    .setAutoWidth(true)
                    .setResizable(true);

            secondOutcomeColumn = grid.addColumn(MarketSnapshot::getPriceSecondOutcome)
                    .setAutoWidth(true)
                    .setResizable(true);

            add(grid);
        }

        public void setMarket(Market market) {
            if (market != null) {
                firstOutcomeColumn.setHeader(market.getFirstOutcome());
                secondOutcomeColumn.setHeader(market.getSecondOutcome());
                grid.setItems(market.getSnapshots());
            } else {
                grid.setItems();
            }
        }
    }

    private static class TrendTabLayout extends VerticalLayout {

        public TrendTabLayout() {
            setSizeFull();
            setPadding(false);
        }

        public void setMarket(Market market) {
            removeAll();
            if (market != null) {
                SOChart chart = new SOChart();
                chart.setSizeFull();

                LocalDateTime[] xValues = market.getSnapshots()
                        .stream()
                        .map(MarketSnapshot::getTimestamp)
                        .map(instant -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
                        .toArray(LocalDateTime[]::new);

                Number[] yValuesFirst = extractPrices(market, MarketSnapshot::getPriceFirstOutcome);
                Number[] yValuesSecond = extractPrices(market, MarketSnapshot::getPriceSecondOutcome);

                XAxis xAxis = new XAxis(DataType.TIME);
                YAxis yAxis = new YAxis(DataType.NUMBER);
                yAxis.setMin(0);
                yAxis.setMax(1);
                RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);

                LineChart firstLineChart = getLineChart(market.getFirstOutcome(), xValues, yValuesFirst);
                firstLineChart.plotOn(rc);

                LineChart secondLineChart = getLineChart(market.getSecondOutcome(), xValues, yValuesSecond);
                secondLineChart.plotOn(rc);

                chart.add(firstLineChart, secondLineChart);
                add(chart);
            }
        }

        private Number[] extractPrices(Market market, Function<MarketSnapshot, Double> extraction) {
            return market.getSnapshots()
                    .stream()
                    .map(extraction)
                    .toArray(Number[]::new);
        }

        private LineChart getLineChart(String name, LocalDateTime[] xValues, Number[] yValues) {
            LineChart lineChart = new LineChart();
            lineChart.setXData(new TimeData(xValues));
            lineChart.setYData(new Data(yValues));
            lineChart.setName(name);
            return lineChart;
        }
    }
}
