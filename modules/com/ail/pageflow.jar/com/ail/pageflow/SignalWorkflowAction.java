/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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

import java.util.ArrayList;
import java.util.List;

import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.core.persistence.OutsideTransactionContext;
import com.ail.core.workflow.CaseType;
import com.ail.core.workflow.SignalWorkflowService.SignalWorkflowCommand;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.portlet.PageFlowCommon;
import com.ail.party.Party;
/**
 * A page action that signals any workflow that contains the signalName and a variable id for the current model type.
 * Currently supports {@link Policy}, {@link Claim}, and {@link Party}
 * The actual command to signal the workflow is only added to the list of commands to run outside of the current
 * persistence session. This is because we don't want the workflow to make changes to the model at the same time
 * as the session is also potentially making changes as we then end up with synchronisation errors.
 */
public class SignalWorkflowAction extends Action {

    private String signalName;
    private String signalValue;
    private List<String> onChangeField = new ArrayList<>();

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getSignalValue() {
        return signalValue;
    }

    public void setSignalValue(String signalValue) {
        this.signalValue = signalValue;
    }

    public List<String> getOnChangeField() {
        return onChangeField;
    }

    public void setOnChangeField(List<String> onChangeField) {
        this.onChangeField = onChangeField;
    }

    @Override
    public Type executeAction(Type model, ActionType currentPhase) {
        if (getWhen().equals(currentPhase) && conditionIsMet(model)) {
            CoreProxy coreProxy = PageFlowContext.getCoreProxy();

            try {
                Type boundModel = (Type) fetchBoundObject(model, model);
                CaseType caseType = null;
                try {
                    caseType = CaseType.forName(boundModel.getClass().getSimpleName());
                } catch (IllegalArgumentException e) {
                    // no matter, just means we're starting a workflow that hopefully does not require a Type
                }

                registerChanges(boundModel);

                SignalWorkflowCommand signalWorkflowCommand = coreProxy.newCommand(SignalWorkflowCommand.class);
                signalWorkflowCommand.setSignalNameArg(getSignalName());
                signalWorkflowCommand.setSignalValueArg(expand(getSignalValue(),model,model));
                signalWorkflowCommand.setCaseTypeArg(caseType);
                signalWorkflowCommand.setModelArg(boundModel);
                signalWorkflowCommand.setOnChangeFieldArg(getOnChangeField());
                signalWorkflowCommand.setPropertiesArg(coreProxy.getGroup("jBPMProperties").getParameterAsProperties());
                OutsideTransactionContext.addPostCommitCommand(signalWorkflowCommand);
            } catch (Exception e) {
                coreProxy.logError("Failed to signal workflow", e);
            }
        }

        return model;
    }

    private void registerChanges(Type model) {
        if (getOnChangeField() != null) {
            for (String onChangeField : getOnChangeField()) {
                PageFlowCommon.registerOldFieldValue(model, onChangeField);
            }
        }
    }
}