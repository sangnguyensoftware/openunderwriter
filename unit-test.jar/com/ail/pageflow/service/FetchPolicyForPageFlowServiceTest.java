package com.ail.pageflow.service;

import static com.ail.core.context.PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.RequestAdaptor.PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME;
import static com.ail.core.context.RequestAdaptor.POLICY_PORTLET_REQUEST_PARAMETER_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
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
import com.ail.core.PreconditionException;
import com.ail.core.ThreadLocale;
import com.ail.core.context.PreferencesAdaptor;
import com.ail.core.context.PreferencesWrapper;
import com.ail.core.context.RequestWrapper;
import com.ail.core.security.ConfirmObjectAccessibilityToUserService.ConfirmObjectAccessibilityToUserCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class, PortalUtil.class})
public class FetchPolicyForPageFlowServiceTest {
    private static final String TEST_PAGEFLOW_NAME = "TEST PAGEFLOW NAME";
    private static final String TEST_PRODUCT_NAME = "TEST PRODUCT";
    private static final String TEST_START_PAGE = "TEST_PAGE";
    private static final Long TEST_POLICY_SYSTEM_ID = 1234L;

    FetchPolicyForPageFlowService sut;

    Locale locale=Locale.CANADA;

    @Mock
    ExecutePageActionService.ExecutePageActionArgument argument;
    @Mock
    CoreProxy coreProxy;
    @Mock
    PortletRequest portletRequest;
    @Mock
    ResourceRequest resourceRequest;
    @Mock
    RequestWrapper requestWrapper;
    @Mock
    RenderRequest renderRequest;
    @Mock
    PortletSession portletSession;
    @Mock
    PreferencesWrapper preferencesWrapper;
    @Mock
    Policy policy;
    @Mock
    PageFlow pageflow;
    @Mock
    HttpServletRequest currentHttpServletRequest;
    @Mock
    HttpServletRequest originalHttpServletRequest;
    @Mock
    ConfirmObjectAccessibilityToUserCommand confirmObjectAccessibilityToUserCommand;
    @Mock
    User user;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(PortalUtil.class);

