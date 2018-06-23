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

/**
 * A chapter represents a part of a document which defines the characteristics
 * of one or more contiguous pages. A chapter define may information for the
 * header, footer (in the form of blocks), watermark, etc. Bear in mind that a
 * chapter only defines the information to be included in each of these parts,
 * it does not define the formating/style to be used when they are rendered.
 */
@TypeDefinition
public class ChapterData extends ItemContainer {

    @Override
    public void render(RenderContext context) {
        if (conditionIsMet(context.getModel())) {
            context.getOutput().printf("<chapterData%s%s%s%s>", idAsAttribute(), titleAsAttribute(), orderAsAttribute(), styleClassAsAttribute());
            super.render(context);
            context.getOutput().println("</chapterData>");
        }
    }
}
