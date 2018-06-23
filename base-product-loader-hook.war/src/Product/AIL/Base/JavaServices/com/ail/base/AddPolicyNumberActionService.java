package com.ail.base;
/* Copyright Applied Industrial Logic Limited. 2016. All rights Reserved */
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
import com.ail.core.CoreProxy;
import com.ail.core.product.ProductServiceCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.AddPolicyNumberService.AddPolicyNumberCommand;

/**
 * <p>
 * This page action service adds a policy number to the current quote. If the
 * quote already contains a policy number, an error will be written to the
 * console.
 * </p>
 * <p>
 * This action can be invoked from a page flow as follows:
 * 
 * <pre>
 * &lt;questionPage id="..."&gt;
 * &nbsp;&lt;action when="onRenderResponse" commandName="AddPolicyNumberAction"/&gt;
 *  ...
 * &lt;/questionPage&gt;
 * </pre>
 * 
 * </p>
 */
@ProductServiceCommand(serviceName = "AddPolicyNumberActionService", commandName = "AddPolicyNumberAction")
public class AddPolicyNumberActionService {
    public static void invoke(ExecutePageActionArgument args) {
        CoreProxy core = PageFlowContext.getCoreProxy();

        try {
            Policy policy = (Policy) args.getModelArgRet();
            AddPolicyNumberCommand command = (AddPolicyNumberCommand) core.newCommand(AddPolicyNumberCommand.class);
            command.setPolicyArgRet(policy);
            command.invoke();
            args.setModelArgRet(command.getPolicyArgRet());
        } catch (Exception e) {
            core.logError("Error in AddPolicyNumberActionService: " + e);
        }
    }
}
