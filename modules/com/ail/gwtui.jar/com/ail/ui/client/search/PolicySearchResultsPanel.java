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

import java.util.ArrayList;
import java.util.List;

import com.ail.ui.client.common.Style;
import com.ail.ui.client.common.UIUtil;
import com.ail.ui.client.common.component.AilButton;
import com.ail.ui.client.common.component.AilCellTable;
import com.ail.ui.client.common.component.AilHorizontalPanel;
import com.ail.ui.client.common.component.AilLabel;
import com.ail.ui.client.common.component.AilPager;
import com.ail.ui.client.common.component.AilSimpleFlexTable;
import com.ail.ui.client.common.component.AilVerticalPanel;
import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.ail.ui.shared.validation.FieldVerifier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;

public final class PolicySearchResultsPanel extends AilHorizontalPanel {

    private static final String ROW_HIGHLIGHT_STYLE = "gui-highlight-result";

    public static final int RESULTS_PAGE_SIZE = 15;

    private final Messages messages = GWT.create(Messages.class);
    protected final SearchServiceAsync searchService = GWT.create(SearchService.class);

    private final AilVerticalPanel resultsListPanel = new AilVerticalPanel();
    private final AilSimpleFlexTable resultsDetailGrid = new AilSimpleFlexTable(145, 280, true);

    final NoSelectionModel<PolicyDetailDTO> selectionModel = new NoSelectionModel<>();
    private final ListDataProvider<PolicyDetailDTO> dataProvider = new ListDataProvider<>();
    private SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
    private AilPager pager = new AilPager(pagerResources);

    private AilCellTable<PolicyDetailDTO> resultsTable = new AilCellTable<>();
    private AilLabel messageLabel = new AilLabel("", Style.STATNDARD_TEXT);

    private AdvancedSearchPanel callback;

    public PolicySearchResultsPanel(AdvancedSearchPanel callback) {
        this.callback = callback;
        setup();
    }

    public PolicySearchResultsPanel display() {

        pager.setDisplay(resultsTable);
        pager.setPageSize(RESULTS_PAGE_SIZE);
        messageLabel.setTextNormal(messages.fetchingResults());

        this.setHeight("410px");
        return this;
    }

    public void clearResults() {
        dataProvider.setList(new ArrayList<PolicyDetailDTO>());
        resultsDetailGrid.removeAllRows();
    }

    /**
     * Set results
     * @param results
     */
    public void setResults(List<PolicyDetailDTO> results, String setSelected) {

        dataProvider.setList(results);

        if (results.size() == 0) {
            messageLabel.setTextRed(messages.noResultsFound());
        } else {
            messageLabel.setTextNormal("");
        }


        if (!"".equals(setSelected)) {
            for (int i = 0 ; i < results.size() ; i ++) {
                PolicyDetailDTO row = results.get(i);

                if (setSelected.equals(row.getSystemId() +"")) {

                    pager.setPage(i / RESULTS_PAGE_SIZE);
                    setSelected(row.getSystemId());
                    setHighlight(row.getSystemId());
                    break;
                }
            }
        } else {
            pager.setPage(0);
        }
    }


