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

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.core.workflow.CaseType;
import com.ail.core.workflow.SignalWorkflowService.SignalWorkflowCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

/**
 * A service to signal a workflow. Expects nothing more than a Policy on the
 * context and a signalName arg, and will call any active workflows containing
 * the context policy external system id with the signalName. If the signalValue
 * arg is populated then that will be the value to the signal but it is not
 * necessarily required.
 */
@ProductServiceCommand(serviceName = "SignalWorkflowService", commandName = "SignalWorkflow")
public class SignalWorkflowService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new SignalWorkflowService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws PostconditionException, JSONException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("SignalWorkflowService argument: " + arg);

        if (StringUtils.isBlank(arg.signalName)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg signalName: " + arg.signalName);
        }

        signalWorkflow(arg, coreProxy);

        return new RestfulServiceReturn(HTTP_OK);
    }

    private void signalWorkflow(Argument arg, CoreProxy coreProxy) throws PostconditionException {
        try {
            Policy policy = PageFlowContext.getPolicy();

            SignalWorkflowCommand signalWorkflowCommand = (SignalWorkflowCommand) coreProxy.newCommand(SignalWorkflowCommand.class);
            signalWorkflowCommand.setCaseTypeArg(CaseType.POLICY);
            signalWorkflowCommand.setModelArg(policy);
            signalWorkflowCommand.setSignalNameArg(arg.signalName);
            signalWorkflowCommand.setSignalValueArg(arg.signalValue);
            signalWorkflowCommand.setPropertiesArg(coreProxy.getGroup("jBPMProperties").getParameterAsProperties());
            signalWorkflowCommand.invoke();
        } catch (Exception e) {
            coreProxy.logError("Failed to signal workflow", e);
        }
    }

    public static class Argument {

        public String signalName;
        public String signalValue;

    }

}