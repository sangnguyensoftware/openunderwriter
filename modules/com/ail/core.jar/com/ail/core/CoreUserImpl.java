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
package com.ail.core;

import java.security.Principal;

/**
 * Clients of the Core may use an instance of this class to satisfy the Core's
 * callback methods.
 */
public class CoreUserImpl implements CoreUser {
    /**
     * The current date will be returned whenever the version effective date
     * is requested.
     */
    public static final int SELECT_LATEST_CONFIGURATIONS=0;

    /**
     * Whenever CoreUserImpl was instantiated becomes the version effective
     * date for this instance.
     */
    public static final int SELECT_CONSISTENT_CONFIGURATIONS=1;

    private VersionEffectiveDate ved=null;
    private Principal securityPrincipal=null;
    private String configurationNamespace=null;
    
    
    public CoreUserImpl() {
    }
    
    /**
     * Constructor
     * @param ved VersionEffectiveDate to use when selecting configurations.
     * @param configurationNamespace Namespace to associate with this core user.
     * @param securityPrincipal Principal to associate with this Core User, this may be null.
     */
    public CoreUserImpl(VersionEffectiveDate ved, String configurationNamespace, Principal securityPrincipal) {
        this.ved=ved;
        this.configurationNamespace=configurationNamespace;
        this.securityPrincipal=securityPrincipal;
    }

    /**
     * Create a new instance based on an existing CoreUser. The core uses this
     * constructor to build stripped down instance of client's CoreUser to reduce
     * RMI traffic. A client's CoreUser is always passed into the services they
     * use - it's how the service gets to know things about the client. However,
     * client's also often implement CoreUser and pass themselves into the Core
     * constructor. If they do, then we'd end up sending the whole client class
     * across RMI. So the Core uses this constructor to pull the minimum necessary
     * information out of the client's CoreUser and pass that onto the service.
     * @param user The CoreUser to copy properties from.
     */
    public CoreUserImpl(CoreUser user) {
        this(user.getVersionEffectiveDate(), user.getConfigurationNamespace(), user.getSecurityPrincipal());
    }

    /**
     * Constructor
     * @param configSelectionFlag Either {@link #SELECT_CONSISTENT_CONFIGURATIONS SELECT_CONSISTENT_CONFIGURATIONS} or {@link #SELECT_LATEST_CONFIGURATIONS SELECT_LATEST_CONFIGURATIONS}
     * @param configurationNamespace Namespace to associate with this core user.
     * @param securityPrincipal Principal to associate with this Core User, this may be null.
     */
    public CoreUserImpl(int configSelectionFlag, String configurationNamespace, Principal securityPrincipal) {
        switch(configSelectionFlag) {
            case SELECT_CONSISTENT_CONFIGURATIONS:
                ved=new VersionEffectiveDate();
                break;
            case SELECT_LATEST_CONFIGURATIONS:
                ved=null;
                break;
            default:
                throw new IllegalArgumentException("ConfigSelectionFlag is invalid");
        }

        this.configurationNamespace=configurationNamespace;
        this.securityPrincipal=securityPrincipal;
    }

    /**
     * The Core uses this callback to determine which versions of artifacts it
     * should use on the CoreUser's behalf.
     * @return The version date that the CoreUser is working at.
     */
    public VersionEffectiveDate getVersionEffectiveDate() {
        if (ved==null) {
            return new VersionEffectiveDate();
        }

        return ved;
    }

    public void setVersionEffecvtiveDateToNow() {
        ved=new VersionEffectiveDate();
    }
    
    public void setVersionEffectiveDate(VersionEffectiveDate ved) {
        this.ved=ved;
    }
    
    /**
     * Get the security principal associated with this instance.
     * @return The associated security principal - if defined, null otherwise.
     */
    public Principal getSecurityPrincipal() {
        return securityPrincipal;
    }

    /**
     * Set the security principal associated with this instance.
     * @param securityPrincipal The security principal of this core user
     */
    public void setSecurityPrincipal(Principal securityPrincipal) {
        this.securityPrincipal=securityPrincipal;
    }

    public String getConfigurationNamespace() {
        return configurationNamespace;
    }

    public void setConfigurationNamespace(String configurationNamespace) {
        this.configurationNamespace=configurationNamespace;
    }
}
