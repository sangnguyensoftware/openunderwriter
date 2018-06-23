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
package com.ail.core.context;

import java.util.Map;

import com.ail.core.RestfulServiceReturn;
import com.ail.core.Type;
import com.ail.core.logging.ServiceRequestRecord;

public class SessionWrapper {
    public static final String SESSION_TEMP_ATTRIBUTE = "session-temp";
    public static final String PRODUCT_NAME_SESSION_ATTRIBUTE = "product";
    public static final String SUCCESS_REDIRECT_ATTRIBUTE = "pageflow.success.redirect";
    public static final String FAILURE_REDIRECT_ATTRIBUTE = "pageflow.failure.redirect";
    public static final String RESTFUL_REQUEST_POST_DATA = "restful-request-post-data";
    public static final String RESTFUL_REQUEST_ATTACHMENT = "restful-request-attachment";
    public static final String RESFTUL_RESPONSE = "restful-response";
    public static final String REMOTE_USER = "remote-user";
    public static final String SERVICE_REQUEST_RECORD = "service-request-record";

    private SessionAdaptor attributeHandler;

    public SessionWrapper(SessionAdaptor attributeHandler) {
        this.attributeHandler = attributeHandler;
    }

    public void clear() {
        attributeHandler.set(SESSION_TEMP_ATTRIBUTE, null);
        attributeHandler.set(PRODUCT_NAME_SESSION_ATTRIBUTE, null);
        attributeHandler.set(SUCCESS_REDIRECT_ATTRIBUTE, null);
        attributeHandler.set(FAILURE_REDIRECT_ATTRIBUTE, null);
        attributeHandler.set(RESTFUL_REQUEST_POST_DATA, null);
        attributeHandler.set(RESFTUL_RESPONSE, null);
        attributeHandler.set(SERVICE_REQUEST_RECORD, null);
    }

    public String getFailureRedirect() {
        return attributeHandler.get(FAILURE_REDIRECT_ATTRIBUTE, String.class);
    }

    public void setFailureRedirect(String redirectUrl) {
        attributeHandler.set(FAILURE_REDIRECT_ATTRIBUTE, redirectUrl);
    }

    public String getSuccessRedirect() {
        return attributeHandler.get(SUCCESS_REDIRECT_ATTRIBUTE, String.class);
    }

    public void setSuccessRedirect(String redirectUrl) {
        attributeHandler.set(SUCCESS_REDIRECT_ATTRIBUTE, redirectUrl);
    }

    public Type getSessionTemp() {
        return attributeHandler.get(SESSION_TEMP_ATTRIBUTE, Type.class);
    }

    public void setSessionTemp(Type sessionTemp) {
        attributeHandler.set(SESSION_TEMP_ATTRIBUTE, sessionTemp);
    }

    public String getProductName() {
        return attributeHandler.get(PRODUCT_NAME_SESSION_ATTRIBUTE, String.class);
    }

    public void setProductName(String productName) {
        attributeHandler.set(PRODUCT_NAME_SESSION_ATTRIBUTE, productName);
    }

    public void setRestfulRequestPostData(String data) {
        attributeHandler.set(RESTFUL_REQUEST_POST_DATA, data);
    }

    public String getRestfulRequestPostData() {
       return attributeHandler.get(RESTFUL_REQUEST_POST_DATA, String.class);
    }

    public void setRestfulResponse(RestfulServiceReturn ret) {
        attributeHandler.set(RESFTUL_RESPONSE, ret);
    }

    public RestfulServiceReturn getRestfulResponse() {
        return attributeHandler.get(RESFTUL_RESPONSE, RestfulServiceReturn.class);
    }

    public void setRestfulRequestAttachment(Map<String, Object> attachment) {
        attributeHandler.set(RESTFUL_REQUEST_ATTACHMENT, attachment);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRestfulRequestAttachment() {
        return attributeHandler.get(RESTFUL_REQUEST_ATTACHMENT, Map.class);
    }

    public void setRemoteUser(Long remoteUser) {
        attributeHandler.set(REMOTE_USER, remoteUser);
    }

    public Long getRemoteUser() {
        return attributeHandler.get(REMOTE_USER, Long.class);
    }

    public void setServiceRequestRecord(ServiceRequestRecord serviceRequestRecord) {
        attributeHandler.set(SERVICE_REQUEST_RECORD, serviceRequestRecord);
    }

    public ServiceRequestRecord getServiceRequestRecord() {
        return attributeHandler.get(SERVICE_REQUEST_RECORD, ServiceRequestRecord.class);
    }
}
