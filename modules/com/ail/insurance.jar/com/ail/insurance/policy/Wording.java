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

package com.ail.insurance.policy;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * Represents an individual wording either by id, text or URL. A wording may be defined in one of three ways:
 * 1) An String ID which has meaning in the context of a product;
 * 2) A String of text - i.e. the text of the wording itself;
 * 3) A URL to some external document.
 */
@TypeDefinition
public class Wording extends Type {
    private static final long serialVersionUID = 2598714090687003463L;

    /**
     * The ID of the wording. This identifies the type of wording being used,
     * and is used by other services to generate the correct text for the
     * wording.
     */
    private String id;

    /**
     * The text of the wording. This may be null if the wording text has not
     * been generated, or wording URL is being used.
     */
    private String text;

    /**
     * A URL reference to a wording document.
     */
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasText() {
        return text != null;
    }

    public boolean hasUrl() {
        return url != null;
    }

    public boolean hasId() {
        return id!=null;
    }
}
