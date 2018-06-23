/* Copyright Applied Industrial Logic Limited 2013. All rights reserved. */
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
package com.ail.core.urlhandler.product;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

public class DocumentLibraryURLConnection extends URLConnection {

    private FileEntry fileEntry;
    private String version;

    public DocumentLibraryURLConnection(URL url) {
        super(url);
    }

    public void setFileEntry(FileEntry fileEntry) throws PortalException, SystemException {
        this.fileEntry=fileEntry;
        this.version = fileEntry.getLatestFileVersion().getVersion();
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public String getContentEncoding() {
        return super.getContentEncoding();
    }

    @Override
    public int getContentLength() {
        return (int)fileEntry.getSize();
    }

    @Override
    public String getContentType() {
        return fileEntry.getMimeType();
    }

    @Override
    public long getDate() {
        return fileEntry.getModifiedDate().getTime();
    }

    @Override
    public boolean getDoInput() {
        return true;
    }

    @Override
    public boolean getDoOutput() {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return DLFileEntryLocalServiceUtil.getFileAsStream(fileEntry.getFileEntryId(), version);
        }
        catch(Throwable t) {
            throw new IOException(t);
        }
    }

    @Override
    public long getLastModified() {
        return super.getLastModified();
    }
}
