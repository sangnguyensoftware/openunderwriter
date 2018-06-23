/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * Help text is associated with a PageElement. Allows help text to be configured for use in rendered output. 
 */
@TypeDefinition
public class HelpText extends Type {
    private static final long serialVersionUID = 6414533098371760797L;

    /** The text of the hint  */
    private String text = null;

    /** Hints to the UI rendering engine specifying details of how this help should be rendered. The values supported
     * are specific to the type of attribute being rendered. */ 
    private String renderHint = null;

	/**
     * Default constructor
     */
    public HelpText() {
    }

    /**
     * Create help text specifying all fields.
     * @param text Help Text (may include markup). May be null.
     */
    public HelpText(String text) {
        super();
        this.text = text;
    }
    
    /**
     * The text of the help if it exists. Null otherwise.
     * 
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @see #setText(String)
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Render hint to guide the UI as to how the help should be rendered.
     * @return RenderHint if defined, null otherwise.
     */
    public String getRenderHint() {
		return renderHint;
	}

    /**
     * @see #getRenderHint()
     * @param renderHint
     */
    public void setRenderHint(String renderHint) {
		this.renderHint = renderHint;
	}
}
