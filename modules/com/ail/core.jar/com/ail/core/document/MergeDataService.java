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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Type;
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.model.DocumentData;

@ServiceInterface
public interface MergeDataService {

    @ServiceArgument
    public interface MergeDataArgument extends Argument {
        /**
         * Getter for the modelArg property. This is the root of the object graph containing the data
         * to be merged.
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
         * Getter for the documentData property. Defines the document into which data from the model
         * must be merged.
         * @return Value of keyArg, or null if it is unset
         */
        DocumentData getDocumentDataArg();

        /**
         * Setter for the keyArg property. 
         * @see #getDocumentDataArg
         * @param keyArg new value for property.
         */
        void setDocumentDataArg(DocumentData documentDataArg);

        /**
         * Set the name of the product for which data is being merged.
         * @param productNameArg
         */
        void setProductNameArg(String productNameArg);
        
        /**
         * @see #setProductNameArg(String)
         * @return 
         */
         String getProductNameArg();
        
        /**
         * Getter for the mergedData property. The result of the merge process
         * @return Value of renderedDocumentRet, or null if it is unset
         */
        XMLString getMergedDataRet();

        /**
         * Setter for the mergedData property. 
         * @see #getMergedDataRet
         * @param renderedDocumentRet new value for property.
         */
        void setMergedDataRet(XMLString mergedDataRet);
    }

    @ServiceCommand(defaultServiceClass=JavaMergeDataService.class)
    public interface MergeDataCommand extends Command, MergeDataArgument {}
}
