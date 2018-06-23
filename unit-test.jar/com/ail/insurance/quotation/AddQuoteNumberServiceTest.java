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
import com.ail.insurance.quotation.AddQuoteNumberService.AddQuoteNumberArgument;
import com.ail.insurance.quotation.GenerateQuoteNumberService.GenerateQuoteNumberCommand;

public class AddQuoteNumberServiceTest {
    
    private final String QUOTE_NUMBER="QUONUM";
    private AddQuoteNumberService sut;
    private AddQuoteNumberArgument mockArgs;
    private Policy mockPolicyArgRet;
    private Core mockCore;
    private GenerateUniqueKeyCommand mockGenerateUniqueKeyCommand;
    private GenerateQuoteNumberCommand mockGenerateQuoteNumberCommand;

    @Before
    public void setup() {
        mockCore=mock(Core.class);
        mockArgs=mock(AddQuoteNumberArgument.class);
        mockPolicyArgRet=mock(Policy.class);

        sut=new AddQuoteNumberService();
        sut.setArgs(mockArgs);
        sut.setCore(mockCore);
        
        when(mockArgs.getPolicyArgRet()).thenReturn(mockPolicyArgRet);

        when(mockPolicyArgRet.getStatus()).thenReturn(APPLICATION);
        when(mockPolicyArgRet.getProductTypeId()).thenReturn("Test");
        when(mockPolicyArgRet.getQuotationNumber()).thenReturn(null, QUOTE_NUMBER);

        mockGenerateUniqueKeyCommand=mock(GenerateUniqueKeyCommand.class);
        mockGenerateQuoteNumberCommand=mock(GenerateQuoteNumberCommand.class);

        when(mockGenerateQuoteNumberCommand.getQuoteNumberRet()).thenReturn(QUOTE_NUMBER);
        
        when(mockCore.newCommand(GenerateUniqueKeyCommand.class)).thenReturn(mockGenerateUniqueKeyCommand);
        when(mockCore.newCommand(anyString(), eq(GenerateQuoteNumberCommand.class))).thenReturn(mockGenerateQuoteNumberCommand);
    }
    
    @Test(expected=PreconditionException.class)
    public void testNullStatusPrecondition() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void testInvalidStatus() throws Exception {
        when(mockPolicyArgRet.getStatus()).thenReturn(SUBMITTED);
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

        when(mockPolicyArgRet.getStatus()).thenReturn(REFERRED);
        try {
            sut.invoke();
            fail("REFERRED policy status should throw a PreconditionException");
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

        when(mockPolicyArgRet.getStatus()).thenReturn(QUOTATION);
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
        verify(mockPolicyArgRet).setQuotationNumber(eq(QUOTE_NUMBER));
    }
}
