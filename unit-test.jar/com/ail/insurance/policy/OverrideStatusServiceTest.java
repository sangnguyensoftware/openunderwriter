package com.ail.insurance.policy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.insurance.quotation.OverrideStatusService;
import com.ail.insurance.quotation.OverrideStatusService.OverrideStatusArgument;

public class OverrideStatusServiceTest {

    private OverrideStatusService service;
    
    @Mock
    OverrideStatusArgument args;

    @Mock
    Core core;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this); 
        
        service=new OverrideStatusService();
        service.setArgs(args);
        service.setCore(core);
        
    }
    
    @Test
    public void testNewStatus() throws BaseException {
        
        Policy policy = new Policy();
        policy.setStatus(PolicyStatus.REFERRED);
        
        PolicyStatus newStatus = PolicyStatus.ON_RISK;
        
        doReturn(policy).when(args).getPolicyArg();
        doReturn(newStatus).when(args).getPolicyStatusArg();

        service.invoke();
        
        assertEquals(PolicyStatus.ON_RISK, policy.getStatus());
        
    }
    
    
}
