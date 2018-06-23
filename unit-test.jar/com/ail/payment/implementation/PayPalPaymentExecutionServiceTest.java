package com.ail.payment.implementation;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.payment.PaymentExecutionService.PaymentExecutionArgument;
import com.ail.payment.implementation.FetchPayPalAccessTokenService.FetchPayPalAccessTokenCommand;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PayPalPaymentExecutionService.class, Payment.class, PaymentExecution.class, APIContext.class })
public class PayPalPaymentExecutionServiceTest {

    private static final String SALE_ID = "SALE_ID";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String PRODUCT_ID = "PRODUCT_ID";
    private static final String TRANSACTION_ID = "TRANSACTION_ID";
    private static final String PAYER_ID = "PAYER_ID";

    PayPalPaymentExecutionService sut;

    @Mock
    PaymentExecutionArgument argument;
    @Mock
    Core core;
    @Mock
    FetchPayPalAccessTokenCommand fetchPayPalAccessTokenCommand;
    @Mock
    Payment payment;
    @Mock
    PayPalRESTException palPalRESTException;
    @Mock
    PaymentExecution paymentExecution;
    @Mock
    Transaction transaction;
    @Mock
    RelatedResources relatedResources;
    @Mock
    Sale sale;
    @Mock
    APIContext apiContext;
    
    List<Transaction> transations=new ArrayList<Transaction>();
    List<RelatedResources> relatedResourcesList=new ArrayList<RelatedResources>();
     
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new PayPalPaymentExecutionService();
        sut.setArgs(argument);
        sut.setCore(core);

        doReturn(PAYER_ID).when(argument).getPayerIdArg();
        doReturn(TRANSACTION_ID).when(argument).getPaymentIdArg();
        doReturn(PRODUCT_ID).when(argument).getProductTypeIdArg();

        doReturn(fetchPayPalAccessTokenCommand).when(core).newCommand(FetchPayPalAccessTokenCommand.class);
        doReturn(ACCESS_TOKEN).when(fetchPayPalAccessTokenCommand).getAccessTokenRet();
        
        mockStatic(Payment.class);
        when(Payment.get(eq(apiContext), eq(TRANSACTION_ID))).thenReturn(payment);

        doReturn(payment).when(payment).execute(eq(apiContext), eq(paymentExecution));
        doReturn(transations).when(payment).getTransactions();
        transations.add(transaction);
        
        doReturn(relatedResourcesList).when(transaction).getRelatedResources();
        relatedResourcesList.add(relatedResources);
        
        doReturn(sale).when(relatedResources).getSale();
        doReturn(SALE_ID).when(sale).getId();
        
        whenNew(PaymentExecution.class).withNoArguments().thenReturn(paymentExecution);
        whenNew(APIContext.class).withAnyArguments().thenReturn(apiContext);
    }

    @Test
    public void checkThatNullPayerIdIsTrapped() throws BaseException {
        try {
            doReturn(null).when(argument).getPayerIdArg();
            sut.invoke();
            fail("Service did not trap null payer id");
        } catch (PreconditionException e) {
            // ignore - we want this
        }

        try {
            doReturn("").when(argument).getPayerIdArg();
            sut.invoke();
            fail("Service did not trap empty payer id");
        } catch (PreconditionException e) {
            // ignore - we want this
        }
    }

    @Test
    public void checkThatUndefinedTransactionIdIsTrapped() throws BaseException {
        try {
            doReturn(null).when(argument).getPaymentIdArg();
            sut.invoke();
            fail("Service did not trap null transaction id");
        } catch (PreconditionException e) {
            // ignore - we want this
        }

        try {
            doReturn("").when(argument).getPaymentIdArg();
            sut.invoke();
            fail("Service did not trap empty transaction id");
        } catch (PreconditionException e) {
            // ignore - we want this
        }
    }

    @Test
    public void checkThatAccessTokenIsFetchedUsingSuppliedProductId() throws BaseException {
        sut.invoke();
        verify(fetchPayPalAccessTokenCommand).setProductTypeIdArg(eq(PRODUCT_ID));
        verify(fetchPayPalAccessTokenCommand).invoke();
    }

    @Test
    public void checkThatPaymentIsCreatedWithCorrectValues() throws BaseException, PayPalRESTException {
        sut.invoke();
        verifyStatic();
        Payment.get(eq(apiContext), eq(TRANSACTION_ID));
    }

    @Test(expected=PreconditionException.class)
    public void checkThatPayPalExeceptionIsCaughtAndWrapped() throws PayPalRESTException, BaseException {
        when(Payment.get(eq(apiContext), eq(TRANSACTION_ID))).thenThrow(palPalRESTException);
        sut.invoke();
    }
    
    @Test
    public void checkThatPaymentExecutionIsCorrectlyPopulate() throws BaseException {
        sut.invoke();
        verify(paymentExecution).setPayerId(eq(PAYER_ID));
    }

    @Test
    public void checkThatPaymentIsExecutedWithCorrectArguments() throws BaseException, PayPalRESTException {
        sut.invoke();
        verify(payment).execute(eq(apiContext), eq(paymentExecution));
    }
    
    @Test
    public void checkThatSaleIdIsReadAndReturned() throws BaseException {
        sut.invoke();
        verify(argument).setSaleIdRet(eq(SALE_ID));
    }
}
