/* Copyright Applied Industrial Logic Limited 2015. All rights reserved. */
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

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ail.annotation.Builder;

/**
 * This factory instantiates the class specified as it's key using Spring.<p>
 * The following section of configuration shows how this factory can be used to create an instance of 
 * {@link com.ail.core.Version Version} and populate some of it's properties:
 * <pre>
 * &lt;configuration&gt;
 *    &lt;types&gt;
 *      &lt;type name="MyVersion" builder="SpringBuilder" key="com.ail.core.Version"/&gt;
 *    &lt;/types&gt;
 * &lt;/configuration&gt;
 * </pre>
 * The builder is used to define the type <code>MyVersion</code>. When an instance of <code>MyVersion</code>
 * is requested, the class <code>com.ail.core.Version</code> will be instantiated, and the setters setDate, setAuthor,
 * and setComment are each invoked in turn and passed the value for corresponding <code>parameter</code>.
 */
@Builder(name="SpringBuilder")
public class SpringFactory extends AbstractFactory {
    private ClassPathXmlApplicationContext applicationContext;
    
    public SpringFactory() {
        super();
        applicationContext =  new ClassPathXmlApplicationContext("com/ail/core/factory/SpringFactoryContext.xml");
    }

    public Object instantiateType(String typeName, Class<?> typeClass) {
        return applicationContext.getBean(typeName, typeClass);
    }
    
    public Object instantiateType(com.ail.core.configure.Type typeSpec) {
		String className=typeSpec.getKey();
		String typeName=typeSpec.getName();

		try {
	        return instantiateType(typeName, Class.forName(className));
		}
        catch(ClassNotFoundException e) {
            throw new FactoryConfigurationError("Class not found ("+className+") for type: "+typeSpec.getName());
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
