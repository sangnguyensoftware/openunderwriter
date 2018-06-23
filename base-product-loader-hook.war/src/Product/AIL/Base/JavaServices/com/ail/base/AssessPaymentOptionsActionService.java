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
import com.ail.insurance.acceptance.AssessPaymentOptionsService.AssessPaymentOptionsCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

@ProductServiceCommand(serviceName = "AssessPaymentOptionsActionService", commandName = "AssessPaymentOptionsAction")
public class AssessPaymentOptionsActionService {
    public static void invoke(ExecutePageActionArgument args) {
        CoreProxy core = PageFlowContext.getCoreProxy();

        Policy policy = (Policy) args.getModelArgRet();

        try {
            AssessPaymentOptionsCommand command = (AssessPaymentOptionsCommand) core.newCommand("AssessPaymentOptions", AssessPaymentOptionsCommand.class);
            command.setPolicyArg(policy);
            command.invoke();
            policy.setPaymentOption(command.getOptionsRet());
        } catch (Exception e) {
            core.logError("Error in AssessPaymentOptionsActionService: " + e);
        }
    }
}