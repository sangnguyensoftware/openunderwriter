/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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
package com.ail.core.workflow.jbpm;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.util.Base64;

import com.ail.core.CoreProxy;
/**
 * Provides utility methods for calling rest services in jBPM. Requires the jBPM server properties.
 */
public class JBPMRestHelper {

    private JBPMProperties jbpmProperties;

    public JBPMRestHelper(Properties properties) {
        jbpmProperties = new JBPMProperties(properties);
    }

    public JBPMProperties getJbpmProperties() {
        return jbpmProperties;
    }

    /**
     * Turns the username and password from the JBPM properties into a Base64 encoded string
     * @return  the encoded auth string
     */
    public String getEncodedAuthString() {
        return Base64.encodeBytes((jbpmProperties.getUsername() + ":" + jbpmProperties.getPassword()).getBytes());
    }

    /**
     * Constructs the base URI string for the jBPM rest services
     * @return
     */
    public String getRestURIString() {
        return jbpmProperties.getProtocol() + "://" + jbpmProperties.getHostname() + ":" + jbpmProperties.getPort() + "/jbpm-console/rest/";
    }

    /**
     * Gets a {@link ClientRequest} for a process rest call, not returning any vars, and using the default deployment id.
     * @param deploymentId  the deployment id
     * @param path  the relative path after the base URI
     * @param withvars  whether to return vars with the processes
     * @return  the ClientRequest
     * @throws URISyntaxException
     */
    public ClientRequest getProcessRequest(String deploymentId, String path, boolean withvars) throws URISyntaxException {
        String uriPath = getRestURIString() + "runtime/" + deploymentId + (withvars ? "/withvars" : "") + "/process/";
        new CoreProxy().logDebug("getProcessRequest fullPath = " + uriPath + path);

        return getClientRequest(path, uriPath);
    }

    /**
     * Gets a {@link ClientRequest} for a history rest call.
     * @param path  the relative path after the base URI
     * @return  the ClientRequest
     * @throws URISyntaxException
     */
    public ClientRequest getHistoryRequest(String path) throws URISyntaxException {
        String uriPath = getRestURIString() + "history/";
        new CoreProxy().logDebug("getHistoryRequest fullPath = " + uriPath + path);

        return getClientRequest(path, uriPath);
    }

    /**
     * Gets a {@link ClientRequest} for a task rest call.
     * @param path  the relative path after the base URI
     * @return  the ClientRequest
     * @throws URISyntaxException
     */
    public ClientRequest getTaskRequest(String path) throws URISyntaxException {
        String uriPath = getRestURIString() + "task/";
        new CoreProxy().logDebug("getTaskRequest fullPath = " + uriPath + path);

        return getClientRequest(path, uriPath);
    }

    /**
     * Gets a {@link ClientRequest} with accept type of JSON with basic authorisation for the given path
     * @param path
     * @param uriPath
     * @return
     * @throws URISyntaxException
     */
    private ClientRequest getClientRequest(String path, String uriPath) throws URISyntaxException {
        ClientRequestFactory factory = new ClientRequestFactory(new URI(uriPath));
        ClientRequest request = factory.createRelativeRequest(path).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Basic " + getEncodedAuthString());

        return request;
    }
}
