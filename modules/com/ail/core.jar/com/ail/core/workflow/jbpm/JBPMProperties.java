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

import java.util.Properties;
/**
 * Simple object to retrieve properties required to communicate with jBPM and hold them.
 * This requires that the properties live in a group in a product registry called jBPMProperties that contains
 * values for the keys specified here as constants.
 */
public class JBPMProperties {

    private static final String JBPM_CONSOLE_PROTOCOL = "jbpm.console.protocol";
    private static final String JBPM_CONSOLE_HOSTNAME = "jbpm.console.hostname";
    private static final String JBPM_CONSOLE_PORT = "jbpm.console.port";
    private static final String JBPM_CONSOLE_USERNAME = "jbpm.console.username";
    private static final String JBPM_CONSOLE_PASSWORD = "jbpm.console.password";
    private static final String JBPM_DEPLOYMENT_ID = "jbpm.deployment.id";

    private String protocol;
    private String hostname;
    private String port;
    private String username;
    private String password;
    private String[] deploymentIds;

    public JBPMProperties(Properties properties) {
        setJBPMProperties(properties);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getDeploymentIds() {
        return deploymentIds;
    }

    public void setDeploymentIds(String[] deploymentIds) {
        this.deploymentIds = deploymentIds;
    }

    private void setJBPMProperties(Properties properties) {
        setProtocol(properties.getProperty(JBPM_CONSOLE_PROTOCOL));
        setHostname(properties.getProperty(JBPM_CONSOLE_HOSTNAME));
        setPort(properties.getProperty(JBPM_CONSOLE_PORT));
        setUsername(properties.getProperty(JBPM_CONSOLE_USERNAME));
        setPassword(properties.getProperty(JBPM_CONSOLE_PASSWORD));
        setDeploymentIds(properties.getProperty(JBPM_DEPLOYMENT_ID).split(","));
    }
}