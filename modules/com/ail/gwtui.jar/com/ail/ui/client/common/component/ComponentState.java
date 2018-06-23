/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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
package com.ail.ui.client.common.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ail.ui.client.search.SearchService;
import com.ail.ui.client.search.SearchServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ComponentState {

    protected final SearchServiceAsync sessionService = GWT.create(SearchService.class);

    private String screenId = "";
    private Map<String, String> componentState = new HashMap<String, String>();

    private FlexTable[] conatainerWidgets;

    public interface ComponentStateCallbackHandler {
        void afterScreenStateStore();
    }

    /**
     * Constructor
     * @param callback Callback component after store
     * @param screenId Screen id used to id session container
     * @param conatainerWidgets Container of stateful components
     */

    public ComponentState(ComponentStateCallbackHandler callback, String screenId, FlexTable[] conatainerWidgets) {
        this.screenId = screenId;
        this.conatainerWidgets = conatainerWidgets;
    }

    /**
     * Store state of stateful components in session
     */
    public void storeState() {

        for (int i = 0; i < conatainerWidgets.length; i++) {
            Iterator<Widget> widgets = conatainerWidgets[i].iterator();
            addStatefulWidgets(widgets);
        }

        sessionService.storeScreenState(screenId, componentState, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(final Void v) {
//                callback.afterScreenStateStore();
            }

            @Override
            public void onFailure(final Throwable t) {
//                UIUtil.showErrorMessage("Failed to save state", t);
//                callback.afterScreenStateStore();
            }
        });
    }

    private void addStatefulWidgets(Iterator<Widget> widgets) {
        while (widgets.hasNext()) {
            Widget widget = widgets.next();
            if (widget instanceof Panel) {
                addStatefulWidgets(((Panel)widget).iterator());
            } else if (widget instanceof SessionState) {
                final SessionState stateful = (SessionState)widget;
                if (stateful.isStatefull()) {
                   componentState.put(stateful.getStateId(), stateful.getState());
                }

            }
        }
    }

    /**
     * Retrieve component state from session and apply to components
     */
    public void restoreState() {

        final Set<SessionState> statefulComponents = new HashSet<SessionState>();

        for (int i = 0; i < conatainerWidgets.length; i++) {

            Iterator<Widget> widgets = conatainerWidgets[i].iterator();
            addStatefulComponents(widgets, statefulComponents);
        }

        sessionService.retrieveScreenState(screenId, new AsyncCallback<Map<String, String>>() {

            @Override
            public void onSuccess(Map<String, String> data) {

                for (Iterator<SessionState> components = statefulComponents.iterator(); components.hasNext();) {
                    SessionState component = components.next();
                    component.setState(data.get(component.getStateId()));
                }
            }

            @Override
            public void onFailure(Throwable t) {
//                UIUtil.showErrorMessage("Failed to restore state", t);
            }
        });
    }

    private void addStatefulComponents(Iterator<Widget> widgets, Set<SessionState> statefulComponents) {
        while (widgets.hasNext()) {
            Widget widget = widgets.next();
            if (widget instanceof Panel) {
                addStatefulComponents(((Panel)widget).iterator(), statefulComponents);
            } else if (widget instanceof SessionState) {

                SessionState stateful = (SessionState)widget;
                if (stateful.isStatefull()) {
                    statefulComponents.add(stateful);

                }
            }
        }
    }

}
