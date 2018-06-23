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

public interface PreferencesAdaptor {

    public static final String CONFIGURE_BY_REQUEST = "Request";
    public static final String CONFIGURE_BY_SESSION = "Session";
    public static final String CONFIGURE_BY_PREFERENCES = "Preferences";
    public static final String CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME = "configuration.source";
    public static final String PAGEFLOW_PORTLET_PREFERENCE_NAME = "pageflow";
    public static final String PRODUCT_PORTLET_PREFERENCE_NAME = "product";

    boolean isConfiguredByPreferences();

    boolean isConfiguredByRequest();

    String getValue(String preferenceName, String defaultValue);
}
