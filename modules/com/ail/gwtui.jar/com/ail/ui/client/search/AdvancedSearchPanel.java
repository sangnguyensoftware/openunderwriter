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
package com.ail.ui.client.search;

import static com.ail.ui.client.common.UIUtil.DATE_FIELD_FORMAT;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ail.ui.client.common.Style;
import com.ail.ui.client.common.UIUtil;
import com.ail.ui.client.common.component.AilButton;
import com.ail.ui.client.common.component.AilCheckBox;
import com.ail.ui.client.common.component.AilDateRangePanel;
import com.ail.ui.client.common.component.AilHorizontalPanel;
import com.ail.ui.client.common.component.AilLabel;
import com.ail.ui.client.common.component.AilResetButton;
import com.ail.ui.client.common.component.AilSimpleFlexTable;
import com.ail.ui.client.common.component.AilSimpleListBox;
import com.ail.ui.client.common.component.AilTextBox;
import com.ail.ui.client.common.component.AilVerticalPanel;
import com.ail.ui.client.common.component.ComponentState;
import com.ail.ui.client.common.component.ComponentState.ComponentStateCallbackHandler;
import com.ail.ui.client.common.component.ResultsCallbackHandler;
import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.model.AdvancedSearchCriteria;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

public class AdvancedSearchPanel extends AilVerticalPanel implements ComponentStateCallbackHandler {
    protected final SearchServiceAsync searchService = GWT.create(SearchService.class);

    private final Messages messages = GWT.create(Messages.class);

    // Paging session indexes
    private static final int PAGE_INDEX = 0;
    private static final int SELECTED_INDEX = 1;

    private static final String BUTTON_WIDTH = "65px";

    private long userId = -1;
    private long companyId = -1;

    private static final String SCREEN_ID = "advanced-policy-search";

    private final AilHorizontalPanel criteriaPanel = new AilHorizontalPanel();
    private final AilHorizontalPanel filterCheckboxPanel = new AilHorizontalPanel();
    private final AilSimpleFlexTable policyDetailsLayoutGrid = new AilSimpleFlexTable(80, 230, true);
    private final AilSimpleFlexTable policyDatesLayoutGrid = new AilSimpleFlexTable(80, 230, true);
    private final AilSimpleFlexTable clientDetailsLayoutGrid = new AilSimpleFlexTable(80, 230, true);
    private final AilSimpleFlexTable controlLayoutGrid = new AilSimpleFlexTable(80, 230, true);
    private final AilSimpleFlexTable supersededFilterLayoutGrid = new AilSimpleFlexTable(80, 64, true);
    private final AilSimpleFlexTable testFilterLayoutGrid = new AilSimpleFlexTable(80, 64, true);

    private final AilTextBox policyId = new AilTextBox("policy-id-input");
    private final AilSimpleListBox products = new AilSimpleListBox("product-list");
    private final AilSimpleListBox statuses = new AilSimpleListBox("status-list", true);

    private final AilDateRangePanel createdDatePeriod = new AilDateRangePanel("created-date-period");
    private final AilDateRangePanel quoteDatePeriod = new AilDateRangePanel("quote-date-period");
    private final AilDateRangePanel inceptionDatePeriod = new AilDateRangePanel("inception-date-period");
    private final AilDateRangePanel expiryDatePeriod = new AilDateRangePanel("expiry-date-period");

    private final AilTextBox clientId = new AilTextBox("client-id-input");
    private final AilTextBox clientName = new AilTextBox("client-name-input");
    private final AilTextBox clientAddress = new AilTextBox("client-address-input");
    private final AilTextBox clientEmailAddress = new AilTextBox("client-emailaddress-input");

    private final AilButton searchAllButton = new AilButton("Search");
    private final AilResetButton resetButton = new AilResetButton(SCREEN_ID, getContainers());

    private final AilCheckBox includeTest = new AilCheckBox("include-test");
    private final AilCheckBox includeSuperseded = new AilCheckBox("include-superseded");

    private final AilSimpleListBox orderBy = new AilSimpleListBox("order-by-list",
            UIUtil.toListArray("[Sort By]", "Status ASC","Status DESC","Product ASC","Product DESC", "Created Date ASC","Created Date DESC", "Quote Date ASC",
                                            "Quote Date DESC", "Quote Expiry Date ASC", "Quote Expiry Date DESC", "Policy Number ASC", "Policy Number DESC"));

