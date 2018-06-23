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
import com.ail.core.xmlbinding.castor.CastorFromXMLService;

/**
 * Interface defining the bean class to encapsulate the arguments and returns
 * used by the FromXML entry point(s).
 */
@ServiceInterface
public interface FromXMLService {

    /**
     * Interface defining the bean class to encapsulate the arguments and returns
     * used by the FromXML entry point(s).
     */
    @ServiceArgument
    public interface FromXMLArgument extends Argument {

        /**
         * Fetch the value of ObjectOut. This is the value retured from the
         * FromXML entry point, and represents the object created when
         * <code>XmlIn</code> was unmarshalled.
         * @return Unmarshalled object
         */
        Object getObjectOut();

        /**
         * Set the value of the ObjectOut property.
         * @param objectOut The value to be set.
         */
        void setObjectOut(Object objectOut);

        /**
         * Fetch the XMLString that will be unmarshalled by the entry point.
         * This is the input to the unmarshal process, and should represent an
         * instance of the class specificed in <code>ClassIn</code>.
         * @return The XMLString to be unmarshalled.
         */
        XMLString getXmlIn();

        /**
         * Set the value of the XmlIn property.
         * @param xmlIn The value to be set.
         */
        void setXmlIn(XMLString xmIn);

        /**
         * Get the value of the ClassIn argument.
         * This argument specifies the class which <code>XmlIn</code> represents
         * an instance of.
         * @return The class to be unmarshalled into.
         */
        Class<?> getClassIn();

        /**
         * Set the value of the ClassIn property.
         * @param classIn The value to be set.
         */
        void setClassIn(Class<?> classIn);
    }

    @ServiceCommand(defaultServiceClass=CastorFromXMLService.class)
    public interface FromXMLCommand extends Command, FromXMLArgument {
    }
}
