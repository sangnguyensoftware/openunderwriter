package com.ail.pageflow.service;

import static com.ail.core.context.RequestAdaptor.PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.ResponseWrapper;
import com.ail.core.context.portlet.PortletPreferencesAdaptor;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;
import com.liferay.portal.util.PortalUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class, PortalUtil.class, PortletPreferencesAdaptor.class})
public class CreateNewBusinessQuotationServiceTest {
    private static final String TEST_PRODUCT_NAME = "TEST PRODUCT";
    private static final String TEST_START_PAGE = "TEST_PAGE";
    private static final String TEST_PAGEFLOW_NAME = "TEST_PAGEFLOW_NAME";

    CreateNewBusinessQuotationService sut;

    @Mock
    ExecutePageActionService.ExecutePageActionArgument argument;
    @Mock
    RequestWrapper requestWrapper;
    @Mock
    ResponseWrapper responseWrapper;
    @Mock
    PortletSession portletSession;
    @Mock
    CoreProxy coreProxy;
    @Mock
    Policy policy;
    @Mock
    PageFlow pageflow;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(PortalUtil.class);

        sut = spy(new CreateNewBusinessQuotationService());
        sut.setArgs(argument);

        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getResponseWrapper()).thenReturn(responseWrapper);
        when(CoreContext.isPortletRequest()).thenReturn(true);

        doReturn(TEST_START_PAGE).when(pageflow).getStartPage();
        doReturn(policy).when(coreProxy).newProductType(eq(TEST_PRODUCT_NAME), eq("Policy"), eq(Policy.class));
        doReturn((Policy)null).when(sut).getPolicyFromPageFlowContext();
        doReturn("_pageflow_").when(responseWrapper).getNamespace();

        when(PageFlowContext.getPageFlow()).thenReturn(pageflow);
        when(PageFlowContext.getPageFlowName()).thenReturn(TEST_PAGEFLOW_NAME);
        when(PageFlowContext.getProductName()).thenReturn(TEST_PRODUCT_NAME);
        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);
        when(PageFlowContext.getPolicy()).thenReturn(policy);

        doReturn(false).when(sut).configurationSourceIsRequest();
    }

    @Test(expected=PreconditionException.class)
    public void ensureNullProductNameInPageflowContextErrorIsTrapped() throws BaseException {
        when(PageFlowContext.getProductName()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void ensureEmptyStringProductNameInPageflowContextErrorIsTrapped() throws BaseException {
        when(PageFlowContext.getProductName()).thenReturn("");
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void ensureNullPageflowInPageflowContextErrorIsTrapped() throws BaseException {
        when(PageFlowContext.getPageFlow()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void ensureExistingPolicyInSessionErrorIsTrapped() throws BaseException {
        doReturn(policy).when(sut).getPolicyFromPageFlowContext();
        sut.invoke();
        verify(sut, never()).setPolicyToPageFlowContext(any(Policy.class));
    }

    @Test(expected=PostconditionException.class)
    public void ensurePostconditionIsChecked() throws BaseException {
        when(PageFlowContext.getPolicy()).thenReturn(null);
        sut.invoke();
        PowerMockito.verifyStatic();
        PageFlowContext.setPolicy(eq(policy));
    }

    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();
        verify(sut).setPolicyToPageFlowContext(eq(policy));
    }

    @Test
    public void testModelArgRetIsPopulated() throws BaseException {
        sut.invoke();
        verify(argument).setModelArgRet(eq(policy));
    }

    @Test
    public void shouldPopulatePolicyWithProductTypeId() throws BaseException {
        doReturn("TEST_PRODUCT_NAME").when(sut).getProductNameFromPageFlowContext();
        sut.invoke();
        verify(policy).setProductTypeId(eq("TEST_PRODUCT_NAME"));
    }

    @Test
    public void checkThatPoliciesCreatedInTheSandpitAreMarkedAsTestCases() throws BaseException {
        doReturn("_sandpit_").when(responseWrapper).getNamespace();
        sut.invoke();
        verify(policy).setTestCase(eq(true));
    }

    @Test
    public void checkThatPoliciesCreatedOutsideTheSandpitAreNotMarkedAsTestCases() throws BaseException {
        sut.invoke();
        verify(policy, never()).setTestCase(eq(true));
    }

    @Test
    public void pageFromRequestMustBeRespectedWhenRequestConfigIsInForce() throws Exception {
        doReturn(true).when(sut).configurationSourceIsRequest();
        doReturn(true).when(sut).isNotAnAjaxRequest();

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        when(requestWrapper.getServletRequest()).thenReturn(httpServletRequest);
        doReturn(TEST_START_PAGE).when(httpServletRequest).getParameter(eq(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME));

        sut.invoke();

        verify(policy).setPage(eq(TEST_PAGEFLOW_NAME), eq(TEST_START_PAGE));
    }
}