    private final PolicySearchResultsPanel resultsPanel = new PolicySearchResultsPanel(this);

    private ResultsCallbackHandler callback;

    public AdvancedSearchPanel(ResultsCallbackHandler callback) {
        this.callback = callback;
    }

    public AdvancedSearchPanel display() {
        initControls();
        policyDetailsLayoutGrid.addRowHeader(new AilLabel(messages.policyDetails().toUpperCase(), Style.SEARCH_HEADER_LABEL));
        policyDetailsLayoutGrid.addRow(messages.id(), policyId);
        policyDetailsLayoutGrid.addRow(messages.product(), products);
        policyDetailsLayoutGrid.addRow(messages.status(), statuses);

        policyDatesLayoutGrid.addRowHeader(new AilLabel(messages.policyDates().toUpperCase(), Style.SEARCH_HEADER_LABEL));
        policyDatesLayoutGrid.addRow(messages.created(), createdDatePeriod.display());
        policyDatesLayoutGrid.addRow(messages.quote(), quoteDatePeriod.display());
        policyDatesLayoutGrid.addRow(messages.inception(), inceptionDatePeriod.display());
        policyDatesLayoutGrid.addRow(messages.expiry(), expiryDatePeriod.display());

        clientDetailsLayoutGrid.addRowHeader(new AilLabel(messages.proposerHolderDetails().toUpperCase(), Style.SEARCH_HEADER_LABEL));
        clientDetailsLayoutGrid.addRow(messages.id(), clientId);
        clientDetailsLayoutGrid.addRow(messages.name(), clientName);
        clientDetailsLayoutGrid.addRow(messages.address(), clientAddress);
        clientDetailsLayoutGrid.addRow(messages.emailAddress(), clientEmailAddress);

        controlLayoutGrid.addRow(resetButton, getControlPanel());
        supersededFilterLayoutGrid.addRow("Superseded", includeSuperseded);
        testFilterLayoutGrid.addRow("Test", includeTest);
        filterCheckboxPanel.add(supersededFilterLayoutGrid);
        filterCheckboxPanel.add(new AilLabel(Style.SPACER));
        filterCheckboxPanel.add(testFilterLayoutGrid);

        criteriaPanel.add(policyDetailsLayoutGrid);
        criteriaPanel.add(new AilLabel(Style.SPACER));
        criteriaPanel.add(clientDetailsLayoutGrid);
        criteriaPanel.add(new AilLabel(Style.SPACER));
        criteriaPanel.add(policyDatesLayoutGrid);

        add(criteriaPanel);
        add(new AilLabel(Style.SPACER));
        add(filterCheckboxPanel);
        add(new AilLabel(Style.SPACER));
        add(controlLayoutGrid);

        restoreState();

        return this;
    }

    private AilHorizontalPanel getControlPanel() {

        final AilHorizontalPanel controlPanel = new AilHorizontalPanel();

        orderBy.setWidth("156px");

        searchAllButton.setWidth(BUTTON_WIDTH);

        controlPanel.add(searchAllButton);
        controlPanel.add(new AilLabel(Style.SPACER));
        controlPanel.add(orderBy);

        return controlPanel;
    }

