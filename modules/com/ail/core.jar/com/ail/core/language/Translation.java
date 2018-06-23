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
package com.ail.core.language;

import java.util.HashMap;
import java.util.Map;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Identified;
import com.ail.core.Type;

/**
 * A Translation associates a given collection of text
 *
 */
@TypeDefinition
public class Translation extends Type implements Identified {
    private String extendsLanguage = null;
    private String language = null;
    private Map<String,String>key = null;

    public Translation() {
        key=new HashMap<String,String>();
    }

    public Translation(String language) {
        this();
        this.language=language;
    }

    public Translation(String language, String extendsLanguage) {
        this(language);
        this.extendsLanguage=extendsLanguage;
    }

    /**
     * @return the key
     */
    public Map<String, String> getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(Map<String, String> key) {
        this.key = key;
    }

    public void setExtendsLanguage(String extendsLanguage) {
        this.extendsLanguage = extendsLanguage;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExtendsLanguage() {
        return extendsLanguage;
    }

    public String translate(String key) {
        return this.key.get(key);
    }

    /* (non-Javadoc)
     * @see com.ail.core.Identified#compareById(java.lang.Object)
     */
    @Override
    public boolean compareById(Identified that) {
        try {
            return ((Translation)that).getId().equals(this.getId());
        }
        catch(Throwable e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see com.ail.core.Identified#getId()
     */
    @Override
    public String getId() {
        return language;
    }

    /* (non-Javadoc)
     * @see com.ail.core.Identified#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        language=id;
    }
}
