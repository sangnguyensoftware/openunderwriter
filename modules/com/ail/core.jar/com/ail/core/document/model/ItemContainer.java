/* Copyright Applied Industrial Logic Limited 2006. All rights reserved. */
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
package com.ail.core.document.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A document structure item that has children.
 */
public abstract class ItemContainer extends ItemData {
    private List<ItemData> item=new ArrayList<ItemData>();

    /**
     * Default implementation of the render method; implementing class will generally
     * override this.
     */
    @Override
    public void render(RenderContext context) {
        
        Collections.sort(item);
        
        for(ItemData it: item) {
            it.render(context);
        }
    }

    public List<ItemData> getItem() {
        return item;
    }

    public void setItem(List<ItemData> item) {
        this.item = item;
    }
}
