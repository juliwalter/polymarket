package com.tschayjay.base.export;

import com.vaadin.flow.component.grid.Grid;
import software.xdev.vaadin.grid_exporter.grid.GridDataExtractor;

import java.util.stream.Stream;

/**
 * Implements the {@link GridDataExtractor} by overriding the fetch method. It uses the
 * {@link com.vaadin.flow.data.provider.ListDataView} to extract the data. This enables to respect the filters when
 * exporting the data
 *
 * @author julianwalter
 */
public class ListViewDataExtractor<T> extends GridDataExtractor<T> {
    public ListViewDataExtractor(Grid<T> grid) {
        super(grid);
    }

    @Override
    protected Stream<T> getSortedAndFilteredData(Grid<T> grid) {
        return grid.getListDataView().getItems();
    }
}
