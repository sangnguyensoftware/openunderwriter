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
/*
 * Created on 22-Feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ail.core.factory;

import javax.xml.transform.TransformerException;

import com.ail.core.Core;
import com.ail.core.TypeXPathException;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.core.configure.Type;
import com.ail.annotation.Builder;

/**
 * Base a type definition on an XSTL. The XSLT must output a type definition that the
 * CastorXMLFactory can consume. The factory may optionally take an instance of another type
 * (derived from another type definition) as input using the 'extends' parameter.<p>
 * For example: in the configuration sample below "OtherVersion" defines an instance of
 * com.ail.core.Version and sets the source, state, date, etc properties on the instance.
 * The NewVersion type is based on OtherVersion (see the "extends" parameter) and overrides
 * the setting of the 'source' property. So an instance created by NewVersion will contain
 * all of the property settings defined in OtherVersion, but will have 'source' set to 
 * "orange and pineapple" rather than "Peach and mint".<p>
 * <pre>
 * &lt;type name="OtherVersion" builder="CastorBuilder" key="com.ail.core.Version"&gt;
 *   &lt;parameter name="script"&gt;&lt;![CDATA[
 *     &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 *     &lt;version serialVersion='0' lock='false' xsi:type='java:com.ail.core.Version' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'&gt;" +
 *       &lt;source&gt;Peach and mint&lt;/source&gt;
 *       &lt;state&gt;state&lt;/state&gt;
 *       &lt;date&gt;14/10/2002&lt;/date&gt;
 *       &lt;author&gt;T.S.Elliot&lt;/author&gt;
 *       &lt;comment&gt;The loganberry's are sweet&lt;/comment&gt;
 *       &lt;copyright&gt;Copyright us.&lt;/copyright&gt;
 *       &lt;version&gt;1.0&lt;/version&gt;
 *     &lt;/version&gt;
 *   ]]&gt;&lt;/parameter&gt;
 * &lt;/type&gt;
 *
 * &lt;type name="NewVersion" builder="XSLTBuilder" key="com.ail.core.Version"&gt;
 *   &lt;parameter name="extends"&gt;OtherVersion&lt;/parameter&gt;
 *   &lt;parameter name="script"&gt;&lt;![CDATA[
 *      &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 *      &lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0"&gt;
 *      &lt;xsl:output encoding="UTF-8" indent="no" method="xml" version="1.0"/&gt;
 *
 *        &lt;xsl:template match='/'&gt;
 *          &lt;xsl:apply-templates/&gt;
 *        &lt;/xsl:template&gt;
 *
 *        &lt;xsl:template match='&#64;*|*'&gt;
 *          &lt;xsl:copy&gt;
 *            &lt;xsl:apply-templates select='&#64;*|node()'/&gt;
 *          &lt;/xsl:copy&gt;
 *        &lt;/xsl:template&gt;
 *
 *        &lt;xsl:template match='source/text()'&gt;orange and pineapple&lt;/xsl:template&gt;
 *      &lt;/xsl:stylesheet&gt;
 * 
 *   ]]&gt;&lt;/parameter&gt;
 * &lt;/type&gt;
 * </pre>
 */
@Builder(name="XSLTBuilder")
public class XSLTFactory extends AbstractFactory {
  /**
   * Nothing to do here, leave all the work to initialiseType.
   */
  protected Object instantiateType(Type typeSpec) {
      return null;
  }

  protected Object initialiseType(Object o, Type typeSpec, Core core) {
      XMLString xml = new XMLString("<root/>");

      try {
          String extend = typeSpec.xpathGet("parameter[nameLowerCase='extends']/value", String.class);

          if (extend != null && extend.length() != 0) {
              Object ob = core.newType(extend);
              xml = core.toXML(ob);
          }
      }
      catch (TypeXPathException  e) {
          // ignore this - extends is optional
      }

      try {
          String script = typeSpec.xpathGet("parameter[nameLowerCase='script']/value", String.class);

          try {
              xml.transformInline(script);

              return core.fromXML(xml.getType(), xml);
          }
          catch (ClassNotFoundException e) {
              e.printStackTrace();
              throw new FactoryConfigurationError("Type script: '" + typeSpec.getName() + "' " + e.getMessage());
          }
          catch (XMLException e) {
              e.printStackTrace();
              throw new FactoryConfigurationError("Type script: '" + typeSpec.getName() + "' " + e.getMessage());
          }
          catch (TransformerException e) {
              e.printStackTrace();
              throw new FactoryConfigurationError("Type script: '" + typeSpec.getName() + "' " + e.getMessage());
          }
      }
      catch (Throwable e) {
          throw new FactoryConfigurationError("TypeXML for: '" + typeSpec.getName() + "' does not define variable 'script'");
      }
  }

    /**
     * Creating types using this factory is potentially expensive in terms of
     * performance. At a minimum we have to run and XSLT, but a more normal usage
     * of this factory where a type is based on another type is far more
     * intensive. It means creating an instance of the base type, marshalling
     * it, running the XSLT, then unmarshal the result of the XSLT. Creating an
     * instance of the base type may also use the XSLTFactory, so this can be
     * very expensive. For that reason, we'll cache the instance as a prototype.
     * 
     * @see com.ail.core.factory.AbstractFactory#cachePrototype()
     * @return true always.
     */
    protected boolean cachePrototype() {
        return true;
    }
}
