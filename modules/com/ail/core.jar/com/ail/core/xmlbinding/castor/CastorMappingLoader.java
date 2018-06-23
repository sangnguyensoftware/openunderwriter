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
import java.io.InputStream;

import org.castor.mapping.BindingType;
import org.castor.mapping.MappingUnmarshaller;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.xml.sax.InputSource;

/**
 * The methods defined in this class are shared by the Castor ToxML and FromXML services.
 */
public class CastorMappingLoader {
    private static XMLClassDescriptorResolver resolver;

    private synchronized void loadResolver() throws MappingException, IOException {

        Mapping mapping = new Mapping(Thread.currentThread().getContextClassLoader());

        try (InputStream defaultMappingStream = CastorMappingLoader.class.getResourceAsStream("CastorMapping.xml");) {
            mapping.loadMapping(new InputSource(defaultMappingStream));

            MappingUnmarshaller mum = new MappingUnmarshaller();

            MappingLoader loader = mum.getMappingLoader(mapping, BindingType.XML);

            resolver = (XMLClassDescriptorResolver) ClassDescriptorResolverFactory.createClassDescriptorResolver(BindingType.XML);

            resolver.setMappingLoader(loader);
        }
    }

    /**
     * Fetch a class resolver based on the mappings supplied: by default in CastorBaseMapping.xml; and per caller as
     * specified in the argument. Building a resolver is a reasonably cycle consuming activity, so the method saves
     * the resolved back onto the XMLMapping argument provided. If the caller provides an XMLMapping which already
     * contains a resolver, that resolver is simply returned.<p/>
     * The expectation is that the caller will cache the instance of XMLMapping that it passes in and use it again
     * in future calls to this method.
     * @param argsMappingInOut
     * @return A resolver for use with castor's marshaller and unmarshaller.
     * @throws MappingException
     * @throws IOException
     */
    public XMLClassDescriptorResolver fetchClassResolver() throws MappingException, IOException {
        if (resolver == null) {
            loadResolver();
        }
        return resolver;
    }
}