    private void initControls() {

        searchService.getPolicyStatuses(new AsyncCallback<Map<String,String>>() {
            @Override
            public void onSuccess(Map<String,String> result) {
                statuses.setModel(result);
                statuses.setDefaultState("1,2,3,4,5,6,7"); // all selected bar NOT_TAKEN_UP, UNUSED and DELETED
                statuses.setVisibleItemCount(4);
            }

            @Override
            public void onFailure(Throwable arg0) {
                statuses.failedToLoad();
            }
        });

        searchService.getProducts(new AsyncCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                products.setModel(result);
            }

            @Override
            public void onFailure(Throwable arg0) {
                products.failedToLoad();
            }
        });

        searchAllButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                doSearchAll();
            }
        });

        resetButton.setWidth(BUTTON_WIDTH);

        getUserId();
    }


    private void doSearchAll() {
        doSearch();
    }

    public void doClientAllSearch(String id) {
        resetButton.click();
        statuses.setSelectedIndex(0);
        clientId.setValue(id);
        includeSuperseded.setValue(true);
        doSearch();
    }

    public void doPolicyAllSearch(String policyNumber) {
        resetButton.click();
        statuses.setSelectedIndex(0);
        policyId.setValue(policyNumber);
        includeSuperseded.setValue(true);
        doSearch();
    }


    private void doSearch() {

        if (!createdDatePeriod.validate()
            || !quoteDatePeriod.validate()
            || !inceptionDatePeriod.validate()
            || !expiryDatePeriod.validate()) {

            UIUtil.showErrorMessage(messages.dateValidation());
            return;
        }

        removeSelectedPolicyId();

        new ComponentState(this, SCREEN_ID,
                getContainers()).storeState();

        afterScreenStateStore();
    }

    private String getToday() {
        JsDate jsDate = JsDate.create(new Date().getTime());
        return DateTimeFormat.getFormat(DATE_FIELD_FORMAT).format(new Date((long)jsDate.getTime()));
    }

    public void getUserId() {
        String auth = getAuth();
        String[] ids = auth.split(",");
        companyId = Long.valueOf(ids[0]);
        userId = Long.valueOf(ids[1]);

        searchAllButton.setEnabled(true);
    }

    private void restoreState() {

        new ComponentState(this, SCREEN_ID, getContainers()).restoreState();

        searchService.getSessionVariable(SearchService.SESSION_ADVANCED_SEARCH_RESULTS, new AsyncCallback<List<PolicyDetailDTO>>() {

            @Override
            public void onSuccess(final List<PolicyDetailDTO> searchResults) {

                if (searchResults != null && !searchResults.isEmpty()) {

                    displayResultsPanel();
                    resultsPanel.setResults(searchResults, getSelectedPolicyId());

                } else {
                    callback.onResultsNotFound();
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });


    }

    private void displayResultsPanel() {
        RootPanel resultsRoot = RootPanel.get("gui-POLICYSEARCH-advancedsearch-results");
        resultsPanel.clearResults();
        resultsRoot.clear();
        resultsRoot.add(resultsPanel.display());
    }

    @Override
    public void afterScreenStateStore() {

        displayResultsPanel();

        AdvancedSearchCriteria criteria = new AdvancedSearchCriteria(
                policyId.getValue(),
                products.getSelectedItem(),
                statuses.getSelectedItems(),
                createdDatePeriod.getDateFrom(),
                createdDatePeriod.getDateTo(),
                quoteDatePeriod.getDateFrom(),
                quoteDatePeriod.getDateTo(),
                inceptionDatePeriod.getDateFrom(),
                inceptionDatePeriod.getDateTo(),
                expiryDatePeriod.getDateFrom(),
                expiryDatePeriod.getDateTo(),
                clientId.getValue(),
                clientName.getValue(),
                clientAddress.getValue(),
                clientEmailAddress.getValue(),
                userId,
                companyId,
                includeTest.getValue(),
                includeSuperseded.getValue(),
                orderBy.getSelectedItem(),
                "ASC");

        criteria.setCachedResults(PolicySearchResultsPanel.RESULTS_PAGE_SIZE);

        searchService.advancedSearch(criteria, new AsyncCallback<List<PolicyDetailDTO>>() {

            @Override
            public void onSuccess(List<PolicyDetailDTO> result) {

                if (result != null && !result.isEmpty() ) {
                    callback.onResultsFound();
                }
                resultsPanel.setResults(result, "");
            }

            @Override
            public void onFailure(Throwable t) {
                UIUtil.showErrorMessage(messages.serverError(), t);
            }
        });
    }

    private FlexTable[] getContainers() {
        return new FlexTable[]{
                        policyDetailsLayoutGrid,
                        policyDatesLayoutGrid,
                        clientDetailsLayoutGrid,
                        controlLayoutGrid,
                        supersededFilterLayoutGrid,
                        testFilterLayoutGrid};
    }

    public native String getAuth() /*-{
        return $wnd.comusr;
    }-*/;

    private native String getSelectedPolicyId() /*-{
        return $wnd.psysid;
    }-*/;

    private native void removeSelectedPolicyId() /*-{
        $wnd.psysid='';
        $wnd.disableTabs();
    }-*/;

}

