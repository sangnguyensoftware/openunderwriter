/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.pageflow.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;

import com.ail.annotation.TypeDefinition;
import com.ail.pageflow.PageElement;

@TypeDefinition
public class OrderedList<E extends PageElement> extends LinkedList<E> implements PropertyChangeListener {
    private static final long serialVersionUID = -8406993961417200354L;

    public OrderedList() {
        super();
    }
    
    public OrderedList(Collection<E> elements) {
        super();
        for(E element: elements) {
            add(element);
        }
    }
    
    /**
     * Add a new element to the list. Elements are placed into the list based on the value of their 'order' property. The result
     * being that the list is sorted by ascending value of 'order'. An element that has no value in it's 'order' property
     * is appended to the end of the list.
     */
    @Override
    public boolean add(E element) {
        element.addPropertyChangeListener(this);
        
        if (element.getOrder()==null || element.getOrder().length()==0) {
            super.addLast(element);
        }
        else {
            int i=0;
            
            while(i<super.size() && element.compareTo(super.get(i))>0) {
                i++;
            }

            super.add(i, element);
        }
        
        return true;
    }

    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        remove(evt.getSource());
        add((E)evt.getSource());
    }
}
