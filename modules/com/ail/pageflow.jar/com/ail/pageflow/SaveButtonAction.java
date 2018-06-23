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
import com.ail.core.CoreContext;
import com.ail.core.Owned;
import com.ail.core.Type;
import com.ail.insurance.policy.Policy;

/**
 * <p>
 * Adds a save button to a page. By default this button saves the quote and
 * takes the user to the product's SavedQuotations page when selected.
 * </p>
 * <p>
 * <img src="doc-files/SaveButtonAction.png"</>
 * </p>
 * <p>
 * This page element cooperates with any {@link LoginSection LoginSection} page
 * element on the same page and will enable the LoginSection if the user is not
 * currently logged in.
 * </p>
 * <p>
 * The "Save" label may be overridden using the element's
 * {@link #setLabel(String) label} property.
 * </p>
 */
public class SaveButtonAction extends CommandButtonAction {
    private static final long serialVersionUID = 7575333161831400599L;

    public SaveButtonAction() {
        setDestinationPageId("SavedQuotation");
        setLabel("i18n_save_button_label");
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (buttonPressed()) {
            model = super.processActions(model);

            ((Policy)model).setUserSaved(true);

            if (isNotOwned(model)) {
                giveCurrentUserOwnershipOf(model);
            }

            PageFlowContext.savePolicy();

            PageFlowContext.getCoreProxy().flush();

            model = super.processActions(model);

            PageFlowContext.reinitialise();

            PageFlowContext.setNextPageName(getDestinationPageId());

            PageFlowContext.flagActionAsProcessed();
        }

        return model;
    }

    private boolean isNotOwned(Type model) {
        return model instanceof Owned && ((Owned)model).getOwningUser()==null;
    }

    private void giveCurrentUserOwnershipOf(Type model) {
        ((Owned)model).setOwningUser(CoreContext.getRemoteUser());
    }

    @Override
    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return this.getClass().getSimpleName().equals(op);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("SaveButtonAction", model);
    }
}
