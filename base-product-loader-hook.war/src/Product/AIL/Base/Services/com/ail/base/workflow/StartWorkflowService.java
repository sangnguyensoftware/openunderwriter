package com.ail.base.workflow;
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.core.workflow.CaseType;
import com.ail.core.workflow.StartWorkflowService.StartWorkflowCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

/**
 * A service to start a workflow. Expects nothing more than a workflowId arg,
 * which will start the workflow with that id. Any parameters that need to be
 * passed to the workflow should be added to the Attributes. This can include
 * for example policyId, claimId, partyId etc; whatever the new workflow needs.
 */
@ProductServiceCommand(serviceName = "StartWorkflowService", commandName = "StartWorkflow")
public class StartWorkflowService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new StartWorkflowService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws PostconditionException, JSONException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("StartWorkflowService argument: " + arg);

        if (StringUtils.isBlank(arg.workflowId)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg workflowId: " + arg.workflowId);
        }

        startWorkflow(arg, coreProxy);

        return new RestfulServiceReturn(HTTP_OK);
    }

    private void startWorkflow(Argument arg, CoreProxy coreProxy) throws PostconditionException {
        try {
            StartWorkflowCommand startWorkflowCommand = (StartWorkflowCommand) coreProxy.newCommand(StartWorkflowCommand.class);
            startWorkflowCommand.setPropertiesArg(coreProxy.getGroup("jBPMProperties").getParameterAsProperties());
            startWorkflowCommand.setWorkflowIdArg(arg.workflowId);
            startWorkflowCommand.setAbortExistingArg(arg.abortExisting);
            if (arg.caseType != null) {
                CaseType caseType = CaseType.forName(arg.caseType);
                startWorkflowCommand.setCaseTypeArg(caseType);

                if (CaseType.POLICY.equals(caseType)) {
                    startWorkflowCommand.setModelArg(PageFlowContext.getPolicy());
                } else if (CaseType.PARTY.equals(caseType)) {
                    startWorkflowCommand.setModelArg(PageFlowContext.getParty());
                } else if (CaseType.CLAIM.equals(caseType)) {
                    startWorkflowCommand.setModelArg(PageFlowContext.getClaim());
                }
            }

            if (arg.attributes != null) {
                List<com.ail.core.Attribute> attributes = new ArrayList<com.ail.core.Attribute>();
                for (StartWorkflowService.Argument.Attribute a : arg.attributes) {
                	String value = a.value;
                	if(value.startsWith("RequestProperty=")) {
                		value = value.replaceFirst("RequestProperty=", "");
                		value = (String)PageFlowContext.getSessionTemp().xpathGet("attribute[id='"+value+"']/value",null);                		
                	}
                    attributes.add(new com.ail.core.Attribute(a.name, value, "String"));
                }
                startWorkflowCommand.setAttributeArg(attributes);
            }

            startWorkflowCommand.invoke();
        } catch (Exception e) {
            coreProxy.logError("Failed to start workflow", e);
        }
    }

    public static class Argument {

        public String caseType;
        public String workflowId;
        public boolean abortExisting = true;
        public Attribute[] attributes;

        public static class Attribute {
            public String name;
            public String value;
        }
    }

}