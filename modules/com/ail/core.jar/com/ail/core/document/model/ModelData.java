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

import com.ail.annotation.TypeDefinition;
import com.ail.core.Functions;
import com.ail.core.TypeXPathException;
import static com.ail.core.language.I18N.i18n;
/**
 * Node of the document structure object graph.
 */
@TypeDefinition
public class ModelData extends ItemData {
    private String binding;

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public void render(RenderContext context) {
        String out = null;

        if (conditionIsMet(context.getModel())) {
            if (binding != null) {
                try {
                    out = i18n(context.getModel().xpathGet(binding, String.class));
                } catch (TypeXPathException e) {
                    if (getValue() != null) {
                        out = Functions.expand(getValue(), context.getModel());
                    } else {
                        out = "undefined: " + binding;
                    }
                }

                if (out == null && getValue() != null) {
                    out = Functions.expand(getValue(), context.getModel());
                }
            } else {
                out = Functions.expand(getValue(), context.getModel());
            }

            context.getOutput().printf("<itemData%s%s%s%s>%s</itemData>", idAsAttribute(), titleAsAttribute(), styleClassAsAttribute(), orderAsAttribute(), out != null ? out : "");
        }
    }
}
