/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

import java.io.Serializable;
import java.security.Principal;

/**
 * Any class that wishes to use the Core class must implement this class. The
 * Core uses the methods defined he to query the client class.
 */
public interface CoreUser extends Serializable {
	/**
     * The Core uses this callback to determine which versions of artifacts it
     * should use on the CoreUser's behalf.
     * @return The version date that the CoreUser is working at.
     */
	VersionEffectiveDate getVersionEffectiveDate();
    
	/**
     * Get the callers security principal.
     * @return The callers security principal 
	 */
    Principal getSecurityPrincipal();

    /**
     * Retrieve the configuration namespace used by this class.
     * This namespace is simply a unique string used by the class to identify
     * its configuration.
     * @return Namespace string
     */
    String getConfigurationNamespace();

}
