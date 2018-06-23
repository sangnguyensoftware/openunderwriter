package com.ail.pageflow.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletModeException;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.ail.core.CoreProxy;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;
import com.liferay.portal.util.PortalUtil;
import static com.ail.pageflow.PageFlow.QUOTATION_PAGE_FLOW_NAME;

public class PageFlowRemotePortlet extends GenericPortlet {

    private static final String HEADER_PRODUCTNAME = "openunderwriter.product";

    private String errorJSP = null;

    private CoreProxy coreProxy;
    private PageFlowCommon pageFlowCommon;

    public PageFlowRemotePortlet() {
        coreProxy = new CoreProxy();
        pageFlowCommon = new PageFlowCommon();
    }

    @Override
    public void init() throws PortletException {
        errorJSP = getInitParameter("error-jsp");
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        response.setContentType("text/html");

        try {
            PageFlowContext.initialise(request, response, getPortletConfig());

            String productName = getProductName(request);

            PageFlowContext.setProductName(productName);
            PageFlowContext.setPageFlowName(QUOTATION_PAGE_FLOW_NAME);
            PageFlow pageFlow = coreProxy.newProductType(productName, QUOTATION_PAGE_FLOW_NAME, PageFlow.class);
            PageFlowContext.setPageFlow(pageFlow);
            PageFlowContext.initialise(request, response, getPortletConfig());

            pageFlowCommon.doView();

        } catch (Throwable t) {
            pageFlowCommon.handleError(t, errorJSP, getPortletContext());
        } finally {
            PageFlowContext.destroy();
        }

    }

    private String getProductName(RenderRequest request) {
        String productName = request.getProperty(HEADER_PRODUCTNAME);

        if (StringUtils.isBlank(productName)) {
            HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
            productName = httpServletRequest.getHeader(HEADER_PRODUCTNAME);
        }
        return productName;
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws ReadOnlyException, ValidatorException, IOException, PortletModeException {
        try {
            PageFlowContext.initialise(request, response, getPortletConfig());

            pageFlowCommon.processAction();
        } catch (Throwable t) {
            pageFlowCommon.handleError(t, errorJSP, getPortletContext());
        } finally {
            PageFlowContext.destroy();
        }

    }

}
