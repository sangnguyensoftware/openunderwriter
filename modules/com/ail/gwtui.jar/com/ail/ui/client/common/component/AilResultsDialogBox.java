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

import com.ail.ui.client.common.i18n.Messages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;

public class AilResultsDialogBox extends DialogBox {

    private final Messages messages = GWT.create(Messages.class);

    private final AilVerticalPanel mainPanel = new AilVerticalPanel();
    private final AilVerticalPanel resultDialogPanel = new AilVerticalPanel();
    private final AilHorizontalPanel closeButtonPanel = new AilHorizontalPanel();
    private final AilButton closeButton = new AilButton(messages.close());
    
    private boolean status;
    
    private ResultsDialog caller;
    
    public AilResultsDialogBox() {
    }
    
    public AilResultsDialogBox(ResultsDialog caller, String title) {
        this.caller = caller;
        setText(title.toUpperCase());
        setup();
    }

    private void setup() {
        
        closeButtonPanel.add(closeButton);
        closeButtonPanel.getElement().setId("gui-quicksearch-dialog-close-button");
        
        
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                status = false;
                hide();
                caller.onDialogClose(); 
            }
        });
        
        setAnimationEnabled(true);
        setGlassEnabled(true);
        
        mainPanel.getElement().setId("gui-quicksearch-dialog");
        mainPanel.add(resultDialogPanel);
        mainPanel.add(closeButtonPanel);
        
        setWidget(mainPanel);
    }
    
    public void showResults(Panel resultPanel) {
        if (status) {
            resultDialogPanel.clear();
            resultDialogPanel.add(resultPanel);
            center();
        }
    }
    
    public void showDialog() {
        status = true;
        resultDialogPanel.clear();
        resultDialogPanel.add(new AilLabel(messages.fetchingResults()));
        closeButton.setFocus(true);
        center();
    }
    
    
    
}
