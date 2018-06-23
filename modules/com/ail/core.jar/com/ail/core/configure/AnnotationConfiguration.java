/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */
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

import java.io.IOException;
import java.io.InputStream;

import com.ail.core.CoreProxy;
import com.ail.core.XMLException;
import com.ail.core.XMLString;

/**
 * The Annotation configuration defined by this class is constructed based o
 */
public class AnnotationConfiguration {
    private static Configuration configuration;
    private static Error loadError;

    static {
        try (InputStream in = AnnotationConfiguration.class.getResourceAsStream("/com/ail/core/AnnotatedTypeConfig.xml")) {
            XMLString factoryConfigXML = new XMLString(in);
            configuration = new CoreProxy().fromXML(Configuration.class, factoryConfigXML);
            configuration.setName("Annotation configuration");
            configuration.setVersion("1.0");
            configuration.setNamespace(AnnotationConfiguration.class.getName());
        } catch (IOException | XMLException e) {
            loadError = new ConfigurationError("Failed to load Annotations configuration", e);
        }
    }

    static Configuration getInstance() {
        if (loadError != null) {
            throw loadError;
        }
        return configuration;
    }
}
