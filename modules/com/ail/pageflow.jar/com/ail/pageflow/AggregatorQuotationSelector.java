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

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.Functions.loadUrlContentAsString;
import static com.ail.pageflow.PageFlow.QUOTATION_PAGE_FLOW_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.DisaggregatePolicyService.DisaggregatePolicyCommand;
/**

 */
public class AggregatorQuotationSelector extends PageContainer {
    private static final long serialVersionUID = -4810599255559321748L;

    private String featureHtml = "~/HTML/ProductFeatures.html";
    private String destinationPageId = "QuotationSelected";
    private Boolean hideReferrals = Boolean.TRUE;
    private Boolean hideDeclines = Boolean.TRUE;

    /**
     * Default constructor
     */
    public AggregatorQuotationSelector() {
        super();
    }

    public boolean isSheetIncludedInResults(AssessmentSheet sheet) {
        if (sheet == null) {
            return false;
        }

        if (sheet.isMarkedForRefer() && hideReferrals) {
            return false;
        }

        if (sheet.isMarkedForDecline() && hideDeclines) {
            return false;
        }

        return true;
    }

    public String featureContentFor(String productName) {
        String content;

        try {
            Policy policy = PageFlowContext.getPolicy();

            URL contentURL = new URL(expandRelativeUrlToProductUrl(getFeatureHtml(), productName));

            String rawContent = loadUrlContentAsString(contentURL);

            content = expand(rawContent, policy, policy);
        }
        catch(Throwable e) {
            PageFlowContext.getCoreProxy().logError("Failed to load aggregator feature content for: "+productName, e);
            content = "Content not found";
        }

        return content;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        Policy policy = (Policy)model;
        Properties params = PageFlowContext.getOperationParameters();
        String op = PageFlowContext.getRequestedOperation();

        if ("select".equals(op)) {
            String product = params.getProperty("product");

            policy = disaggregatePolicy(model, product);

            policy.setPage(QUOTATION_PAGE_FLOW_NAME, destinationPageId);

            PageFlowContext.selectProductName(product);
            PageFlowContext.setPageFlowName(QUOTATION_PAGE_FLOW_NAME);
            PageFlowContext.setPageFlow(PageFlowContext.getCoreProxy().newProductType(
                                        PageFlowContext.getProductName(),
                                        PageFlowContext.getPageFlowName(),
                                        PageFlow.class));
            PageFlowContext.setPolicy(policy);
            PageFlowContext.setCurrentPageName(destinationPageId);
            PageFlowContext.flagActionAsProcessed();
        }

        return policy;
    }

    private Policy disaggregatePolicy(Type model, String product) throws BaseException {
        DisaggregatePolicyCommand dpc = getCoreProxy().newCommand(DisaggregatePolicyCommand.class);

        dpc.setAggregatedPolicyArg((Policy) model);
        dpc.setTargetPolicyTypeIdArg(product);
        dpc.invoke();

        return dpc.getPolicyRet();
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("AggregatorQuotationSelector", model);
    }

    public String getFeatureHtml() {
        return featureHtml;
    }

    public void setFeatureHtml(String featureHtml) {
        this.featureHtml = featureHtml;
    }

    public Boolean getHideReferrals() {
        return hideReferrals;
    }

    public void setHideReferrals(Boolean hideReferrals) {
        this.hideReferrals = hideReferrals;
    }

    public Boolean getHideDeclines() {
        return hideDeclines;
    }

    public void setHideDeclines(Boolean hideDeclines) {
        this.hideDeclines = hideDeclines;
    }

    public String getDestinationPageId() {
        return destinationPageId;
    }

    public void setDestinationPageId(String destinationPageId) {
        this.destinationPageId = destinationPageId;
    }
}
