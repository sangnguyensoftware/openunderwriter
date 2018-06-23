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
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.OverrideStatusService.OverrideStatusCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

@ProductServiceCommand(serviceName = "QuotationToApplicationService", commandName = "QuotationToApplication")
public class QuotationToApplicationService {

    public static void invoke(ExecutePageActionArgument args) throws Exception {
        Policy policy = (Policy) args.getModelArgRet();

        CoreProxy core = PageFlowContext.getCoreProxy();

        OverrideStatusCommand osc = (OverrideStatusCommand) core.newCommand(OverrideStatusCommand.class);
        osc.setPolicyArg(policy);
        osc.setPolicyStatusArg(PolicyStatus.APPLICATION);
        osc.invoke();
        policy = osc.getPolicyArg();
        policy.setQuotationNumber(null);
        policy.setPolicyNumber(null);
        args.setModelArgRet(policy);

    }
}
