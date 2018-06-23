/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

package com.ail.core.document;

import static com.ail.core.Functions.isEmpty;
import static com.ail.core.Functions.productNameToConfigurationNamespace;

import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.MergeDataService.MergeDataCommand;
import com.ail.core.document.RenderDocumentService.RenderDocumentCommand;
import com.ail.core.document.StyleDocumentService.StyleDocumentCommand;
import com.ail.core.document.model.DocumentDefinition;

@ServiceImplementation
public class GenerateDocumentService extends Service<GenerateDocumentService.GenerateDocumentArgument> {

    @ServiceArgument
    public interface GenerateDocumentArgument extends Argument {
        /**
         * Getter for the modelArg property. This is the representation of the dynamic data required to render the document
         * @return Value of modelArg, or null if it is unset
         */
        Type getModelArg();

        /**
         * Setter for the modelArg property.
         * @see #getModelArg
         * @param modelArg new value for property.
         */
        void setModelArg(Type modelArg);

        /**
         * Getter for the documentDefinition property. Defines the type of document to be rendered in
         * terms of the name of a DocumentDefinition type. Once instantiated this type defines all of the
         * pipeline processes that the document may need to pass through to perform the full render.
         * @see #getDocumentTypeArg() as an alternative
         * @return Value of documentDefinition, or null if it is unset
         */
        String getDocumentDefinitionArg();

        /**
         * Setter for the documentDefinition property.
         * @see #getDocumentDefinitionArg
         * @param documentDefinition new value for property.
         */
        void setDocumentDefinitionArg(String documentDefinition);

        /**
         * Getter returning the template to be used in document generation. Using this as an alternative
         * to {@link #getDocumentDefinitionArg()} avoids the need for Registry entries to define the document
         * definition and it's render commands and instead relies on conventions to identify which render service
         * to use based on the type (file extension) of the template.
         * @see #getDocumentDefinitionArg()
         * @return Value of template arg
         */
        String getDocumentTypeArg();

        /**
         * @see #getDocumentTypeArg()
         * @param documentTypeArg
         */
        void setDocumentTypeArg(String documentTypeArg);

        /**
         * Getter returning the name of the product for which the document is to be generated,
         * @return Value of productNameArg, or null if it is unset.
         */
        String getProductNameArg();

        /**
         * @see #getProductNameArg()
         * @param productName
         */
        void setProductNameArg(String productNameArg);

        /**
         * Getter for the renderedDocumentRet property. The result of the rendering process
         * @return Value of renderedDocumentRet, or null if it is unset
         */
        byte[] getRenderedDocumentRet();

        /**
         * Setter for the renderedDocumentRet property. * @see #getRenderedDocumentRet
         * @param renderedDocumentRet new value for property.
         */
        void setRenderedDocumentRet(byte[] renderedDocumentRet);

        /**
         * An optional list of documents which are required to be included in
         * the generated document as sub-parts. This may be set to null or be an
         * empty list. See the javadocs associated with each render service for
         * details of this option's usage.
         */
        void setAttachmentArgRet(List<Document> attachmentArgRet);

        /**
         * @see #setAttachmentArgRet(List)
         */
        List<Document> getAttachmentArgRet();
}

    @ServiceCommand(defaultServiceClass=GenerateDocumentService.class)
    public interface GenerateDocumentCommand extends Command, GenerateDocumentArgument {}

    /**
     * Return the product name from the arguments as the configuration namespace.
     * The has the effect of selecting the product's configuration.
     * @return product name
     */
    @Override
    public String getConfigurationNamespace() {
        return productNameToConfigurationNamespace(args.getProductNameArg());
    }

    /** The 'business logic' of the entry point. */
    @Override
    public void invoke() throws BaseException, PreconditionException, PostconditionException {

        if (args.getModelArg()==null) {
            throw new PreconditionException("args.getModelArg()==null");
        }

        if (args.getProductNameArg()==null || args.getProductNameArg().length()==0) {
            throw new PreconditionException("args.getProductNameArg()==null || args.getProductNameArg().length()==0");
        }

        if (isEmpty(args.getDocumentDefinitionArg()) && isEmpty(args.getDocumentTypeArg())) {
            throw new PreconditionException("isEmpty(args.getDocumentDefinitionArg()) && isEmpty(args.getDocumentTemplateArg())");
        }

        RenderDocumentCommand renderCommand = null;

        if (!isEmpty(args.getDocumentDefinitionArg())) {
            renderCommand = buildRenderCommandFromDocumentDefinition();
        }

        if (!isEmpty(args.getDocumentTypeArg())) {
            renderCommand = buildRenderCommandFromDocumentTemplate();
        }

        if (renderCommand == null) {
            throw new PreconditionException("Failed to determine renderCommand for args: "+args);
        }

        renderCommand.invoke();

        args.setRenderedDocumentRet(renderCommand.getRenderedDocumentRet());

        if (args.getRenderedDocumentRet()==null || args.getRenderedDocumentRet().length==0) {
            throw new PostconditionException("args.getRenderedDocumentRet()==null || args.getRenderedDocumentRet().length==0");
        }
    }

    RenderDocumentCommand buildRenderCommandFromDocumentDefinition() throws BaseException {
        XMLString subject;
        DocumentDefinition docDef = (DocumentDefinition) core.newProductType(args.getProductNameArg(), args.getDocumentDefinitionArg());

        // 1st step: data merge (if configured)
        if (docDef.getMergeCommand() != null) {
            MergeDataCommand merge = core.newCommand(docDef.getMergeCommand(), MergeDataCommand.class);
            merge.setDocumentDataArg(docDef.getDocumentData());
            merge.setModelArg(args.getModelArg());
            merge.invoke();
            subject = merge.getMergedDataRet();
        } else {
            subject = core.toXML(args.getModelArg());
        }

        // 2nd step: apply style (if configured)
        if (docDef.getStyleCommand() != null && docDef.getStyleCommand().length() != 0) {
            StyleDocumentCommand style = core.newCommand(docDef.getStyleCommand(), StyleDocumentCommand.class);
            style.setMergedDataArg(subject);
            style.invoke();
            subject = style.getStyledDocumentRet();
        }

        // 3rd step: render
        RenderDocumentCommand render = core.newCommand(docDef.getRenderCommand(), RenderDocumentCommand.class);
        render.setSourceDataArg(subject);
        render.setAttachmentArgRet(args.getAttachmentArgRet());
        return render;
    }

    private RenderDocumentCommand buildRenderCommandFromDocumentTemplate() throws BaseException {

        String template = fetchDocumentTemplateForType(args.getDocumentTypeArg());

        if (isEmpty(template)) {
            throw new PreconditionException("template not defined for document type: "+args.getDocumentTypeArg());
        }

        if (template.endsWith("docx")) {
            RenderDocumentCommand render = CoreContext.getCoreProxy().newCommand("RenderXDocDocumentCommand", RenderDocumentCommand.class);
            render.setSourceModelArg(args.getModelArg());
            render.setTemplateUrlArg(template);

            args.getModelArg().fetchJXPathContext().getVariables().declareVariable("selectedDocumentType", args.getDocumentTypeArg());

            return render;
        }

        throw new PreconditionException("template file type not supported for document type: "+args.getDocumentTypeArg());
    }

    private String fetchDocumentTemplateForType(String documentTypeArg) {
        // TODO when Choice is moved to core rework this to avoid xpath usage.
        return CoreContext.getCoreProxy().newType("DocumentTypes", Type.class).xpathGet("choice[name='"+documentTypeArg+"']/attribute[id='template']/value", null, String.class);
    }
}


