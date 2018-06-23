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
package com.ail.pageflow;

import java.util.Collection;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.pageflow.util.OrderedList;

/**
 * An abstract UI element which provides support for any concrete page element which itself contains other page
 * elements.
 */
public abstract class PageContainer extends PageElement {
    private static final long serialVersionUID = 297215265083279666L;

    /** List of elements contained. */
    private List<PageElement> pageElement;

    public PageContainer() {
        super();
        pageElement = new OrderedList<>();
    }

    /**
     * Get the list of elements contained.
     * @return List of elements.
     */
    public List<PageElement> getPageElement() {
        return pageElement;
    }

    /**
     * Set the list of page elements contained.
     * @param pageElement list of elements
     */
    public void setPageElement(List<PageElement> pageElement) {
         this.pageElement=pageElement;
    }

    /**
     * Get the list of elements as a collection (this is provided to support Castor).
     * @return Collection of elements.
     */
    public Collection<PageElement> getPageElementCollection() {
        return pageElement;
    }

    /**
     * @see #getPageElementCollection()
     * @param pageElements Collection of elements
     */
    public void setPageElementCollection(Collection<PageElement> pageElements) {
        this.pageElement = new OrderedList<>(pageElements);
    }

    @Override
    public void onStartProcessAction(Type model) {
        Type localModel = (Type)fetchBoundObject(model, model);

        for (PageElement e : pageElement) {
            e.onStartProcessAction(localModel);
        }
    }

    @Override
    public Type applyRequestValues(Type model) {
        Type localModel = (Type)fetchBoundObject(model, model);

        for (PageElement e : pageElement) {
            localModel=e.applyRequestValues(localModel);
        }

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        Type localModel = (Type)fetchBoundObject(model, model);

        // If our condition isn't met, validate nothing.
	    if (!conditionIsMet(localModel)) {
    		return false;
    	}

	    boolean result = false;

        for (PageElement e : getPageElement()) {
            result |= e.processValidations(localModel);
        }

        return result;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        Type localModel = (Type)fetchBoundObject(model, model);

        // If our condition isn't met, validate nothing.
        if (!conditionIsMet(localModel)) {
            return model;
        }

        for (PageElement e : getPageElement()) {
            model=e.processActions(localModel);
        }

        if (PageFlowContext.getPageFlow().isAdvancingPage()) {
            for(Action a: getAction()) {
                localModel=a.executeAction(localModel, ActionType.ON_PAGE_EXIT);
            }
        }

        return model;
    }

    @Override
    public void applyElementId(String basedId) {
        int idx = 0;
        for (PageElement e : pageElement) {
            e.applyElementId(basedId + ID_SEPARATOR + (idx++));
        }
        super.applyElementId(basedId);
    }
}
