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

package com.ail.core.configure;

import com.ail.core.factory.AbstractFactory;

/**
 * A "Builder" represents an instance of a class builder - i.e. a class factory. There may
 * be an arbitrary number of builders in any live system each capable of building class based
 * on different inputs. e.g. and BeanShellBuilder builds class based on a bean shell script, 
 * or a CastorXMLBuilder builds classes based on the XML representation of a class instance.
 */
public class Builder extends Group {
    static final long serialVersionUID = -2392094239125625998L;
    private String factory;
    private transient AbstractFactory instance=null;

	/**
     * Default constructor.
     */
    public Builder() {
    }

    /**
     * Get the name of the factory class (e.g. com.ail.core.factory.BeanShellFactory).
     * @return The name of the factory class.
     */
    public String getFactory() {
      return factory; 
    }

    /**
     * Set the name of the factory class.
     * @param factory The name of the Factory class
     */
    public void setFactory(String factory) { 
      this.factory = factory; 
    }

    /**
     * Fetch the cached instance of the factory.
     * @return An instance of the factory class.
     */
	public AbstractFactory getInstance() {
        return instance;
    }

    /**
     * Set the instance of the factory class.
     * @param instance Instance of the class.
     */
    public void setInstance(AbstractFactory instance) {
        this.instance=instance;
    }
}
