package com.ail.payment.implementation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.financial.CurrencyAmount;
import com.ail.payment.PaymentRequestService.PaymentRequestArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreContext.class, CoreProxy.class, Functions.class, IWinPayPaymentRequestService.class, URL.class })
public class IWinPayPaymentRequestServiceTest {

    private static final String DUMMY_CURRENCY = "GBP";
    private static final String DUMMY_CREDENTIALS = "DUMMY_CREDENTIALS";
    private static final String DUMMY_CLIENT_ID = "DUMMY_CLIENT_ID";
    private static final String DUMMY_URL = "DUMMY_URL";
    private static final String DUMMY_PRODUCT_NAMESPACE = "DUMMY_PRODUCT_NAMESPACE";
    private static final String DUMMY_PRODUCY_TYPE_ID = "DUMMY_PRODUCY_TYPE_ID";
    private static final String DUMMY_DESCRIPTION = "DUMMY_DESCRIPTION";
    private static final String DUMMY_CANCELLED_URL = "http://HOST:80/CANCELLED_URL";
    private static final String DUMMY_APPROVED_URL = "http://HOST:80/APPROVED_URL";
    private static final String DUMMY_MOBILE_NUMBER = "DUMMY_MOBILE_NUMBER";
    private static final String CALCULATED_URL="DUMMY_URL/requestPayment?amount=null&principal=null&expiryDate=2015-12-25&paymentType=gbpWallet.gbpPayment&paymentDescription=DUMMY_DESCRIPTION&clientId=DUMMY_CLIENT_ID";

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
    @Mock
    HttpURLConnection httpURLConnection;

    private URL approvedURL;
    private URL cancelledURL;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new IWinPayPaymentRequestService());
        doReturn(httpURLConnection).when(sut).openHttpURLConnection(eq(CALCULATED_URL));

        sut.setCore(core);

        approvedURL = new URL(DUMMY_APPROVED_URL);
        cancelledURL = new URL(DUMMY_CANCELLED_URL);

        doReturn(new BigDecimal("10")).when(currencyAmount).getAmount();
        doReturn(DUMMY_CURRENCY).when(currencyAmount).getCurrencyAsString();
        doReturn(currencyAmount).when(args).getAmountArg();
        doReturn(DUMMY_MOBILE_NUMBER).when(args).getCustomerMobileNumberArg();
        doReturn(DUMMY_DESCRIPTION).when(args).getDescriptionArg();
        doReturn(DUMMY_PRODUCY_TYPE_ID).when(args).getProductTypeIdArg();
        doReturn(approvedURL).when(args).getApprovedURLArg();
        doReturn(cancelledURL).when(args).getCancelledURLArg();
        doReturn(coreUser).when(args).getCallersCore();

        doReturn(DUMMY_URL).when(core).getParameterValue(eq("PaymentMethods.iWinPay.URL"));
        doReturn(DUMMY_CLIENT_ID).when(core).getParameterValue(eq("PaymentMethods.iWinPay.ClientID"));
        doReturn(DUMMY_CREDENTIALS).when(core).getParameterValue(eq("PaymentMethods.iWinPay.Credentials"));
        doReturn(DUMMY_CURRENCY).when(core).getParameterValue(eq("PaymentMethods.iWinPay.WalletCurrency"));

        mockStatic(Functions.class);
        when(Functions.productNameToConfigurationNamespace(eq(DUMMY_PRODUCY_TYPE_ID))).thenReturn(DUMMY_PRODUCT_NAMESPACE);

        mockStatic(CoreContext.class);
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        whenNew(CoreProxy.class).withAnyArguments().thenReturn(coreProxy);
        doReturn(core).when(coreProxy).getCore();

        sut.setArgs(args);
    }

    @Test(expected = PreconditionException.class)
    public void checkThatNullMobileNumberIsTrapped() throws BaseException {
        doReturn(null).when(args).getCustomerMobileNumberArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatMissingURLisTrapped() throws BaseException {
        doReturn(null).when(core).getParameterValue(eq("PaymentMethods.iWinPay.URL"));
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatMissingClientIDisTrapped() throws BaseException {
        doReturn(null).when(core).getParameterValue(eq("PaymentMethods.iWinPay.ClientID"));
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatMissingCredentialIsTrapped() throws BaseException {
        doReturn(null).when(core).getParameterValue(eq("PaymentMethods.iWinPay.Credentials"));
        sut.invoke();
    }

    @Test
    public void checkNonFxPaymentTypeCalculation() throws PreconditionException {
        doReturn("USD").when(currencyAmount).getCurrencyAsString();
        doReturn("USD").when(core).getParameterValue(eq("PaymentMethods.iWinPay.WalletCurrency"));

        String paymentType = sut.calculatePaymentType();

        assertEquals("usdWallet.usdPayment", paymentType);
    }

    @Test
    public void checkFxPaymentTypeCalculation() throws PreconditionException {
        doReturn("USD").when(currencyAmount).getCurrencyAsString();
        doReturn("GBP").when(core).getParameterValue(eq("PaymentMethods.iWinPay.WalletCurrency"));

        String paymentType = sut.calculatePaymentType();

        assertEquals("gbpWallet.gbpUsdFxPayment", paymentType);
    }
}
