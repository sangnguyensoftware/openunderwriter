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
import java.io.StringWriter;

import javax.xml.transform.Result;

import org.apache.xml.serializer.ToXMLStream;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.core.xmlbinding.ToXMLService.ToXMLArgument;

/**
 * This entry point converts an objects into an XMLString representing it using
 * the castor framework. The object passed as an argument is marshalled into an
 * XMLString and returned. This entry points accepts one argument:
 * <ul>
 * <li>ObjectIn - The object to be marshalled.</li>
 * </ul>
 * And generates one return value:
 * <ul>
 * <li>XmlOut - The result of the marshalling process.</li>
 * </ul>
 * These arguments and returns are encapsulated in an instance of ToXMLCommand.
 */
@ServiceImplementation
public class CastorToXMLService extends Service<ToXMLArgument> {

    /**
     * Insert an xsi:type attribute in the root element of a string of XML.
     * @param xml XML to insert into
     * @param type Class to insert name of.
     * @return Modified XML string
     */
    private String insertRootXsiType(String xml, Class<?> type) {
        // find the second '>' we'll insert the xsi:type just before it.
        // Note: the first '>' will be closing the header.
        int indx = xml.indexOf('>', xml.indexOf('>') + 1);

        // allow for the root element being alone (i.e. terminated with at />).
        if (xml.charAt(indx - 1) == '/') {
            indx--;
        }

        StringBuffer xmlSb = new StringBuffer(xml);

        xmlSb.insert(indx, " xsi:type=\"java:" + type.getName() + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");

        return xmlSb.toString();
    }

    /**
     * Use the castor marshaller to translate an object into an XML string.
     */
    @Override
    public void invoke() throws XMLException, PreconditionException {
        StringWriter str = new StringWriter();
        XMLClassDescriptorResolver resolver=null;

        // check the preconditions
        if (args == null) {
            throw new PreconditionException("args==null");
        }

        if (args.getObjectIn() == null) {
            throw new PreconditionException("args.getObjectIn()==null");
        }

        try {
            ToXMLStream xmlStreamer = new ToXMLStream();
            xmlStreamer.setWriter(str);
            xmlStreamer.setIndent(true);
            xmlStreamer.setIndentAmount(1);
            xmlStreamer.setEncoding("UTF-8");
            org.xml.sax.ContentHandler handler = xmlStreamer.asContentHandler();

            resolver = new CastorMappingLoader().fetchClassResolver();

            // Create a marshaller - Is it worth putting this in the cache?
            Marshaller m = new Marshaller(handler);

            // Have the marshaller include 'xsi:type' attributes in the output
            m.setMarshalExtendedType(true);

            // Turn validation off to avoid URL hits
            m.setValidation(false);

            // Stop the conversion of < to &lt; etc.
            m.addProcessingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, "");

            // Tell the marshaller which mapping to use (will be null if none
            // was defined.)
            if (resolver!=null) {
                m.setResolver(resolver);
            }

            // Marshal - the results will end up in str
            m.marshal(args.getObjectIn());
        }
        catch (MarshalException e) {
            throw new XMLException("XML Marshal exception", e);
        }
        catch (ValidationException e) {
            throw new XMLException("XML Validation exception", e);
        }
        catch (MappingException e) {
            throw new XMLException("Failed to set XML mapping from configuration", e);
        }
        catch (IOException e) {
            throw new XMLException("Failed to load mapping from configuration", e);
        }

        // Castor doesn't include an xsi:type for the root element, so we'll
        // force
        // one in ourselves
        String res = insertRootXsiType(str.toString(), args.getObjectIn().getClass());

        args.setXmlOut(new XMLString(res));
    }
}
