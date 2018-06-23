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

import com.ail.ui.client.common.component.AilSimpleFlexTable;
import com.ail.ui.client.common.component.AilVerticalPanel;
import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.core.client.GWT;


/**
 * Search panel results to show retrieved data or 'no results found' message
 */
public class SearchResultPanel extends AilVerticalPanel {

    private final Messages messages = GWT.create(Messages.class);
    
    private AilSimpleFlexTable grid = new AilSimpleFlexTable(130, 230, true);
    
    private PolicyDetailDTO detail;

    public SearchResultPanel(PolicyDetailDTO detail) {
        this.detail = detail;
    }

    public SearchResultPanel display() {
        
        grid.addRow(messages.quoteNumber(), detail.getQuotationNumber());
        grid.addRow(messages.policyNumber(), detail.getPolicyNumber());
        grid.addRow(messages.name(), detail.getClientName());
        
        if (!detail.getClientAddress().isEmpty()) {
            grid.addRow(messages.address(), detail.getClientAddress().get(0)); // address first line
            for (int i = 1; i < detail.getClientAddress().size(); i++) {
                grid.addRow("", detail.getClientAddress().get(i)); // rest of address
            }
        }
        grid.addRow(messages.quoteDate(), detail.getQuoteDate());
        grid.addRow(messages.expiryDate(), detail.getExpiryDate());
        grid.addRow(messages.product(), detail.getProduct());
        grid.addRow(messages.grossPremium(), detail.getPremium());
        
        add(grid);
        
        return this;
    }

    
}
