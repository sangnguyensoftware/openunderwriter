/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
package com.ail.ui.shared.validation;

import static com.ail.ui.client.common.UIUtil.DATE_FIELD_FORMAT;

import com.google.gwt.i18n.client.DateTimeFormat;


/**
 * Field validation
 */
public class FieldVerifier {

	/**
	 * Verifies that the input has length.
	 * 
	 * @param input to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean hasLength(String input) {
		return input != null && input.trim().length() > 0;
	}
	
	/**
	 * Verifies value is valid date
	 * @param date
	 * @return
	 */
	public static boolean isValidDate(String date) {
	    try {
	        DateTimeFormat.getFormat(DATE_FIELD_FORMAT).parse(date);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
