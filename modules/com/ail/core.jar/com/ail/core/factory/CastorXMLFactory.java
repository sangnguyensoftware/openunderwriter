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

package com.ail.core.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.castor.mapping.BindingType;
import org.castor.mapping.MappingUnmarshaller;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ail.annotation.Builder;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.configure.Type;
import com.ail.core.xmlbinding.castor.CastorMappingLoader;

/**
 * Factory to create class instances from XML using the core's FromXML service.
 * This builder expects to find a configuration parameter called either "Script" or Url", and optionally
 * a parameter called "Extends".<p>
 * The "Script" and "Url" parameters offer alternative ways for the factory to find the XML string defining
 * the type. If "Script" is used, then the value if the Script parameter is taken to be a string of XML which
 * can be passed directly into castor. If "Url" is used, then the factory will attempt to load content from
 * the URL and pass that into castor.<p>
 * The "Extends" parameter tells the factory that this type is based on another, and that it should merge
 * the values from that type into this one before returning. See {@link com.ail.core.Type#mergeDonorIntoSubject(Type, Type, Core)}
 * for a description of the rules used during merging.<p>
 * For example, assume that the following two types have been defined in configuration:
 * <pre><small>
 *      &lt;type name="BaseType" builder="CastorXMLBuilder"&gt;
 *        &lt;parameter name="Script"&gt;&lt;![CDATA[
 *          &lt;type lock="false" serialVersion="21" xsi:type="java:com.ail.core.Version" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;
 *            &lt;author&gt;T.S.Elliot&lt;/author&gt;
 *            &lt;comment&gt;The loganberry's are sweet&lt;/comment&gt;
 *            &lt;copyright&gt;Copyright us.&lt;/copyright&gt;
 *            &lt;version&gt;1.0&lt;/version&gt;
 *            &lt;attribute id="baseattr" format="string" value="2"/&gt;
 *            &lt;attribute id='one' value='valueone'/&gt;
 *            &lt;attribute id='two' value='valuetwo' format='string,32'/&gt;
 *          &lt;/type&gt;
 *        ]]&gt;&lt;/parameter&gt;
 *      &lt;/type&gt;
 *
 *      &lt;type name="ExtendingType" builder="CastorXMLBuilder"&gt;
 *        &lt;parameter name="Extends"&gt;BaseType&lt;/parameter&gt;
 *        &lt;parameter name="Script"&gt;&lt;![CDATA[
 *          &lt;type lock="true" xsi:type="java:com.ail.core.Version" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;
 *            &lt;author&gt;H.G.Wells&lt;/author&gt;
 *            &lt;source&gt;Peach and mint&lt;/source&gt;
 *            &lt;state&gt;state&lt;/state&gt;
 *            &lt;date&gt;27/07/2006&lt;/date&gt;
 *            &lt;attribute id="subattr" format="string" value="2"/&gt;
 *            &lt;attribute id="one" format="string" unit="feet"/&gt;
 *            &lt;attribute id="two" value="overriden-two"/&gt;
 *          &lt;/type&gt;
 *        ]]&gt;&lt;/parameter&gt;
 *      &lt;/type&gt;
 * </small></pre>
 * Here we have two types: BaseType, and ExtendingType, both of which define a {@link com.ail.core.Version}. ExtendingType
 * <i>extends</i> BaseType. Incidentally, there is no reason why BaseType shouldn't extends another type.<p/>
 * The result of calling <code>core.ToXML(core.newType("ExtendingType"))</code> would be the following String:
 * <pre><small>
 *          &lt;type lock="true" serialVersion="21" xsi:type="java:com.ail.core.Version" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;
 *            &lt;author&gt;H.G.Wells&lt;/author&gt;
 *            &lt;comment&gt;The loganberry's are sweet&lt;/comment&gt;
 *            &lt;source&gt;Peach and mint&lt;/source&gt;
 *            &lt;copyright&gt;Copyright us.&lt;/copyright&gt;
 *            &lt;state&gt;state&lt;/state&gt;
 *            &lt;date&gt;27/07/2006&lt;/date&gt;
 *            &lt;version&gt;1.0&lt;/version&gt;
 *            &lt;attribute id="subattr" format="string" value="2"/&gt;
 *            &lt;attribute id="baseattr" format="string" value="2"/&gt;
 *            &lt;attribute id="one" format="string" unit="feet"/&gt;
 *            &lt;attribute id="two" value="overriden-two" format='string,32'/&gt;
 *          &lt;/type&gt;
 * </small></pre>
 * As you can see, in the resulting instance the property values defined in ExtendingType have overriden those is BaseType. So,
 * "H.G.Wells" has replaced "T.S.Elliot". But where ExtendingType doesn't define a value ('comment' for example), BaseType's
 * value appears in the result. Also, notice that the merge is 'deep'; if you look at the properties on the 'id="two"' attribute
 * you can see that the same merging rules have been applied to them.
 * @see com.ail.core.xmlbinding.castor.CastorFromXMLService
 */
