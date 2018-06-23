package com.ail.pageflow.service;

import static com.ail.core.context.PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.PreferencesAdaptor.PAGEFLOW_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.RequestAdaptor.PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import com.ail.core.PreconditionException;
import com.ail.core.context.PreferencesAdaptor;
import com.ail.core.context.PreferencesWrapper;
import com.ail.core.context.RequestWrapper;
import com.ail.pageflow.ExecutePageActionService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class AddPageFlowNameToPageFlowContextServiceTest {
    AddPageFlowNameToPageFlowContextService sut;

    @Mock
    ExecutePageActionService.ExecutePageActionArgument argument;
    @Mock
    RequestWrapper requestWrapper;
    @Mock
    PreferencesWrapper preferencesWrapper;
    @Mock
    PortletSession portletSession;
    @Mock
    HttpServletRequest httpServletRequest;

    @Before
    public void setupSut() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(CoreContext.class);

        sut = spy(new AddPageFlowNameToPageFlowContextService());
        sut.setArgs(argument);

        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getPreferencesWrapper()).thenReturn(preferencesWrapper);
        when(requestWrapper.getServletRequest()).thenReturn(httpServletRequest);
    }

    @Test
    public void checkThatSessionAttributeIsUsed() throws BaseException {
        doReturn("TEST_ATTR").when(sut).getPageFlowNameFromPageFlowContext();
        doReturn(null).when(preferencesWrapper).getValue(eq(PAGEFLOW_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(requestWrapper).getProperty(eq(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setPageFlowNameToPageFlowContext(eq("TEST_ATTR"));

        sut.invoke();

        verify(sut).setPageFlowNameToPageFlowContext(eq("TEST_ATTR"));
    }

    @Test
    public void checkThatPortletPreferenceIsUsedWhenConfigured() throws BaseException {
        doReturn(true).when(preferencesWrapper).isConfiguredByPreferences();
        doReturn("TEST_ATTR").when(sut).getPageFlowNameFromPageFlowContext();
        doReturn("TEST_PREF").when(preferencesWrapper).getValue(eq(PAGEFLOW_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(requestWrapper).getProperty(eq(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setPageFlowNameToPageFlowContext(eq("TEST_PREF"));

        sut.invoke();

        verify(sut).setPageFlowNameToPageFlowContext(eq("TEST_PREF"));
    }

    @Test
    public void checkThatPortletPreferenceIsNotUsedWhenNotConfigured() throws BaseException {
        doReturn(PreferencesAdaptor.CONFIGURE_BY_SESSION).when(preferencesWrapper).getValue(eq(CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME), any(String.class));

        doReturn("TEST_ATTR").when(sut).getPageFlowNameFromPageFlowContext();
        doReturn("TEST_PREF").when(preferencesWrapper).getValue(eq(PAGEFLOW_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(requestWrapper).getProperty(eq(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setPageFlowNameToPageFlowContext(eq("TEST_ATTR"));

        sut.invoke();

        verify(sut).setPageFlowNameToPageFlowContext(eq("TEST_ATTR"));
    }

    @Test
    public void checkThatRequestParamIsUsed() throws BaseException {
        doReturn(false).when(preferencesWrapper).isConfiguredByPreferences();
        doReturn(false).when(preferencesWrapper).isConfiguredByRequest();

        doReturn("TEST_ATTR").when(sut).getPageFlowNameFromPageFlowContext();
        doReturn("TEST_PREF").when(preferencesWrapper).getValue(eq(PAGEFLOW_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn("TEST_PARAM").when(requestWrapper).getProperty(eq(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setPageFlowNameToPageFlowContext(eq("TEST_PARAM"));

        sut.invoke();

        verify(sut).setPageFlowNameToPageFlowContext(eq("TEST_PARAM"));
    }

    @Test(expected = PreconditionException.class)
    public void checkNullRequestWrapperPreconditionsIsChecked() throws BaseException {
        when(CoreContext.getRequestWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkNullPortletPreferencesPreconditionsIsChecked() throws BaseException {
        when(CoreContext.getPreferencesWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void checkThatPostconditionsAreChecked() throws BaseException {
        doReturn(null).when(sut).getPageFlowNameFromPageFlowContext();
        doReturn(null).when(preferencesWrapper).getValue(eq(PAGEFLOW_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(requestWrapper).getProperty(eq(PAGEFLOW_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setPageFlowNameToPageFlowContext((String)isNull());

        sut.invoke();

        verify(sut).setPageFlowNameToPageFlowContext((String)isNull());
    }
}
