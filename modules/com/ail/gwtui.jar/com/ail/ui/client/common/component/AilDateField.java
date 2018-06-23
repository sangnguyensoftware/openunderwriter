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

import static com.ail.ui.client.common.UIUtil.DATE_FIELD_FORMAT;

import java.util.Date;

import com.ail.ui.client.common.Style;
import com.ail.ui.shared.model.NullableDate;
import com.ail.ui.shared.validation.FieldVerifier;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class AilDateField extends AilHorizontalPanel {


    private  AilTextBox dateField = new AilTextBox(Style.DATE_TEXTBOX);
    private final PopupPanel datePickerPanel = new PopupPanel(true);
    private final DatePicker datePicker = new DatePicker();

    /**
     * Default Constructor
     */
    public AilDateField() {
        this("");
    }

    /**
     * Constructor - Use if session state required
     * @param stateId
     */
    public AilDateField(String stateId) {
        this.dateField = new AilTextBox(Style.DATE_TEXTBOX, stateId);
    }

    /**
     * Set up and display
     * @return date field to display
     */
    public AilDateField display() {

        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> result) {
               dateField.setText(
                       DateTimeFormat.getFormat(DATE_FIELD_FORMAT).format(result.getValue()));
               datePickerPanel.hide();
            }
         });

        dateField.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                datePickerPanel.setPopupPosition(
                        dateField.getAbsoluteLeft() + dateField.getOffsetWidth(), dateField.getAbsoluteTop());
                datePickerPanel.show();
            }
        });

        dateField.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent e) {
                if (KeyCodes.KEY_TAB == e.getNativeKeyCode()) {
                    datePickerPanel.hide();
                }
            }
        });

        dateField.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent arg0) {
                validateDate();
            }

        });

        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> arg0) {
                validateDate();
            }
        });

        datePicker.setYearArrowsVisible(true);
        datePickerPanel.setWidget(datePicker);

        add(dateField);

        return this;
    }

    public NullableDate getValue() {
        if (hasValue()) {
            return new NullableDate(
                    DateTimeFormat.getFormat(DATE_FIELD_FORMAT).parse(dateField.getValue()));
        } else {
            return new NullableDate();
        }
    }

    public boolean hasValue() {
        return dateField.hasValue();
    }

    private void validateDate() {
        if (isValidDateOrBlank()) {
            dateField.validationReset();
        } else {
            dateField.validationFailed();
        }
    }

    public boolean isValidDateOrBlank() {
        if (dateField.hasValue()) {
            return FieldVerifier.isValidDate(dateField.getValue());
        }
        return true;
    }

    public AilTextBox getField() {
        return this.dateField;
    }



}