    private void setup() {

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {

                return  (FieldVerifier.hasLength(detail.getStatus()) ? detail.getStatus() : " - ");
            }
        }, messages.status());

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {

                return (FieldVerifier.hasLength(detail.getPolicyNumber()) ? detail.getPolicyNumber() :
                                (FieldVerifier.hasLength(detail.getQuotationNumber()) ? detail.getQuotationNumber() : detail.getSystemId() + ""));
            }
        }, messages.id());

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {

                return detail.getRenewalIndex();
            }
        }, "R");


        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {

                return detail.getMtaIndex();
            }
        }, "M");

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {
                return detail.getClientName();
            }

        }, messages.name());

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {

                return detail.getProduct();
            }
        }, messages.product());

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {
                return detail.getCreatedDate();
            }
        }, "Created");

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {
                return detail.getInceptionDate();
            }
        }, "Start");

        resultsTable.addColumn(new TextColumn<PolicyDetailDTO>() {

            @Override
            public String getValue(PolicyDetailDTO detail) {
                return detail.getExpiryDate();
            }
        }, "End");

        resultsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                //removeSelectedPolicy();
                removeHighlight();
                resultsDetailGrid.removeAllRows();
                resultsDetailGrid.addRowHeader("Loading...");

                final PolicyDetailDTO selectedPolicy = selectionModel.getLastSelectedObject();

                setHighlight(selectedPolicy.getSystemId());
                disableTabs();
                        getPolicyDetail(selectedPolicy.getSystemId() + "", selectedPolicy.getStatus());

            }

        });

        resultsTable.setSelectionModel(selectionModel);
        dataProvider.addDataDisplay(resultsTable);

        resultsListPanel.add(resultsTable);
        resultsListPanel.add(messageLabel);
        resultsListPanel.add(pager);

        add(resultsListPanel);
        add(resultsDetailGrid);
    }

    public void getPolicyDetail(final String policySystemId, final String policyStaus) {

        setSelectedPolicyId(policySystemId, policyStaus);


        searchService.policyIdSearch(policySystemId, -1L, new AsyncCallback<PolicyDetailDTO>() {

            @Override
            public void onSuccess(PolicyDetailDTO detail) {
                setPolictDetailGrid(detail);
            }

            @Override
            public void onFailure(Throwable t) {
                UIUtil.showErrorMessage(messages.serverError(), t);
            }
        });
    }

    private void setPolictDetailGrid(final PolicyDetailDTO detail) {

        resultsDetailGrid.removeAllRows();

        StringBuilder contactInto = new StringBuilder(detail.getClientName());
        for (int i = 0; i < detail.getClientAddress().size(); i++) {
            contactInto.append(", " + detail.getClientAddress().get(i));
        }

        resultsDetailGrid.addRow(messages.status(), detail.getStatus() + (detail.getSupersededBy() != -1 ? " / SUPERSEDED (" + detail.getSupersededBy() + ")" : ""));
        resultsDetailGrid.addRow(messages.quoteNumber(), detail.getQuotationNumber());
        resultsDetailGrid.addRow(messages.policyNumber(), getPolicyIdPanel(detail));
        resultsDetailGrid.addRow("Ext. Ref", detail.getExternalRef());
        resultsDetailGrid.addRow(messages.policyId(), detail.getSystemId() + "");
        resultsDetailGrid.addRow(messages.clientId(), getClientPanel(detail.getClientId()));
        resultsDetailGrid.addRow(messages.address(), contactInto.toString());
        resultsDetailGrid.addRow(messages.emailAddress(), detail.getEmail());
        resultsDetailGrid.addRow(messages.phone(), detail.getPhone());
        resultsDetailGrid.addRow(messages.product(), detail.getProduct());
    }

    private AilButton getClientPanel(final String clientId) {

        AilButton clientLink = new AilButton(clientId, Style.BUTTON_TO_LINK, Style.STATNDARD_TEXT);
        clientLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                callback.doClientAllSearch(clientId);
            }
        });

        return clientLink;
    }

    private AilButton getPolicyIdPanel(final PolicyDetailDTO detail) {

        String policyNumber = detail.getPolicyNumber();
        if (policyNumber != null && policyNumber.length() > 0) {
            if (detail.getRenewalIndex().length() > 0) {
                policyNumber += " / " + detail.getRenewalIndex();
            }
            if (detail.getMtaIndex().length() > 0) {
                policyNumber += " / " + detail.getMtaIndex();
            }
        }

        AilButton clientLink = new AilButton(policyNumber, Style.BUTTON_TO_LINK, Style.STATNDARD_TEXT);
        clientLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                callback.doPolicyAllSearch(detail.getPolicyNumber());
            }
        });

        return clientLink;
    }



    private void setSelected(long storedSystemId) {
        selectionModel.setSelected(
                getSelectedRow(storedSystemId), true);
    }



    private PolicyDetailDTO getSelectedRow(long id) {
        Range range = resultsTable.getVisibleRange();
        int start = range.getStart();
        for (int i = 0; i < resultsTable.getPageSize(); i++) {
            PolicyDetailDTO row = dataProvider.getList().get(start + i);
            if (row.getSystemId() == id){
                return row;
            }
        }
        return new PolicyDetailDTO();
    }

    private void setHighlight(long storedSystemId) {
        Range range = resultsTable.getVisibleRange();
        int start = range.getStart();
        for (int i = 0; i < resultsTable.getPageSize(); i++) {
            PolicyDetailDTO row = dataProvider.getList().get(start + i);
            if (row.getSystemId() == storedSystemId){
                resultsTable.getRowElement(i).addClassName(ROW_HIGHLIGHT_STYLE);
                break;
            }
        }
    }

    private native void setSelectedPolicyId(String id, String status) /*-{

        $wnd.psysid=id;
        $wnd.polsts=status;
        $wnd.enableTabs();
    }-*/;

    private native void disableTabs() /*-{
        $wnd.disableTabs();
    }-*/;

    private native void removeHighlight() /*-{
        $wnd.removeResultHighlight();
    }-*/;
}
