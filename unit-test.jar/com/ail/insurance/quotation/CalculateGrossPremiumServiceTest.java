package com.ail.insurance.quotation;

import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
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
import com.ail.insurance.quotation.CalculateBrokerageService.CalculateBrokerageCommand;
import com.ail.insurance.quotation.CalculateCommissionService.CalculateCommissionCommand;
import com.ail.insurance.quotation.CalculateGrossPremiumService.CalculateGrossPremiumArgument;
import com.ail.insurance.quotation.CalculateManagementChargeService.CalculateManagementChargeCommand;
import com.ail.insurance.quotation.CalculateTaxService.CalculateTaxCommand;
import com.ail.insurance.quotation.RefreshAssessmentSheetsService.RefreshAssessmentSheetsCommand;

public class CalculateGrossPremiumServiceTest {

    private static final String DUMMY_PRODUCT_TYPE_ID = "DUMMY_PRODUCT_TYPE_ID";

    CalculateGrossPremiumService sut;
    
    @Mock
    private CalculateGrossPremiumArgument args;
    @Mock
    private Policy policy;
    @Mock
    private AssessmentSheet assessmentSheet;
    @Mock
    private Core core;
    @Mock
    private RefreshAssessmentSheetsCommand refreshAssessmentSheetsCommand;
    @Mock
    private CalculateTaxCommand calculateTaxCommand;
    @Mock
    private CalculateCommissionCommand calculateCommissionCommand;
    @Mock
    private CalculateBrokerageCommand calculateBrokerageCommand;
    @Mock
    private CalculateManagementChargeCommand calculateManagementChargeCommand;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        sut = new CalculateGrossPremiumService();
        sut.setArgs(args);
        sut.setCore(core);
        
        doReturn(policy).when(args).getPolicyArgRet();
        doReturn(false).when(policy).isAggregator();
        doReturn(assessmentSheet).when(policy).getAssessmentSheetFor(eq(DEFAULT_SHEET_NAME));
        doReturn(DUMMY_PRODUCT_TYPE_ID).when(args).getProductTypeIdArg();
        
        doReturn(refreshAssessmentSheetsCommand).when(core).newCommand(eq(RefreshAssessmentSheetsCommand.class));
        doReturn(calculateTaxCommand).when(core).newCommand(eq(CalculateTaxCommand.class));
        doReturn(calculateCommissionCommand).when(core).newCommand(CalculateCommissionCommand.class);
        doReturn(calculateBrokerageCommand).when(core).newCommand(CalculateBrokerageCommand.class);
        doReturn(calculateManagementChargeCommand).when(core).newCommand(CalculateManagementChargeCommand.class);
    }

    @Test(expected=PreconditionException.class)
    public void policyArgCannotBeNull() throws BaseException {
        doReturn(null).when(args).getPolicyArgRet();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void policyArgAssessmentSheetCannotBeNull() throws BaseException {
        doReturn(null).when(policy).getAssessmentSheetFor(eq(DEFAULT_SHEET_NAME));
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void productTypeIdArgCannotBeBlankForAggregatorPolicies() throws BaseException {
        doReturn(true).when(policy).isAggregator();
        doReturn("").when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void productTypeIdArgCannotBeNullForAggregatorPolicies() throws BaseException {
        doReturn(true).when(policy).isAggregator();
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void productTypeIdArgCanBeBlankForNonAggregatorPolicies() throws BaseException {
        doReturn("").when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void productTypeIdArgCanBeNullForNonAggregatorPolicies() throws BaseException {
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }
    
    @Test
    public void verifyThatProductTypeIdIsPassedToSubServiced() throws BaseException {
        sut.invoke();
        verify(calculateTaxCommand).setProductTypeIdArg(eq(DUMMY_PRODUCT_TYPE_ID));
        verify(calculateCommissionCommand).setProductTypeIdArg(eq(DUMMY_PRODUCT_TYPE_ID));
        verify(calculateBrokerageCommand).setProductTypeIdArg(eq(DUMMY_PRODUCT_TYPE_ID));
        verify(calculateManagementChargeCommand).setProductTypeIdArg(eq(DUMMY_PRODUCT_TYPE_ID));
    }
}

