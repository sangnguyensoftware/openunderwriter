/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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
package com.ail.core.configure;

import java.util.Date;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * This value object encapsulates the details of a configuration, but excludes the configuration
 * itself.
 */
@TypeDefinition
public class ConfigurationSummary extends Type {
    private String namespace;
    private String manager;
    private Date validFrom;
    private Date validTo;
    private String who;
    private String version;
    
    public ConfigurationSummary() {
    }
    
    public ConfigurationSummary(String namespace, String manager, Date validFrom, Date validTo, String who, String version) {
        this.namespace=namespace;
        this.manager=manager;
        this.validFrom=validFrom;
        this.validTo=validTo;
        this.who=who;
        this.version=version;
    }
    
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String name) {
        this.namespace = name;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