@Builder(name="CastorXMLBuilder")
public class CastorXMLFactory extends AbstractFactory {
    private XMLClassDescriptorResolver xmlClassResolver;

    /**
     * Nothing to do here, leave all the work to initialiseType.
     */
    @Override
    protected Object instantiateType(Type typeSpec) {
        return null;
    }

    /**
     * Fetch the castor mapping and return it. This class shares the castor mapping used by the
     * Castor[ToFrom]Xml services.
     * @return Mapping instance.
     */
    private XMLClassDescriptorResolver fetchXmlClassResolver() {
        if (xmlClassResolver==null) {
            try {
                // ... create a new Mapping
                Mapping mapping=new Mapping(Thread.currentThread().getContextClassLoader());

                // add the default mapping to it
                InputStream stream=CastorMappingLoader.class.getResourceAsStream("CastorMapping.xml");
                mapping.loadMapping(new InputSource(stream));

                MappingUnmarshaller mum = new MappingUnmarshaller();
                MappingLoader loader = mum.getMappingLoader(mapping, BindingType.XML);
                xmlClassResolver = (XMLClassDescriptorResolver) ClassDescriptorResolverFactory.createClassDescriptorResolver(BindingType.XML);
                xmlClassResolver.setMappingLoader(loader);

                stream.close();
            }
            catch(Exception e) {
                e.printStackTrace();
                throw new FactoryConfigurationError("Failed to load castor mapping file");
            }
        }

        return xmlClassResolver;
    }

    @Override
    protected Object initialiseType(Object o, Type typeSpec, Core core) {
        String script=typeSpec.findParameterValue("Script");
        String url=typeSpec.findParameterValue("Url");
        Reader reader=null;
        String configurationURL=null;

        if (typeSpec.getKey()==null) {
            throw new FactoryConfigurationError("Type: '"+typeSpec.getName()+"' does not defined the 'key' property.");
        }

        if (url==null && script==null) {
            throw new FactoryConfigurationError("Type: '"+typeSpec.getName()+"' must define either a 'Url' or 'Script' property");
        }

        try {
            // Load script from Script parameter if present, or from Url content if Url is present.
            // Fail it neither or both are present.
            if (script!=null) {
                reader=new StringReader(script);
            }
            else {
                URL configureUrl=Functions.absoluteConfigureUrl(core, url);
                reader=new InputStreamReader(configureUrl.openStream(), "UTF-8");
                configurationURL=configureUrl.toExternalForm();
            }

            Class<?> type=Thread.currentThread().getContextClassLoader().loadClass(typeSpec.getKey());

            Unmarshaller unmarshaller=new Unmarshaller(type, Thread.currentThread().getContextClassLoader());

            unmarshaller.setResolver(fetchXmlClassResolver());

            unmarshaller.setEntityResolver(new RelativeURLEntityResolver(configurationURL));

            Object ret=unmarshaller.unmarshal(reader);

            return mergeWithBase((com.ail.core.Type)ret, typeSpec, core);
        }
        catch(Exception e) {
            core.logError("CastorXMLFactory failed to unmarshal: '"+typeSpec.getName()+"' "+e, e);
            throw new FactoryConfigurationError("Config url: '" + configurationURL + "', Type script: '"+typeSpec.getName()+"' "+e, e);
        }
        finally {
            if (reader!=null) {
                try {
                    reader.close();
                }
                catch(IOException e) {
                    core.logWarning("CastorXMLFactory failed to close Reader for script: '"+typeSpec.getName()+"' "+e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.ail.core.factory.AbstractFactory#cachePrototype()
     */
    @Override
    protected boolean cachePrototype()
    {
        return true;
    }

    /**
     * EntityResolver to convert '~/' URIs into a URI relative to the URI from which the
     * type is being loaded. If the string defining a type is read from a URL (see URL Parameter here:
     * {@link CastorXMLFactory}) we want the contents of that URL to be able to 'xinclude' other
     * content from the same location. However, by default xinclude doesn't support relative href
     * (this is a simplification, but for us using xml:base doesn't help because it has to defined
     * an absolute URI too). This Resolver turns any 'href' found in the type's string into
     * a URI relative path to the file that xincludes it.
     */
    class RelativeURLEntityResolver implements EntityResolver {
        String baseURL=null;

        /**
         * Constructor taking the configuration's source URL as an argument. This URL (sans the
         * trailing configuration file name) will be used as a base URL for product: xinclude's
         * @param configurationURL
         */
        public RelativeURLEntityResolver(String configurationURL) {
            if (configurationURL!=null) {
                int idx=configurationURL.lastIndexOf('/');
                if (idx!=-1) {
                    this.baseURL=configurationURL.substring(0, idx+1);
                }
            }
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId!=null && systemId.indexOf("~/")!=-1 && baseURL!=null) {
                return new InputSource(baseURL+systemId.substring(systemId.indexOf("~/")+1));
            }
            return null;
        }
    }
}
