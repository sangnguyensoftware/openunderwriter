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

package com.ail.core.xmlbinding;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.xmlbinding.castor.CastorToXMLService;

@ServiceInterface
public interface ToXMLService {

    /**
     * This interface defines the Bean used to encapsulate the argument and return
     * values used by the ToXml entry point.
     */
    @ServiceArgument
    public interface ToXMLArgument extends Argument {
        /**
         * Fetch the ObjectIn value. ObjectIn represents the object to be
         * converted into XML.
         * @return The object to be marshalled
         */
        Object getObjectIn();

        /**
         * Set the value of the ObjectIn property.
         * @param objectIn The new value.
         */
        void setObjectIn(Object objectIn);

        /**
         * Fetch the XmlOut value. XmlOut represents the result of the marshal
         * process: a string of XML built from <code>objectIn</code>
         * @return marshalled XML object.
         */
        XMLString getXmlOut();

        /**
         * Set the value of the XmlOut property.
         * @param xmlOut The new value.
         */
        void setXmlOut(XMLString xmlOut);
    }

    @ServiceCommand(defaultServiceClass=CastorToXMLService.class)
    public interface ToXMLCommand extends Command, ToXMLArgument {}
}
