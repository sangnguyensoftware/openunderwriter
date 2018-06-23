package com.ail.pageflow.service;

import static com.ail.pageflow.service.SaveRedirectsInSessionService.PAGEFLOW_FAILURE_REDIRECT_PARAM_NAME;
import static com.ail.pageflow.service.SaveRedirectsInSessionService.PAGEFLOW_SUCCESS_REDIRECT_PARAM_NAME;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.context.PreferencesWrapper;
import com.ail.core.context.RequestWrapper;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.liferay.portal.util.PortalUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class, PortalUtil.class})
public class SaveRedirectsInSessionServiceTest {
    private static final String DUMMY_FAILURE_REDIRECT = "DUMMY_FAILURE_REDIRECT";

    private static final String DUMMY_SUCCESS_REDIRECT = "DUMMY_SUCCESS_REDIRECT";

    private SaveRedirectsInSessionService sut;

    @Mock
    private ExecutePageActionArgument args;
    @Mock
    private RequestWrapper requestWrapper;
    @Mock
    private PortletRequest portletRequest;
    @Mock
    private HttpServletRequest currentRequest;
    @Mock
    private HttpServletRequest originalRequest;
    @Mock
    private PreferencesWrapper preferencesWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new SaveRedirectsInSessionService();

        sut.setArgs(args);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(PortalUtil.class);

        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getPreferencesWrapper()).thenReturn(preferencesWrapper);
        when(CoreContext.isPortletRequest()).thenReturn(true);


        doReturn(true).when(preferencesWrapper).isConfiguredByRequest();
        doReturn(false).when(preferencesWrapper).isConfiguredByPreferences();

        doReturn(originalRequest).when(requestWrapper).getServletRequest();

        doReturn(DUMMY_SUCCESS_REDIRECT).when(originalRequest).getParameter(eq(PAGEFLOW_SUCCESS_REDIRECT_PARAM_NAME));
        doReturn(DUMMY_FAILURE_REDIRECT).when(originalRequest).getParameter(eq(PAGEFLOW_FAILURE_REDIRECT_PARAM_NAME));
    }

    @Test(expected=PreconditionException.class)
    public void checkThatNullPortletRequestIsTrapped() throws BaseException {
        when(CoreContext.getRequestWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void checkThatContextIsUpdatedIfConfigureByRequest() throws BaseException {
        sut.invoke();

        PowerMockito.verifyStatic();
        PageFlowContext.setFailureRedirect(eq(DUMMY_FAILURE_REDIRECT));
        PowerMockito.verifyStatic();
        PageFlowContext.setSuccessRedirect(eq(DUMMY_SUCCESS_REDIRECT));
    }

    @Test
    public void checkThatContextIsNotUpadedIfConfigureNotByRequest() throws BaseException {
        doReturn(false).when(preferencesWrapper).isConfiguredByRequest();
        doReturn(true).when(preferencesWrapper).isConfiguredByPreferences();

        sut.invoke();

        PowerMockito.verifyStatic(Mockito.never());
        PageFlowContext.setFailureRedirect(eq(DUMMY_FAILURE_REDIRECT));

        PowerMockito.verifyStatic(Mockito.never());
        PageFlowContext.setSuccessRedirect(eq(DUMMY_SUCCESS_REDIRECT));
    }

    @Test
    public void checkThatContextIsNotUpadedIfRequestIsAjax() throws BaseException {
        doReturn(true).when(requestWrapper).isAjaxRequest();

        sut.invoke();

        PowerMockito.verifyStatic(Mockito.never());
        PageFlowContext.setFailureRedirect(eq(DUMMY_FAILURE_REDIRECT));

        PowerMockito.verifyStatic(Mockito.never());
        PageFlowContext.setSuccessRedirect(eq(DUMMY_SUCCESS_REDIRECT));
    }
}
