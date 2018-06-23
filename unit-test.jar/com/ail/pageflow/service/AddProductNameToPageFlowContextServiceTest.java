package com.ail.pageflow.service;

import static com.ail.core.context.PreferencesAdaptor.CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.PreferencesAdaptor.PRODUCT_PORTLET_PREFERENCE_NAME;
import static com.ail.core.context.RequestAdaptor.PRODUCT_PORTLET_REQUEST_PARAMETER_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

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
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.context.PreferencesAdaptor;
import com.ail.core.context.PreferencesWrapper;
import com.ail.core.context.RequestWrapper;
import com.ail.pageflow.ExecutePageActionService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, AddProductNameToPageFlowContextService.class})
public class AddProductNameToPageFlowContextServiceTest {
    AddProductNameToPageFlowContextService sut;

    @Mock
    ExecutePageActionService.ExecutePageActionArgument argument;
    @Mock
    PortletRequest portletRequest;
    @Mock
    PreferencesWrapper preferencesWrappper;
    @Mock
    PortletSession portletSession;
    @Mock
    CoreProxy coreProxy;
    @Mock
    RequestWrapper requestWrapper;

    @Before
    public void setupSut() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new AddProductNameToPageFlowContextService());
        sut.setArgs(argument);

        PowerMockito.mockStatic(CoreContext.class);

        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getPreferencesWrapper()).thenReturn(preferencesWrappper);
        when(requestWrapper.getPortletRequest()).thenReturn(portletRequest);
        when(portletRequest.getPortletSession()).thenReturn(portletSession);
    }

    @Test
    public void checkThatSessionAttributeIsUsed() throws BaseException {
        doReturn("TEST_ATTR").when(sut).getProductNameFromPageFlowContext();
        doReturn(null).when(preferencesWrappper).getValue(eq(PRODUCT_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(portletRequest).getProperty(eq(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setProductNameToPageFlowContext(eq("TEST_ATTR"));

        sut.invoke();

        verify(sut).setProductNameToPageFlowContext(eq("TEST_ATTR"));
    }

    @Test
    public void checkThatPortletPreferenceIsUsedWhenConfigured() throws BaseException {
        doReturn(PreferencesAdaptor.CONFIGURE_BY_PREFERENCES).when(preferencesWrappper).getValue(eq(CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME), any(String.class));
        doReturn(true).when(preferencesWrappper).isConfiguredByPreferences();
        doReturn(null).when(sut).getProductNameFromPageFlowContext();
        doReturn("TEST_PREF").when(preferencesWrappper).getValue(eq(PRODUCT_PORTLET_PREFERENCE_NAME), any(String.class));
        doReturn(null).when(portletRequest).getProperty(eq(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setProductNameToPageFlowContext(eq("TEST_PREF"));

        sut.invoke();

        verify(sut).setProductNameToPageFlowContext(eq("TEST_PREF"));
    }

    @Test
    public void checkThatPortletPreferenceIsNotUsedWhenNotConfigured() throws BaseException {
        doReturn(PreferencesAdaptor.CONFIGURE_BY_SESSION).when(preferencesWrappper).getValue(eq(CONFIGURATION_SOURCE_PORTLET_PREFERENCE_NAME), any(String.class));

        doReturn("TEST_ATTR").when(sut).getProductNameFromPageFlowContext();
        doReturn("TEST_PREF").when(preferencesWrappper).getValue(eq(PRODUCT_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(portletRequest).getProperty(eq(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setProductNameToPageFlowContext(eq("TEST_ATTR"));

        sut.invoke();

        verify(sut).setProductNameToPageFlowContext(eq("TEST_ATTR"));
    }

    @Test
    public void checkThatRequestParamIsUsed() throws BaseException {
        doReturn("TEST_ATTR").when(sut).getProductNameFromPageFlowContext();
        doReturn("TEST_PREF").when(preferencesWrappper).getValue(eq(PRODUCT_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn("TEST_PARAM").when(requestWrapper).getProperty(eq(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setProductNameToPageFlowContext(eq("TEST_PARAM"));

        sut.invoke();

        verify(sut).setProductNameToPageFlowContext(eq("TEST_PARAM"));
    }

    @Test(expected = PreconditionException.class)
    public void checkNullPortletRequestPreconditionsIsChecked() throws BaseException {
        when(CoreContext.getRequestWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkNullPortletPreferencesPreconditionsIsChecked() throws BaseException {
        when(CoreContext.getPreferencesWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void checkThatNoSelectionLeadsToNull() throws BaseException {
        doReturn(null).when(sut).getProductNameFromPageFlowContext();
        doReturn(null).when(preferencesWrappper).getValue(eq(PRODUCT_PORTLET_PREFERENCE_NAME), (String) isNull());
        doReturn(null).when(portletRequest).getProperty(eq(PRODUCT_PORTLET_REQUEST_PARAMETER_NAME));
        doNothing().when(sut).setProductNameToPageFlowContext((String)isNull());

        sut.invoke();

        verify(sut).setProductNameToPageFlowContext((String)isNull());
    }

    @Test
    public void testCoreAddedToPageFlowContext() throws Exception {
        doReturn("TEST_ATTR").when(sut).getProductNameFromPageFlowContext();
        doNothing().when(sut).setProductNameToPageFlowContext("TEST_ATTR");
        CoreProxy productCoreProxy = Mockito.mock(CoreProxy.class);
        whenNew(CoreProxy.class).withArguments("TEST_ATTR.Registry").thenReturn(productCoreProxy);
        sut.invoke();
        verifyNew(CoreProxy.class).withArguments("TEST_ATTR.Registry");
    }
}
