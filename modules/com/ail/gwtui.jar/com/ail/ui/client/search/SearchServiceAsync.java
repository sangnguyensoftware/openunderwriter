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
import java.util.Map;

import com.ail.ui.shared.ServiceException;
import com.ail.ui.shared.model.AdvancedSearchCriteria;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.ail.ui.shared.viewer.ViewerCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of SearchService.
 */
public interface SearchServiceAsync {

    void policyIdSearch(String input, Long userId, AsyncCallback<PolicyDetailDTO> callback)
            throws ServiceException;

    void advancedSearch(AdvancedSearchCriteria criteria, AsyncCallback<List<PolicyDetailDTO>> callback)
            throws ServiceException;

    void getMode(AsyncCallback<ViewerCommand.ViewerId> callback)
            throws ServiceException;

    void getProducts(AsyncCallback<List<String>> callback)
            throws ServiceException;

    void getPolicyStatuses(AsyncCallback<Map<String,String>> callback)
            throws ServiceException;

    <T> void setSessionVariable(String name, List<T> var, AsyncCallback<Void> callback)
            throws ServiceException;

    <T> void getSessionVariable(String name, AsyncCallback<List<T>> callback)
            throws ServiceException;

    void storeScreenState(String screenId, Map<String, String> state, AsyncCallback<Void> callback)
            throws ServiceException;

    void retrieveScreenState(String screenId, AsyncCallback<Map<String, String>> callback)
            throws ServiceException;

}
