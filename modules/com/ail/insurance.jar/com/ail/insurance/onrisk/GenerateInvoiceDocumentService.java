/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.GenerateDocumentService.GenerateDocumentCommand;
import com.ail.core.document.model.DocumentDefinition;
import com.ail.insurance.claim.SectionNotFoundException;
import com.ail.insurance.policy.Policy;

/**
 * Service to generate an invoice document. This service delegates to the three document
 * generation phase services: Merge, Style and Render. The actual services used in the
 * generation phases depends on the {@link DocumentDefinition} type defined in the product associated
 * with the policy for which a document is being generated. By convention, this type is named "InvoiceDocument".
 */
@ServiceImplementation
public class GenerateInvoiceDocumentService extends Service<GenerateInvoiceDocumentService.GenerateInvoiceDocumentArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @ServiceArgument
    public interface GenerateInvoiceDocumentArgument extends Argument {
        /**
         * The policy for which a policy is to be generated. The policy's status must be
         * policy for the generation to be successful.
         * @return policy to generate for.
         */
        Policy getPolicyArg();

        /**
         * @see #getPolicyArg()
         * @param policyArg
         */
        void setPolicyArg(Policy policyArg);

        /**
         * The generated document.
         * @return document
         */
        byte[] getDocumentRet();

        /**
         * @see #getDocumentRet()
         * @param documentRet
         */
        void setDocumentRet(byte[] documentRet);
    }

    @ServiceCommand(defaultServiceClass=GenerateInvoiceDocumentService.class)
    public interface GenerateInvoiceDocumentCommand extends Command, GenerateInvoiceDocumentArgument {}

    /**
     * Return the product name from the arguments as the configuration namespace.
     * The has the effect of selecting the product's configuration.
     * @return product name
     */
    @Override
    public String getConfigurationNamespace() {
        return Functions.productNameToConfigurationNamespace(args.getPolicyArg().getProductTypeId());
    }

    /**
     * The 'business logic' of the entry point.
     * @throws PreconditionException If one of the preconditions is not met
     * @throws SectionNotFoundException If one of the sections identified in the
     */
    @Override
	public void invoke() throws BaseException {

        if (args.getPolicyArg()==null) {
            throw new PreconditionException("args.getPolicyArg()==null");
        }

        Policy policy=args.getPolicyArg();

        if (policy.getProductTypeId()==null || policy.getProductTypeId().length()==0) {
            throw new PreconditionException("policy.getProductTypeId()==null || policy.getProductTypeId().length()==0");
        }

        if (policy.getTotalPremium()==null) {
            throw new PreconditionException("policy.getTotalPremium()==null");
        }

        if (policy.getClient()==null) {
            throw new PreconditionException("policy.getClient()==null");
        }

        GenerateDocumentCommand gdc=getCore().newCommand(GenerateDocumentCommand.class);

        gdc.setDocumentDefinitionArg("InvoiceDocument");
        gdc.setModelArg(args.getPolicyArg());
        gdc.setProductNameArg(args.getPolicyArg().getProductTypeId());

        gdc.invoke();

        args.setDocumentRet(gdc.getRenderedDocumentRet());

        if (args.getDocumentRet()==null || args.getDocumentRet().length==0) {
            throw new PostconditionException("args.getDocumentRet()==null || args.getDocumentRet().length==0");
        }
    }
}
