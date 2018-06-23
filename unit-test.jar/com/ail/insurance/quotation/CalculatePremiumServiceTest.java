package com.ail.insurance.quotation;

import static com.ail.core.product.Product.AGGREGATOR_CONFIGURATION_GROUP_NAME;
import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;
import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

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
import com.ail.core.ExceptionRecord;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.product.Product;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.AssessRiskService.AssessRiskCommand;
import com.ail.insurance.quotation.CalculateGrossPremiumService.CalculateGrossPremiumCommand;
import com.ail.insurance.quotation.EnforceComplianceService.EnforceComplianceCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreProxy.class, CalculatePremiumService.class})
public class CalculatePremiumServiceTest {

    private static final String DUMMY_PRODUCT_TYPE_ID = "DUMMY_PRODUCT_TYPE_ID";

    private CalculatePremiumService sut;

    @Mock
    private CalculatePremiumService.CalculatePremiumArgument args;
    @Mock
    private Core core;
    @Mock
    private CoreUser coreUser;
    @Mock
    private Policy policy;
    @Mock
    private AssessRiskCommand assessRiskCommand;
    @Mock
    private AssessmentSheet assessmentSheet;
    @Mock
    private CalculateGrossPremiumCommand calculateGrossPremiumCommand;
    @Mock
    private EnforceComplianceCommand enforceComplianceCommand;
    @Mock
    private CoreProxy coreProxy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut=new CalculatePremiumService();

        sut.setArgs(args);
        sut.setCore(core);

        doReturn(coreUser).when(args).getCallersCore();
        doReturn(policy).when(args).getPolicyArgRet();
        doReturn(APPLICATION).when(policy).getStatus();
        doReturn(assessmentSheet).when(policy).getAssessmentSheetFor(eq(DEFAULT_SHEET_NAME));
        doReturn(DUMMY_PRODUCT_TYPE_ID).when(policy).getProductTypeId();
        whenNew(CoreProxy.class).withAnyArguments().thenReturn(coreProxy);
        doReturn(core).when(coreProxy).getCore();
        doReturn(null).when(core).getGroup(eq(Product.AGGREGATOR_CONFIGURATION_GROUP_NAME));

        doReturn(assessRiskCommand).when(core).newCommand(eq(AssessRiskCommand.class));
        doReturn(calculateGrossPremiumCommand).when(core).newCommand(eq(CalculateGrossPremiumCommand.class));
        doReturn(enforceComplianceCommand).when(core).newCommand(eq(EnforceComplianceCommand.class));
        doReturn(policy).when(assessRiskCommand).getPolicyArgRet();
    }

    @Test(expected=PreconditionException.class)
    public void policyArgCannotBeNull() throws PreconditionException, BaseException {
        doReturn(null).when(args).getPolicyArgRet();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void policyStatusCannnotBeNull() throws BaseException {
        doReturn(null).when(policy).getStatus();
        sut.invoke();
    }

    @Test
    public void policyStatusOfApplicationShouldWork() throws BaseException {
        doReturn(APPLICATION).when(policy).getStatus();
        sut.invoke();
    }

    @Test
    public void anyPolicyStatusExceptApplicationShoulFail() throws BaseException {
        for(PolicyStatus s: PolicyStatus.values() ) {
            if (!APPLICATION.equals(s)) {
                doReturn(s).when(policy).getStatus();
                try {
                    sut.invoke();
                    fail("Policy status: "+s+" should cause a precondition exception");
                }
                catch(PreconditionException e) {
                    // this is what we want.
                }
            }
        }
    }

    @Test
    public void productTypeMustBePassedOnAsArgument() throws BaseException {
        sut.invoke();
        verify(assessRiskCommand).setProductTypeIdArg(eq(DUMMY_PRODUCT_TYPE_ID));
        verify(calculateGrossPremiumCommand).setProductTypeIdArg(eq(DUMMY_PRODUCT_TYPE_ID));
    }

    @Test
    public void testAggregatorProductInvoke() throws BaseException {
        Group aggregator=new Group();
        aggregator.setName(AGGREGATOR_CONFIGURATION_GROUP_NAME);

        Parameter parameter=new Parameter();
        parameter.setName("TEST_PRODUCT_ONE");
        aggregator.getParameter().add(parameter);

        parameter=new Parameter();
        parameter.setName("TEST_PRODUCT_TWO");
        aggregator.getParameter().add(parameter);

        parameter=new Parameter();
        parameter.setName("TEST_PRODUCT_THREE");
        aggregator.getParameter().add(parameter);

        doReturn(aggregator).when(core).getGroup(eq(AGGREGATOR_CONFIGURATION_GROUP_NAME));

        sut.invoke();

        verify(assessRiskCommand).setProductTypeIdArg(eq("TEST_PRODUCT_ONE"));
        verify(calculateGrossPremiumCommand).setProductTypeIdArg(eq("TEST_PRODUCT_ONE"));
        verify(assessRiskCommand).setProductTypeIdArg(eq("TEST_PRODUCT_TWO"));
        verify(calculateGrossPremiumCommand).setProductTypeIdArg(eq("TEST_PRODUCT_TWO"));
        verify(assessRiskCommand).setProductTypeIdArg(eq("TEST_PRODUCT_THREE"));
        verify(calculateGrossPremiumCommand).setProductTypeIdArg(eq("TEST_PRODUCT_THREE"));
    }

    @Test
    public void testAggregatorProductInvokeException() throws BaseException {

        Group aggregator=new Group();
        aggregator.setName(AGGREGATOR_CONFIGURATION_GROUP_NAME);

        Parameter parameter=new Parameter();
        parameter.setName("TEST_PRODUCT_ONE");
        aggregator.getParameter().add(parameter);


        BaseException ex = mock(BaseException.class);

        doReturn("err").when(ex).toString();
        doReturn(new StackTraceElement[]{}).when(ex).getStackTrace();

        doReturn(aggregator).when(core).getGroup(eq(AGGREGATOR_CONFIGURATION_GROUP_NAME));
        doThrow(ex).when(assessRiskCommand).invoke();

        sut.invoke();

        verify(assessRiskCommand).setProductTypeIdArg(eq("TEST_PRODUCT_ONE"));
        verify(calculateGrossPremiumCommand, never()).setProductTypeIdArg(eq("TEST_PRODUCT_ONE"));
        verify(core).logError(eq("Failed to run Calculate Premium Service for product TEST_PRODUCT_ONE: policy id:0"));

        verify(policy).addException(any(ExceptionRecord.class));

    }

}
