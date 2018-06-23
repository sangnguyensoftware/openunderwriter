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
package com.ail.ui.server.search;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.ail.core.language.I18N.i18n;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.product.ListProductsService.ListProductsCommand;
import com.ail.core.product.ProductDetails;
import com.ail.core.transformer.Transformers;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.search.PolicySearchService.PolicySearchCommand;
import com.ail.ui.client.search.SearchService;
import com.ail.ui.server.common.CommandArgUtil;
import com.ail.ui.server.transformer.PolicyDetailTransformer;
import com.ail.ui.server.transformer.PolicySummaryDetailTransformer;
import com.ail.ui.shared.ServiceException;
import com.ail.ui.shared.model.AdvancedSearchCriteria;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.ail.ui.shared.validation.FieldVerifier;
import com.ail.ui.shared.viewer.ViewerCommand;
import com.ail.ui.shared.viewer.ViewerCommand.ViewerId;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the Quick Search RPC service.
 */
@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService {

    private static final String SCREEN_STATE = "gwtui-screen-state-";

    private static final int MAX_RESULTS_SIZE = 250;

    /**
     * Servlet request called by client
     *
     * @param search
     *            Search string
     * @return Populated PolicyDetailDTO
     */
    @Override
    public PolicyDetailDTO policyIdSearch(String id, Long userId) throws ServiceException {

        if (!FieldVerifier.hasLength(id)) {
            throw new ServiceException(this.getClass().getName() + ":Invalid policy id", new IllegalArgumentException());
        }
        Long systemId = Long.valueOf(id);

        CoreProxy core = new CoreProxy();
        Policy policy = (Policy) core.queryUnique("get.policy.by.systemId", systemId);

        try {
            return new PolicyDetailTransformer().apply(policy);
        } catch (Exception e) {
            core.logError(this.getClass().getName() , e);
            throw new ServiceException(e);
       }
    }

    /**
     * Servlet request called by client
     *
     * @param search
     *            Search string
     * @return Populated PolicyDetailDTO
     */
    @Override
    public Collection<PolicyDetailDTO> advancedSearch(AdvancedSearchCriteria criteria) throws ServiceException {
        return doSearch(criteria);
    }

    private Collection<PolicyDetailDTO> doSearch(AdvancedSearchCriteria criteria) {
        CoreProxy core =  new CoreProxy();

        try {
            PolicySearchCommand command = CommandArgUtil.getPolicySearchCommand(criteria, core);

            command.invoke();

            List<PolicyDetailDTO> results = new Transformers<Policy, PolicyDetailDTO>().transform(
                                    command.getPoliciesRet(), new PolicySummaryDetailTransformer(), MAX_RESULTS_SIZE);

            setSessionVariable(SESSION_ADVANCED_SEARCH_RESULTS, results); // store results in session
            return results;

        } catch (Exception e) {
            core.logError(this.getClass().getName() , e);
            throw new ServiceException(e);
       }
    }

    @Override
    public ViewerId getMode() throws ServiceException {
        String mode = ((PortletConfig) getThreadLocalRequest().getAttribute("javax.portlet.config")).getInitParameter("mode");

        return ViewerCommand.ViewerId.valueOf(mode);
    }

    @Override
    public List<String> getProducts()  throws ServiceException {
        CoreProxy coreProxy = new CoreProxy();
        ListProductsCommand listProductsCommand = coreProxy.newCommand(ListProductsCommand.class);
        try {
            listProductsCommand.invoke();
        } catch (BaseException e) {
            coreProxy.logError(this.getClass().getName(), e);
            throw new ServiceException(e);
        }
        return extract(listProductsCommand.getProductsRet(), on(ProductDetails.class).getName());
    }

    @Override
    public <T> Void setSessionVariable(String name, List<T> var) throws ServiceException {
        getThreadLocalRequest().getSession().setAttribute(name, var);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getSessionVariable(String name)  throws ServiceException{
        return  (List<T>)getThreadLocalRequest().getSession().getAttribute(name);
    }

    @Override
    public Void storeScreenState(String screenId, Map<String, String> state) throws ServiceException {
        getThreadLocalRequest().getSession().setAttribute(SCREEN_STATE + screenId, state);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> retrieveScreenState(String screenId) throws ServiceException {
        Map<String, String> state = (Map<String, String>) getThreadLocalRequest().getSession().getAttribute(SCREEN_STATE + screenId);
        if (state == null) {
            state = new HashMap<>();
        }
        return state;
    }

    @Override
    public Map<String,String> getPolicyStatuses() throws ServiceException {
        Map<String,String> statuses = new LinkedHashMap<>();

        statuses.put(ALL_POLICY_STATUS_CODE, i18n(ALL_POLICY_STATUS_CODE));

        for(PolicyStatus status: PolicyStatus.values()) {
            statuses.put(status.getLongName(), i18n(status.getLongName()));
        }

        return statuses;
    }
}
