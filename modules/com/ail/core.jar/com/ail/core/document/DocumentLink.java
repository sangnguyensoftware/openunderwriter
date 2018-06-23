/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

package com.ail.core.document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.ail.annotation.TypeDefinition;
import com.ail.core.NotImplementedError;

/**
 * Represents a link to an external document. An external document is one who's
 * content is not owned by this class, but is externally referenced by a URL.
 * This is in contrast to the {@link Document} class which does own its content.
 * TODO There is technical debt here. This class and {@link Document} should
 * extend a common base class. That change cannot be made in a point release.
 */
@TypeDefinition
public class DocumentLink extends Document {

    static final long serialVersionUID = 3175904078994715552L;

    private String url;

    protected DocumentLink() {
    }

    public DocumentLink(String type, String url, String title) {
        this.url = url;
        setType(type);
        setTitle(title);
        setFileName(title);
    }

    public DocumentLink(DocumentType type, String url, String title) {
        this(type.getLongName(), url, title);
    }

    /**
     * @deprecated Use {@link #getDocumentContent()} instead
     */
    @Deprecated
    @Override
    public byte[] getDocument() {
        return fetchLinkContent();
    }

    private byte[] fetchLinkContent() throws DocumentLoadError {
        try (InputStream in = new URL(url).openStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new DocumentLoadError("Failed to load document from:"+url, e);
        }
    }

    /**
     * @deprecated Use {@link #setDocumentContent()} instead
     */
    @Deprecated
    @Override
    public void setDocument(byte[] document) {
        throw new NotImplementedError("setDocument cannot be invoked on a DocumentLink.");
    }

    @Override
    public DocumentContent getDocumentContent() {
        return new DocumentContent(fetchLinkContent());
    }

    @Override
    public void setDocumentContent(DocumentContent content) {
        throw new NotImplementedError("setDocumentContent cannot be invoked on a DocumentLink.");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DocumentLink clone = (DocumentLink) super.clone();
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocumentLink other = (DocumentLink) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DocumentLink [url=" + url + ", getType()=" + getType() + ", getTitle()=" + getTitle() + ", getOtherType()=" + getOtherType() + ", getFileName()=" + getFileName()
                + ", getDescription()=" + getDescription() + ", getMimeType()=" + getMimeType() + ", getSystemId()=" + getSystemId() + "]";
    }
}
