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

import com.ail.core.Type;

/**
 * <p>A Section scroller displays repeating blocks of questions. The data for the questions is selected
 * from the model by the scroller's {@link #getBinding() binding} property.</p>
 * <p><img src="doc-files/SectionScroller.png"/></p>
 * <p>The screenshot above shows a SectionScroller which is bound to assets - in this case vehicle
 * assets. The binding has selected two of these assets (as Records) from the {@link com.ail.openquote.Policy Policy}
 * being processed, and for each of those assets it renders a section built up from the page elements contained
 * in the SectionScroller. In this example, the SectionScroller contains 8 page elements, two of which have
 * detail associated with them.</p>
 * @version 1.1
 */
public class SectionScroller extends Repeater {
	private static final long serialVersionUID = -6043887157243002172L;

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("SectionScroller", model);
    }
}

