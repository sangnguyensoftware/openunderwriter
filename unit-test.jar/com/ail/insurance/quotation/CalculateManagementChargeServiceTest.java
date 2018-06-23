package com.ail.insurance.quotation;

import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.CalculatePolicyManagementChargeService.CalculatePolicyManagementChargeCommand;

public class CalculateManagementChargeServiceTest {

    CalculateManagementChargeService sut;
    
    @Mock
    private CalculateManagementChargeService.CalculateManagementChargeArgument args;
    @Mock
    private Core core;
    @Mock
    private Policy policy;
    @Mock
    private AssessmentSheet assessmentSheet;
    @Mock
    private CalculatePolicyManagementChargeCommand calculatePolicyManagementChargeCommand;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        sut=new CalculateManagementChargeService();
        
        sut.setArgs(args);
        sut.setCore(core);
        
        doReturn(policy).when(args).getPolicyArgRet();
        doReturn(false).when(policy).isAggregator();
        doReturn(assessmentSheet).when(policy).getAssessmentSheetFor(eq(DEFAULT_SHEET_NAME));
        doReturn("DUMMY_PRODUCT_TYPE_ID").when(args).getProductTypeIdArg();
        doReturn(calculatePolicyManagementChargeCommand).when(core).newCommand(eq("CalculatePolicyManagementCharge"), eq(CalculatePolicyManagementChargeCommand.class));
        doReturn(assessmentSheet).when(calculatePolicyManagementChargeCommand).getAssessmentSheetArgRet();
    }

    @Test(expected=PreconditionException.class)
    public void nullPolicyArgIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getPolicyArgRet();
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void nullProductTypeArgIsNotAllowedForAggregationPolicies() throws BaseException {
        doReturn(true).when(policy).isAggregator();
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void blankProductTypeArgIsNotAllowedForAggregationPolicies() throws BaseException {
        doReturn(true).when(policy).isAggregator();
        doReturn("").when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void nullProductTypeArgIsAllowedForNonAggregationPolicies() throws BaseException {
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void blankProductTypeArgIsAllowedForNonAggregationPolicies() throws BaseException {
        doReturn("").when(args).getProductTypeIdArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void policyMustAlreadyHaveAnAssessmentSheet() throws BaseException {
        doReturn(null).when(policy).getAssessmentSheetFor(eq(DEFAULT_SHEET_NAME));
        sut.invoke();
    }
    
    @Test
    public void policyProductTypeIDShouldNotBeReferredTo() throws BaseException {
        sut.invoke();
        verify(policy, never()).getProductTypeId();
    }

    @Test
    public void verifyThatUnnamedAssessmentSheetMethodsAreNotUsed() throws BaseException {
        sut.invoke();
        verify(policy, never()).getAssessmentSheet();
        verify(policy, never()).setAssessmentSheet(any(AssessmentSheet.class));
    }
}
