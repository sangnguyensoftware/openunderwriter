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

import com.ail.annotation.Builder;

/**
 * This factory performs the same function as the ClassFactory, but unlike that factory it allows its
 * prototypes to be cached. This can increase performance dramatically for certain types - especially
 * those that are based on other types (e.g. BeanShell of Drools types which extend (or inherit from)
 * other types.)
 */
@Builder(name="CachingClassBuilder")
public class CachingClassFactory extends ClassFactory {
    /* 
     * It'll be quicker to create a new type than use the cache, so turn 
     * caching off for this Factory. 
     * @see com.ail.core.factory.AbstractFactory#cachePrototype()
     */
    protected boolean cachePrototype() {
        return true;
    }
}
