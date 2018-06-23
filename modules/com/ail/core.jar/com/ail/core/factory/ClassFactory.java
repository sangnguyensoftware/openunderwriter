/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

import static com.ail.core.Functions.classForName;

import com.ail.annotation.Builder;

/**
 * This factory instantiates the class specified as it's key, and initialises the properties of the instance
 * based on the parameters nested in the types definition.<p>
 * The following section of configuration shows how this factory can be used to create an instance of 
 * {@link com.ail.core.Version Version} and populate some of it's properties:
 * <pre>
 * &lt;configuration&gt;
 *    &lt;builders&gt;
 *      &lt;builder name="ClassBuilder" factory="com.ail.core.factory.ClassFactory"/&gt;
 *    &lt;/builders&gt;
 *    
 *    &lt;types&gt;
 *      &lt;type name="MyVersion" builder="ClassBuilder" key="com.ail.core.Version"&gt;
 *          &lt;parameter name="date"&gt;01/01/2005&lt/parameter&gt;
 *          &lt;parameter name="author"&gt;H.G.Wells&lt/parameter&gt;
 *          &lt;parameter name="comment"&gt;This coffee smells yellow&lt/parameter&gt;
 *      &lt;/type&gt;
 *    &lt;/types&gt;
 * &lt;/configuration&gt>
 * </pre>
 * The example shows the factory itself being bound to a builder name (<code>ClassBuilder</code>). This
 * builder is then used to define the type <code>MyVersion</code>. When an instance of <code>MyVersion</code>
 * is requested, the class <code>com.ail.core.Version</code> will be instantiated, and the setters setDate, setAuthor,
 * and setComment are each invoked in turn and passed the value for corresponding <code>parameter</code>.
 */
@Builder(name="ClassBuilder")
public class ClassFactory extends AbstractFactory {
    public Object instantiateType(com.ail.core.configure.Type typeSpec) {
		String className=typeSpec.getKey();

		try {
	        Class<?> clazz=classForName(className);
	        Object instance=clazz.newInstance();
	        return instance;
		}
        catch(ClassNotFoundException e) {
            throw new FactoryConfigurationError("Class not found ("+className+") for type: "+typeSpec.getName());
		}
		catch(InstantiationException e) {
            throw new FactoryConfigurationError("Class ("+className+") failed to instantiate for type:"+typeSpec.getName());
		}
		catch(IllegalAccessException e) {
            throw new FactoryConfigurationError("Class ("+className+") cannot be instantiated, IllegalAccessExceptio, for type:"+typeSpec.getName());
        }
    }

    /* 
     * It'll be quicker to create a new type than use the cache, so turn 
     * caching off for this Factory. 
     * @see com.ail.core.factory.AbstractFactory#cachePrototype()
     */
    protected boolean cachePrototype() {
        return false;
    }
}
