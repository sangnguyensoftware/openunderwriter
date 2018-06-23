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

import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Type;
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

@ServiceInterface
public interface RenderDocumentService {

    @ServiceArgument
    public interface RenderDocumentArgument extends Argument {

        /**
         * Get the XML data which the renderer will use as a source.
         * @return Source data
         */
        XMLString getSourceDataArg();

        /**
         * @see #getSourceDataArg()
         * @param sourceDataArg
         */
        void setSourceDataArg(XMLString sourceDataArg);

        /**
         * Source model object which the renderer can use as a source. This is an alternative to {@link #getSourceDataArg()}
         * @return Source model
         */
        Type getSourceModelArg();

        /**
         * @see #getSourceModelArg()
         * @param sourceModelArg
         */
        void setSourceModelArg(Type sourceModelArg);

        /**
         * Get the rendered document. This document is the result of the rendering process -
         * a fully formed PDF, RTF, or whatever other format of file the implementing
         * services support.
         * @return The document resulting from the render process
         */
        byte[] getRenderedDocumentRet();

        /**
         * @see #getRenderedDocumentRet()
         * @param renderedDocumentRet
         */
        void setRenderedDocumentRet(byte[] renderedDocumentRet);

        /**
         * Get the list of render service specific options which will applied during the
         * render operation. See the javadocs associated with each type of render service
         * for a list of the options supported, and a description of how the list is
         * interpreted.
         * @return Comma separated list of options
         */
        String getRenderOptionsArg();

        /**
         * @see #getRenderOptionsArg()
         * @param renderOptionsArg
         */
        void setRenderOptionsArg(String renderOptionsArg);

        /**
         * Set the translation (if any) to be applied to the document pre-render. The interpretation
         * of this argument is dependent on the render service implementation. See the javadocs associated
         * with each render service for details of this option's usage.
         * @param translationUrlArg Translation URL
         */
        void setTranslationUrlArg(String translationUrlArg);

        /**
         * @see #setTranslationUrlArg(String)
         * @return translationUrlArg
         */
        String getTranslationUrlArg();

        /**
         * Set the template (if any) to be used to generate the document. The interpretation of this
         * argument is dependent on the render service implementation. See the javadocs associated
         * with each render service for details of this option's usage.
         * @param templateUrlArg Translation URL
         */
        void setTemplateUrlArg(String templateUrlArg);

        /**
         * @see #setTemplateUrlArg(String)
         * @return templateUrlArg
         */
        String getTemplateUrlArg();

        /**
         * Specify if PDF_A conformance (e.g. embedded fonts) is to be applied
         * @param pdfaConformanceArg "TRUE" if PDF_A conformance required
         */
        void setPDFaConformanceArg(String pdfaConformanceArg);

        /**
         * @see #setPDFaConformanceArg(String)
         * @return pdfaConformanceArg
         */
        String getPDFaConformanceArg();

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

    @ServiceCommand
    public interface RenderDocumentCommand extends Command, RenderDocumentArgument {
    }
}
