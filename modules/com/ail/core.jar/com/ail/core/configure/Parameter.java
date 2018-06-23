/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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
package com.ail.core.configure;

/**
 * This type represents the simplest element used in the configuration
 * pattern. Parameter is simply a name/value pair. The value is always
 * a String.
 */
public class Parameter extends Component {
    static final long serialVersionUID = -8141145979210301320L;
	private String value=null;

	public Parameter(String name, String value) {
	    setName(name);
	    setValue(value);
	}

	/**
     * Default constructor
     */
    public Parameter() {
    }

	/**
     * Getter for the parameter's value.
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the value wrapped in an XML style CDATA tag.
     */
    public String getValueCDATA() {
        return "<![CDATA["+value+"]]>";
    }

	/**
     * Setter for the parameter's value.
	 * @param value Value to use.
     */
    public void setValue(String value) {
        this.value=value;
    }
}
