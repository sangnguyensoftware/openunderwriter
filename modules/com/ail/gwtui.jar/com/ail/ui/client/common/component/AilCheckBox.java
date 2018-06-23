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
import com.ail.ui.shared.validation.FieldVerifier;
import com.google.gwt.user.client.ui.CheckBox;

public class AilCheckBox extends CheckBox implements SessionState, Resetable {

    private String stateId = "";
    
    public AilCheckBox() {
    }

    /**
     * Constructor - Use if session state required
     * @param stateId
     */
    public AilCheckBox(String stateId) {
       this(Style.STATNDARD_TEXT, stateId);
    }
    
    /**
     * Constructor
     * @param style
     */
    public AilCheckBox(Style style) {
        this(style, "");
    }
    
    /**
     * Constructor - Use if session state required
     * @param style
     * @param stateId
     */
    public AilCheckBox(Style style, String stateId) {
        addStyleName(style.toString());
        this.stateId = stateId;
    }
    

    @Override
    public String getState() {
        return String.valueOf(getValue());
    }

    @Override
    public void setState(String data) {
        setValue(Boolean.valueOf(data));
    }

    @Override
    public String getStateId() {
        return stateId;
    }

    @Override
    public boolean isStatefull() {
        return FieldVerifier.hasLength(stateId);
    }

    @Override
    public void reset() {
       setValue(false);
    }
    
}
