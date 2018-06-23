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

import com.ail.core.Identified;
import com.ail.core.Type;

/**
 * This is the base of the composite pattern implementation which defines
 * configuration data.
 */
public class Component extends Type implements Identified {
    static final long serialVersionUID = -1770023766173749861L;
    private String name=null;

    /** Configuration namespace in which this component was defined */
    private String namespace=null;

    /**
     * Getter for the component's name.
	 * The name generally equates to the key by which the component is found.
     * For example a component referred to as "insurance.types.policy" would
     * relate to a component by the name 'policy' which is held in a component
     * called 'types', which it itself held within a component named 'policy'.
     * @return The component's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the component's name that always returns the name in lower case.
     * This method simply returns a lower-case version of the string that {@link #getName getName()} returns.
     * @return The component's name.
     */
    public String getNameLowerCase() {
      return name.toLowerCase();
    }

	/**
     * Setter for the component's name.
     * @see #getName for a description of name.
     * @param name The name to set the component to.
     */
    public void setName(String name) {
        this.name=name;
    }

    /**
     * Get the namespace in which this component is defined.
     * @return component's namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set the namespace in which this component is defined.
     * @param namespace Component's namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public void setId(String Id) {
        throw new IllegalStateException("Builders.setId() cannot be invoked, the Id always resolves to the builder's group name.");
    }

    @Override
    public boolean compareById(Identified that) {
        try {
            return that.getId().equals(this.getId());
        }
        catch(Exception e) {
            return false;
        }
    }
}
