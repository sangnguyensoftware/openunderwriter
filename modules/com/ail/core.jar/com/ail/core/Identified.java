/* Copyright Applied Industrial Logic Limited 2006. All rights reserved. */
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

/**
 * Classes that implement this interface have identifiers, and can be safely compared with
 * any other implementors.
 */
public interface Identified {
    String getId();

    void setId(String Id);

    /**
     * Return true if <i>that</i> is of the same type and has the same Id as <i>this</i>.
     * @param that Object to compare with.
     * @return true if <i>this</i> and <i>that</i> are the same by Id.
     */
    default boolean compareById(Identified that) {
        if (that.getId() != null && this.getId() != null && this.getClass().isAssignableFrom(that.getClass())) {
            return (this.getId().equals(that.getId()));
        } else {
            return false;
        }
    }
}
