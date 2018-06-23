package com.ail.insurance.acceptance;

import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static com.ail.insurance.policy.PolicyStatus.DECLINED;
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;
import static com.ail.insurance.policy.PolicyStatus.SUBMITTED;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.URL;
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
import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionStatus;
import com.ail.financial.PayPal;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.CommercialProposer;
import com.ail.insurance.policy.Policy;
import com.ail.payment.PaymentRequestService.PaymentRequestCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreProxy.class, PremiumCollectionRequestService.class, Functions.class })
public class PremiumCollectionRequestServiceTest {

    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String PRODUCT_TYPE_ID = "PRODUCT_TYPE_ID";
    private static final String PRODUCT_NAMESPACE = "PRODUCT_NAMESPACE";

    PremiumCollectionRequestService sut;
    List<MoneyProvision> moneyProvisionList = new ArrayList<>();
    URL approvedURL;
    URL cancelledURL;
    URL authorisationURL;

    @Mock
    PremiumCollectionRequestService.PremiumCollectionRequestArgument collectPremiumArgument;
    @Mock
    Policy policy;
    @Mock
    PaymentSchedule paymentSchedule;
    @Mock
    MoneyProvision moneyProvision;
    @Mock
    PayPal paypal;
    @Mock
    DirectDebit directDebit;
    @Mock
    Core core;
    @Mock
    PaymentRequestCommand paymentRequestCommand;
    @Mock
    CurrencyAmount currencyAmount;
    @Mock
    CoreUser coreUser;
    @Mock
    CoreProxy coreProxy;
    @Mock
    CommercialProposer client;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        approvedURL=new URL("http://approved");
        authorisationURL=new URL("http://authorisation");
        cancelledURL=new URL("http://cancelled");

        sut = new PremiumCollectionRequestService();
        sut.setArgs(collectPremiumArgument);
        sut.setCore(core);

        doReturn(core).when(coreProxy).getCore();
        doReturn(coreUser).when(collectPremiumArgument).getCallersCore();
        doReturn(policy).when(collectPremiumArgument).getPolicyArgRet();
        doReturn(ON_RISK).when(policy).getStatus();
        doReturn(paymentSchedule).when(policy).getPaymentDetails();
        doReturn(moneyProvisionList).when(paymentSchedule).getMoneyProvision();
        doReturn(paypal).when(moneyProvision).getPaymentMethod();
        doReturn("PayPal").when(paypal).getId();
        doReturn(paymentRequestCommand).when(core).newCommand(eq("BuyWithPayPalCommand"), eq(PaymentRequestCommand.class));
        doReturn(PRODUCT_TYPE_ID).when(policy).getProductTypeId();
        doReturn(DESCRIPTION).when(moneyProvision).getDescription();
        doReturn(currencyAmount).when(moneyProvision).getAmount();
        doReturn(approvedURL).when(collectPremiumArgument).getApprovedURLArg();
        doReturn(cancelledURL).when(collectPremiumArgument).getCancelledURLArg();
        doReturn(authorisationURL).when(paymentRequestCommand).getAuthorisationURLRet();
        doReturn(MoneyProvisionStatus.NEW).when(moneyProvision).getStatus();
        doReturn(client).when(policy).getClient();

        mockStatic(Functions.class);

        when(Functions.productNameToConfigurationNamespace(eq(PRODUCT_TYPE_ID))).thenReturn(PRODUCT_NAMESPACE);

        whenNew(CoreProxy.class).withArguments(eq(PRODUCT_NAMESPACE), eq(coreUser)).thenReturn(coreProxy);

        moneyProvisionList.add(moneyProvision);
    }

    @Test(expected = PreconditionException.class)
    public void checkThatNullPolicyArgIsTrapped() throws BaseException {
        doReturn(null).when(collectPremiumArgument).getPolicyArgRet();
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

    @Test(expected = PreconditionException.class)
    public void checkThatAnEmptyPaymentDetailsIsTrapped() throws BaseException {
        doReturn(null).when(policy).getPaymentDetails();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatNullMoneyProvisionIsTrapped() throws BaseException {
        doReturn(null).when(paymentSchedule).getMoneyProvision();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void checkThatAnEmptyMoneyProvisionIsTrapped() throws BaseException {
        List<MoneyProvision> emptyList=new ArrayList<>();
        doReturn(emptyList).when(paymentSchedule).getMoneyProvision();
        sut.invoke();
    }

    @Test
    public void checkThatOnlyNewRequestsAreProcessed() throws BaseException  {
        doReturn(MoneyProvisionStatus.REQUESTED).when(moneyProvision).getStatus();
        sut.invoke();
        verify(paymentRequestCommand, never()).invoke();
    }

    @Test(expected=PreconditionException.class)
    public void checkThatNullProductIsTrapped() throws BaseException  {
        doReturn(null).when(policy).getProductTypeId();
        sut.invoke();
    }

    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();
        verify(paymentRequestCommand).setProductTypeIdArg(eq(PRODUCT_TYPE_ID));
        verify(paymentRequestCommand).setDescriptionArg(eq(DESCRIPTION));
        verify(paymentRequestCommand).setAmountArg(eq(currencyAmount));
        verify(paymentRequestCommand).setApprovedURLArg(eq(approvedURL));
        verify(paymentRequestCommand).setCancelledURLArg(eq(cancelledURL));
        verify(paymentRequestCommand, times(1)).invoke();
        verify(collectPremiumArgument).setAuthorisationURLRet(eq(authorisationURL));
    }

    @Test
    public void customerIsDerivedFromClient() throws BaseException {
        sut.invoke();
        verify(client, times(1)).getLegalName();
        verify(client, times(1)).getFirstName();
        verify(client, times(1)).getSurname();
    }

}
