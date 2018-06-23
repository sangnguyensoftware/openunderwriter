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
import static javax.tools.JavaFileObject.Kind.CLASS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.SimpleJavaFileObject;

/**
 * Wraps compiled bytecode into a JavaFileObject.
 */
public class ClassFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream bos;
    private String binaryName;
    private Class<?> classInstance;

    public ClassFileObject(String binaryName) {
        super(URI.create("string:///" + binaryName.replace('.', '/') + CLASS.extension), CLASS);
        this.binaryName = binaryName;
        this.bos = new ByteArrayOutputStream();
    }

    public byte[] getFileBytes() {
        return bos.toByteArray();
    }

    @Override
    public final InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(getFileBytes());
    }

    @Override
    public final OutputStream openOutputStream() throws IOException {
        return bos;
    }

    @Override
    public final Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Writer openWriter() throws IOException {
        return new OutputStreamWriter(openOutputStream());
    }

    @Override
    public final long getLastModified() {
        return 0L;
    }

    @Override
    public final boolean delete() {
        throw new UnsupportedOperationException();
    }

    public String binaryName() {
        return binaryName;
    }

    @Override
    public final Kind getKind() {
        return kind;
    }

    @Override
    public final NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Modifier getAccessLevel() {
        return null;
    }

    public Class<?> getClassInstance() {
        return classInstance;
    }

    public void setClassInstance(Class<?> classInstance) {
        this.classInstance = classInstance;
    }

    @Override
    public String toString() {
        return "ClassFileObject [binaryName=" + binaryName + ", classInstance=" + (classInstance == null ? "null" : classInstance.getName()) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((binaryName == null) ? 0 : binaryName.hashCode());
        result = prime * result + ((classInstance == null) ? 0 : classInstance.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
        ClassFileObject other = (ClassFileObject) obj;
        if (binaryName == null) {
            if (other.binaryName != null)
                return false;
        } else if (!binaryName.equals(other.binaryName))
            return false;
        if (classInstance == null) {
            if (other.classInstance != null)
                return false;
        } else if (!classInstance.equals(other.classInstance))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }
}