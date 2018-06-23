package com.ail.insurance.quotation;

import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static com.ail.insurance.policy.PolicyStatus.DECLINED;
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;
import static com.ail.insurance.policy.PolicyStatus.SUBMITTED;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyCommand;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.AddPolicyNumberService.AddPolicyNumberArgument;
import com.ail.insurance.quotation.GeneratePolicyNumberService.GeneratePolicyNumberCommand;

public class AddPolicyNumberServiceTest {
    
    private final String POLICY_NUMBER="POLNUM";
    private AddPolicyNumberService sut;
    private AddPolicyNumberArgument mockArgs;
    private Policy mockPolicyArgRet;
    private Core mockCore;
    private GenerateUniqueKeyCommand mockGenerateUniqueKeyCommand;
    private GeneratePolicyNumberCommand mockGeneratePolicyNumberCommand;

    @Before
    public void setup() {
        mockCore=mock(Core.class);
        mockArgs=mock(AddPolicyNumberArgument.class);
        mockPolicyArgRet=mock(Policy.class);

        sut=new AddPolicyNumberService();
        sut.setArgs(mockArgs);
        sut.setCore(mockCore);
        
        when(mockArgs.getPolicyArgRet()).thenReturn(mockPolicyArgRet);

        when(mockPolicyArgRet.getStatus()).thenReturn(QUOTATION);
        when(mockPolicyArgRet.getProductTypeId()).thenReturn("Test");
        when(mockPolicyArgRet.getPolicyNumber()).thenReturn(null, POLICY_NUMBER);

        mockGenerateUniqueKeyCommand=mock(GenerateUniqueKeyCommand.class);
        mockGeneratePolicyNumberCommand=mock(GeneratePolicyNumberCommand.class);

        when(mockGeneratePolicyNumberCommand.getPolicyNumberRet()).thenReturn(POLICY_NUMBER);
        
        when(mockCore.newCommand(GenerateUniqueKeyCommand.class)).thenReturn(mockGenerateUniqueKeyCommand);
        when(mockCore.newCommand(anyString(), eq(GeneratePolicyNumberCommand.class))).thenReturn(mockGeneratePolicyNumberCommand);
    }
    
    @Test(expected=PreconditionException.class)
    public void testNullStatusPrecondition() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void testInvalidStatus() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(APPLICATION);
        try {
            sut.invoke();
            fail("APPLICATION policy status should throw a PreconditionException");
        }
        catch(PreconditionException e) {
            // expected
        }

        when(mockPolicyArgRet.getStatus()).thenReturn(DECLINED);
        try {
            sut.invoke();
            fail("DECLINED policy status should throw a PreconditionException");
        }
        catch(PreconditionException e) {
            // expected
        }

        when(mockPolicyArgRet.getStatus()).thenReturn(ON_RISK);
        try {
            sut.invoke();
            fail("ON_RISK policy status should throw a PreconditionException");
        }
        catch(PreconditionException e) {
            // expected
        }
    }

    @Test
    public void testHappyPath() throws Exception {
        sut.invoke();
        verify(mockPolicyArgRet).setPolicyNumber(eq(POLICY_NUMBER));
    }

    @Test
    public void testQuotationStatus() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(QUOTATION);
        sut.invoke();
        verify(mockPolicyArgRet).setPolicyNumber(eq(POLICY_NUMBER));
    }

    @Test
    public void testSubmittedStatus() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(SUBMITTED);
        sut.invoke();
        verify(mockPolicyArgRet).setPolicyNumber(eq(POLICY_NUMBER));
    }
    
    @Test
    public void testReferredStatus() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(REFERRED);
        sut.invoke();
        verify(mockPolicyArgRet).setPolicyNumber(eq(POLICY_NUMBER));
    }
}
