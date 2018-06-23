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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Points to another part/element of an object graph by the class and ID of the object.
 */
@Embeddable
public class Reference {
    /** Type of the object being referenced (e.g. asset, section, excess, ...). */
    @Column(name="refType")
    private String type = null;

    /** ID of the object being referenced */
    @Column(name="refId")
    private String refId = null;

    public Reference() {
    }

    /**
     * Constructor
     * @param type The type of the object being referenced
     * @param id The Id of the object being referenced
     */
    public Reference(String type, String id) {
        this.type = type;
        this.refId = id;
    }

    /**
     * Constructor
     * @param type The type of the object being referenced
     * @param id The Id of the object being referenced
     */
    public Reference(Class<?> type, String id) {
        this(type.getName(), id);
    }

    /**
     * Set the type property. This indicates the type of the object being referenced (e.g. asset, section, excess, ...).
     * @param type New value for type property.
     */
    public void setType(Class<?> type) {
        this.type = type.getName();
    }

    /**
     * Set the type property from a String.
     * @param type New value for type property.
     */
    public void setTypeAsString(String type) {
        this.type = type;
    }

    /**
     * Get the value of the type property. This indicates the type of the object being referenced (e.g.
     * asset, section, excess, ...).
     * @throws ClassNotFoundException
     * @return Value of type
     */
    public Class<?> getType() throws ClassNotFoundException {
        return Class.forName(this.type);
    }

    /**
     * Get the current value of the type property as a String.
     * @see #getType
     * @return Value of type property as a String, or null if type is null.
     */
    public String getTypeAsString() {
        return type;
    }

    public String getId() {
        return refId;
    }

    public void setId(String id) {
        this.refId = id;
    }

    @Deprecated
    public void setSerialVersion(long serialVersion) {
    }

    @Deprecated
    public void setLock(boolean lock) {
    }

    @Deprecated
    public void setSystemId(long systemId) {
    }

    @Deprecated
    public long getSerialVersion() {
        return -1;
    }

    @Deprecated
    public boolean getLock() {
        return false;
    }

    @Deprecated
    public long getSystemId() {
        return -1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((refId == null) ? 0 : refId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Reference other = (Reference) obj;
        if (refId == null) {
            if (other.refId != null)
                return false;
        } else if (!refId.equals(other.refId))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
