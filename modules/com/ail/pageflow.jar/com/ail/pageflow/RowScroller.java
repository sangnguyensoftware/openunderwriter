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

import com.ail.core.Attribute;
import com.ail.core.Type;

/**
 * A RowScroller represents a Collection of records in a table format with one row per record.<br>
 * <p><img src="doc-files/RowScroller.png"/></p>
 * <p>The records included in the scroller are selected by the scroller's {@link #getBinding() binding}, and
 * the columns are defined by the {@link AttributeField AttributeFields} included in the scroller's
 * {@link #getItem() items} list.</p>
 * <p>The form elements used to render each item cell and the validations applied to them are both defined by
 * the nature of the {@link Attribute} that the item is bound to. The binding itself is evaluated relative to the binding
 * of the scroller. See {@link SectionScoller SectionScroller} for an example.</p>
 * <p>RowScroller supports the addition and removal of rows from the scroller if the {@link #isAddAndDeleteEnabled()
 * addAndDeleteEnabled} property is set to true. If this is the case, then the <b>row controls</b> (see screenshot)
 * are rendered. The {@link #getMinRows() minRows} and {@link #getMaxRows() maxRows} properties limit what the user
 * can do. A user cannot remove rows if that would leave the scroller with less than <i>minRows</i>, and the <b>add
 * row</b> icon will be disabled if the scroller already contains <i>maxRows</i>. In the screenshot above, the scroller
 * has <i>minRows</i> set to 1, so the user cannot remove the first row. <i>maxRows</i> is set to 4, so in its
 * current state the user can add one more row.</p>
 * @see SectionScroller
 * @see AttributeField
 */
public class RowScroller extends Repeater {
	private static final long serialVersionUID = -6043887157243002172L;

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("RowScroller", model);
    }

    /**
     * Is the specified column bound to a "required" attribute - that is a column with it's required
     * attribute set to true.
     * @param model The model instance to be checked (into which binding will be evaluated).
     * @param scrollerBinding The binding of the asset in which the attribute defined.
     * @param columnBinding The index of the attribute inside the asset
     */
    public boolean isBoundToRequiredColumnAttribute(Type model, String columnBinding) {
        try {
            Attribute t = (Attribute)fetchBoundObject(columnBinding, model);
            if (t != null) {
                return t.isRequired();
            }
        }
        catch(Throwable t) {
            // fall through to default return.
        }
        return false;
    }
}

