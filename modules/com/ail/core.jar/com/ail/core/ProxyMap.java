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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ail.annotation.TypeDefinition;

/**
 * ProxyMap wraps a {@link java.util.HashMap HashMap} in a form which Castor can
 * handle as a root object. This is necessary where a model object needs to
 * represent a map which we also want persist in hibernate as a
 * {@link com.ail.core.XMLBLob XMLBlob}
 *
 * @param <K>
 * @param <V>
 */
@TypeDefinition
public class ProxyMap<K,V> extends Type {
    private Map<K,V> map = new HashMap<>();

    public Map<K,V> getMap() {
        return map;
    }

    public void setMap(Map<K,V> assessmentLine) {
        this.map = assessmentLine;
    }

    public Collection<V> values() {
        return map.values();
    }

    public void put(K id, V l) {
        map.put(id, l);
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(K k) {
        return map.containsKey(k);
    }

    public void remove(K k) {
        map.remove(k);
    }

    public V get(K k) {
        return map.get(k);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + typeHashCode();
        result = prime * result + ((map == null) ? 0 : map.hashCode());
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
        if (!typeEquals(obj))
            return false;
        @SuppressWarnings("unchecked")
        ProxyMap<K,V> other = (ProxyMap<K,V>) obj;
        if (map == null) {
            if (other.map != null)
                return false;
        } else if (!map.equals(other.map))
            return false;
        return true;
    }
}
