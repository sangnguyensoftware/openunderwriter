/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.pageflow;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicySummaries;

/**
 * <p>
 * Display a list of a user's saved quotations. If the user is logged in, a list
 * of their saved quotes is displayed with options to process them. If the user
 * is not logged in, an invitation to "login to view your saved quotes" is
 * displayed. This page element also manages the login process should the user
 * accept the invitation.
 * </p>
 * <p>
 * The following three screen shots show how the page is rendered when the user
 * is not logged in; when they are in the process of logging in; and listing
 * their quotes when they have logged in.
 * </p>
 * <p>
 * <img src="doc-files/SavedQuotations-1.png"/>
 * </p>
 * <p>
 * <img src="doc-files/SavedQuotations-2.png"/>
 * </p>
 * <p>
 * <img src="doc-files/SavedQuotations-3.png"/>
 * </p>
 * <p>
 * The quotations are listed with three options:
 * <ul>
 * <li><b>Confirm and Pay</b> This take the user to a page (specified by the
 * {@link #getConfirmAndPayDestinationPageId() cofirmAndPayDestinationPageId}
 * property. This could be any page in the flow, but the expectation is that
 * this takes the user to either select payment options, or enter payment
 * details as is appropriate.</li>
 * <li>
 * <li><b>Requote</b> Takes the user to the page specified by the
 * {@link #getRequoteDestinationPageId() requotePageDestination} property and in
 * doing so creates a new quotation based on the one selected.</li>
 * <li>View</li>View the quotation as a PDF. The system generates PDFs to
 * details a quote only on demand. If this button is selected, the quote PDF
 * will be generated if it has not already been done.</li>
 * </ul>
 */
