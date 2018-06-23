/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

import static com.ail.core.Functions.isEmpty;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;
import com.ail.pageflow.portlet.PageFlowException;

public class ActionSwitch extends Action {

    @Override
    public Type processActions(Type model) throws BaseException {
        if (conditionIsMet(model)) {
            String selectedSwitchId = determineSelectedSwitchId(model);

            if (selectedSwitchId != null) {

                Action matchedAction = null, defaultAction = null;

                for (Action a : getAction()) {
                    if (selectedSwitchId.equals(a.getId())) {
                        matchedAction = a;
                    }
                    if ("default".equals(a.getId())) {
                        defaultAction = a;
                    }
                }

                if (matchedAction != null) {
                    matchedAction.processActions(model);
                } else if (defaultAction != null) {
                    defaultAction.processActions(model);
                }
            }
        }

        return model;
    }

    private String determineSelectedSwitchId(Type model) throws PageFlowException {
        if (!isEmpty(getBinding())) {
            Object target = fetchBoundObject(model, null);
            return target != null ? target.toString() : null;
        }

        if (!isEmpty(getCommandName())) {
            return executeCommand(model).getSelectedSwitchIdRet();
        }

        throw new PageFlowException("ActionSwtich must define one of 'commandName' or 'binding'.");
    }

    private ExecutePageActionCommand executeCommand(Type model) {
        try {
            // Flush the existing policy to the DB - just in case the
            // service kicks off something that reads the DB.
            PageFlowContext.savePolicy();

            // Execute the command
            ExecutePageActionCommand c = PageFlowContext.getCoreProxy().newCommand(ExecutePageActionCommand.class);

            c.setServiceNameArg(super.getCommandName());
            c.setModelArgRet(model);
            c.invoke();

            return c;
        } catch (RenderingError e) {
            throw e;
        } catch (Throwable e) {
            throw new RenderingError("Failed to updated persisted model: " + getCommandName(), e);
        }
    }
}
