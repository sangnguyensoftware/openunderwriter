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

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.CoreContext.getProductName;
import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.core.CoreContext.getResponseWrapper;
import static com.ail.core.CoreContext.setProductName;
import static com.ail.pageflow.PageFlowContext.clear;
import static com.ail.pageflow.PageFlowContext.getCurrentPageName;
import static com.ail.pageflow.PageFlowContext.getNextPageName;
import static com.ail.pageflow.PageFlowContext.getPageFlow;
import static com.ail.pageflow.PageFlowContext.getPageFlowName;
import static com.ail.pageflow.PageFlowContext.getPolicy;
import static com.ail.pageflow.PageFlowContext.getPolicySystemId;
import static com.ail.pageflow.PageFlowContext.initialise;
import static com.ail.pageflow.PageFlowContext.restart;
import static com.ail.pageflow.PageFlowContext.savePolicy;
import static com.ail.pageflow.PageFlowContext.selectProductName;
import static com.ail.pageflow.PageFlowContext.setNextPageName;
import static com.ail.pageflow.PageFlowContext.setPageFlowInitliased;
import static com.ail.pageflow.PageFlowContext.setPageFlowName;
import static com.ail.pageflow.PageFlowContext.setPolicy;
import static com.ail.pageflow.PageFlowContext.setPolicySystemId;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreContextError;
import com.ail.core.CoreProxy;
import com.ail.core.ExceptionRecord;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.core.document.DocumentRequest;
import com.ail.core.persistence.hibernate.ForceSilentRollbackError;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.AbstractPage;
import com.ail.pageflow.AssessmentSheetDetails;
import com.ail.pageflow.ExceptionDetails;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.service.ListToOptionService.ListToOptionCommand;

/**
 * This Portlet performs a similar function to the {@link PageFlowPortlet}, but
 * is slightly specialised to better fit into a product development environment.
 * Specialisations include:
 * <ul>
 * <li>Can be started without being tied to a product.</li>
 * <li>Allows quotations to be stored as test quotes for later re-use.</li>
 * <li>Allows the quotation to be viewed (and modified) as XML at any point.</li>
 * </ul>
 */
public class SandpitPortlet extends GenericPortlet {
    private static String WIZARD_MODE = "Wizard";
    private static String XML_MODE = "XML";
    private static String JSON_MODE = "JSON";
    private static String ASSESSMENT_SHEET_MODE = "Assessment sheet";
    private static String EXCEPTION_MODE = "Exception";

    public static String RESET_PAGEFLOW_ACTION = "resetPageflow";
    public static String RESET_SANDPIT_ACTION = "resetSandpit";
    public static String CLEAR_CACHE_ACTION = "clearCache";
    public static String VIEW_PARAMETER_NAME = "view";

    private static List<String> VIEW_MODES = asList(WIZARD_MODE, XML_MODE, JSON_MODE, ASSESSMENT_SHEET_MODE, EXCEPTION_MODE);

    private CoreProxy coreProxy;
    private String statusMessage = null;
    private ListToOptionCommand listToOptionCommand;
    private AssessmentSheetDetails assessmentSheetDetails = new AssessmentSheetDetails();
    private ExceptionDetails exceptionDetails = new ExceptionDetails();
    private PageFlowCommon pageFlowCommon = null;

