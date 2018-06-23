package com.ail.payment.implementation;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.payment.PaymentRequestService.PaymentRequestArgument;
import com.stripe.model.Charge;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Charge.class })
public class StripePaymentRequestServiceTest {

    StripePaymentRequestService sut;

    @Mock
    private PaymentRequestArgument args;
    @Mock
    private Charge charge;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        sut = new StripePaymentRequestService();
        sut.setArgs(args);

        PowerMockito.mockStatic(Charge.class);
        when(Charge.create(anyMap())).thenReturn(charge);

        doReturn(new CurrencyAmount(BigDecimal.TEN, Currency.USD)).when(args).getAmountArg();
        doReturn("customer-id").when(args).getCustomerIdArg();
        doReturn("tx123").when(args).getPaymentIdRet();
    }

    @Test
    public void testStripePaymentRequest() throws BaseException {
        sut.invoke();
        verify(args).setPaymentIdRet(anyString());
    }

    @Test
    public void testDescriptionDerived() throws BaseException {
        doReturn(null).when(args).getDescriptionArg();
        sut.invoke();
        verify(args).getCustomerNameArg();
    }

    @Test
    public void testDescriptionNotDerived() throws BaseException {
        doReturn("jon").when(args).getDescriptionArg();
        sut.invoke();
        verify(args, never()).getCustomerNameArg();
    }

    @Test(expected=PreconditionException.class)
    public void testNoAmountEx() throws BaseException {
        doReturn(null).when(args).getAmountArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNoCustomerIdEx() throws BaseException {
        doReturn(null).when(args).getCustomerIdArg();
        sut.invoke();
    }
}
