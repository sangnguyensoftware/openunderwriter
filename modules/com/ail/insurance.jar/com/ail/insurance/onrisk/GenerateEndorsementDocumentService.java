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

import static com.ail.insurance.policy.PolicyStatus.ON_RISK;

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
import com.ail.insurance.policy.Policy;

/**
 * Service to generate an certificate document. This service delegates to the three document
 * generation phase services: Merge, Style and Render. The actual services used in the
 * generation phases depends on the {@link DocumentDefinition} type defined in the product associated
 * with the policy for which a document is being generated. By convention, this type is named "InvoiceDocument".
 */
@ServiceImplementation
public class GenerateEndorsementDocumentService extends Service<GenerateEndorsementDocumentService.GenerateEndorsementDocumentArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @ServiceArgument
    public interface GenerateEndorsementDocumentArgument extends Argument {
        /**
         * The policy for which a certificate is to be generated. The policy's status must be
         * certificate for the generation to be successful.
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

    @ServiceCommand(defaultServiceClass=GenerateEndorsementDocumentService.class)
    public interface GenerateEndorsementDocumentCommand extends Command, GenerateEndorsementDocumentArgument {}

    /**
     * Return the product name from the arguments as the configuration namespace.
     * The has the effect of selecting the product's configuration.
     * @return product name
     */
    @Override
    public String getConfigurationNamespace() {
        return Functions.productNameToConfigurationNamespace(args.getPolicyArg().getProductTypeId());
    }

    @Override
	public void invoke() throws BaseException {
        if (args.getPolicyArg()==null) {
            throw new PreconditionException("args.getPolicyArg()==null");
        }

		if (!ON_RISK.equals(args.getPolicyArg().getStatus())) {
            throw new PreconditionException("!ON_RISK.equals(args.getPolicyArg().getStatus())");
        }

        if (args.getPolicyArg().getProductTypeId()==null || args.getPolicyArg().getProductTypeId().length()==0) {
            throw new PreconditionException("args.getPolicyArg().getProductTypeId()==null || args.getPolicyArg().getProductTypeId().length()==0");
        }

        GenerateDocumentCommand gdc=getCore().newCommand(GenerateDocumentCommand.class);

        gdc.setDocumentDefinitionArg("EndorsementDocument");
        gdc.setModelArg(args.getPolicyArg());
        gdc.setProductNameArg(args.getPolicyArg().getProductTypeId());

        gdc.invoke();

        args.setDocumentRet(gdc.getRenderedDocumentRet());

        if (args.getDocumentRet()==null || args.getDocumentRet().length==0) {
            throw new PostconditionException("args.getDocumentRet()==null || args.getDocumentRet().length==0");
        }
    }
}
