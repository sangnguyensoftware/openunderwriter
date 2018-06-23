/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.core.data;

import java.util.ArrayList;
import java.util.Collection;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Identified;
import com.ail.core.Type;

/**
 * Definition defines an item in a data dictionary. This encompasses a name (id), description, and a binding
 */
@TypeDefinition
public class Definition extends Type implements Identified {

    private String name;
    private boolean nameAbsolute;
    private String description;
    private String binding;

    private Collection<Definition> definitions = null;

    public Definition() {
        definitions = new ArrayList<>();
    }

    public Definition(String name, String binding) {
        this();
        setName(name);
        this.binding = binding;
    }

    public Definition(String name, String description, String binding) {
        this(name, binding);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public boolean isNameAbsolute() {
        return nameAbsolute;
    }

    public void setNameAbsolute(boolean nameAbsolute) {
        this.nameAbsolute = nameAbsolute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    /**
     * @return the definitions
     */
    public Collection<Definition> getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions the definitions to set
     */
    public void setDefinitions(Collection<Definition> definitions) {
        this.definitions = definitions;
    }

    /* (non-Javadoc)
     * @see com.ail.core.Identified#compareById(java.lang.Object)
     */
    @Override
    public boolean compareById(Identified that) {
        try {
            return ((Definition) that).getId().equals(this.getId());
        }
        catch(Throwable e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see com.ail.core.Identified#getId()
     */
    @Override
    public String getId() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.ail.core.Identified#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        name = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((binding == null) ? 0 : binding.hashCode());
        result = prime * result + ((definitions == null) ? 0 : definitions.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (nameAbsolute ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Definition other = (Definition) obj;
        if (binding == null) {
            if (other.binding != null)
                return false;
        } else if (!binding.equals(other.binding))
            return false;
        if (definitions == null) {
            if (other.definitions != null)
                return false;
        } else if (!definitions.equals(other.definitions))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (nameAbsolute != other.nameAbsolute)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Definition [name=" + name + ", nameAbsolute=" + nameAbsolute + ", description=" + description + ", binding=" + binding + ", definitions=" + definitions + "]";
    }

}