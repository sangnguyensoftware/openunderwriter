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

import java.util.Collections;
import java.util.Iterator;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

import static com.ail.core.document.model.Placement.BODY;
import static com.ail.core.document.model.Placement.HEADER;
import static com.ail.core.document.model.Placement.FOOTER;

/**
 */
@TypeDefinition
public class RepeatingData extends ItemContainer {
    private String binding;

    public void render(RenderContext context) {
        if (conditionIsMet(context.getModel())) {
            context.getOutput().printf("<repeatingData%s%s%s%s>", idAsAttribute(), styleClassAsAttribute(), orderAsAttribute(), titleAsAttribute());

            Collections.sort(getItem());

            for (ItemData idata : getItem()) {
                if (idata instanceof BlockData && HEADER.equals(((BlockData) idata).getPlacement())) {
                    idata.render(context);
                }
            }

            for (@SuppressWarnings("rawtypes")
            Iterator it = context.getModel().xpathIterate(getBinding()); it.hasNext();) {
                RenderContext ctx = new RenderContext(context.getOutput(), (Type) it.next());

                for (ItemData idata : getItem()) {
                    if (idata instanceof BlockData && BODY.equals(((BlockData) idata).getPlacement())) {
                        idata.render(ctx);
                    }
                }
            }

            for (ItemData idata : getItem()) {
                if (idata instanceof BlockData && FOOTER.equals(((BlockData) idata).getPlacement())) {
                    idata.render(context);
                }
            }

            context.getOutput().printf("</repeatingData>");
        }
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }
}
