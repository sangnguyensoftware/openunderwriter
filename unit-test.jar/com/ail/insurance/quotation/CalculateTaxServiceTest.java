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
import com.ail.insurance.quotation.CalculatePolicyTaxService.CalculatePolicyTaxCommand;

public class CalculateTaxServiceTest {

    CalculateTaxService sut;
    
    @Mock
    private CalculateTaxService.CalculateTaxArgument args;
    @Mock
    private Core core;
    @Mock
    private Policy policy;
    @Mock
    private AssessmentSheet assessmentSheet;
    @Mock
    private CalculatePolicyTaxCommand calculatePolicyTaxCommand;
    
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        sut=new CalculateTaxService();
        
        sut.setArgs(args);
        sut.setCore(core);
        
        doReturn(policy).when(args).getPolicyArgRet();
        doReturn(false).when(policy).isAggregator();
        doReturn(assessmentSheet).when(policy).getAssessmentSheetFor(eq(DEFAULT_SHEET_NAME));
        doReturn("DUMMY_PRODUCT_TYPE_ID").when(args).getProductTypeIdArg();
        doReturn(calculatePolicyTaxCommand).when(core).newCommand(eq("CalculatePolicyTax"), eq(CalculatePolicyTaxCommand.class));
        doReturn(assessmentSheet).when(calculatePolicyTaxCommand).getAssessmentSheetArgRet();
    }

    @Test(expected=PreconditionException.class)
    public void nullPolicyArgIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getPolicyArgRet();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void nullProductTypeArgIsNotAllowedForAggregatorPolicies() throws BaseException {
        doReturn(true).when(policy).isAggregator();
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void blankProductTypeArgIsNotAllowedForAggregatorPolicies() throws BaseException {
        doReturn(true).when(policy).isAggregator();
        doReturn("").when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void nullProductTypeArgIsAllowedForNonAggregatorPolicies() throws BaseException {
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void blankProductTypeArgIsAllowedForNonAggregatorPolicies() throws BaseException {
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