public class SavedQuotations extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;

    /** Set to true to hide the requote button. */
    private boolean requoteDisabled;

    /** Set to true to hide the confirm and pay button. */
    private boolean confirmAndPayDisabled;

    /** Set to true to hide the view quotation button. */
    private boolean viewQuotationDisabled;

    /**
     * Id of the page to forward to in the pageflow if the user selected
     * "requote"
     */
    private String requoteDestinationPageId;

    /**
     * Id of the page to forward to in the pageflow if the user selected
     * "confirm and pay"
     */
    private String confirmAndPayDestinationPageId;

    /** Label to appear on the confirm button. Defaults to "Confirm and Pay" */
    private String confirmAndPayLabel = "i18n_saved_quotations_confirm_button_label";

    /** Label to appear on the requote button. Defaults to "Requote" */
    private String requoteLabel = "i18n_saved_quotations_requote_button_label";

    /** Button to handle the "view quote" action. */
    private transient ViewQuotationButtonAction viewQuotationButtonAction = null;

    /** Button to handle requote action. */
    private transient RequoteButtonAction requoteButtonAction = null;

    public SavedQuotations() {
        super();
    }

    /**
     * Id of the page to forward to in the pageflow if the user selected
     * "confirm and pay".
     *
     * @return Page ID
     */
    public String getConfirmAndPayDestinationPageId() {
        return confirmAndPayDestinationPageId;
    }

    /**
     * @see #getConfirmAndPayDestinationPageId()
     * @param confirmAndPayDestinationPageId
     *            Page ID
     */
    public void setConfirmAndPayDestinationPageId(String confirmAndPayDestinationPageId) {
        this.confirmAndPayDestinationPageId = confirmAndPayDestinationPageId;
    }

    /**
     * Id of the page to forward to in the pageflow if the user selected
     * "requote"
     *
     * @return Page ID
     */
    public String getRequoteDestinationPageId() {
        return requoteDestinationPageId;
    }

    /**
     * @see #getRequoteDestinationPageId()
     * @param requoteDestinationPageId
     */
    public void setRequoteDestinationPageId(String requoteDestinationPageId) {
        this.requoteDestinationPageId = requoteDestinationPageId;
    }

    /**
     * Button to handle the "view quote" action.
     *
     * @return View button
     */
    public ViewQuotationButtonAction getViewQuotationButtonAction() {
        if (viewQuotationButtonAction == null) {
            viewQuotationButtonAction = new ViewQuotationButtonAction();
        }

        return viewQuotationButtonAction;
    }

    /**
     * Button to handle the "requote" action.
     *
     * @return View button
     */
    public RequoteButtonAction getRequoteButtonAction() {
        if (requoteButtonAction == null) {
            requoteButtonAction = new RequoteButtonAction();
            requoteButtonAction.setDestinationPageId(requoteDestinationPageId);
        }

        return requoteButtonAction;
    }

    @Override
    public Type applyRequestValues(Type model) {
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        return false;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        CoreProxy core = PageFlowContext.getCoreProxy();
        RequestWrapper request = CoreContext.getRequestWrapper();
        Properties opParams = PageFlowContext.getOperationParameters();
        String op = PageFlowContext.getRequestedOperation();

        if (isRequestFromLoggedInUser(request)) {

            String id = opParams.getProperty("id");

            if (id != null) {
                Policy policy = (Policy) core.queryUnique("get.policy.by.systemId", new Long(id));

                policy = (Policy) getViewQuotationButtonAction().processActions(policy);

                policy = (Policy) getRequoteButtonAction().processActions(policy);

                if ("confirm".equals(op)) {
                    PageFlowContext.getPageFlow().setNextPage(confirmAndPayDestinationPageId);
                }

                PageFlowContext.setPolicy(policy);

                PageFlowContext.flagActionAsProcessed();

                return policy;
            }
        } else if ("login".equals(op) && request.getUserPrincipal() == null) {
            // We're performing a save and the user isn't logged in yet.
            String password = request.getParameter("password");
            String username = request.getParameter("username");
            String url = "Unknown";

            try {
                String pageName = PageFlowContext.getOperationParameters().getProperty("page");
                String portalName = PageFlowContext.getOperationParameters().getProperty("portal");
                url = "/portal/auth/portal/" + portalName + "/" + pageName + "/QuoteWindow?action=1&username=" + username + "&password=" + password;
                CoreContext.getResponseWrapper().sendRedirect(url);
            } catch (Exception e) {
                throw new RenderingError("Page forward on login failed. Forward was to: " + url, e);
            }

            PageFlowContext.flagActionAsProcessed();
        }

        return model;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        RequestWrapper request = CoreContext.getRequestWrapper();

        // If the user is logged in...
        if (isRequestFromLoggedInUser(request)) {
            // get a list of the user's saved quotes.
            Long user = PageFlowContext.getRemoteUser();
            String product = PageFlowContext.getProductName();
            List<?> policies = PageFlowContext.getCoreProxy().query("get.policy.by.user.and.product", user, product);

            // If the user has saved quotes...
            if (policies.size() != 0) {
                // copy the quotations summaries into a SavedPolicySummary
                // instance
                PolicySummaries summaries = PageFlowContext.getCoreProxy().newType(PolicySummaries.class);
                for (Object policy : policies) {
                    summaries.getPolicies().add((Policy) policy);
                }

                executeTemplateCommand("SavedQuotations", summaries);
            }
        }

        return model;
    }

    /**
     * @see #setConfirmAndPayLabel(String)
     * @return the confirmAndPayLabel
     */
    public String getConfirmAndPayLabel() {
        return confirmAndPayLabel;
    }

    /**
     * Set the label to appear on the confirm button. The default is
     * "Confirm and Pay".
     *
     * @param confirmAndPayLabel
     *            the confirmAndPayLabel to set
     */
    public void setConfirmAndPayLabel(String confirmAndPayLabel) {
        this.confirmAndPayLabel = confirmAndPayLabel;
    }

    /**
     * @return the requoteLabel
     */
    public String getRequoteLabel() {
        return requoteLabel;
    }

    /**
     * Set the label to appear on the requote button. The default is "Requote"
     *
     * @param requoteLabel
     *            the requoteLabel to set
     */
    public void setRequoteLabel(String requoteLabel) {
        this.requoteLabel = requoteLabel;
    }

    public boolean isRequoteDisabled() {
        return requoteDisabled;
    }

    public void setRequoteDisabled(boolean requoteDisabled) {
        this.requoteDisabled = requoteDisabled;
    }

    public boolean isConfirmAndPayDisabled() {
        return confirmAndPayDisabled;
    }

    public void setConfirmAndPayDisabled(boolean confirmAndPayDisabled) {
        this.confirmAndPayDisabled = confirmAndPayDisabled;
    }

    public boolean isViewQuotationDisabled() {
        return viewQuotationDisabled;
    }

    public void setViewQuotationDisabled(boolean viewQuotationDisabled) {
        this.viewQuotationDisabled = viewQuotationDisabled;
    }

    private boolean isRequestFromLoggedInUser(RequestWrapper request) {
        return PageFlowContext.getRemoteUser() != null;
    }
}
