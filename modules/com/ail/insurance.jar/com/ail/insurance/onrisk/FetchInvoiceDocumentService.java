/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
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

package com.ail.insurance.onrisk;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.document.FetchDocumentService.FetchDocumentArgument;
import com.ail.insurance.onrisk.GenerateInvoiceDocumentService.GenerateInvoiceDocumentCommand;
import com.ail.insurance.policy.Policy;

@ServiceImplementation
public class FetchInvoiceDocumentService  extends Service<FetchDocumentArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    /**
     * The 'business logic' of the entry point.
     * @throws PreconditionException If one of the preconditions is not met
     */
    @Override
    public void invoke() throws BaseException {
        if (args.getModelIDArg() == null) {
            throw new PreconditionException("args.getModelIDArg() == null");
        }

        // Fetch the saved quote from persistence
        Policy policy = (Policy) getCore().queryUnique("get.policy.by.systemId", args.getModelIDArg());

        if (policy == null) {
            throw new PreconditionException("core.queryUnique(get.policy.by.systemId, "+args.getModelIDArg()+")==null");
        }

        if (policy == null || policy.getPaymentDetails() == null) {
            throw new PreconditionException("policy == null || policy.getPaymentDetails() == null");
        }

        // We only generate invoice docs on demand, so if there isn't one - generate it.
        if (policy.retrieveInvoiceDocument()==null) {
            GenerateInvoiceDocumentCommand cmd = getCore().newCommand(GenerateInvoiceDocumentCommand.class);
            cmd.setPolicyArg(policy);
            cmd.invoke();
            policy.attachInvoiceDocument(cmd.getDocumentRet());
            policy = getCore().update(policy);
            args.setModelRet(policy);
        }

        args.setDocumentRet(policy.retrieveInvoiceDocument());
    }
}
