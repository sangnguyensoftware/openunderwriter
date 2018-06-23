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

import static com.ail.pageflow.ActionType.ON_PROCESS_ACTIONS;

import com.ail.core.Type;

/**
 * A page action than conditionally moves the context to a specified page.
 * PageForwardActions may be nested inside {@link CommandButtonAction
 * CommandButtonActions} in order to override the CommandButtonAction's default
 * page destination if some condition is met.</br>
 * For example, it is common for the last page in a quotation page flow
 * (immediately before the quote or referral summary is shown) to include a
 * CommandButtonAction directing the pageflow to the referral page, but nest a
 * PageForwardAction which will forward to the quote summary page if the quote
 * is successful.
 *
 * @see CommandButtonAction
 */
public class PageForwardAction extends Action {
    private static final long serialVersionUID = 7575333161831400599L;
    private String destinationPageId;

    public String getDestinationPageId() {
        return destinationPageId;
    }

    public void setDestinationPageId(String destinationPageId) {
        this.destinationPageId = destinationPageId;
    }

    @Override
    public Type executeAction(Type model, ActionType currentPhase) {
        if (getWhen().equals(currentPhase)) {
            if (ON_PROCESS_ACTIONS.equals(getWhen()) && conditionIsMet(model)) {
                PageFlowContext.getPageFlow().setNextPage(getDestinationPageId());
            }
        }
        return model;
    }
}
