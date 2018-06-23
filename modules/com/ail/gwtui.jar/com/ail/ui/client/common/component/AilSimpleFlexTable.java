/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.ui.client.common.component;

import com.ail.ui.client.common.Style;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * Container used to display results and forms
 */
public class AilSimpleFlexTable extends FlexTable {

    private static final int DEFAULT_CELL_PADDING = 4;

    private int labelWidth;

    private int valueWidth;

    private boolean border;

    /**
     * Constructor
     * @param labelWidth Column 1 width
     * @param valueWidth Column 2 width
     */
    public AilSimpleFlexTable(int labelWidth, int valueWidth) {
        this(labelWidth, valueWidth, false);
    }

    /**
     * Constructor
     * @param labelWidth Column 1 width
     * @param valueWidth Column 2 width
     * @param border Show border
     */
    public AilSimpleFlexTable(int labelWidth, int valueWidth, boolean border) {
        setCellPadding(DEFAULT_CELL_PADDING);
        this.labelWidth = labelWidth;
        this.valueWidth = valueWidth;
        this.border = border;
    }

    /**
     * Add row to table
     * @param label Column 1 item (String or Widget)
     * @param value Column 2 item (String or Widget)
     */
    public void addRow(Object label, Object value) {
        int row = getRowCount();
        setObject(label, row, 0);

        getCellFormatter().setStyleName(row, 0, getCellFormat());

        setObject(value, row, 1);
        getCellFormatter().setWidth(row, 0, labelWidth + "px");
        getCellFormatter().setWidth(row, 1, valueWidth + "px");

        getCellFormatter().setStyleName(row, 1, getCellFormat());
    }

    public void addRowHeader(Object label) {
        int row = getRowCount();
        setObject(label, row, 0);

        getCellFormatter().setStyleName(row, 0, getCellFormat());
        getFlexCellFormatter().setColSpan(row, 0, 2);
    }

    private void setObject(Object value, int row, int col) {
        if (value instanceof String) {
            setText(row, col, (String)value);
        } else {
            setWidget(row, col, (Widget)value);
        }
    }

    private String getCellFormat() {
        return  Style.STATNDARD_TEXT +
                (border ? " " + Style.CELL_BORDER : "");
    }


}
