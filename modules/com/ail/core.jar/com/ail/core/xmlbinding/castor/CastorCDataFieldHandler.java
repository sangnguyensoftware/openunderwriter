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

import org.apache.commons.beanutils.PropertyUtils;
import org.exolab.castor.mapping.AbstractFieldHandler;
import org.exolab.castor.mapping.FieldDescriptor;

/**
 * Castor data field handler which wraps a fields value with CDATA tags.
 * CDATA tags have never been very well supported by castor (i.e. not at all!), but castor
 * does provide support for specifying "handlers" on a per field basis in the mapping file.
 * If defined, castor uses the handler to marshal objects to XML.<p>
 * To use this handler, set the field up as follows in the mapping file:<p>
 * &lt;field name="value" type="string" required="false"<p>
 * &nbsp;&nbsp;direct="false" transient="false" handler="com.ail.core.xmlbinding.CastorCDataFieldHandler"&gt;<p>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;bind-xml node="text" reference="false"/&gt;<p>
 * &nbsp;&nbsp;&lt;/field&gt;<p>
 * @version $Revision: 1.3 $
 */
public class CastorCDataFieldHandler extends AbstractFieldHandler {

    /**
     * uses reflection to retrieve the value then wraps
     * it in a CDATA section
     */
    @Override
    public Object getValue(Object object) throws IllegalStateException {
        FieldDescriptor f = getFieldDescriptor();
        String fieldName = f.getFieldName();
        String value = null;

        try {
            value = (String)PropertyUtils.getProperty(object, fieldName);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (value==null || value.length()==0) {
            return value;
        }

        return "<![CDATA[" + value.toString() + "]]>";
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

    @Override
    public void setValue(Object object, Object value) throws IllegalStateException, IllegalArgumentException {
        FieldDescriptor f = getFieldDescriptor();
        String fieldName = f.getFieldName();

        try {
            PropertyUtils.setProperty(object, fieldName, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
