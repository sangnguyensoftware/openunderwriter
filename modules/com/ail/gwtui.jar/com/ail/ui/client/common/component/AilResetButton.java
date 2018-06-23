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

import java.util.Iterator;

import com.ail.ui.client.common.component.ComponentState.ComponentStateCallbackHandler;
import com.ail.ui.client.common.i18n.Messages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Form reset button to reset any controls on given containers
 */
public class AilResetButton extends AilButton {

    private final Messages messages = GWT.create(Messages.class);
    
    String screenId = "";
    FlexTable[] containers;
    
    /**
     * 
     * @param screenId Screen id for session state management
     * @param containers Containers of controls to be reset
     */
    public AilResetButton(String screenId, FlexTable[] containers) {
        super();
        this.screenId = screenId;
        this.containers = containers;
        
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                resetContainers();
            }
        });
        
        setText(messages.reset());
    }
        
    private void resetContainers() {
        for (int i = 0; i < containers.length; i++) {
            resetWidgets(containers[i].iterator());
        }
        
        // Reset session state
        new ComponentState(
                new ComponentStateCallbackHandler() {
                    public void afterScreenStateStore() {}}, screenId, containers).storeState();
    }

    private void resetWidgets(Iterator<Widget> widgets) {
        while (widgets.hasNext()) {
            Widget widget = widgets.next();
            if (widget instanceof Panel) {
                resetWidgets(((Panel)widget).iterator());
            } else if (widget instanceof Resetable) {
                ((Resetable)widget).reset();
            }
        }
    }


}
