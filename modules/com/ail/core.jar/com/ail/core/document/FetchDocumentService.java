/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

/**
 * A generalised service interface for the fetching of documents.
 *
 * <p>Implementations of this service are specific to document types. They accept
 * an ID ({@link FetchDocumentArgument#getModelIDArg() modelIDArg}) which
 * identifies the data to be merged into the document, and either return the
 * previously generated document or generate it if it does not already exist.
 * The then return the generated document in
 * {@link FetchDocumentArgument#getDocumentRet() documentRet} and the model used
 * in {@link FetchDocumentArgument#getModelRet() modelRet}.</p>
 *
 * <p>
 * Each implementation is free to interpret the meaning of the (
 * {@link FetchDocumentArgument#getModelIDArg() modelIDArg}) argument -
 * specifically, the type of object that it relates to is known to the
 * implementation. For example, an implementation that generates policy
 * documents knows that the ID must relate to a policy.
 * </p>
 */
@ServiceInterface
public interface FetchDocumentService  {

    @ServiceArgument
    public interface FetchDocumentArgument extends Argument {
        /**
         * The ID of the model object for which the requested document is being generated.
         *
         * @return model ID.
         */
        Long getModelIDArg();

        /**
         * @see #getModelIDArg()
         * @param modelIDArg
         */
        void setModelIDArg(Long modelIDArg);

        /**
         * Fetch the policy which may have been modified by the process of document generation
         *
         * @return Modified quotation
         */
        Type getModelRet();

        /**
         * @see #getModelRet()
         * @param modelRet
         */
        void setModelRet(Type modelRet);

        /**
         * The generated document.
         *
         * @return document
         */
        Document getDocumentRet();

        /**
         * @see #getDocumentRet()
         * @param documentRet
         */
        void setDocumentRet(Document documentRet);
    }

    @ServiceCommand
    public interface FetchDocumentCommand extends Command, FetchDocumentArgument {
    }
}
