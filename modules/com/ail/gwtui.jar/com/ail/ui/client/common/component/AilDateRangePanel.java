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
import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.model.NullableDate;
import com.google.gwt.core.client.GWT;

public class AilDateRangePanel extends AilHorizontalPanel {

    private final Messages messages = GWT.create(Messages.class);

    private AilDateField dateFrom;
    private AilDateField dateTo;

    /**
     * Default Constructor
     */
    public AilDateRangePanel() {
        dateFrom = new AilDateField();
        dateTo = new AilDateField();
    }

    /**
     * Constructor - Use if session state required
     * @param stateId
     */
    public AilDateRangePanel(String stateId) {
        dateFrom = new AilDateField(stateId + "-from");
        dateTo = new AilDateField(stateId + "-to");
    }

    /**
     * Set up and display panel
     * @return Date range panel to display
     */
    public AilDateRangePanel display() {

        add(dateFrom.display());
        add(new AilLabel(messages.to(), Style.STATNDARD_TEXT));
        add(dateTo.display());

        return this;
    }

    public NullableDate getDateFrom() {
        return dateFrom.getValue();
    }

    public AilDateField getDateFromField() {
        return dateFrom;
    }

    public AilDateField getDateToField() {
        return dateTo;
    }

    public NullableDate getDateTo() {
        return dateTo.getValue();
    }

    public boolean hasDateFrom() {
        return dateFrom.hasValue();
    }

    public boolean hasDateTo() {
        return dateTo.hasValue();
    }

    public boolean validate() {
        return dateFrom.isValidDateOrBlank() && dateTo.isValidDateOrBlank();
    }

}
