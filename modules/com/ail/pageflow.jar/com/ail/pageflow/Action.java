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

import static com.ail.pageflow.ActionType.ON_APPLY_REQUEST_VALUES;
import static com.ail.pageflow.ActionType.ON_PROCESS_ACTIONS;
import static com.ail.pageflow.ActionType.ON_PROCESS_VALIDATIONS;
import static com.ail.pageflow.ActionType.ON_RENDER_RESPONSE;

import java.io.IOException;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionInSubProductService.ExecutePageActionInSubProductCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;

/**
 * Actions allow arbitrary commands to be invoked during a page flow. A number
 * of page elements (e.g. {@link AbstractPage} and {@link CommandButtonAction}) allow
 * Actions to be associated with them and will invoke the commands they define
 * in response to events.
 * </p>
 * For example, any number of Actions can be attached to a CommandButtonAction.
 * When the button is selected all the commands associated with the actions are
 * executed (see {@link #condition}). If the commands execute successfully, the
 * quote's persisted database record is automatically updated to keep it in sync
 * with any changes that the command may have made.
 */
public class Action extends PageElement {
    private static final long serialVersionUID = -1320299722728499324L;
    private ActionType when = ON_PROCESS_ACTIONS;
    private boolean executeInAggregated = false;

    /**
     * The name of the command to be invoked. This command must be a service
     * which implements
     * {@link com.ail.core.product.executepageaction.ExecutePageActionCommand
     * ExecutePageActionCommand}.
     */
    private String commandName;

    public Action() {
        super();
    }

    public Action(ActionType when, String commandName, String condition) {
        super(condition);
        this.when = when;
        this.commandName = commandName;
    }

    @Override
    public void onStartProcessAction(Type model) {
        if (conditionIsMet(model)) {
            model = executeAction(model, ActionType.ON_START_PROCESS_ACTION);
        }
    }


    @Override
    public Type processActions(Type model) throws BaseException {
        if (conditionIsMet(model)) {
            model = executeAction(model, ON_PROCESS_ACTIONS);
        }

        return model;
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (conditionIsMet(model)) {
            model = executeAction(model, ON_APPLY_REQUEST_VALUES);
        }

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        if (conditionIsMet(model)) {
            return executeValidation(model, ON_PROCESS_VALIDATIONS);
        } else {
            return false;
        }
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeAction(model, ON_RENDER_RESPONSE);
    }

    /**
     * Get the action's command
     *
     * @see #commandName
     * @return The action's command name
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Set the actions command name
     *
     * @see #commandName
     * @param commandName
     */
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Get the action's when value
     *
     * @see #when
     * @return The action's when value
     */
    public ActionType getWhen() {
        return when;
    }

    /**
     * Set the action's when value
     *
     * @see #when
     * @param when
     *            When value
     */
    public void setWhen(ActionType when) {
        this.when = when;
    }

    public Type executeAction(Type model, ActionType currentPhase) {
        if (when.equals(currentPhase)) {
            try {
                // Flush the existing policy to the DB - just in case the
                // service kicks off something that reads the DB.
                PageFlowContext.savePolicy();

                // Execute the command
                Policy policy = (Policy) execute(model).getModelArgRet();

                // The policy may have been changed, so get the PageFlowContext
                // back into sync with it
                PageFlowContext.setPolicy(policy);
                PageFlowContext.savePolicy();
            } catch (RenderingError e) {
                throw e;
            } catch (Throwable e) {
                throw new RenderingError("Failed to updated persisted model: " + getCommandName(), e);
            }

            return PageFlowContext.getPolicy();
        } else {
            return model;
        }
    }

    private boolean executeValidation(Type model, ActionType currentPhase) {
        if (when.equals(currentPhase)) {
            return execute(model).getValidationFailedRet();
        } else {
            return false;
        }
    }

    protected ExecutePageActionCommand execute(Type model) {
        if (isExecuteInAggregated()) {
            return executeInAggregatedProducts(model);
        } else {
            return executeInCurrentProduct(model);
        }
    }

    protected ExecutePageActionCommand executeInCurrentProduct(Type model) throws RenderingError {
        ExecutePageActionCommand c = PageFlowContext.getCoreProxy().newCommand(ExecutePageActionCommand.class);

        c.setActionArg(this);
        c.setServiceNameArg(commandName);
        c.setModelArgRet(model);

        try {
            c.invoke();
        } catch (Throwable e) {
            throw new RenderingError("Failed to execute action command: " + getCommandName(), e);
        }

        return c;
    }

    protected ExecutePageActionCommand executeInAggregatedProducts(Type model) {
        try {
            if (!PageFlowContext.getPolicy().isAggregator()) {
                throw new RenderingError("executeInAggregated is only valid in aggregator product");
            }

            ExecutePageActionInSubProductCommand exInSubCommand = PageFlowContext.getCoreProxy().newCommand(ExecutePageActionInSubProductCommand.class);
            exInSubCommand.setCommandNameArg(commandName);
            exInSubCommand.setExecuteInAllSubProductsArg(true);
            exInSubCommand.setModelArgRet(model);
            exInSubCommand.invoke();

            ExecutePageActionCommand c = PageFlowContext.getCoreProxy().newCommand(ExecutePageActionCommand.class);
            c.setModelArgRet(exInSubCommand.getModelArgRet());
            c.setValidationFailedRet(exInSubCommand.getValidationFailedRet());

            return c;
        } catch (BaseException e) {
            throw new RenderingError("Failed to execute action command in sub products: " + getCommandName(), e);
        }
    }

    public boolean isExecuteInAggregated() {
        return executeInAggregated;
    }

    public void setExecuteInAggregated(boolean executeInAggregated) {
        this.executeInAggregated = executeInAggregated;
    }
}
