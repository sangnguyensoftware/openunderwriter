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
package com.ail.ui.client.search;

import com.ail.ui.client.common.Style;
import com.ail.ui.client.common.component.AilButton;
import com.ail.ui.client.common.component.ResultsCallbackHandler;
import com.ail.ui.shared.viewer.ViewerCommand;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GWT entry point class for Quick Search.
 */
public class PolicySearch extends BaseSearch implements ResultsCallbackHandler {

    private static final String SHOW = "+";

    private static final String HIDE = "-";

    protected final SearchServiceAsync searchService = GWT.create(SearchService.class);

    private AdvancedSearchPanel advancedSearchPanel = new AdvancedSearchPanel(this);

    private AilButton showhide = new AilButton(SHOW, Style.SHOW_HIDE_BUTTON);

    /**
     * Called on load
     */
    @Override
    public void onModuleLoad() {
        doOnLoad(ViewerCommand.ViewerId.POLICYSEARCH);
        doOnLoadLocal();
    }


    private void doOnLoadLocal() {
        advancedSearchPanel.setVisible(false);

        RootPanel.get("gui-" + mode + "-advancedsearch-container").add(advancedSearchPanel.display());

        showhide.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                displayAdvancedSearch(isShow());
            }
        });

        RootPanel.get("gui-" + mode + "-advancedsearch-showhide").add(showhide);
    }

    private boolean isShow() {
        return SHOW.equals(showhide.getText());
    }

    private void displayAdvancedSearch(boolean show) {
        advancedSearchPanel.setVisible(show);
        if (show) {
            showhide.setText(HIDE);
        } else {
            showhide.setText(SHOW);
        }

    }

    @Override
    public void onResultsFound() {
        if (!isShow()) {
            showhide.click();
        }
    }

    @Override
    public void onResultsNotFound() {
        if (isShow()) {
            showhide.click();
        }
    }

}
