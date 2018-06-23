package com.ail.insurance.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.AddManualLoadingService;
import com.ail.insurance.quotation.AddManualLoadingService.AddManualLoadingArgument;
import com.ail.util.Rate;

public class AddManualLoadingServiceTest {

    private AddManualLoadingService service;
    
    @Mock
    AddManualLoadingArgument args;

    @Mock
    Core core;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this); 
        
        service=new AddManualLoadingService();
        service.setArgs(args);
        service.setCore(core);
        
    }
    
    @Test
    public void testFixedSumLoading() throws BaseException {
        
        FixedSum fixedSum = new FixedSum("fixed","fixed", new CurrencyAmount(BigDecimal.TEN, Currency.GBP));
        AssessmentSheet assessmentSheet = new AssessmentSheet();
        
        doReturn(assessmentSheet).when(args).getAssessmentSheetArg();
        doReturn(fixedSum).when(args).getCalculationLineArg();

        service.invoke();
        
        AssessmentLine line = assessmentSheet.findLineById("fixed");
        assertEquals("fixed", line.getId());
        
    }
    
    @Test
    public void testFixedSumZeroLoading() throws BaseException {
        
        FixedSum fixedSum = new FixedSum("fixed","fixed", new CurrencyAmount(BigDecimal.ZERO, Currency.GBP));
        AssessmentSheet assessmentSheet = new AssessmentSheet();
        
        doReturn(assessmentSheet).when(args).getAssessmentSheetArg();
        doReturn(fixedSum).when(args).getCalculationLineArg();

        service.invoke();
        
        AssessmentLine line = assessmentSheet.findLineById("fixed");
        assertNull("fixed", line);
        
    }
    
    @Test
    public void testPercentageLoading() throws BaseException {
        
        RateBehaviour fixedSum = new RateBehaviour(
                "rate", "rate", null, "to", "on", BehaviourType.LOAD, new Rate("10%"));
        AssessmentSheet assessmentSheet = new AssessmentSheet();
        
        doReturn(assessmentSheet).when(args).getAssessmentSheetArg();
        doReturn(fixedSum).when(args).getCalculationLineArg();

        service.invoke();
        
        AssessmentLine line = assessmentSheet.findLineById("rate");
        assertEquals("rate", line.getId());
        
    }
    
    @Test
    public void testPercentageZeroLoading() throws BaseException {
        
        RateBehaviour fixedSum = new RateBehaviour(
                "rate", "rate", null, "to", "on", BehaviourType.LOAD, new Rate("0%"));
        AssessmentSheet assessmentSheet = new AssessmentSheet();
        
        doReturn(assessmentSheet).when(args).getAssessmentSheetArg();
        doReturn(fixedSum).when(args).getCalculationLineArg();

        service.invoke();
        
        AssessmentLine line = assessmentSheet.findLineById("fixed");
        assertNull(line);
        
    }
    
    @Test
    public void testNoAddLoading() throws BaseException {
        
        SumBehaviour fixedSum = new SumBehaviour(
                "sum", "rate", null, "to", BehaviourType.LOAD, new CurrencyAmount(BigDecimal.TEN, Currency.GBP));
        AssessmentSheet assessmentSheet = new AssessmentSheet();
        
        doReturn(assessmentSheet).when(args).getAssessmentSheetArg();
        doReturn(fixedSum).when(args).getCalculationLineArg();

        service.invoke();
        
        AssessmentLine line = assessmentSheet.findLineById("sum");
        assertNull(line);
        
    }
}
