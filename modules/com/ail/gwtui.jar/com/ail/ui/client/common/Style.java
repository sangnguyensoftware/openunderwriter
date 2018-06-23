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
package com.ail.ui.client.common;

public enum Style {

    DATE_TEXTBOX("gui-date-textbox"),
    TEXT_BOX("gui-text-box"),
    STATNDARD_TEXT("gui-standard-text"),
    STATNDARD_CELL("gui-standard-cell"),
    CELL_BORDER("gui-cell-border"),
    ERROR_LABEL("gui-error-label"),
    SHOW_HIDE_BUTTON("gui-show-button"),
    SEARCH_HEADER_LABEL("gui-search-header-label"),
    TITLE_TEXT("gui-title-text"),
    HIGHLIGHT_RESULT("gui-highlight-result"),
    SPACER("gui-spacer"),
    BUTTON_TO_LINK("gui-button-to-link");

    String name;

    private Style(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}