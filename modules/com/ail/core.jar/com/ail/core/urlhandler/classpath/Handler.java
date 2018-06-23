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

package com.ail.core.urlhandler.classpath;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * The handler for the "classpath://" URLs. All this class does is to
 * transparently transform a "classpath://" URL into a reference for a class
 * resource.
 * <p>
 * For example, a URL like: 'classpath://myschema.xsd' is translated into a call
 * to <ul><code>getClass().getResource("myschema.xsd")</code>;</ul><p>
 * and a URL like: 'classpath://com.ail.core.configure/Configure.xsd' is translated
 * to <ul><code>getClass().getResource("/com/ail/core/configure/Configure.xsd")</code>.</ul>
 * 
 */
public class Handler extends URLStreamHandler {
    
    protected URLConnection openConnection(URL u) throws java.io.IOException {
        String pkg="/"+u.getHost().replace('.', '/');
        String res=u.getPath();
        
        URL actualURL = getClass().getResource(pkg+res);

        if (actualURL == null) {
            throw new FileNotFoundException(u.toExternalForm());
        }

        return actualURL.openConnection();
    }
}
