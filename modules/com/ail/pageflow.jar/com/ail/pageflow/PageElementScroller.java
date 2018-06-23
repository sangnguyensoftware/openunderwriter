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

import java.io.IOException;
import java.util.ArrayList;

import com.ail.core.BaseException;
import com.ail.core.Type;

/**
 * <p>An PageElementScroller displays a repeating pattern of elements.</p>
 * <p>In the above example the PageElementScroller is {@link #getBinding() bound} to assets.
 * Two assets match the binding's criteria (an ALFA ROMEO and a BENTLEY). The PageElementScroller contains a list of
 * {@link #getElement() elements} (in this example: Make, Model and Registration).</p>
 * The binding's of the PageElement are evaluated relative to the binding of the PageElementScroller itself. For example, if the
 * PageElementScroller's binding was: <code>/asset[assetTypeId='Vehicle']</code>, the PageElement would be bound to <code>
 * attribute[id='Make']</code>, <code>attribute[id='Model']</code> and <code>attribute[id='Registration']</code>. So
 * for each element returned by the PageElementScroller's binding, a section is rendered containing the PageElement relative
 * to it</p>
 */
public class PageElementScroller extends PageElement {
    /* TODO This class should extend PageContainer */
	private static final long serialVersionUID = -6043887157243022172L;

    /**
     * A list of elements to be rendered in this scroller. These are rendered once for each of the
     * elements returned by {@link #getBinding()} and are evaluated relative to it.
     */
    private ArrayList<PageElement> element;

    public PageElementScroller() {
        element=new ArrayList<>();
    }

    /**
    * A list of page elements to be rendered in this scroller. These are rendered once for each of the
    * elements returned by {@link #getBinding()} and are evaluated relative to it.
    * @return The list of elements associated with this scroller
    */
    public ArrayList<PageElement> getElement() {
        return element;
    }

    /**
     * @see #getElement()
     * @param
     */
    public void setElement(ArrayList<PageElement> element) {
        this.element = element;
    }

    @Override
    public Type processActions(Type model) throws BaseException {

        for(PageElement element: getElement()) {
            model=element.processActions(model);
        }

        return model;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
   	    return executeTemplateCommand("PageElementScroller", model);
    }

    @Override
    public void applyElementId(String basedId) {
        int idx=0;
        for(PageElement e: element) {
            e.applyElementId(basedId+ID_SEPARATOR+(idx++));
        }
        super.applyElementId(basedId);
    }
}

