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
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.MergeDataService.MergeDataCommand;
import com.ail.core.document.RenderDocumentService.RenderDocumentCommand;
import com.ail.core.document.StyleDocumentService.StyleDocumentCommand;
import com.ail.core.document.model.DocumentDefinition;
import com.ail.financial.Invoice;

/**
 * Service to generate an invoice document. This service delegates to the three document
 * generation phase services: Merge, Style and Render. The actual services used in the
 * generation phases depends on the {@link DocumentDefinition} type defined in the product associated
 * with the policy for which a document is being generated. By convention, this type is named "InvoiceDocument".
 */
@ServiceImplementation
public class GenerateInvoiceService extends Service<GenerateInvoiceService.GenerateInvoiceArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    /**
     * Interface defining the arguments and returns associated with the invoice document generation service.
     */
    @ServiceArgument
    public interface GenerateInvoiceArgument extends Argument {
        /**
         * The invoice to generate a document for.
         * @return
         */
        Invoice getInvoiceArg();
        
        /**
         * @see #setInvoiceArg(Invoice)
         * @param invoiceArg
         */
        void setInvoiceArg(Invoice invoiceArg);
        
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

    @ServiceCommand(defaultServiceClass=GenerateInvoiceService.class)
    public interface GenerateInvoiceCommand extends Command, GenerateInvoiceArgument {}
    
    /**
     * Return the product name from the arguments as the configuration namespace. 
     * The has the effect of selecting the product's configuration.
     * @return product name
     */
    @Override
    public String getConfigurationNamespace() {
        return Functions.productNameToConfigurationNamespace(args.getInvoiceArg().getProductTypeId());
    }

    /**
     * The 'business logic' of the entry point.
     * @throws PreconditionException If one of the preconditions is not met
     * @throws SectionNotFoundException If one of the sections identified in the
     */
    @Override
	public void invoke() throws BaseException {
        XMLString subject=null;

        if (args.getInvoiceArg().getProductTypeId()==null || args.getInvoiceArg().getProductTypeId().length()==0) {
            throw new PreconditionException("args.getInvoiceArg().getProductTypeId()==null || args.getInvoiceArg().getProductTypeId().length()==0");
        }

        if (args.getInvoiceArg().getInvoiceNumber()==null || args.getInvoiceArg().getInvoiceNumber().length()==0) {
            throw new PreconditionException("args.getInvoiceArg().getInvoiceNumber()==null || args.getInvoiceArg().getInvoiceNumber().length()==0");
        }
        
        if (args.getInvoiceArg().getAddressee()==null) { 
            throw new PreconditionException("args.getInvoiceArg().getAddressee()==null");
        }
        
        if (args.getInvoiceArg().getFrom()==null) {
            throw new PreconditionException("args.getInvoiceArg().getFrom()==null");
        }
        
        if (args.getInvoiceArg().getNarative()==null) {
            throw new PreconditionException("args.getInvoiceArg().getNarative()==null");
        }
        
        if (args.getInvoiceArg().getFinancialAmounts()==null || args.getInvoiceArg().getFinancialAmounts().size()==0) {
            throw new PreconditionException("args.getInvoiceArg().getFinancialAmounts()==null || args.getInvoiceArg().getFinancialAmounts().size()==0");
        }
        
        if (args.getInvoiceArg().getRegisteredOffice()==null) {
            throw new PreconditionException("args.getInvoiceArg().getRegisteredOffice()==null");
        }
        
        DocumentDefinition docDef=(DocumentDefinition)core.newProductType(args.getInvoiceArg().getProductTypeId(), "InvoiceDocument");
        
        // 1st step: data merge (if configured)
        if (docDef.getMergeCommand()!=null && docDef.getMergeCommand().length()!=0) {
            MergeDataCommand merge=core.newCommand(docDef.getMergeCommand(), MergeDataCommand.class);
            merge.setDocumentDataArg(docDef.getDocumentData());
            merge.setModelArg(args.getInvoiceArg());
            merge.invoke();
            subject=merge.getMergedDataRet();
        }

        // 2nd step: apply style (if configured)
        if (docDef.getStyleCommand()!=null && docDef.getStyleCommand().length()!=0) {
            StyleDocumentCommand style=core.newCommand(docDef.getStyleCommand(), StyleDocumentCommand.class);
            style.setMergedDataArg(subject);
            style.invoke();
            subject=style.getStyledDocumentRet();
        }
        
        // 3rd step: render
        RenderDocumentCommand render=core.newCommand(docDef.getRenderCommand(), RenderDocumentCommand.class);
        render.setSourceDataArg(subject);
        render.invoke();
        
        args.setDocumentRet(render.getRenderedDocumentRet());
        
        if (args.getDocumentRet()==null || args.getDocumentRet().length==0) {
            throw new PostconditionException("args.getDocumentRet()==null || args.getDocumentRet().length==0");
        }
    }
}
