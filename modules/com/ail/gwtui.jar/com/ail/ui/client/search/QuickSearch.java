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


import java.util.List;

import com.ail.ui.client.common.Style;
import com.ail.ui.client.common.UIUtil;
import com.ail.ui.client.common.component.AilButton;
import com.ail.ui.client.common.component.AilLabel;
import com.ail.ui.client.common.component.AilTextBox;
import com.ail.ui.client.common.component.ResultsDialog;
import com.ail.ui.shared.model.AdvancedSearchCriteria;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.ail.ui.shared.validation.FieldVerifier;
import com.ail.ui.shared.viewer.ViewerCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GWT entry point class for Quick Search.
 */
public class QuickSearch extends BaseSearch implements ResultsDialog {

    private final AilLabel validationLabel = new AilLabel(Style.ERROR_LABEL);

    private final AilButton searchButton = new AilButton(messages.search());
    private final AilTextBox searchField = new AilTextBox(Style.TEXT_BOX);

    private long userId = -1;
    private long companyId = -1;

	/**
	 * Called on load
	 */
	@Override
	public void onModuleLoad() {
	    doOnLoad(ViewerCommand.ViewerId.QUICKSEARCH);
        setJspElements();
        setComponentStyles();

        searchButton.setEnabled(false);

        getUserId();

	}

    @Override
    public void onDialogClose() {
    }

    private void doSearch() {
        resetControls();

        String searchInput = searchField.getText();

        if (!FieldVerifier.hasLength(searchInput)) {
            validationLabel.setText(messages.quickSearchValidation());
            return;
        }

        search(searchInput);
    }

    public String getTitle() {
        return messages.quickSearch();
    }

    public void search(final String search) {

        // Clear cached results and place new policy sys id in session
        searchService.setSessionVariable(SearchService.SESSION_ADVANCED_SEARCH_RESULTS, UIUtil.toListArray(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void v) {

                AdvancedSearchCriteria criteria = new AdvancedSearchCriteria(search, userId, companyId);

                searchService.advancedSearch(criteria, new AsyncCallback<List<PolicyDetailDTO>>() {

                    @Override
                    public void onSuccess(List<PolicyDetailDTO> result) {

                        if (result != null && !result.isEmpty()) {
                            UIUtil.showPolicy(result.get(0).getSystemId());
                        } else {
                            validationLabel.setText(messages.noResultsFound());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        UIUtil.showErrorMessage(messages.serverError(), t);
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                UIUtil.showErrorMessage(messages.serverError(), t);
            }

        });

    }

    public void getUserId() {

        String url = "/delegate/SessionDelegate?session_key=userId";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        builder.setHeader("Access-Control-Allow-Origin", "*");
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {

                    if (200 == response.getStatusCode()) {
                        String[] ids = response.getText().split(",");
                        companyId = Long.valueOf(ids[0]);
                        userId = Long.valueOf(ids[1]);

                        // Handler for the sendButton and searchField
                        class SearchHandler implements ClickHandler, KeyUpHandler {

                            @Override
                            public void onClick(ClickEvent event) {
                                doSearch();
                            }

                            /**
                             * Fire on Enter key.
                             */
                            @Override
                            public void onKeyUp(KeyUpEvent event) {
                                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                                    doSearch();
                                }
                            }
                        }

                        SearchHandler handler = new SearchHandler();
                        searchButton.addClickHandler(handler);
                        searchField.addKeyUpHandler(handler);
                        searchButton.setEnabled(true);
                    } else {
//                        UIUtil.showErrorMessage(UIUtil.SERVER_ERROR + ":" + response.getStatusCode());
                    }
                }

                @Override
                public void onError(Request request, Throwable t) {
                    UIUtil.showErrorMessage(UIUtil.SERVER_ERROR, t);
                }

            });
        } catch (RequestException e) {
            UIUtil.showErrorMessage(UIUtil.SERVER_ERROR, e);
        }
    }

    // clear error messages
    private void resetControls() {
        validationLabel.setText("");
    }

    private void setComponentStyles() {
        searchField.getElement().setId("gui-quick-search-input");
    }
    protected void setJspElements() {

        RootPanel.get("gui-" + mode + "-search-title").add(new AilLabel(getTitle()));
        RootPanel.get("gui-" + mode + "-search-field-container").add(searchField);
        RootPanel.get("gui-" + mode + "-search-button-container").add(searchButton);
        RootPanel.get("gui-" + mode + "-error-label-container").add(validationLabel);
    }

}



