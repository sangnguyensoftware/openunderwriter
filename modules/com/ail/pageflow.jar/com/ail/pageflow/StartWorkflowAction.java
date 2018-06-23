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
import com.ail.core.workflow.StartWorkflowService.StartWorkflowCommand;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.portlet.PageFlowCommon;
import com.ail.party.Party;
/**
 * A page action that starts a workflow, passing in a variable id for the current model type.
 * Currently supports {@link Policy}, {@link Claim}, and {@link Party}
 * The actual command to start the workflow is only added to the list of commands to run outside of the current
 * persistence session. This is because we don't want the workflow to make changes to the model at the same time
 * as the session is also potentially making changes as we then end up with synchronisation errors.
 */
public class StartWorkflowAction extends Action {

    private String workflowId;
    private boolean abortExisting = true;
    private List<String> onChangeField = new ArrayList<>();

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public boolean isAbortExisting() {
        return abortExisting;
    }

    public void setAbortExisting(boolean abortExisting) {
        this.abortExisting = abortExisting;
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

                StartWorkflowCommand startWorkflowCommand = coreProxy.newCommand(StartWorkflowCommand.class);
                startWorkflowCommand.setWorkflowIdArg(getWorkflowId());
                startWorkflowCommand.setAbortExistingArg(isAbortExisting());
                startWorkflowCommand.setCaseTypeArg(caseType);
                startWorkflowCommand.setModelArg(boundModel);
                startWorkflowCommand.setOnChangeFieldArg(getOnChangeField());
                startWorkflowCommand.setAttributeArg(new ArrayList<>());
                startWorkflowCommand.getAttributeArg().addAll(getAttribute());
                startWorkflowCommand.getAttributeArg().addAll(PageFlowContext.getSessionTemp().getAttribute());
                startWorkflowCommand.setPropertiesArg(coreProxy.getGroup("jBPMProperties").getParameterAsProperties());
                OutsideTransactionContext.addPostCommitCommand(startWorkflowCommand);
            } catch (Exception e) {
                coreProxy.logError("Failed to start workflow", e);
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