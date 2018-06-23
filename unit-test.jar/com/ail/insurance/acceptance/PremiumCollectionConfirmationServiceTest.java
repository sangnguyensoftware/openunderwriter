package com.ail.insurance.acceptance;

import static com.ail.financial.PaymentRecordType.REQUESTED;
import static com.ail.financial.PaymentRecordType.AUTHORISED;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentRecord;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.acceptance.PremiumCollectionAuthorisedService.PremiumCollectionAuthorisedArgument;
import com.ail.insurance.policy.Policy;

public class PremiumCollectionConfirmationServiceTest {

    PremiumCollectionAuthorisedService sut;
    List<PaymentRecord> paymentHistory = new ArrayList<PaymentRecord>();
    List<MoneyProvision> moneyProvisionList = new ArrayList<MoneyProvision>();
    
    @Mock
    PremiumCollectionAuthorisedArgument argument;
    @Mock
    Policy policy;
    @Mock
    PaymentRecord paymentRequest;
    @Mock
    PaymentRecord requestApproved;
    @Mock
    PaymentSchedule paymentSchedule;
    @Mock
    MoneyProvision requestedMoneyProvision;
    @Mock
    MoneyProvision cancelledMoneyProvision;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        sut=new PremiumCollectionAuthorisedService();
        sut.setArgs(argument);
        
        doReturn(policy).when(argument).getPolicyArgRet();
        doReturn(paymentHistory).when(policy).getPaymentHistory();
        paymentHistory.add(paymentRequest);
        doReturn(REQUESTED).when(paymentRequest).getType();
        doReturn(AUTHORISED).when(requestApproved).getType();
        doReturn(paymentSchedule).when(policy).getPaymentDetails();
        doReturn(moneyProvisionList).when(paymentSchedule).getMoneyProvision();
        moneyProvisionList.add(requestedMoneyProvision);
    }
    
    @Test(expected=PreconditionException.class)
    public void checkThatNullPolicyArgIstTapped() throws BaseException {
        doReturn(null).when(argument).getPolicyArgRet();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void checkThatEmptyPaymentHistoryArgIstTapped() throws BaseException {
        doReturn(null).when(policy).getPaymentHistory();
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void checkThatNoRequestsIsTrapped() throws BaseException {
        moneyProvisionList.clear();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void checkThatNoOutstandingRequestsIsTrapped() throws BaseException {
        moneyProvisionList.clear();
        moneyProvisionList.add(cancelledMoneyProvision);
        sut.invoke();
    }
}
