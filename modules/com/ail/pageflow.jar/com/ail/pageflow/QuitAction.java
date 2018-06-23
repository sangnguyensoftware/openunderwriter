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

import com.ail.core.Type;

/**
 */
public class QuitAction extends Action {
    private static final long serialVersionUID = 7575816361831400599L;

    public QuitAction() {
    }

    @Override
    public Type executeAction(Type model, ActionType currentPhase) {
        if (getWhen().equals(currentPhase)) {
            if (conditionIsMet(model)) {
                PageFlowContext.reinitialise();
            }
        }
        return model;
    }
}
