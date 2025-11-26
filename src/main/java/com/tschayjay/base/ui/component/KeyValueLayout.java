package com.tschayjay.base.ui.component;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * @author julianwalter
 */
public class KeyValueLayout extends VerticalLayout {

    private final HorizontalLayout keyLayout;
    private final HorizontalLayout valueLayout;

    public KeyValueLayout() {
        keyLayout = new HorizontalLayout();
        valueLayout = new HorizontalLayout();
        add(keyLayout, valueLayout);
    }

    public void addEntry(String key, String value) {
        keyLayout.add(key);
        valueLayout.add(value);
    }
}
