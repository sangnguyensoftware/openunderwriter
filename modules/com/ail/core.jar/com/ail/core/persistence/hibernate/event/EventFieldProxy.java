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
package com.ail.core.persistence.hibernate.event;

class EventFieldProxy {
    private Object[] currentState;
    private String[] propertyNames;

    EventFieldProxy(Object[] currentState, String[] propertyNames) {
        this.currentState = currentState;
        this.propertyNames = propertyNames;
    }

    Object get(String targetProperty) {
        for (int i = propertyNames.length - 1; i >= 0; i--) {
            if (targetProperty.equals(propertyNames[i])) {
                return currentState[i];
            }
        }
        return null;
    }

    EventFieldProxy set(String targetProperty, Object value) {
        for (int i = propertyNames.length - 1; i >= 0; i--) {
            if (targetProperty.equals(propertyNames[i])) {
                currentState[i] = value;
            }
        }
        return this;
    }

    String[] getPropertyNames() {
        return propertyNames;
    }
}
