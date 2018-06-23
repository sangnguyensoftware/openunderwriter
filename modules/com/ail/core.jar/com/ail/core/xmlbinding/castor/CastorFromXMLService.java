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

package com.ail.core.xmlbinding.castor;

import java.io.IOException;
import java.io.StringReader;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Service;
import com.ail.core.XMLException;
import com.ail.core.xmlbinding.FromXMLService.FromXMLArgument;

/**
 * This entry point uses the castor framework to unmarshal an XMLString into
 * the object it represents.<p>
 * Two arguments are accepted:
 * <ul>
 * <li>classIn - The Class object to be used. The unmarshal will create an
 * instance of this class and populate it as the root element of the XMLString.</li>
 * <li>XmlIn - The XMLString representing the XML to be unmarshalled.</li>
 * </ul>
 * One return object is generated:
 * <ul>
 * <li>ObjectOut - The object resulting from the unmarshal.</li>
 * </ul>
 * These arguments and returns are encapsulated in an instance of FromXMLCommandArg.
 */
@ServiceImplementation
public class CastorFromXMLService extends Service<FromXMLArgument> {

	/**
     * The 'business logic' of the entry point.
     */
    @Override
	public void invoke() throws XMLException {
        XMLClassDescriptorResolver resolver=null;
		StringReader reader=new StringReader(args.getXmlIn().toString());

		try {
            resolver = new CastorMappingLoader().fetchClassResolver();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Unmarshaller unmarshaller=new Unmarshaller(args.getClassIn(), classLoader);

            // many configs (etc) contain embedded text and we don't want to lose the formatting.
            unmarshaller.setWhitespacePreserve(true);

            if (resolver!=null) {
                unmarshaller.setResolver(resolver);
            }

            args.setObjectOut(unmarshaller.unmarshal(reader));
		}
        catch(MarshalException e) {
			throw new XMLException("Marshal exception ", e);
        }
        catch(ValidationException e) {
			throw new XMLException("Validation exception ", e);
        }
        catch(MappingException e) {
            throw new XMLException("Failed to set XML mapping from configuration", e);
        }
        catch(IOException e) {
            throw new XMLException("Failed to load mapping from configuration", e);
        }
    }
}
