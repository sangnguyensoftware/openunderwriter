package com.ail.payment.implementation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.URL;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Parameter;
import com.ail.payment.implementation.FetchPayPalAccessTokenService.FetchPayPalAccessTokenArgument;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.OAuthTokenCredential;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Functions.class, CoreProxy.class, FetchPayPalAccessTokenService.class, OAuthTokenCredential.class, URL.class, APIContext.class })
public class FetchPayPalAccessTokenServiceTest {

    FetchPayPalAccessTokenService sut;

    @Mock
    FetchPayPalAccessTokenArgument argument;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Core core;
    @Mock
    private OAuthTokenCredential oAuthTokenCredential;
    @Mock
    private Parameter clientId;
    @Mock
    private Parameter clientSecret;
    @Mock
    private CoreUser coreUser;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new FetchPayPalAccessTokenService();
        sut.setCore(core);
        sut.setArgs(argument);

        mockStatic(Functions.class);
        when(Functions.productNameToConfigurationNamespace(eq("PRODUCT_TYPE"))).thenReturn("PRODUCT_NAMESPACE");

        doReturn("PRODUCT_TYPE").when(argument).getProductTypeIdArg();

        whenNew(CoreProxy.class).withArguments(eq("PRODUCT_NAMESPACE"), eq(coreUser)).thenReturn(coreProxy);
        whenNew(OAuthTokenCredential.class).withArguments(eq("CLIENT_ID"), eq("CLIENT_SECRET"), any(Map.class)).thenReturn(oAuthTokenCredential);

        doReturn(core).when(coreProxy).getCore();
        doReturn(coreUser).when(argument).getCallersCore();
        doReturn("MODE").when(core).getParameterValue(eq("PaymentMethods.PayPal.Mode"));
        doReturn("CLIENT_ID").when(core).getParameterValue(eq("PaymentMethods.PayPal.ClientID"));
        doReturn("CLIENT_SECRET").when(core).getParameterValue(eq("PaymentMethods.PayPal.ClientSecret"));
        doReturn("TOKEN").when(oAuthTokenCredential).getAccessToken();
    }

    @Test(expected = PreconditionException.class)
    public void testProductTypeIdNotSupplied() throws PreconditionException, PostconditionException {
        doReturn(null).when(argument).getProductTypeIdArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testClientIdNotConfigured() throws PreconditionException, PostconditionException {
        doReturn(null).when(core).getParameterValue(eq("PaymentMethods.PayPal.ClientID"));
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testModeNotConfigured() throws PreconditionException, PostconditionException {
        doReturn(null).when(core).getParameterValue(eq("PaymentMethods.PayPal.Mode"));
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testClientSecretNotConfigured() throws PreconditionException, PostconditionException {
        doReturn(null).when(core).getParameterValue(eq("PaymentMethods.PayPal.ClientSecret"));
        sut.invoke();
    }

}
