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
package com.ail.pageflow.portlet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.pageflow.portlet.SubmitMode.PAGE;
import static com.ail.pageflow.portlet.SubmitMode.TARGETTED;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserImpl;
import com.ail.core.ExceptionRecord;
import com.ail.core.FieldChange;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.core.context.PreferencesAdaptor;
import com.ail.core.persistence.OutsideTransactionContext;
import com.ail.core.product.ListProductsService.ListProductsCommand;
import com.ail.core.product.ProductDetails;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.RowScroller;
import com.ail.pageflow.service.ListPageFlowsForProductService.ListPageFlowsForProductCommand;
import com.ail.pageflow.service.ListToOptionService.ListToOptionCommand;

/**
 * This class defines methods which are common to both the quotation and sandpit
 * Portlets.
 */
public class PageFlowCommon {
    private ListProductsCommand listProductsCommand;
    private ListToOptionCommand listToOptionCommand;
    private ListPageFlowsForProductCommand listPageFlowsForProductCommand;

    public PageFlowCommon() {
        CoreProxy coreProxy = new CoreProxy();

        listProductsCommand = coreProxy.newCommand(ListProductsCommand.class);
        listPageFlowsForProductCommand = coreProxy.newCommand(ListPageFlowsForProductCommand.class);
        listToOptionCommand = coreProxy.newCommand(ListToOptionCommand.class);
    }

    public void processAction() throws BaseException {
        PageFlow pageFlow = PageFlowContext.getPageFlow();
        Policy policy = PageFlowContext.getPolicy();

        if (targettedSubmit()) {
            PageFlowContext.setSubmitMode( targettedSubmit() ? TARGETTED : PAGE);
        }

        // do any actions necessary before anything else happens
        pageFlow.onStartProcessAction(policy);

        // apply values from the request back into the model
        pageFlow.applyRequestValues(policy);

        // if the request is immediate, or if the page passes validation...
        if (immediate() || getRequestWrapper().isDocumentBeingUploaded() || !pageFlow.processValidations(policy)) {
            // ...process the actions in the request (e.g. move to next page)
            pageFlow.processActions(policy);
        }

        // update the persisted quote
        PageFlowContext.savePolicy();
    }

    public void doView() throws IOException, BaseException {
        if (getRequestWrapper().isDocumentBeingUploaded()) {
            return;
        }

        PageFlow pageFlow = PageFlowContext.getPageFlow();
        Policy policy = PageFlowContext.getPolicy();

        pageFlow.renderResponse(policy);

        PageFlowContext.savePolicy();
    }

    /**
     * Fetch the productTypeID of the policy currently being quoted, or an empty
     * String if no product is currently being quoted.
     *
     * @return ProductTypeId of the policy being processed, or "" if none is
     *         being processed.
     */
    String currentProduct() {
        if (PageFlowContext.getPolicy() != null && PageFlowContext.getPolicy().getProductTypeId() != null) {
            return PageFlowContext.getPolicy().getProductTypeId();
        } else {
            return "";
        }
    }

    /**
     * Return true if this request is 'immediate'. Immediate requests are used
     * to indicate that the actions associated with the request should be
     * immediately executed - even if the page would fail validation. This is
     * useful, for example, when implementing a '&lt;&lt;Back' button on a page
     * as it will allow the user to move back even if the current page isn't
     * fully filled in yet. It's also used by the {@link RowScroller} to allow
     * the 'Add' and 'Delete' buttons to work even when the contents of the
     * scroller may not be valid.
     *
     * @return true if the request is immediate, false otherwise.
     */
    boolean immediate() {
        String im = PageFlowContext.getOperationParameters().getProperty("immediate");

        return "true".equals(im);
    }

    /**
     * Return true if the current operation is a targetted submit. This is indicated by the
     * presence of a 'submitTarget' property in the operation params.
     * @return
     */
    boolean targettedSubmit() {
        return PageFlowContext.getOperationParameters().getProperty("submitTarget") != null;
    }

    /**
     * Fetch the value of the submitTarget operation param.
     * @return Value of submitedTarget param
     */
    String fetchSubmitTarget() {
        return PageFlowContext.getOperationParameters().getProperty("submitTarget");
    }

    /**
     * Build an HTML select option string listing all the available products.
     *
     * @param product
     *            Name of the currently
     * @return String of products in option format.
     * @throws BaseException
     */
    public String buildProductSelectOptions(String product) throws BaseException {
        listProductsCommand.invoke();

        List<String> productNames = extract(listProductsCommand.getProductsRet(), on(ProductDetails.class).getName());

        // TODO: This should be data driven!!
        productNames.remove("AIL.Base");
        productNames.remove("AIL.Demo.TradePL.GenericQB");

        listToOptionCommand.setOptionsArg(productNames);
        listToOptionCommand.setSelectedArg(product);
        listToOptionCommand.setUnknownOptionArg("Product?");
        listToOptionCommand.invoke();

        return listToOptionCommand.getOptionMarkupRet();
    }

