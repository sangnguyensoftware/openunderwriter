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
package com.ail.core.persistence.hibernate.event;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.ail.core.CoreContext;

public class EventInterceptor extends EmptyInterceptor {

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof com.ail.core.Type) {
            new EventFieldProxy(currentState, propertyNames)
                .set("updatedDate", new Date())
                .set("updatedBy", CoreContext.getRemoteUser());
            return true;
        }

        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (entity instanceof com.ail.core.Type) {
            new EventFieldProxy(state, propertyNames)
                .set("createdDate", new Date())
                .set("externalSystemId", UUID.randomUUID().toString())
                .set("createdBy", CoreContext.getRemoteUser());
            return true;
        }

        return super.onSave(entity, id, state, propertyNames, types);
    }
}