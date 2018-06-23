/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
package com.ail.jbpm.handler;

import java.io.DataOutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Call a Type restful service on OU, for example PolicyService.
 *
 * This class does not rely on the core OU code as it runs inside the jbpm-console war. This means everything it needs to know
 * about OU needs to be either passed in or worked out, even just the baseURL of the jboss server must be acquired from JMX.
 *
 */
public class ServiceCaller {

    private String baseURL;

    private static ServiceCaller instance;

    private ServiceCaller() {
        initialiseBaseURL();
    }

    public static synchronized ServiceCaller getInstance() {
        if (instance == null) {
            instance = new ServiceCaller();
        }
        return instance;
    }

    /**
     * Calls a service on OU, using POST and expecting the content type as JSON for input and output
     * @param urlPath   the path, e.g. pageflow/{pageFlowName}/product/{productName}/page/{pageName}
     * @param argument    the argument that will be passed to the service body as JSON
     * @return  the JsonNode representing the return body, or null if the call fails for any reason at all
     */
    public JsonNode callService(String urlPath, Object argument) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseURL + urlPath).openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            connection.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            ObjectMapper mapper = new ObjectMapper();

            if (argument != null) {
                mapper.writeValue(dos, argument);
            }

            dos.flush();
            dos.close();

            return mapper.readTree(connection.getInputStream());
        } catch (Exception e) {
            Logger.getGlobal().info(e.getMessage());
        }

        return null;
    }

    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Initialises the base URL of the server this code is running on
     */
    private void initialiseBaseURL() {
        baseURL = getBaseURLFromJBossConnectorMBean() + "/";
    }

    /**
     * Gets the base URL from the jboss webservice MBean.
     * @return  the URL of this server, e.g. http://localhost:8080
     */
    private String getBaseURLFromJBossConnectorMBean() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName jbossWSServiceServerConfig = new ObjectName("jboss.ws:service=ServerConfig");
            String host = (String) server.getAttribute(jbossWSServiceServerConfig, "WebServiceHost");
            Integer port = (Integer) server.getAttribute(jbossWSServiceServerConfig, "WebServicePort");
            Logger.getGlobal().info("Got baseURL from JBoss connector MBean: " + baseURL);
            return "http://" + host + ":" + port.intValue();
        } catch (JMException e) {
            Logger.getGlobal().log(Level.WARNING, "Unable to get address:port from jboss connector MBean", e);
        }

        return null;
    }
}
