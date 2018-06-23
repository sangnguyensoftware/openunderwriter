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
import com.google.gwt.user.client.ui.Label;

public class AilLabel extends Label {

    public AilLabel(String label) {
        super(label);
    }

    public AilLabel(String label, Style... styles) {
        super(label);
        for (Style style : styles) {
            addStyleName(style.toString());
        }
    }
    
    public AilLabel(Style... styles) {
        this("", styles);
    }
    
    public AilLabel() {
    }
    
    public void setTextRed(String text) {
        getElement().getStyle().setColor("red");
        setText(text);
    }
    
    public void setTextNormal(String text) {
        getElement().getStyle().setColor("black");
        setText(text);
    }
}
