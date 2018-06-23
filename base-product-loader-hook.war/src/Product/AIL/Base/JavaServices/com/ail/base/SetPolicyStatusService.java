package com.ail.base;
import com.ail.core.product.ProductServiceCommand;
/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
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
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

@ProductServiceCommand(serviceName = "SetPolicyStatusService", commandName = "SetPolicyStatus")
public class SetPolicyStatusService {
    /**
     * Set the quotation status
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        String status = (String) args.getActionArg().xpathGet("attribute[id='policyStatus']/value");
        policy.setStatus(PolicyStatus.forName(status));
    }
}