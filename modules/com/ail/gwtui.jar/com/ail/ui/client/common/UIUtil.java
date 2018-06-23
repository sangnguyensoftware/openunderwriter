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
package com.ail.ui.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;


public final class UIUtil {

    public static final String DATE_FIELD_FORMAT = "yyyy-MM-dd";

    public static final String SERVER_ERROR = "SERVER ERROR";


    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     *
     * @param html the html string to escape
     * @return the escaped string
     */
    public static String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    public static void showErrorMessage(String message, Throwable t) {
        showErrorMessage(message + "\n" + t.getMessage());
    }

    public static void showErrorMessage(String message) {
        Window.alert(message);
    }

    /**
     * Store policy entity id in session
     * @param entityId Policy system id
     */
    public static void showPolicy(Long entityId) {

        String url = "/delegate/SessionDelegate?session_key=policyEntityId&session_value=" + entityId;
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {

                        Window.Location.assign("/web/ou/policies");
                    } else {
                        showErrorMessage(SERVER_ERROR + ":" + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable t) {
                    showErrorMessage(SERVER_ERROR, t);
                }

            });
        } catch (RequestException e) {
            showErrorMessage(SERVER_ERROR, e);
        }
    }

    public static String sessionStateIdBuilder(String id) {
        return "session-state-id-" + id;
    }

    public static List<String> toListArray(String... listItems) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < listItems.length; i++) {
            if (listItems[i] != null && !"".equals(listItems[i]))
            list.add(listItems[i]);
        }
        return list;
    }

}
