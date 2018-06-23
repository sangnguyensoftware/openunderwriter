/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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

import static org.hibernate.type.StringType.INSTANCE;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.Type;

public class ConcatProjection extends SimpleProjection {

    private static final String CONCAT_FUNCTION_NAME = "concat";

    private final String[] properties;

    public ConcatProjection(String... properties) {
        this.properties = properties;
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
        String result = getFunction(criteriaQuery).render(INSTANCE, propertiesToColumns(criteria, criteriaQuery), criteriaQuery.getFactory());
        return result + " as y" + position + '_';
    }

    private List<String> propertiesToColumns(Criteria criteria, CriteriaQuery criteriaQuery) {
        List<String> result = new ArrayList<>(properties.length);

        for (String property : properties) {
            if (property.charAt(0) == '\'') {
                result.add(property);
            }
            else {
                result.add(criteriaQuery.getColumn(criteria, property));
            }
        }

        return result;
    }

    private SQLFunction getFunction(CriteriaQuery criteriaQuery) {
        return criteriaQuery.getFactory().getSqlFunctionRegistry().findSQLFunction(CONCAT_FUNCTION_NAME);
    }

    @Override
    public Type[] getTypes(Criteria criteria, org.hibernate.criterion.CriteriaQuery criteriaQuery) throws HibernateException {
        return new Type[] { INSTANCE };
    }
}