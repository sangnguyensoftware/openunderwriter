/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
package com.ail.core;
/**
 * RestfulServiceReturn implementation that is intended to return status and nothing more, i.e. no entities.
 * This is so that a service using this return can write directly to the servlet response output and has
 * full control over what is written. See RestfulServiceBridge.buildResponse
 */
public class RestfulServiceStatusReturn extends RestfulServiceReturn {

    public RestfulServiceStatusReturn(int status) {
        super(status);
    }
}