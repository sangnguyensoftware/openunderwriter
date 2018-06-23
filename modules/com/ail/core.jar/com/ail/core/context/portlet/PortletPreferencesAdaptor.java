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
package com.ail.core.context.portlet;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import com.ail.core.context.PreferencesAdaptor;

public class PortletPreferencesAdaptor implements PreferencesAdaptor {

    private PortletPreferences preferences;

    public PortletPreferencesAdaptor(PortletRequest request) {
        this.preferences = request.getPreferences();
    }

    @Override
    public boolean isConfiguredByPreferences() {
        return PreferencesAdaptor.CONFIGURE_BY_PREFERENCES.equals(preferences.getValue(PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME, null));
    }

    @Override
    public boolean isConfiguredByRequest() {
        return PreferencesAdaptor.CONFIGURE_BY_REQUEST.equals(preferences.getValue(PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME, null));
    }

    @Override
    public String getValue(String preferenceName, String defaultValue) {
        return preferences.getValue(preferenceName, defaultValue);
    }
}
