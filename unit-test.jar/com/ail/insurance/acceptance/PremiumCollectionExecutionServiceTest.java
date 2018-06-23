package com.ail.insurance.acceptance;

import static com.ail.insurance.acceptance.PremiumCollectionExecutionService.PAYMENT_EXECUTION_COMMAND_NAME_SUFFIX;
import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static com.ail.insurance.policy.PolicyStatus.DECLINED;
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;
import static com.ail.insurance.policy.PolicyStatus.SUBMITTED;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
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
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentMethod;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.acceptance.PremiumCollectionExecutionService.PremiumCollectionExecutionArgument;
import com.ail.insurance.policy.Policy;
import com.ail.payment.PaymentExecutionService.PaymentExecutionCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreProxy.class, PremiumCollectionExecutionService.class, Functions.class })
public class PremiumCollectionExecutionServiceTest {

    private static final String PAYMENT_METHOD_ID = "PAYMENT_METHOD_ID";
    private static final String PRODUCT_NAMESPACE = "PRODUCT_NAMESPACE";
    private static final String PRODUCT_TYPE_ID = "PRODUCT_TYPI_ID";

    PremiumCollectionExecutionService sut;

    @Mock
    PremiumCollectionExecutionArgument argument;
    @Mock
    CoreUser coreUser;
    @Mock
    CoreProxy coreProxy;
    @Mock
    Core core;
    @Mock
    Policy policy;
    @Mock
    PaymentSchedule paymentDetails;
    @Mock
    MoneyProvision moneyProvision;
    @Mock
    PaymentMethod paymentMethod;
    @Mock
    PaymentExecutionCommand paymentExecutionCommand;
    
    List<MoneyProvision> moneyProvisions=new ArrayList<MoneyProvision>();
    
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut=new PremiumCollectionExecutionService();
        sut.setArgs(argument);
        sut.setCore(core);
        
        doReturn(core).when(coreProxy).getCore();
        doReturn(coreUser).when(argument).getCallersCore();
        doReturn(policy).when(argument).getPolicyArgRet();
        doReturn(ON_RISK).when(policy).getStatus();
        doReturn(paymentDetails).when(policy).getPaymentDetails();
        doReturn(moneyProvisions).when(paymentDetails).getMoneyProvision();
        doReturn(paymentMethod).when(moneyProvision).getPaymentMethod();
        doReturn(PAYMENT_METHOD_ID).when(paymentMethod).getId();
        doReturn(PRODUCT_TYPE_ID).when(policy).getProductTypeId();
        moneyProvisions.add(moneyProvision);
        
        doReturn(paymentExecutionCommand).when(core).newCommand(
                eq(PAYMENT_METHOD_ID+PAYMENT_EXECUTION_COMMAND_NAME_SUFFIX), 
                eq(PaymentExecutionCommand.class));

        mockStatic(Functions.class);
        when(Functions.productNameToConfigurationNamespace(eq(PRODUCT_TYPE_ID))).thenReturn(PRODUCT_NAMESPACE);
        whenNew(CoreProxy.class).withArguments(eq(PRODUCT_NAMESPACE), eq(coreUser)).thenReturn(coreProxy);
    }
    
    @Test(expected=PreconditionException.class)
    public void checkThatNullPolicyArgIsTrapped() throws BaseException {
        doReturn(null).when(argument).getPolicyArgRet();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatNUllPolicyStatusIsTrapped() throws BaseException {
        doReturn(null).when(policy).getStatus();
        sut.invoke();
    }

    @Test
    public void checkThatApplicationPolicyStatusAllowed() throws BaseException {
        doReturn(APPLICATION).when(policy).getStatus();
        sut.invoke();
    }

    @Test
    public void checkThatSubmittedPolicyStatusAllowed() throws BaseException {
        doReturn(SUBMITTED).when(policy).getStatus();
        sut.invoke();
    }

    @Test
    public void checkThatQuotationlPolicyStatusAllowed() throws BaseException {
        doReturn(QUOTATION).when(policy).getStatus();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatRefferedPolicyStatusIsNotAllowed() throws BaseException {
        doReturn(REFERRED).when(policy).getStatus();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatDeclinedPolicyStatusIsNotAllowed() throws BaseException {
        doReturn(DECLINED).when(policy).getStatus();
        sut.invoke();
    }

    @Test
    public void checkThatOnRiskPolicyStatusIsAllowed() throws BaseException {
        doReturn(ON_RISK).when(policy).getStatus();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void checkThatNullPaymentDetailsIsTrapped() throws BaseException {
        doReturn(null).when(policy).getPaymentDetails();
        sut.invoke();
    }
    
    @Test
    public void checkThatNullAndEmptyMoneyProvisionsListIsTrapped() throws BaseException {
        try {
            doReturn(null).when(paymentDetails).getMoneyProvision();
            sut.invoke();
            fail("Null money provision was not trapped");
        } catch(PreconditionException e) {
            // ignore this, it's what we want.
        }

        try {
            doReturn(new ArrayList<MoneyProvision>()).when(paymentDetails).getMoneyProvision();
            sut.invoke();
            fail("Null money provision was not trapped");
        } catch(PreconditionException e) {
            // ignore this, it's what we want.
        }
    }
}
