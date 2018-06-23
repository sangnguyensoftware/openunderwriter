/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
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
package com.ail.core.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceFinder;

public class JaninoResources extends ResourceFinder {
    Map<String, StringResource> resource;

    public JaninoResources() {
        resource = new HashMap<>();
    }

    public void add(String name, String source) {
        resource.put(name+".java", new StringResource(name, source));
    }

    @Override
    public Resource findResource(String resourceName) {
        return resource.get(resourceName);
    }
}

class StringResource implements Resource {
    String name;
    String source;

    StringResource(String name, String source) {
        this.name = name;
        this.source = source;
    }

    @Override
    public InputStream open() throws IOException {
        return new ByteArrayInputStream(source.getBytes("UTF-8"));
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long lastModified() {
        return 0L;
    }
}
