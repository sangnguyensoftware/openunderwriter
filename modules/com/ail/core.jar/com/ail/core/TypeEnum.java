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
 * Base type for all AIL Enum classes.
 */
public interface TypeEnum {
    String name();

    /**
     * The long name for an enum. Unlike the name property on Java's Enum type, this
     * need not be a valid Java identifier.
     * @return The Enum's long name.
     */
    String longName();

    /**
     * Returns the same value as name(). This method only exists to make TypeEnum more
     * Bean compatible so things like jxpath can make use of it.
     * @return Enum's name
     */
    String getName();

    /**
     * Returns the same value as longName(). This method only exists to make TypeEnum more
     * Bean compatible so things like jxpath can make use of it.
     * @return Enum's long name
     */
    String getLongName();

    /**
     * Return this Enum's ordinal id. This method only exists to make TypeEnum more
     * Bean compatible so things like jxpath can make use of it.
     * @return Enum's ordinal.
     */
    int getOrdinal();
}
