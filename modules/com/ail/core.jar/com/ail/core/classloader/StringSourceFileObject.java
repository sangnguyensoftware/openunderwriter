package com.ail.core.classloader;
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
import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Wraps source code from a String in a JavaFileObject.
 */
public class StringSourceFileObject extends SimpleJavaFileObject {

    private String name;
    private String source;

    public StringSourceFileObject(String binaryName) {
        super(URI.create("string:///" + binaryName.replace('.', '/') + SOURCE.extension), SOURCE);
    }

    public StringSourceFileObject(String name, String source) {
        super(URI.create("string:///" + name.replace('.', '/') + SOURCE.extension), SOURCE);
        this.name = name;
        this.source = source;
    }

    @Override
    public final CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return source;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StringSourceFileObject [name=" + name + ", source length=" + (source == null ? "0" : source.getBytes().length) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StringSourceFileObject other = (StringSourceFileObject) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        return true;
    }
}