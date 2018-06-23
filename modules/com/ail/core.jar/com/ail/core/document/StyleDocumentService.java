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
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

@ServiceInterface
public interface StyleDocumentService {

    @ServiceArgument
    public interface StyleDocumentArgument extends Argument {
        /**
         * Getter for the mergedDataArg property. This property contains the data which is to be styled.
         * @return Value of mergedDataArg, or null if it is unset
         */
         XMLString getMergedDataArg();

        /**
         * Setter for the mergedDataArg property. 
         * @see #getStyledDocumentRet()
         * @param mergedDataArg new value for property.
         */
        void setMergedDataArg(XMLString mergedDataArg);

        /**
         * Getter for the styledDocumentRet property. This contains the result of the style process.
         * @return Value of styledDocumentRet, or null if it is unset
         */
        XMLString getStyledDocumentRet();

        /**
         * Setter for the styledDocumentRet property.
         * @see #getStyledDocumentRet()
         * @param styledDocumentRet
         * @return The styled document
         */
        void setStyledDocumentRet(XMLString styledDocumentRet);
    }

    @ServiceCommand
    public interface StyleDocumentCommand extends Command, StyleDocumentArgument {}
}


