/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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
package com.ail.core.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.core.XMLException;
import com.ail.core.XMLString;

public class XMLBlob implements UserType, ParameterizedType {
    private static final int[] TYPES = { Types.CLOB };
    private Class<?> type;

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        try {
            if (cached == null) {
                return null;
            }
            XMLString xmlString = new XMLString((String) cached);
            return new CoreProxy().fromXML(xmlString.getType(), xmlString);
        } catch (XMLException | ClassNotFoundException e) {
            throw new XMLBlobError("Failed to translate field content into object.", e);
        }
    }

    @Override
    public Serializable disassemble(Object object) throws HibernateException {
        return object == null ? null : new CoreProxy().toXML(object);
    }

    @Override
    public Object deepCopy(Object object) throws HibernateException {
        try {
            if (object == null) {
                return null;
            }
            else if (object instanceof Type) {
                return ((Type) object).clone();
            }
            else if (object instanceof List) {
                return deepCopyList(object);
            }
            else if (object instanceof Set) {
                return deepCopySet(object);
            }
            else if (object instanceof Map) {
                return deepCopyMap(object);
            }
            throw new XMLBlobError("Failed to clone object. Cannot handle object of type: "+object.getClass());
        } catch (Throwable e) {
            throw new XMLBlobError("Failed to clone object.", e);
        }
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        } else if (x == null || y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return null == object ? 0 : object.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String persistedXml=null;
        try {
            persistedXml = StringType.INSTANCE.nullSafeGet(rs, names[0], session);
            if (persistedXml == null) {
                if (type!=null) {
                    return type.newInstance();
                }
                else {
                    return null;
                }
            }
            XMLString xmlString = new XMLString(persistedXml.toString());
            return new CoreProxy().fromXML(xmlString.getType(), xmlString);
        } catch (XMLException | ClassNotFoundException e) {
            String clazz = owner.getClass().getName();
            Long id = ((Type)owner).getSystemId();
            throw new XMLBlobError("Failed to translate blob field for class: " + clazz + ", id: " + id + " field content:\n" + persistedXml, e);
        } catch (InstantiationException|IllegalAccessException e) {
            throw new XMLBlobError("Could not instantiate instance of (" + (type == null ? "null" : type.getClass()) + ")", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        String xmlString = (shouldBePersistedAsNull(value)) ? null : new CoreProxy().toXML(value).toString();
        StringType.INSTANCE.nullSafeSet(st, xmlString, index, session);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return this.deepCopy(original);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class returnedClass() {
        return Serializable.class;
    }

    @Override
    public int[] sqlTypes() {
        return TYPES;
    }

    @SuppressWarnings("unchecked")
    private Object deepCopyMap(Object rawMap) throws Throwable {
        Map<Object, Object> map = (Map<Object, Object>)rawMap;
        Map<Object, Object> clonedMap = (Map<Object, Object>) rawMap.getClass().newInstance();
        Object value;

        for (Object key : map.keySet()) {
            value = map.get(key);
            value = (value instanceof Type) ? ((Type) value).clone() : value;
            clonedMap.put(key, value);
        }

        return clonedMap;
    }

    @SuppressWarnings("unchecked")
    private Object deepCopyList(Object rawCollection) throws Throwable {
        Collection<? extends Object> collection = (Collection<Object>)rawCollection;
        List<Object> clonedList = (List<Object>) rawCollection.getClass().newInstance();

        for (Object type : collection) {
            if (type instanceof Type) {
                clonedList.add(((Type) type).clone());
            } else {
                clonedList.add(type);
            }
        }

        return clonedList;
    }

    @SuppressWarnings("unchecked")
    private Object deepCopySet(Object rawSet) throws Throwable {
        Set<Object> set = (Set<Object>)rawSet;
        Set<Object> clonedSet = (Set<Object>) rawSet.getClass().newInstance();

        for (Object type : set) {
            if (type instanceof Type) {
                clonedSet.add(((Type) type).clone());
            } else {
                clonedSet.add(type);
            }
        }

        return clonedSet;
    }

    @SuppressWarnings("rawtypes")
    private boolean shouldBePersistedAsNull(Object value) {
        return (value==null || (value instanceof Collection && ((Collection)value).isEmpty()));
    }

    @Override
    public void setParameterValues(Properties parameters) {
        if (parameters!=null) {
            String typeName = null;
            try {
                typeName = parameters.getProperty("type");
                type = (Class<?>) Class.forName(typeName, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("XMLBlob cannot map type: '" + typeName + "'");
            }
        }
    }
}
