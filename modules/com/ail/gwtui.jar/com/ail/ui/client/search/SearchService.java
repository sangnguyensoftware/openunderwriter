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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ail.ui.shared.ServiceException;
import com.ail.ui.shared.model.AdvancedSearchCriteria;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.ail.ui.shared.viewer.ViewerCommand;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the Quick Search RPC service.
 */
@RemoteServiceRelativePath("quickSearch")
public interface SearchService extends RemoteService {

    public static final String ALL_POLICY_STATUS_CODE = "i18n_policy_search_all";

    String SESSION_ADVANCED_SEARCH_RESULTS = "advancedSearchResults";


    /**
     * Servlet request called by client
     *
     * @param search Search string
     * @param userId
     * @return Populated PolicyDetailDTO
     */
    PolicyDetailDTO policyIdSearch(String search, Long userId) throws ServiceException;

    /**
     * Get viewer mode
     * @return
     * @throws ServiceException
     */
    ViewerCommand.ViewerId getMode()  throws ServiceException;

    /**
     * Get list of policy status. Key is OU's i18n code for the status, value is the language specific translation.
     * @return
     */
    Map<String,String> getPolicyStatuses() throws ServiceException;

    /**
     * Get list of products
     * @return
     */
    List<String> getProducts() throws ServiceException;

    /**
     * Get list of policy details base on advanced search criteria
     * @param criteria
     * @return
     * @throws ServiceException
     */
    Collection<PolicyDetailDTO> advancedSearch(AdvancedSearchCriteria criteria) throws ServiceException;

    /**
     * Set values in the session
     * @param name Session attribute id
     * @param vars List of values to store
     * @return
     * @throws ServiceException
     */
    <T> Void setSessionVariable(String name, List<T> vars) throws ServiceException;

    /**
     * Get session values
     * @param name
     * @return
     * @throws ServiceException
     */
    <T> List<T> getSessionVariable(String name) throws ServiceException;

    /**
     * Store component state in session
     * @param screenId
     * @param state
     * @return
     * @throws ServiceException
     */
    Void storeScreenState(String screenId, Map<String, String> state) throws ServiceException;

    /**
     * Retrieve component state from session
     * @param screenId
     * @return
     * @throws ServiceException
     */
    Map<String, String> retrieveScreenState(String screenId) throws ServiceException;


}
