package com.ail.payment.implementation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.math.BigDecimal;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.financial.CurrencyAmount;
import com.ail.party.Address;
import com.ail.party.Country;
import com.ail.payment.PaymentRequestService.PaymentRequestArgument;
import com.sagepay.sdk.api.ApiFactory;
import com.sagepay.sdk.api.IFormApi;
import com.sagepay.sdk.api.ProtocolVersion;
import com.sagepay.sdk.api.TransactionType;
import com.sagepay.sdk.api.messages.IFormPayment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SagePayPaymentRequestService.class, URL.class, CoreContext.class, ApiFactory.class, Country.class })
public class SagePayPaymentRequestServiceTest {

    SagePayPaymentRequestService sut;

    @Mock
    private PaymentRequestArgument args;
    @Mock
    private IFormApi iFormApi;
    @Mock
    private IFormPayment iFormPayment;
    @Mock
    private CoreProxy coreProxy;

    @Before
    public void setup() {
        sut = new SagePayPaymentRequestService();
        sut.setArgs(args);

        PowerMockito.mockStatic(ApiFactory.class);
        when(ApiFactory.getFormApi()).thenReturn(iFormApi);
        doReturn(iFormPayment).when(iFormApi).newFormPaymentRequest();

        PowerMockito.mockStatic(CoreContext.class);
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);
    }

    @Test
    public void sagePayPaymentRequestMustContainMandatoryFields() throws PreconditionException {
        doReturn("0").when(coreProxy).getParameterValue(anyString());

        CurrencyAmount amount = mock(CurrencyAmount.class);
        doReturn(amount).when(args).getAmountArg();

        URL approvedURL = PowerMockito.mock(URL.class);
        URL cancelledURL = PowerMockito.mock(URL.class);

        doReturn(approvedURL).when(args).getApprovedURLArg();
        doReturn(cancelledURL).when(args).getCancelledURLArg();

        Address address = mock(Address.class);
        doReturn(address).when(args).getCustomerAddressArg();
        doReturn("United Kingdom").when(address).getCountry();

        sut.createFormPayment();

        verify(iFormPayment).setTransactionType(TransactionType.PAYMENT);
        verify(iFormPayment).setVpsProtocol(ProtocolVersion.V_300);
        verify(iFormPayment).setAmount(any(BigDecimal.class));
        verify(iFormPayment).setCurrency(anyString());
        verify(iFormPayment).setDescription(anyString());
        verify(iFormPayment).setSuccessUrl(anyString());
        verify(iFormPayment).setFailureUrl(anyString());
        verify(iFormPayment).setCustomerName(anyString());
        verify(iFormPayment).setVendorTxCode(anyString());
        verify(iFormPayment).setBillingFirstnames(anyString());
        verify(iFormPayment).setBillingSurname(anyString());
        verify(iFormPayment).setBillingAddress1(anyString());
        verify(iFormPayment).setBillingCity(anyString());
        verify(iFormPayment).setBillingCountry(anyString());
        verify(iFormPayment).setBillingPostCode(anyString());
        verify(iFormPayment).setDeliveryFirstnames(anyString());
        verify(iFormPayment).setDeliverySurname(anyString());
        verify(iFormPayment).setDeliveryAddress1(anyString());
        verify(iFormPayment).setDeliveryCity(anyString());
        verify(iFormPayment).setDeliveryCountry(anyString());
        verify(iFormPayment).setDeliveryPostCode(anyString());

        verify(iFormPayment).setVendor(anyString());
        verify(iFormPayment).setReferrerId(anyString());
        verify(iFormPayment).setSurchargeXml(anyString());
        verify(iFormPayment).setApply3dSecure(anyInt());
        verify(iFormPayment).setVendorEmail(anyString());
        verify(iFormPayment).setSendEmail(anyInt());
        verify(iFormPayment).setEmailMessage(anyString());
    }
}
