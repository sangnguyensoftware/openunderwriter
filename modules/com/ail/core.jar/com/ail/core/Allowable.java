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

package com.ail.core;

import java.util.ArrayList;
import java.util.List;

import com.ail.annotation.TypeDefinition;

@TypeDefinition
public class Allowable extends Type {

    private List<Allowable> allowable = new ArrayList<Allowable>();

    // field Name
    private String name = null;

    // if collection element, type identifier
    private String typeId = "";

    // class name of field
    private String className = null;

    /**
     * Get the className associated with this allowable.
     *
     * @return The field className.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the className associated with this allowable
     *
     * @param className
     *            New field className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the typeId associated with this allowable.
     *
     * @return The field typeId.
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * Set the typeId associated with this allowable
     *
     * @param typeId
     *            New field typeId
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * Get the name associated with this allowable.
     *
     * @return The field name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name associated with this allowable
     *
     * @param name
     *            New field name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the collection of instances of com.ail.core.Allowable associated with
     * this object.
     *
     * @return A collection of instances of Allowable
     * @see #setAllowable
     */
    public List<Allowable> getAllowable() {
        if (allowable == null) {
            allowable = new ArrayList<>();
        }

        return allowable;
    }

    /**
     * Set the collection of instances of com.ail.core.Allowable associated with
     * this object.
     *
     * @param allowable
     *            A collection of instances of Allowable
     * @see #getAllowable
     */
    public void setAllowable(List<Allowable> allowable) {
        this.allowable = allowable;
    }

    /**
     * Remove the specified instance of com.ail.core.Allowable from the list.
     *
     * @param allowable
     *            Instance to be removed
     */
    public void removeAllowable(Allowable allowable) {
       getAllowable().remove(allowable);
    }

    /**
     * Add an instance of com.ail.core.Allowable to the list associated with
     * this object.
     *
     * @param allowable
     *            Instance to add to list
     */
    public void addAllowable(Allowable allowable) {
        getAllowable().add(allowable);
    }
}
