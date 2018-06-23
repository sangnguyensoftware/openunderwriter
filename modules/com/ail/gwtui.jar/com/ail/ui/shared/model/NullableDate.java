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
package com.ail.ui.shared.model;

import java.io.Serializable;
import java.util.Date;


public class NullableDate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date value;

    private boolean hasValue;

    public NullableDate() {
        hasValue = false;
    }

    public NullableDate(Date value) {
        hasValue = (value != null);
        this.value = value;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public Date getValue() {
        if (!hasValue) {
            throw new UnsupportedOperationException(
                    "Date value cannot be null - use hasValue() check");
        }
        return value;
    }

    public void setValue(Date value) {
        hasValue = (value != null);
        this.value = value;
    }

    @Override
    public String toString() {
        return "NullableDate [value=" + value + ", hasValue=" + hasValue + "]";
    }


}
