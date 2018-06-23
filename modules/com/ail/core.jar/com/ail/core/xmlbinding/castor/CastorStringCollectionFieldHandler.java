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
package com.ail.core.xmlbinding.castor;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.exolab.castor.mapping.AbstractFieldHandler;
import org.exolab.castor.mapping.FieldDescriptor;

import com.ail.core.XMLString;

/**
 * Castor handler to make-safe strings containing XML entities.
 */
public class CastorStringCollectionFieldHandler extends AbstractFieldHandler {

    @SuppressWarnings("unchecked")
    @Override
    public Object getValue(Object object) throws IllegalStateException {
        FieldDescriptor f = getFieldDescriptor();
        String fieldName = f.getFieldName();

        try {
            Collection<String> src = (Collection<String>) PropertyUtils.getProperty(object, fieldName);
            ;

            if (src == null || src.size() == 0) {
                return src;
            }

            Collection<String> dst = new ArrayList<>();

            for (String s : src) {
                dst.add(new XMLString(s).toStringWithEntityReferences(true));
            }

            return dst;
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to fetch field: " + fieldName + " from object of type: " + object.getClass().getName(), e);
        }
    }

    @Override
    public Object newInstance(Object arg0) throws IllegalStateException {
        return new String();
    }

    @Override
    public Object newInstance(Object arg0, Object arg1[]) throws IllegalStateException {
        return new String();
    }

    @Override
    public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
        setValue(object, "");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object object, Object value) throws IllegalStateException, IllegalArgumentException {
        FieldDescriptor f = getFieldDescriptor();
        String fieldName = f.getFieldName();

        try {
            Collection<String> src = (Collection<String>) PropertyUtils.getProperty(object, fieldName);;

            if (src == null || src.size() == 0) {
                PropertyUtils.setProperty(object, fieldName, src);
            } else {
                Collection<String> dst = new ArrayList<>();

                for (String s : src) {
                    dst.add(new XMLString(s).toStringWithoutEntityReferences());
                }

                PropertyUtils.setProperty(object, fieldName, dst);
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to set field: " + fieldName + " from object of type: " + object.getClass().getName(), e);
        }
    }
}
