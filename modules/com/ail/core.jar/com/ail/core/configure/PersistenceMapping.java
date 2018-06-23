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

package com.ail.core.configure;

import com.ail.core.Type;

/**
 * This class holds Persistence information for a configuration. It holds
 * the mapping description for the database & objects - which is generally a string of XML, and transient
 * instance of a persistanceConfiguration.
 */
public class PersistenceMapping extends Type {
    static final long serialVersionUID = 5193477041052835669L;
    private String objectMapping = null;
	private String databaseConfiguration = null;
    private transient Object persistanceConfiguration = null;

    /**
     * Get the XML object mapping definition. The actual format of the information
     * returned depends upon the persistence mechanism being used, but it
     * is generally an XML string that describes how objects are mapped into
     * object instances.
     * @return String describing the mapping.
     */
    public String getObjectMapping() {
        return objectMapping;
    }


	/**
	 * Get the XML object mapping string
	 * @see #getObjectMapping
     * @return String describing the mapping.
	 */
    public String getObjectMappingCDATA() {
        return "<![CDATA["+objectMapping+"]]>";
    }

    /**
     * Set the XML object mapping string
     * @see #getObjectMapping
     * @param mapping The mapping string.
     */
    public void setObjectMapping(String objectMapping) {
        this.objectMapping = objectMapping;
    }


	/**
	 * Get the XML database configuration definition. The actual format database (ie mysql dialect) 
	 * It is generally an XML string, but is not compulsory
	 * @return String describing the configuration.
	 */
	public String getDatabaseConfiguration() {
		return databaseConfiguration;
	}


	/**
	 * Get the XML database configuration string
	 * @see #getDatabaseConfiguration
	 * @return String describing the configuration.
	 */
	public String getDatabaseConfigurationCDATA() {
		return "<![CDATA["+databaseConfiguration+"]]>";
	}

	/**
	 * Set the XML database configuration string
	 * @see #getDatabaseConfiguration
	 * @param configuration The configuration string.
	 */
	public void setDatabaseConfiguration(String databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}

    /**
     * Get the persistence Configuration. The persistence services may use this
     * property to cache an instance of the persistanceConfiguration. The configuration
     * handler will ensure that this is handled in a version safe fashion.
     * @return An instance of a persistanceConfiguration.
     */
    public Object getPersistanceConfiguration() {
        return persistanceConfiguration;
    }

    /**
     * Set the persistence Configuration for this mapping.
     * @see #getPersistanceConfiguration
     * @param persistanceConfiguration The persistence Configuration instance
     */
    public void setPersistanceConfiguration(Object persistanceConfiguration) {
        this.persistanceConfiguration = persistanceConfiguration;
    }
}
