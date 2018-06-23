package com.ail.payment.implementation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.math.BigDecimal;
import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.financial.CurrencyAmount;
import com.ail.payment.PaymentRequestService.PaymentRequestArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreContext.class, CoreProxy.class, Functions.class, IWinPayPaymentRequestService.class, URL.class })
public class IWinPayPaymentRequestServiceIntegrationTest {

    private static final String DUMMY_EMAIL_ADDRESS = "richardianderson@gmail.com";
    private static final String DUMMY_PAYMENT_ID = "DUMMY_PAYMENT_ID";
    private static final String DUMMY_CREDENTIALS = "Basic NTAxNzc3MTIwNzEzMzgzNTpqbWow";
    private static final String DUMMY_CLIENT_ID = "37e29eb8-69d8-486f-80a0-f9c19fbc84e5";
    private static final String DUMMY_CLIENT_SECRET = "rO3lP7rR5rJ7qQ0rJ8eY0mK5lM3vC1qH1eK0sC6uV1aC0sV2eU";
    private static final String DUMMY_URL = "https://api.eu.apim.ibmcloud.com/tech-equity-demo/swifin/web-rpc";
    private static final String DUMMY_PRODUCT_NAMESPACE = "DUMMY_PRODUCT_NAMESPACE";
    private static final String DUMMY_PRODUCY_TYPE_ID = "DUMMY_PRODUCY_TYPE_ID";
    private static final String DUMMY_CANCELLED_URL = "http://HOST:80/CANCELLED_URL";
    private static final String DUMMY_APPROVED_URL = "http://HOST:80/APPROVED_URL";
    private static final String DUMMY_DESCRIPTION = "DUMMY_DESCRIPTION";
    private static final String DUMMY_WALLET_CURRENCY = "USD";
    private static final String DUMMY_PRINCIPAY_TYPE = "username";

    // TODO A bug in iWinPay prevents the mobile number from being recognised for payments, so for
    // now we must pass the iWinPay username as the principal.
    private static final String DUMMY_USERNAME = "3685522452679724";

    IWinPayPaymentRequestService sut;

    @Mock
    PaymentRequestArgument args;
    @Mock
    private CurrencyAmount currencyAmount;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private CoreUser coreUser;
    @Mock
    private Core core;

    private URL approvedURL;
    private URL cancelledURL;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        approvedURL = new URL(DUMMY_APPROVED_URL);
        cancelledURL = new URL(DUMMY_CANCELLED_URL);

        sut = new IWinPayPaymentRequestService();

        doReturn(new BigDecimal("0.02")).when(currencyAmount).getAmount();
        doReturn("GBP").when(currencyAmount).getCurrencyAsString();
        doReturn(currencyAmount).when(args).getAmountArg();
        doReturn(DUMMY_USERNAME).when(args).getCustomerMobileNumberArg();
        doReturn(DUMMY_EMAIL_ADDRESS).when(args).getCustomerEmailAddressArg();
        doReturn(DUMMY_DESCRIPTION).when(args).getDescriptionArg();
        doReturn(DUMMY_PRODUCY_TYPE_ID).when(args).getProductTypeIdArg();
        doReturn(approvedURL).when(args).getApprovedURLArg();
        doReturn(cancelledURL).when(args).getCancelledURLArg();
        doReturn(coreUser).when(args).getCallersCore();
        doReturn(DUMMY_PAYMENT_ID).when(args).getPaymentIdRet();

        doReturn(DUMMY_URL).when(core).getParameterValue(eq("PaymentMethods.iWinPay.URL"));
        doReturn(DUMMY_CLIENT_ID).when(core).getParameterValue(eq("PaymentMethods.iWinPay.ClientID"));
        doReturn(DUMMY_CLIENT_SECRET).when(core).getParameterValue(eq("PaymentMethods.iWinPay.ClientSecret"));
        doReturn(DUMMY_CREDENTIALS).when(core).getParameterValue(eq("PaymentMethods.iWinPay.Credentials"));
        doReturn(DUMMY_WALLET_CURRENCY).when(core).getParameterValue(eq("PaymentMethods.iWinPay.WalletCurrency"));
        doReturn(DUMMY_PRINCIPAY_TYPE).when(core).getParameterValue(eq("PaymentMethods.iWinPay.PrincipalType"), eq("username"));

        mockStatic(Functions.class);
        when(Functions.productNameToConfigurationNamespace(eq(DUMMY_PRODUCY_TYPE_ID))).thenReturn(DUMMY_PRODUCT_NAMESPACE);

        mockStatic(CoreContext.class);
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        whenNew(CoreProxy.class).withAnyArguments().thenReturn(coreProxy);
        doReturn(core).when(coreProxy).getCore();

        sut.setArgs(args);
    }

    @Test
    @Ignore
    public void testHappyPath() throws Exception {
        sut.invoke();
        verify(args, times(1)).setPaymentIdRet(any(String.class));
    }

    @Test(expected=PostconditionException.class)
    @Ignore
    public void shouldFailWithBadCredentials() throws Exception {
        doReturn("BAD CREDENTIALS").when(core).getParameterValue(eq("PaymentMethods.iWinPay.Credentials"));
        sut.invoke();
    }
}
