/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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

import com.ail.core.BaseException;
import com.ail.core.Type;

/**
 * <p>
 * Adds a quit button to a page. By default this button will close the current
 * quote and return the user to the product's home page.
 * {@link NavigationSection NavigationSections} implicitly include a QuitButton
 * unless the NavigationSection's {@link NavigationSection#isQuitDisabled()
 * quitDisabled} property is set to true.
 * </p>
 * <p>
 * Technically, the button simply invalidates the current portlet session. This
 * has the effect of closing the current quote, and putting the portlet back
 * into it's initial state - which is to display the product home page.
 * </p>
 * <p>
 * <img src="doc-files/QuitButtonAction.png"/>
 * </p>
 */
public class QuitButtonAction extends CommandButtonAction {
    private static final long serialVersionUID = 7575333161831400599L;

    public QuitButtonAction() {
        setLabel("i18n_quit_button_label");
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (buttonPressed()) {
            model = super.processActions(model);
            PageFlowContext.reinitialise();
        }
        return model;
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return this.getClass().getSimpleName().equals(op);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("QuitButtonAction", model);
    }
}