        sut = new FetchPolicyForPageFlowService();
        sut.setArgs(argument);

        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getPreferencesWrapper()).thenReturn(preferencesWrapper);

        doReturn(TEST_START_PAGE).when(pageflow).getStartPage();
        doReturn(locale).when(requestWrapper).getLocale();

        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);
        when(PageFlowContext.getProductName()).thenReturn(TEST_PRODUCT_NAME);
        when(PageFlowContext.getPageFlow()).thenReturn(pageflow);
        when(PageFlowContext.getPolicySystemId()).thenReturn(TEST_POLICY_SYSTEM_ID);
        when(PageFlowContext.getPageFlowName()).thenReturn(TEST_PAGEFLOW_NAME);

        doReturn(originalHttpServletRequest).when(requestWrapper).getServletRequest();

        when(CoreContext.getRemoteUser()).thenReturn(1L);

        doReturn(PreferencesAdaptor.CONFIGURE_BY_PREFERENCES).when(preferencesWrapper).getValue(eq(CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME), eq((String)null));

        doReturn(policy).when(coreProxy).queryUnique(anyString(), eq(TEST_POLICY_SYSTEM_ID));

        doReturn(confirmObjectAccessibilityToUserCommand).when(coreProxy).newCommand(eq("ConfirmObjectAccessibilityToUserCommand"),eq(ConfirmObjectAccessibilityToUserCommand.class));
        doReturn(true).when(confirmObjectAccessibilityToUserCommand).getWriteAccessRet();
    }

    @Test(expected = PreconditionException.class)
    public void ensureNullPortletRequestArgIsTrapped() throws BaseException {
        when(CoreContext.getRequestWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void ensureNullProductNameArgIsTrapped() throws BaseException {
        when(PageFlowContext.getProductName()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void ensureZeroLengthProductNameArgIsTrapped() throws BaseException {
        when(PageFlowContext.getProductName()).thenReturn("");
        sut.invoke();
    }

    @Test
    public void ensureThatSessionPolicyIsUsedIfItExists() throws BaseException {
        sut.invoke();
        verify(coreProxy, never()).newProductType(eq(TEST_PRODUCT_NAME), eq("Policy"), eq(Policy.class));
        verify(policy, times(1)).setLocale(any(ThreadLocale.class));
        PowerMockito.verifyStatic();
        PageFlowContext.setPolicy(eq(policy));
    }

    @Test
    public void testModelArgRetIsPopulated() throws BaseException {
        sut.invoke();
        verify(argument).setModelArgRet(eq(policy));
    }

    @Test
    public void checkThatNothingHappensWhenGetPolicySystemIdIsNull() throws BaseException {
        when(PageFlowContext.getPolicySystemId()).thenReturn(null);
        sut.invoke();
        PowerMockito.verifyStatic(never());
        PageFlowContext.setPolicy(eq(policy));
    }

    @Test
    public void checkThatRequestPolicyIdIsIgnoredUnderPreferenceConfiguration() throws BaseException {
        doReturn("1").when(portletRequest).getParameter(eq(POLICY_PORTLET_REQUEST_PARAMETER_NAME));
        sut.invoke();
        PowerMockito.verifyStatic(never());
        PageFlowContext.setPolicySystemId(any(Long.class));
    }

    @Test
    public void checkThatRequestPolicyIdIsUsedUnderRequestConfigurationAndNonAjaxCall() throws BaseException {
        doReturn(true).when(preferencesWrapper).isConfiguredByRequest();
        doReturn(false).when(requestWrapper).isAjaxRequest();
        when(CoreContext.isPortletRequest()).thenReturn(true);
        doReturn("1234").when(originalHttpServletRequest).getParameter(eq(POLICY_PORTLET_REQUEST_PARAMETER_NAME));

        sut.invoke();
        PowerMockito.verifyStatic(times(1));
        PageFlowContext.setPolicySystemId(eq(1234L));
    }

    @Test
    public void checkThatRequestPageIsUsedUnderRequestConfigurationAndNonAjaxCall() throws BaseException {
        doReturn(true).when(preferencesWrapper).isConfiguredByRequest();
        doReturn(false).when(requestWrapper).isAjaxRequest();
        when(CoreContext.isPortletRequest()).thenReturn(true);
        doReturn("PAGENAME").when(originalHttpServletRequest).getParameter(eq(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME));

        sut.invoke();

        verify(policy).setPage(eq(TEST_PAGEFLOW_NAME), eq("PAGENAME"));
    }

    @Test
    public void checkThatRequestPageIsIgnoredIfNullUnderRequestConfigurationAndNonAjaxCall() throws BaseException {
        doReturn(false).when(requestWrapper).isAjaxRequest();
        doReturn(null).when(originalHttpServletRequest).getParameter(eq(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME));
        doReturn(PreferencesAdaptor.CONFIGURE_BY_REQUEST).when(preferencesWrapper).getValue(eq(CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME), eq((String)null));

        sut.invoke();

        verify(policy, never()).setPage(eq(TEST_PAGEFLOW_NAME), eq("PAGENAME"));
    }

    @Test
    public void checkThatRequestPageIsIgnoreddUnderPreferenceConfigurationAndNonAjaxCall() throws BaseException {
        doReturn(false).when(requestWrapper).isAjaxRequest();
        doReturn("PAGENAME").when(originalHttpServletRequest).getParameter(eq(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME));

        sut.invoke();

        verify(policy, never()).setPage(eq(TEST_PAGEFLOW_NAME), eq("PAGENAME"));
    }

    @Test
    public void checkThatRequestPageIsIgnoreddUnderRequestConfigurationAndAjaxCall() throws BaseException {
        doReturn(true).when(requestWrapper).isAjaxRequest();
        doReturn("PAGENAME").when(originalHttpServletRequest).getParameter(eq(PAGE_NAME_PORTLET_REQUEST_PARAMETER_NAME));

        sut.invoke();

        verify(policy, never()).setPage(eq(TEST_PAGEFLOW_NAME), eq("PAGENAME"));
    }

    @Test
    public void checkThatRequestConfiguredPolicyIdIsSetInPageFlowContext() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        doReturn("1234").when(request).getParameter(eq(POLICY_PORTLET_REQUEST_PARAMETER_NAME));

        sut.applyRequestedPolicyId(request );

        PowerMockito.verifyStatic();
        PageFlowContext.setPolicySystemId(eq(1234L));
    }

    @Test
    public void checkThatRequestConfiguredPolicyIdSetsPageFlowContextToInitialised() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        doReturn("1").when(request).getParameter(eq(POLICY_PORTLET_REQUEST_PARAMETER_NAME));

        sut.applyRequestedPolicyId(request );

        PowerMockito.verifyStatic();
        PageFlowContext.setPageFlowInitliased(eq(true));
    }

    @Test
    public void checkThatRequestConfiguredPolicyIdClearsNextPage() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        doReturn("1").when(request).getParameter(eq(POLICY_PORTLET_REQUEST_PARAMETER_NAME));

        sut.applyRequestedPolicyId(request );

        PowerMockito.verifyStatic();
        PageFlowContext.setNextPageName(isNull(String.class));
    }

    @Test(expected=PreconditionException.class)
    public void checkThatWriteAccessDeniedToPolicyCausesException() throws BaseException {
        doReturn(false).when(confirmObjectAccessibilityToUserCommand).getWriteAccessRet();
        sut.invoke();
    }
}
