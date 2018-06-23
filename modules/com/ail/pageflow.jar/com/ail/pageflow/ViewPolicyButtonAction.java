/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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
package com.ail.pageflow;

import java.io.IOException;

import com.ail.core.Type;

/**
 * <p>
 * Adds a "view policy" button to a page. When selected this button will open a
 * new window containing the current policy as a PDF.
 * </p>
 * <p>
 * <img src="doc-files/ViewPolicyButtonAction.png"/>
 * </p>
 * <p>
 * If the PDF does not already exist, this button's action will create it and
 * persist it before returning to the client's browser.
 * </p>
 */
public class ViewPolicyButtonAction extends ViewDocumentButtonAction {
    private static final long serialVersionUID = 7575333161831400599L;

    public ViewPolicyButtonAction() {
        setLabel("i18n_view_policy_document_button_label");
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("ViewPolicyButtonCommand", model);
    }
}
