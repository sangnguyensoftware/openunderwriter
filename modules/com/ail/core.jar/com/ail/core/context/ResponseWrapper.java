/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.core.context;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class ResponseWrapper implements ResponseAdaptor {
    ResponseAdaptor adaptor = null;
    StringWriter bufferWriter = null;
    boolean bufferWriterActive = false;
    boolean validationErrorsFound = false;

    public ResponseWrapper(ResponseAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    @Override
    public void sendRedirect(String target) throws IOException {
        adaptor.sendRedirect(target);
    }

    @Override
    public void addCookie(Cookie cookey) {
        adaptor.addCookie(cookey);
    }

    @Override
    public PortletResponse getPortletResponse() {
        return adaptor.getPortletResponse();
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return adaptor.getServletResponse();
    }

    @Override
    public String getNamespace() {
        return adaptor.getNamespace();
    }

    @Override
    public void setContentType(String contentType) {
        adaptor.setContentType(contentType);
    }

    @Override
    public Writer getWriter() throws IOException {
        return (bufferWriterActive) ? bufferWriter : adaptor.getWriter();
    }

    @Override
    public String createActionURL() {
        return adaptor.createActionURL();
    }

    @Override
    public String createResourceURL() {
        return adaptor.createResourceURL();
    }

    public void startWriter() {
        if (bufferWriterActive) {
            throw new IllegalStateException("BufferedWriter is already started.");
        }

        bufferWriter = new StringWriter();
        bufferWriterActive = true;
    }

    public void stopWriter() {
        if (bufferWriter == null || !bufferWriterActive) {
            throw new IllegalStateException("startBufferWriter has not been called.");
        }

        bufferWriterActive = false;
    }

    public int writerMark() {
        trimStringBuffer(bufferWriter.getBuffer());

        return bufferWriter.getBuffer().length();
    }

    void trimStringBuffer(StringBuffer buffer) {
        int len = buffer.length();
        int pos;

        for(pos = len-1 ; pos > 0 && Character.isWhitespace(buffer.charAt(pos)) ; pos--);

        buffer.delete(pos+1, len);
    }

    public StringWriter getWriterBuffer() {
        if (bufferWriter == null) {
            throw new IllegalStateException("startBufferWriter has not been called.");
        }

        return bufferWriter;
    }

    public boolean isValidationErrorsFound() {
        return validationErrorsFound;
    }

    public void setValidationErrorsFound(boolean validationErrorsFound) {
        this.validationErrorsFound = validationErrorsFound;
    }
}
