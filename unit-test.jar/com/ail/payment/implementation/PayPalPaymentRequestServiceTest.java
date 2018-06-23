package com.ail.payment.implementation;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.financial.CurrencyAmount;
import com.ail.payment.PaymentRequestService;
import com.ail.payment.implementation.FetchPayPalAccessTokenService.FetchPayPalAccessTokenCommand;
import com.liferay.portal.kernel.util.PropsUtil;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PayPalPaymentRequestService.class, URL.class, Amount.class, Transaction.class, Payer.class, Payment.class, RedirectUrls.class, PropsUtil.class })
public class PayPalPaymentRequestServiceTest {

    private static final String MADE_UP_PORT = "1234";

    private static final String MADEUP_SERVER_NAME = "madeup.server.name";

    private PayPalPaymentRequestService sut;

    @Mock
    private PaymentRequestService.PaymentRequestArgument paymentRequestArgument;
    @Mock
    private FetchPayPalAccessTokenCommand fetchPayPalAccessTokenCommand;
    @Mock
    private CurrencyAmount currencyAmount;
    @Mock
    private APIContext apiContext;
    @Mock
    private Amount amount;
    @Mock
    private Transaction transaction;
    @Mock
    private Payer payer;
    @Mock
    private Payment payment;
    @Mock
    private RedirectUrls redirectUrls;
    @Mock
    private Links links;
    @Mock
    private Core core;

    private URL approvedURL;
    private URL cancelledURL;
    private URL authorisationURL;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new PayPalPaymentRequestService();
        sut.setCore(core);
        sut.setArgs(paymentRequestArgument);

        whenNew(APIContext.class).withAnyArguments().thenReturn(apiContext);
        whenNew(Amount.class).withAnyArguments().thenReturn(amount);
        whenNew(Transaction.class).withAnyArguments().thenReturn(transaction);
        whenNew(Payer.class).withAnyArguments().thenReturn(payer);
        whenNew(Payment.class).withNoArguments().thenReturn(payment);
        whenNew(RedirectUrls.class).withAnyArguments().thenReturn(redirectUrls);

        approvedURL = new URL("http://HOST:80/APPROVED_URL");
        cancelledURL = new URL("http://HOST:80/CANCELLED_URL");
        authorisationURL = PowerMockito.mock(URL.class);

        doReturn(currencyAmount).when(paymentRequestArgument).getAmountArg();
        doReturn("DESCRIPTION").when(paymentRequestArgument).getDescriptionArg();
        doReturn(approvedURL).when(paymentRequestArgument).getApprovedURLArg();
        doReturn(cancelledURL).when(paymentRequestArgument).getCancelledURLArg();
        doReturn(authorisationURL).when(paymentRequestArgument).getAuthorisationURLRet();
        doReturn("PRODUCT_TYPE").when(paymentRequestArgument).getProductTypeIdArg();

        doReturn(payment).when(payment).create(eq(apiContext));
        doReturn(redirectUrls).when(payment).getRedirectUrls();
        doReturn(Arrays.asList(links)).when(payment).getLinks();
        doReturn("approval_url").when(links).getRel();
        doReturn("http://localhost:8080").when(links).getHref();
        doReturn("CURRENCY").when(currencyAmount).getCurrencyAsString();
        doReturn("AMOUNT").when(currencyAmount).getAmountAsString();

        doReturn(fetchPayPalAccessTokenCommand).when(core).newCommand(eq(FetchPayPalAccessTokenCommand.class));
        doReturn("ACCESS_TOKEN").when(fetchPayPalAccessTokenCommand).getAccessTokenRet();
        doReturn("MODE").when(fetchPayPalAccessTokenCommand).getModeRet();

        PowerMockito.mockStatic(PropsUtil.class);
        when(PropsUtil.get(eq("web.server.host"))).thenReturn(MADEUP_SERVER_NAME);
        when(PropsUtil.get(eq("web.server.http.port"))).thenReturn(MADE_UP_PORT);

    }

    @Test(expected = PreconditionException.class)
    public void checkThatNullCurrencyAmountArgIsChecked() throws BaseException {
        doReturn(null).when(paymentRequestArgument).getAmountArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void ensureApprovedURLArgIsChecked() throws BaseException {
        doReturn(null).when(paymentRequestArgument).getApprovedURLArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void ensureCancelledURLArgIsChecked() throws BaseException {
        doReturn(null).when(paymentRequestArgument).getCancelledURLArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void ensureDescriptionArgIsChecked() throws BaseException {
        doReturn(null).when(paymentRequestArgument).getDescriptionArg();
        sut.invoke();
    }

    @Test
    public void ensureCurrencyAmountIsPassedToPayPal() throws BaseException {
        sut.invoke();
        verify(amount).setCurrency(eq("CURRENCY"));
        verify(amount).setTotal(eq("AMOUNT"));
    }

    @Test
    public void verifyThatApproveURLIsFoundInPaypalPayment() throws BaseException {
        sut.invoke();
        verify(links).getHref();
    }

    @Test(expected = PostconditionException.class)
    public void verifyThatPostconditionCheckCatchesNullURL() throws BaseException {
        doReturn(null).when(paymentRequestArgument).getAuthorisationURLRet();
        sut.invoke();
    }

    @Test
    public void verifyThatForwardingURLArePassedToPayPal() throws BaseException {
        sut.invoke();
        verify(redirectUrls).setCancelUrl(endsWith("CANCELLED_URL"));
        verify(redirectUrls).setReturnUrl(endsWith("APPROVED_URL"));
    }

    @Test
    public void verifyThatDescriptionIsPassedToPayPal() throws BaseException {
        sut.invoke();
        verify(transaction).setDescription(eq("DESCRIPTION"));
    }

    @Test
    public void verifyThatTransactionModeIsPassedToPayPal() throws BaseException {
        sut.invoke();
        verify(apiContext).setConfigurationMap(mapContains("mode", "MODE"));
    }

    private Map<String, String> mapContains(String key, String value) {
        return argThat(new MapContains(key, value));
    }

    class MapContains extends ArgumentMatcher<Map<String,String>>{
        private String key;
        private String value;

        public MapContains(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean matches(Object argument) {
            @SuppressWarnings("unchecked")
            Map<String,String> arg=(Map<String,String>)argument;
            return (arg.containsKey(key) && value.equals(arg.get(key)));
        }
    }
}
