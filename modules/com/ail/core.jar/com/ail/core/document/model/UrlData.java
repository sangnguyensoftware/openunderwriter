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

import java.net.MalformedURLException;
import java.net.URL;

import com.ail.annotation.TypeDefinition;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;

/**
 * Node of the document structure object graph. 
 */
@TypeDefinition
public class UrlData extends ItemData {
    private String url;
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void render(RenderContext context) {
        context.getOutput().printf("<itemData%s%s%s%s>", idAsAttribute(), titleAsAttribute(), styleClassAsAttribute(), orderAsAttribute());

        try {
            Functions.expand(context.getOutput(), new URL(url), context.getModel());
        }
        catch(MalformedURLException e) {
            // TODO Need some handling here...
            new CoreProxy().logError("Failed to read content from: '"+url+"'", e);
        }

        context.getOutput().printf("</itemData>");
    }
}