    public SandpitPortlet() {
        coreProxy = new CoreProxy();
        listToOptionCommand = coreProxy.newCommand(ListToOptionCommand.class);
        pageFlowCommon = new PageFlowCommon();
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) {
        try {
            initialise(request, response, null);
            doProcessPageFlowAction(request, response);
        } catch (Throwable cause) {
            handleActionException(request.getPortletSession(), cause);
        } finally {
            PageFlowContext.destroy();
        }
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException {
        try {
            initialise(request, response, null);
            doDisplayPageFlowView(request, response);
        } catch (Throwable cause) {
            handleViewException(request, response, cause);
        } finally {
            PageFlowContext.destroy();
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        try {
            initialise(request, response, null);
            doProcessPageFlowAction(request, response);
            if (!getRequestWrapper().isDocumentBeingUploaded()) {
                doDisplayPageFlowView(request, response);
            }
        } catch (Throwable cause) {
            handleViewException(request, response, cause);
        } finally {
            boolean validationRollbackRequired = validationRollbackRequired();

            PageFlowContext.destroy();

            if (validationRollbackRequired) {
                throw new ForceSilentRollbackError();
            }
        }
    }

    private boolean validationRollbackRequired() {
        return getPageFlow() != null && getPageFlow().isRollbackOnValidationFailure() && getResponseWrapper() != null && getResponseWrapper().isValidationErrorsFound();
    }

    private void doProcessPageFlowAction(PortletRequest request, PortletResponse response) {
        boolean processingComplete = false;
        PortletSession session = request.getPortletSession();
        statusMessage = null;

        try {
            coreProxy.setVersionEffectiveDateToNow();

            if (request.getParameter(CLEAR_CACHE_ACTION) != null) {
                processingComplete = clearCache(session);
            }

            if (request.getParameter(RESET_PAGEFLOW_ACTION) != null) {
                processingComplete = resetPageFlow(session);
            }

            if (request.getParameter(RESET_SANDPIT_ACTION) != null) {
                processingComplete = resetSandpit(session);
            }

            if (request.getParameter("loadPolicy") != null) {
                processingComplete = loadPolicy(request, session);
            }

            if (!processingComplete && (request.getParameter("selectedProduct") != null || request.getParameter("selectedPageFlow") != null || request.getParameter("selectedPage") != null)) {
                processingComplete = selectProductOrPageflowOrPage(request, session);
            }

            if (!processingComplete && processingQuotation(request)) {
                processAction(request, response, session);
            }
        }
        catch (CoreContextError e) {
            // This is bad! We won't even be able to redirect to the exception page, so give up.
            throw e;
        }
        catch (Throwable cause) {
            handleActionException(session, cause);
        }
    }

    private void doDisplayPageFlowView(PortletRequest request, MimeResponse response) throws IOException {

        response.setContentType("text/html");

        response.getWriter().write("<div id='pageflow-wrapper'>");

        PrintWriter out = response.getWriter();

        try {
            renderDebugPanel(request, response);

            if (statusMessage != null) {
                renderSandpitMessage(out, statusMessage, "");
            } else if (processingQuotation(request)) {
                if (WIZARD_MODE.equals(getCurrentView(request))) {
                    renderPageFlowView(request, response);
                } else if (ASSESSMENT_SHEET_MODE.equals(getCurrentView(request))) {
                    renderAssessmentSheetView(request, response);
                } else if (EXCEPTION_MODE.equals(getCurrentView(request))) {
                    renderExceptionsView(request, response);
                }
            } else {
                renderSandpitMessage(out, "No product selected.", "Please select a Product and a PageFlow in the debug panel above.");
            }
        }
        catch (CoreContextError e) {
            // This is bad! We won't even be able to redirect to the exception page, so give up.
            throw e;
        }
        catch (Throwable t) {
            handleViewException(request, response, t);
        }

        response.getWriter().write("</div>");
    }

    private void processAction(PortletRequest request, PortletResponse response, PortletSession session) throws BaseException {
        Policy quote = getPolicy();
        int exceptionCount = (quote != null) ? quote.getException().size() : 0;

        pageFlowCommon.processAction();

        try {
            if (getPolicy() != null && getPolicy().getException().size() != exceptionCount) {
                session.setAttribute(VIEW_PARAMETER_NAME, EXCEPTION_MODE);
            }
        } catch (IllegalStateException e) {
            // Ignore IllegalState exceptions, they simply indicate that
            // the Quit button has been selected in the quote - that
            // invalidates the session which prevents
            // getCurrentQuotation from being able to get the Policy as
            // there is no session to get it from! AFAIK there is no way
            // to detect that the session has been invalidated, other
            // than to try and access it.
        }
    }

    private boolean selectProductOrPageflowOrPage(PortletRequest request, PortletSession session) throws XMLException, MalformedURLException, IOException {
        boolean processingComplete = false;

        String selectedProduct = request.getParameter("selectedProduct");
        String currentProduct = getProductName();

        String selectedPageFlow = request.getParameter("selectedPageFlow");
        String currentPageFlow = getPageFlowName();

        String selectedPage = request.getParameter("selectedPage");
        String currentPage = getCurrentPageName();
        String nextPage = getNextPageName();

        String selectedView = request.getParameter("selectedView");

        if (selectedProduct != null && !selectedProduct.endsWith("?")) {
            if (!selectedProduct.equals(currentProduct)) {
                selectProductName(selectedProduct);
                selectedView = WIZARD_MODE;
                processingComplete = true;
            }
        }

        if (selectedPageFlow != null && !selectedPageFlow.endsWith("?")) {
            if (!selectedPageFlow.equals(currentPageFlow)) {
                selectPageFlowName(selectedPageFlow);
                selectedView = WIZARD_MODE;
                processingComplete = true;
            }
        }

        if (selectedPage != null && !selectedPage.endsWith("?")) {
            if (!selectedPage.equals(currentPage) && !selectedPage.equals(nextPage)) {
                setNextPageName(selectedPage);
                selectedView = WIZARD_MODE;
                processingComplete = true;
            }
        }

        if (!XML_MODE.equals(getCurrentView(request)) && XML_MODE.equals(selectedView)) {
            session.setAttribute("policyXml", coreProxy.toXML(getPolicy()));
            processingComplete = true;
        } else if (XML_MODE.equals(getCurrentView(request)) && !XML_MODE.equals(selectedView)) {
            XMLString policyXml = new XMLString(request.getParameter("policyXml"));
            session.setAttribute("policyXml", policyXml);
            Policy quote = coreProxy.fromXML(Policy.class, policyXml);
            setPolicy(quote);
            session.removeAttribute("policyXml");
            processingComplete = true;
        }

        if (!JSON_MODE.equals(getCurrentView(request)) && JSON_MODE.equals(selectedView)) {
            session.setAttribute("pageflowJsonUrl", fetchJsonUrlForCurrentPage(request));
            processingComplete = true;
        } else if (JSON_MODE.equals(getCurrentView(request)) && !JSON_MODE.equals(selectedView)) {
            session.removeAttribute("pageflowJsonUrl");
            processingComplete = true;
        }

        session.setAttribute(VIEW_PARAMETER_NAME, selectedView);

        return processingComplete;
    }

    String fetchJsonUrlForCurrentPage(PortletRequest request) throws MalformedURLException, IOException {
        return String.format("/pageflow/%s/policy/%s/page/%s",
                    getPageFlowName(),
                    getPolicy().getExternalSystemId(),
                    getCurrentPageName());
    }

    private boolean loadPolicy(PortletRequest request, PortletSession session) throws XMLException {
        String selectedPolicy = request.getParameter("selectedPolicy");
        Policy policy;

        policy = (Policy) coreProxy.queryUnique("get.policy.by.quotationNumber", selectedPolicy);

        if (policy == null) {
            policy = (Policy) coreProxy.queryUnique("get.policy.by.policyNumber", selectedPolicy);
        }

        if (policy == null) {
            policy = (Policy) coreProxy.queryUnique("get.policy.by.externalSystemId",  selectedPolicy);
        }

        if (policy == null) {
            Long systemId=null;

            try {
                systemId=new Long(selectedPolicy);
            }
            catch(Exception e) {
                // ignore this, selectedPolicy probably isn't a number
            }

            if (systemId!=null) {
                policy = (Policy) coreProxy.queryUnique("get.policy.by.systemId", systemId);
            }
        }

        if (policy != null) {
            setProductName(policy.getProductTypeId());
            setPolicy(policy);
            setPageFlowName("PolicySummaryPageFlow");
            setNextPageName("Main");

            session.setAttribute(VIEW_PARAMETER_NAME, WIZARD_MODE);
            return true;
        } else {
            statusMessage = "Policy '" + selectedPolicy + "' not found";

            return false;
        }
    }

    private boolean clearCache(PortletSession session) {
        getCoreProxy().clearConfigurationCache();
        return true;
    }

    private boolean resetPageFlow(PortletSession session) {
        restart();
        session.setAttribute(VIEW_PARAMETER_NAME, WIZARD_MODE);
        return true;
    }

    private boolean resetSandpit(PortletSession session) {
        clear();
        session.setAttribute(VIEW_PARAMETER_NAME, WIZARD_MODE);
        return true;
    }

    private void renderSandpitMessage(PrintWriter out, String message, String help) throws IOException {
        out.write("<div class='sandpit-pageflow-panel'>");
        out.write("<div class='sandpit-message'>");
        out.write("<div class='sandpit-message'>"+message+"</div>");
        out.write("<div class='sandpit-message-help'>"+help+"</div>");
        out.write("</div>");
        out.write("</div>");
    }

    private void renderAssessmentSheetView(PortletRequest request, MimeResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        Policy policy = getPolicy();
        if (policy != null && policy.getAssessmentSheetList() != null) {
            out.write("<div class='sandpit-pageflow-panel'>");
            assessmentSheetDetails.renderResponse(getPolicy());
            out.write("</div>");
        } else {
            renderSandpitMessage(out, "No assessment sheet available", "The assessment sheet can only be displayed once the policy being processed has been rated.");
        }
    }

    private void renderPageFlowView(PortletRequest request, MimeResponse response) throws IOException, BaseException {
        PortletSession session = getRequestWrapper().getPortletRequest().getPortletSession();
        PrintWriter out = response.getWriter();

        int exceptionCount = (getPolicy() != null) ? getPolicy().getException().size() : 0;

        out.write("<div class='sandpit-pageflow-panel'>");

        pageFlowCommon.doView();

        if (getPolicy() != null && getPolicy().getException().size() != exceptionCount) {
            session.setAttribute(VIEW_PARAMETER_NAME, EXCEPTION_MODE);
            doDisplayPageFlowView(request, response);
        }

        out.write("</div>");
    }

    private void handleActionException(PortletSession session, Throwable cause) {
        Policy policy = getPolicy();

        if (policy == null) {
            policy = new Policy();
            setPolicy(policy);
        }

        policy.addException(new ExceptionRecord(cause));

        try {
            savePolicy();
        } catch (Throwable th) {
            // Switch to the exception view for 'cause' - not 'th'. The user is probably
            // more interested in what error cause us to be handling exceptions in the
            // first place rather than any exception we encountered while handling them.
            session.setAttribute("exception", new ExceptionRecord(cause));

            // Dump the exception to the console just for information.
            th.printStackTrace();
        }

        session.setAttribute(VIEW_PARAMETER_NAME, EXCEPTION_MODE);
    }


    private void handleViewException(PortletRequest request, MimeResponse response, Throwable cause) throws IOException {
        try {
            PortletSession session=getRequestWrapper().getPortletRequest().getPortletSession();

            Policy policy = getPolicy();

            // All exceptions are associated with a quotation. However, if the
            // quote itself could not be initialised (which is quite likely if
            // the product's Policy.xml has errors) then create a dummy quote
            // here so that the sandpit's exception view can work normally.
            if (policy == null) {
                policy = new Policy();
                setPolicy(policy);
            }

            policy.addException(new ExceptionRecord(cause));

            // If we fail to persist the quote now then things must be very
            // broken. Add the details of this exception to the quote too.
            try {
                savePolicy();
            } catch (Throwable th) {
                // ignore this, we're in the sandpit handling an error anyway,
                // capturing the details of errors during error handling isn't
                // useful.
            }

            session.setAttribute(VIEW_PARAMETER_NAME, EXCEPTION_MODE);

            // we've probably already written something - half formed - to the
            // output stream before the exception was thrown. Reset the buffer
            // so we can display the debug pane and exception cleanly.
            if (response instanceof MimeResponse) {
                response.resetBuffer();
            }

            try {
                renderDebugPanel(request, response);
            } catch (BaseException e) {
                // ignore this, we're in the sandpit handling an error anyway,
                // capturing the details of errors during error handling isn't useful.
            }

            renderExceptionsView(request, response);
        }
        catch(Throwable t) {
            // If all else fails, dump details to the log
            cause.printStackTrace();
        }
    }

    /**
     * Are we portlet processing a pageflow, or is it waiting to be told which
     * product and pageflow to process for?
     *
     * @return true if we've been told which product to quote for, false
     *         otherwise.
     */
    private boolean processingQuotation(PortletRequest request) {
        return getProductName() != null && getPageFlowName() != null;
    }

    private void renderExceptionsView(PortletRequest request, MimeResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        Policy policy = getPolicy();
        if (policy != null && policy.getAssessmentSheetList() != null) {
            out.write("<div class='sandpit-pageflow-panel'>");
            exceptionDetails.renderResponse(policy);
            out.write("</div>");
        } else {
            renderSandpitMessage(out, "Policy contains no exceptions", "");
        }
    }

    private void renderDebugPanel(PortletRequest request, MimeResponse response) throws IOException, BaseException {
        boolean disableResetButton = true;
        boolean disablePageList = true;
        boolean disabledPageFlowList = true;

        PrintWriter out=response.getWriter();

        String product = getProductName();
        String pageFlowName = getPageFlowName();
        Boolean testCase = getPolicy() != null && getPolicy().isTestCase();
        Boolean validationRollback = getPageFlow()!=null && getPageFlow().isRollbackOnValidationFailure();
        String systemId = (getPolicy() != null ? String.valueOf(getPolicy().getSystemId()) : "");
        PageFlow pageFlow = getPageFlow();

        disabledPageFlowList = (product == null || "?".equals(product));

        // hide the pageList if we don't have both the policy and the pageflow
        disablePageList = (getPageFlow() == null);

        // if we're in a quote, enable the reset button.
        disableResetButton = (pageFlow == null);

        out.printf("<form name='productDebug' action='%s' method='post' style='margin: 0'>", response.createActionURL());
        out.printf("<div class='sandpit-debug-panel'>");
        out.printf("<span id='navigator'>");
        out.printf("<select id='selectedProduct' name='selectedProduct' onChange='submit()' class='portlet-form-input-field'>%s</select>", pageFlowCommon.buildProductSelectOptions(product));
        out.printf("<select id='selectedPageFlow' name='selectedPageFlow' %s onChange='submit()' class='portlet-form-input-field'>%s</select>", disabledPageFlowList ? "disabled='disabled'" : "", pageFlowCommon.buildPageFlowSelectOptions(product, pageFlowName));
        out.printf("<select id='selectedPage' name='selectedPage' %s onChange='submit()' class='portlet-form-input-field'>%s</select>", disablePageList ? "disabled='disabled'" : "", buildPageSelectOptioon());
        out.printf("</span>");

        out.printf("<span id='load'>");
        out.printf(" <input placeholder='number or id' title='Policy ID, quote number or policy number' type='text' name='selectedPolicy' class='pn-normal' value='%s'/>", systemId);
        out.printf(" <input type='submit' name='loadPolicy' value='Load' data-hint='enter something!' class='portlet-form-input-field'/>");
        out.printf("</span>");

        out.printf("<span id='view'>");
        out.printf("<select name='selectedView' onChange='submit()' class='portlet-form-input-field'>%s</select>", buildViewSelectOptions(getCurrentView(request)));
        out.printf("</span>");

        out.printf("<span id='save'>");
        out.printf("<input type='checkbox' name='saveAsTestcase' %s value='saveAsTestCase' class='portlet-form-input-field' disabled='disabled' style='margin: 0px 0px 2px 6px'/> Test case<br/>", testCase ? "checked='checked'" : "");
        out.printf("<input type='checkbox' name='validationRollback' %s value='validationRollback' class='portlet-form-input-field' disabled='disabled' style='margin: 0px 0px 2px 6px'/> Validation rollback", validationRollback ? "checked='checked'" : "");
        out.printf("</span>");

        out.printf("<span id='reset'>");
        out.printf("<input id='%1$s' type='submit' name='%1$s' value='' class='portlet-form-input-field' title='Clear Cache'/>", CLEAR_CACHE_ACTION);
        out.printf("<input id='%1$s' type='submit' name='%1$s' %2$s value='' class='portlet-form-input-field' title='Restart PageFlow'/>", RESET_PAGEFLOW_ACTION, disableResetButton ? "disabled='disabled'" : "");
        out.printf("<input id='%1$s' type='submit' name='%1$s' value='' class='portlet-form-input-field' title='Reset Sandpit'/>", RESET_SANDPIT_ACTION);
        out.printf("</span>");

        out.printf("</div>");

        // The XML editor textarea is handled as a part of the debug panel. It
        // might be nice if it were a separate UI component, but
        // a) it's so simple it isn't worth the effort; and,
        // b) it needs to be included in the debug panel's form so that
        //    switching modes also submits the XML content.
        if (XML_MODE.equals(getCurrentView(request))) {
            renderXmlView(request, out);
        }
        else if(JSON_MODE.equals(getCurrentView(request))) {
            renderJsonView(request, out);
        }

        out.printf("</form>");
    }

    private void renderJsonView(PortletRequest request, PrintWriter w) {
        String requestId=CoreContext.getCoreProxy().create(new DocumentRequest()).getRequestId();
        String jsonUrl = (String)request.getPortletSession().getAttribute("pageflowJsonUrl");

        if (jsonUrl != null) {
            w.write("<div class='sandpit-pageflow-panel'>");
            w.write("<textarea id='sandpitJson' name='json' data-editor='xml' style='display:none'></textarea>");
            w.write("<div id='jsonEditor'></div>");
            w.write("<script type='text/javascript'>populateSandpitJson('"+jsonUrl+"', '"+requestId+"');</script>");
            w.write("</div>");
        }
    }

    private void renderXmlView(PortletRequest request, PrintWriter w) {
        XMLString policyXml = (XMLString) request.getPortletSession().getAttribute("policyXml");
        if (policyXml != null) {
            w.write("<div class='sandpit-pageflow-panel'>");
            w.write("<textarea id='sandpitXml' name='policyXml' data-editor='xml' style='display:none'>"+policyXml.toString()+"</textarea>");
            w.write("<div id='policyXmlEditor'></div>");
            w.write("<script type='text/javascript'>");
            w.write("YUI().use('aui-ace-editor',");
            w.write("    function(Y) {");
            w.write("        var textarea=$('#sandpitXml');");
            w.write("        var editor=new Y.AceEditor({");
            w.write("            boundingBox: '#policyXmlEditor',");
            w.write("            width: textarea.width(),");
            w.write("            height: textarea.height(),");
            w.write("            mode: 'xml',");
            w.write("            useWrapMode: 'false',");
            w.write("            readonly: 'true',");
            w.write("            showPrintMargin: false");
            w.write("        });");
            w.write("        editor.set('value', textarea.val());");
            w.write("        editor.render();");
            w.write("    }");
            w.write(");");
            w.write("</script>");
            w.write("</div>");
        }
    }

    /**
     * Build an HTML option list showing the view modes available. The
     * "current view" is selected.
     *
     * @param view
     * @return HTML option list
     * @throws BaseException
     */
    private String buildViewSelectOptions(String view) throws BaseException {
        Collection<String> disabled=new ArrayList<>();
        Policy policy = getPolicy();

        if (getPolicy() == null) {
            disabled.add(XML_MODE);
            disabled.add(JSON_MODE);
        }

        if (policy==null || policy.getAssessmentSheetList()==null || policy.getAssessmentSheetList().size()==0) {
            disabled.add(ASSESSMENT_SHEET_MODE);
        }

        // Disable Exception View unless we have a policy and that policy has
        // exceptions in it, or there is an exception attribute in the session.
        if (!(policy!=null &&
              policy.getException()!=null &&
              policy.getException().size()!=0) &&
            !(getRequestWrapper().getPortletRequest().getPortletSession().getAttribute("exception")!=null)) {
            disabled.add(EXCEPTION_MODE);
        }

        listToOptionCommand.setOptionsArg(VIEW_MODES);
        listToOptionCommand.setDisabledOptionsArg(disabled);
        listToOptionCommand.setExcludeUnknownArg(true);
        listToOptionCommand.setUnknownOptionArg(null);
        listToOptionCommand.setSelectedArg(view);
        listToOptionCommand.invoke();
        return listToOptionCommand.getOptionMarkupRet();
    }

    /**
     * Build an option list for the sandpit's page list. Based on the currently
     * selected PageFlow and policy, populate the list with all of the pages in
     * the PageFlow and mark the page that the policy is currently at as
     * selected.
     */
    private String buildPageSelectOptioon() throws BaseException {
        String selected = getCurrentPageName();
        List<String> pageNames = new ArrayList<>();

        PageFlow pageFlow=getPageFlow();

        if (pageFlow != null) {
            pageNames = pageFlow.getPages().stream().map(AbstractPage::getId).collect(Collectors.toList());
        }

        if (getNextPageName()!=null) {
            selected=getNextPageName();
        }

        listToOptionCommand.setOptionsArg(pageNames);
        listToOptionCommand.setSelectedArg(selected);
        listToOptionCommand.setExcludeUnknownArg(false);
        listToOptionCommand.setUnknownOptionArg("Page?");
        listToOptionCommand.invoke();

        return listToOptionCommand.getOptionMarkupRet();
    }

    /**
     * Figure out which view mode we're in based on the session.
     *
     * @param request
     * @return current view mode.
     */
    private String getCurrentView(PortletRequest request) {
        String view = (String) request.getPortletSession().getAttribute(VIEW_PARAMETER_NAME);
        return (view != null) ? view : VIEW_MODES.get(0);
    }

    public void selectPageFlowName(String pageFlow) {
        boolean initialised = false;

        // If the policy has been in this pageflow before, it has already already initialised for it.
        if (getPolicy() != null) {
            initialised = getPolicy().getPageVisit().stream().anyMatch(p -> p.getPageFlowName().equals(pageFlow));
        }

        // Save values before clear
        String productName = getProductName();
        Long policySystemId = getPolicySystemId();

        clear();

        // Restore values after clear
        setProductName(productName);
        setPolicySystemId(policySystemId);

        if (initialised) {
            setPageFlowInitliased(true);
        }

        setPageFlowName(pageFlow);
    }
}