    /**
     * Build an HTML select option string listing all the available
     * configuration sources.
     *
     * @return String of sources in option format.
     * @throws BaseException
     */
    public String buildConfigurationSourceOptions(String selectedSource) throws BaseException {
        listProductsCommand.invoke();

        List<String> sources = Arrays.asList(PreferencesAdaptor.CONFIGURE_BY_PREFERENCES, PreferencesAdaptor.CONFIGURE_BY_REQUEST, PreferencesAdaptor.CONFIGURE_BY_SESSION);

        listToOptionCommand.setOptionsArg(sources);
        listToOptionCommand.setSelectedArg(selectedSource);
        listToOptionCommand.setUnknownOptionArg("Source?");
        listToOptionCommand.invoke();

        return listToOptionCommand.getOptionMarkupRet();
    }

    /**
     * Builds an HTML select option string listing all the names of PageFlows
     * supported by the named product.
     *
     * @param product
     *            Product to return PageFlows for
     * @param pageFlowName
     *            The currently selected PageFlow, if any.
     * @return String of page flow names in option format.
     * @throws BaseException
     */
    public String buildPageFlowSelectOptions(String product, String pageFlowName) throws BaseException {
        Set<String> pageFlowNames = new HashSet<>();

        if (product != null && product.length() != 0) {
            listPageFlowsForProductCommand.setCallersCore(new CoreUserImpl());
            listPageFlowsForProductCommand.setProductNameArg(product);
            listPageFlowsForProductCommand.invoke();
            pageFlowNames = listPageFlowsForProductCommand.getPageFlowNameRet();
        }

        listToOptionCommand.setOptionsArg(pageFlowNames);
        listToOptionCommand.setSelectedArg(pageFlowName);
        listToOptionCommand.setUnknownOptionArg("PageFlow?");
        listToOptionCommand.setSortResultsArg(true);
        listToOptionCommand.invoke();

        return listToOptionCommand.getOptionMarkupRet();
    }

    public void handleError(Throwable t, String errorJSP, PortletContext context) {
        try {
            PageFlowContext.getPolicy().addException(new ExceptionRecord(t));

            PageFlowContext.savePolicy();

            String errorMessage = t.getClass() + " thrown while processing policy (systemId=" + PageFlowContext.getPolicySystemId() + ", product="+PageFlowContext.getProductName()+"): [" + t.getMessage()+"]. Full details have been persisited with the policy.";

            PageFlowContext.getCoreProxy().logError(errorMessage);
        } catch (Throwable x) {
            PageFlowContext.getCoreProxy().logError(t.toString(), t);
        }
        finally {
            PortletRequestDispatcher portletRequestDispatcher = context.getRequestDispatcher(errorJSP);
            try {
                portletRequestDispatcher.include(
                        PageFlowContext.getRequestWrapper().getPortletRequest(),
                        PageFlowContext.getResponseWrapper().getPortletResponse()
                );
            } catch (Exception e) {
                // We've done everything we can!
            }
        }
    }

    /**
     * When a page is submitted it would be sometimes useful to know if a field has changed value. To that end this method provides a means
     * of storing the old value of the field, if it is invoked at the beginning of the request before the model has been updated.
     * Any fields of interest will be added to a Map in the PageflowContext where the key is the value xpath in the model and the value is a new
     * {@link FieldChange} containing the details of the field being changed.
     * @param model the model containing the field
     * @param fieldXpath    the xpath for this field value relative to the model
     */
    public static void registerOldFieldValue(Type model, String fieldBinding) {
        String xpath = fieldBinding;
        Object oldValue = null;
        if (!fieldBinding.endsWith("/value")) {
            try {
                /** The field may be an attribute, in which case we want its value, but it may be a DataDictionary definition so it will not end in /value.
                 * In this case we should try and xpath get it with value on the end first. If nothing comes back then we can try again without /value */
                xpath = fieldBinding + "/value";
                oldValue = model.xpathGet(xpath);
            } catch (TypeXPathException ignorable) {}
        }
        if (oldValue == null) {
            try {
                xpath = fieldBinding;
                oldValue = model.xpathGet(xpath);
            } catch (TypeXPathException e) {
                /** Still no value so we should probably log something */
                PageFlowContext.getCoreProxy().logError("Could not get value for binding " + xpath, e);
            }
        }
        OutsideTransactionContext.addOnChangeField(xpath, new FieldChange(model, xpath, oldValue));
    }
}
