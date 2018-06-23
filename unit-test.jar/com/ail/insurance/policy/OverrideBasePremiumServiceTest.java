package com.ail.insurance.policy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.OverrideBasePremiumService;
import com.ail.insurance.quotation.CalculateGrossPremiumService.CalculateGrossPremiumCommand;
import com.ail.insurance.quotation.OverrideBasePremiumService.OverrideBasePremiumArgument;

public class OverrideBasePremiumServiceTest {

    private OverrideBasePremiumService service;
    
    @Mock
    OverrideBasePremiumArgument args;

    @Mock
    CalculateGrossPremiumCommand cgpc;
    
    @Mock
    Core core;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this); 
        
        service=new OverrideBasePremiumService();
        service.setArgs(args);
        service.setCore(core);
        
    }
    
    @Test
    public void testBasePremiumOverride() throws BaseException {
        
        CurrencyAmount newAmount = new CurrencyAmount(BigDecimal.TEN, Currency.GBP);
        AssessmentSheet assessmentSheet = new AssessmentSheet();
        
        assessmentSheet.setLockingActor("AssessRisk");
        assessmentSheet.addFixedSum("base premium", "base to replace", new CurrencyAmount(BigDecimal.ONE, Currency.GBP));
        assessmentSheet.clearLockingActor();
        
        
        doReturn(assessmentSheet).when(args).getAssessmentSheetArg();
        doReturn(newAmount).when(args).getBasePremiumAmountArg();
        
        service.invoke();
        
        FixedSum line = (FixedSum)assessmentSheet.findLineById("base premium");
        assertEquals(newAmount, line.getAmount());
        
    }
    
    
}
