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

package com.ail.invoice;

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
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyCommand;
import com.ail.invoice.GenerateInvoiceNumberService.GenerateInvoiceNumberCommand;
import com.ail.financial.Invoice;

/**
 * Service to generate an invoice number. This service delegates to a product specific service via
 * the GenerateInvoiceNumberCommand, passing it a unique number which that command may use and
 * having check all preconditions.
 */
@ServiceImplementation
public class AddInvoiceNumberService extends Service<AddInvoiceNumberService.AddInvoiceNumberArgument> {
    private static final long serialVersionUID = 3198893603833694389L;
    private String configurationNamespace="com.ail.invoice.AddInvoiceNumberService";

    @ServiceArgument
    public interface AddInvoiceNumberArgument extends Argument {
        Invoice getInvoiceArgRet();

        void setInvoiceArgRet(Invoice invoiceArgRet);
    }
    
    @ServiceCommand(defaultServiceClass=AddInvoiceNumberService.class)
    public interface AddInvoiceNumberCommand extends Command, AddInvoiceNumberArgument {}
    
    /**
     * Return the product name from the arguments as the configuration namespace. 
     * The has the effect of selecting the product's configuration.
     * @return product name
     */
    @Override
    public String getConfigurationNamespace() {
        return configurationNamespace;
    }

    /**
     * The 'business logic' of the entry point.
     * @throws PreconditionException If one of the preconditions is not met
     * @throws SectionNotFoundException If one of the sections identified in the
     */
    @Override
	public void invoke() throws BaseException {
        if (args.getInvoiceArgRet()==null) {
            throw new PreconditionException("args.getInvoiceArgRet()==null");
        }
        
        if (args.getInvoiceArgRet().getProductTypeId()==null) {
            throw new PreconditionException("args.getInvoiceArgRet().getProductTypeId()==null");
        }

        if (args.getInvoiceArgRet().getInvoiceNumber()!=null) {
            throw new PreconditionException("args.getInvoiceArgRet().getInvoiceNumber()!=null");
        }
        
        // switch over to the product's configuration
        configurationNamespace=Functions.productNameToConfigurationNamespace(args.getInvoiceArgRet().getProductTypeId());

        // Get a unique invoice number from the number generator
        GenerateUniqueKeyCommand gukc=core.newCommand(GenerateUniqueKeyCommand.class);
        gukc.setKeyIdArg("InvoiceNumber");
        gukc.invoke();
        
        GenerateInvoiceNumberCommand command=core.newCommand(GenerateInvoiceNumberCommand.class);
        command.setInvoiceArg(args.getInvoiceArgRet());
        command.setUniqueNumberArg(gukc.getKeyRet());
        command.invoke();
        String invoiceNumber=command.getInvoiceNumberRet();
        args.getInvoiceArgRet().setInvoiceNumber(invoiceNumber);
        
        core.logInfo("Invoice number: "+invoiceNumber+" generated");

        if (args.getInvoiceArgRet().getInvoiceNumber()==null || args.getInvoiceArgRet().getInvoiceNumber().length()==0) {
            throw new PostconditionException("args.getInvoiceArgRet().getInvoiceNumber()==null || args.getInvoiceArgRet().getInvoiceNumber().length()==0");
        }
    }
}