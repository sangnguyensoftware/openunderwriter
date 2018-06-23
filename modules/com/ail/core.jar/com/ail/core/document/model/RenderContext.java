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

import java.io.PrintWriter;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * An instance of this class is passed into each invocation of the 
 * {@link com.ail.core.document.model.ItemData#render(RenderContext) ItemData} method. The context
 * define everything the render method needs in order to perform it's function. 
 */
@TypeDefinition
public class RenderContext extends Type {
    private Type model;
    private PrintWriter output;

    public RenderContext(PrintWriter output, Type model) {
        setModel(model);
        setOutput(output);
    }
    
    /** 
     * Get the instance of the model being rendered
     * @return Model to be rendered.
     */
    public Type getModel() {
        return model;
    }

    /**
     * {@see #getModel()}
     * @param model
     */
    public void setModel(Type model) {
        this.model = model;
    }
    
    /**
     * Get the PrintWriter which render methods should write their output.
     * @return PrintWriter to output to.
     */
    public PrintWriter getOutput() {
        return output;
    }

    /**
     * {@see #getOutput()}
     * @param output
     */
    public void setOutput(PrintWriter output) {
        this.output=output;
    }
}
