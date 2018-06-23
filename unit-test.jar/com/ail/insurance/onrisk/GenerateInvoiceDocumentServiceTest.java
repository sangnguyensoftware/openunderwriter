package com.ail.insurance.onrisk;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.document.GenerateDocumentService.GenerateDocumentCommand;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.onrisk.GenerateInvoiceDocumentService.GenerateInvoiceDocumentArgument;
import com.ail.insurance.policy.Policy;
import com.ail.party.Party;

public class GenerateInvoiceDocumentServiceTest {
    private static final String PRODUCT_TYPE_ID = "PRODUCT_TYPE_ID";

    private GenerateInvoiceDocumentService sut;

    @Mock
    private GenerateInvoiceDocumentArgument args;
    @Mock
    private Policy policy;
    @Mock
    private CurrencyAmount premium;
    @Mock
    private Party client;
    @Mock
    private Core core;
    @Mock
    private GenerateDocumentCommand generateDocumentCommand;

    private byte[] documentRet=new byte[1];

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new GenerateInvoiceDocumentService());
        sut.setArgs(args);

        doReturn(core).when(sut).getCore();

        doReturn(policy).when(args).getPolicyArg();
        doReturn(documentRet).when(args).getDocumentRet();

        doReturn(PRODUCT_TYPE_ID).when(policy).getProductTypeId();
        doReturn(premium).when(policy).getTotalPremium();
        doReturn(client).when(policy).getClient();

        doReturn(generateDocumentCommand).when(core).newCommand(eq(GenerateDocumentCommand.class));
    }

    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNoClientFail() throws BaseException {
        doReturn(null).when(policy).getClient();
        sut.invoke();
    }


}
