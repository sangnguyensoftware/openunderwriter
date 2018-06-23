/* Copyright Applied Industrial Logic Limited 2013. All rights Reserved */
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
package com.ail.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationOwner;
import com.ail.core.configure.ConfigurationResetError;

class ConfigurationResetHandler {
    private String name;
    private Core core;
    private ConfigurationOwner owner;

    ConfigurationResetHandler(Core core, ConfigurationOwner owner, String name) {
        this.name = name;
        this.core = core;
        this.owner = owner;
    }

    void invoke() {
        if (!resetFromClassResource() && !resetFromProductResource()) {
            throw new ConfigurationResetError("Configuration could not be found as a class resource or a product resource for '"+name+"'.");
        }
    }

    /**
     * Attempt to reset the configuration from a product resource. Typically this will mean reading
     * a "Registry.xml" file from the product definition's CMS folder.
     * @return true if the resource exists; or, false if it does not exist.
     * @throws ConfigurationResetError If any errors are encountered during the processing for the resource.
     */
    boolean resetFromProductResource() {

        try {
            String host = core.getParameterValue("ProductRepository.Host");
            String port = core.getParameterValue("ProductRepository.Port");

            String productPath = "product://" + host + ":" + port + "/" + name.replace('.', '/') + ".xml";

            resetFromURL(new URL(productPath));
            
            return true;

        } catch (FileNotFoundException e) {
            return false;
        } catch (XMLException e) {
            throw new ConfigurationResetError("Configuration found in product resource for '" + name + "' contains sax error(s).", e);
        } catch (Exception e) {
            throw new ConfigurationResetError("Configuration could not be read for '" + name + "'.", e);
        }        
    }
    
    /**
     * Attempt to reset the configuration from a class resource. If a class resource is found which follows the 
     * naming convention &lt;name&gt;DefaultConfig.xml then its contents will be read and used to reset the
     * configuration. If it does not exist, false is returned.
     * @return true if the resource is found, false otherwise.
     * @throws ConfigurationResetError if any problems are encountered while processing the resource.
     */
    boolean resetFromClassResource() {

        try {
            // Actual resource's name will be <name>DefaultConfig.xml
            String resourcePath = name + "DefaultConfig.xml";

            URL inputUrl = owner.getClass().getResource(resourcePath);

            // Null url means the resource does not exist.
            if (inputUrl == null) {
                return false;
            }

            resetFromURL(inputUrl);

            return true;

        } catch (XMLException e) {
            throw new ConfigurationResetError("Configuration found in class resource for '" + name + "' contains sax error(s).", e);
        } catch (Exception e) {
            throw new ConfigurationResetError("Configuration found in class resource for '" + name + "' could not be read.", e);
        }
    }

    void resetFromURL(URL inputURL) throws XMLException, IOException {
        InputStream inStream = inputURL.openStream();

        try {
            XMLString factoryConfigXML = new XMLString(inStream);

            // marshal the config XML into an instance of Configuration
            Configuration factoryConfig = core.fromXML(Configuration.class, factoryConfigXML);

            // Add details of where we loaded the config from back into the
            // config so that anyone who uses this configuration in future can
            // see where it came from.
            factoryConfig.setSource(inputURL.toExternalForm());

            // reset the configuration
            owner.setConfiguration(factoryConfig);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
    }
}
