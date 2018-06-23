package com.ail.insurance.onrisk.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.document.GenerateDocumentService.GenerateDocumentCommand;
import com.ail.insurance.onrisk.GenerateCertificateDocumentArgumentImpl;
import com.ail.insurance.onrisk.GenerateCertificateDocumentService;
import com.ail.insurance.onrisk.GenerateCertificateDocumentService.GenerateCertificateDocumentArgument;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

public class TestGenerateCertificateDocumentService {
    GenerateCertificateDocumentService service;
    GenerateCertificateDocumentArgument args;
    Core mockCore;
    Policy mockPolicy;

    @Before
    public void setUp() {
        mockCore = mock(Core.class);
        service = new GenerateCertificateDocumentService();
        service.setCore(mockCore);
        args = new GenerateCertificateDocumentArgumentImpl();
        mockPolicy = mock(Policy.class);
        service.setArgs(args);
    }

    @Test
    public void testNullPolicyPreconditions() throws Exception {
        args.setPolicyArg(null);

        try {
            service.invoke();
            fail("Expected predondition (policy is null) not thrown.");
        }
        catch (PreconditionException e) {
            // Ignore. This is a good thing.
        }
    }

    @Test
    public void testPolicyStatusPrecondition() throws Exception {
        when(mockPolicy.getStatus()).thenReturn(PolicyStatus.APPLICATION);
        args.setPolicyArg(mockPolicy);

        try {
            service.invoke();
            fail("Expected predondition (policy.status is wrong) not thrown.");
        }
        catch (PreconditionException e) {
            // Ignore. This is a good thing.
        }
    }

    @Test
    public void testPolicyProductPrecondition() throws Exception {
        when(mockPolicy.getStatus()).thenReturn(PolicyStatus.ON_RISK);
        args.setPolicyArg(mockPolicy);

        try {
            service.invoke();
            fail("Expected predondition (product type is null) not thrown.");
        }
        catch (PreconditionException e) {
            // Ignore. This is a good thing.
        }
    }

    @Test
    public void testHappyPath() throws Exception {
        byte[] dummyDocument = new byte[1];
        
        when(mockPolicy.getStatus()).thenReturn(PolicyStatus.ON_RISK);
        when(mockPolicy.getProductTypeId()).thenReturn("ProductTypeID");
        args.setPolicyArg(mockPolicy);

        GenerateDocumentCommand mockGenerateDocumentCommand = mock(GenerateDocumentCommand.class);
        when(mockCore.newCommand(eq(GenerateDocumentCommand.class))).thenReturn(mockGenerateDocumentCommand);
        when(mockGenerateDocumentCommand.getRenderedDocumentRet()).thenReturn(dummyDocument);

        service.invoke();

        verify(mockGenerateDocumentCommand, times(1)).setDocumentDefinitionArg(eq("CertificateDocument"));;
        assertEquals(args.getDocumentRet(), dummyDocument);
    }

    @Test
    public void testPostcondition() throws Exception {
        when(mockPolicy.getStatus()).thenReturn(PolicyStatus.ON_RISK);
        when(mockPolicy.getProductTypeId()).thenReturn("ProductTypeID");
        args.setPolicyArg(mockPolicy);

        GenerateDocumentCommand mockGenerateDocumentCommand = mock(GenerateDocumentCommand.class);
        when(mockCore.newCommand(eq(GenerateDocumentCommand.class))).thenReturn(mockGenerateDocumentCommand);
        when(mockGenerateDocumentCommand.getRenderedDocumentRet()).thenReturn(null);

        try {
            service.invoke();
            fail("Expected postcondition (getRenderedDocumentRet() is null) not thrown.");
        }
        catch (PostconditionException e) {
            // Ignore. This is a good thing.
        }
    }
}
