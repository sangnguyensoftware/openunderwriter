package com.ail.invoice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyCommand;
import com.ail.financial.Invoice;
import com.ail.invoice.AddInvoiceNumberService.AddInvoiceNumberArgument;
import com.ail.invoice.GenerateInvoiceNumberService.GenerateInvoiceNumberCommand;

public class AddInvoiceNumberServiceTest {
    AddInvoiceNumberService sut = null;
    AddInvoiceNumberArgument args=null;
    private Invoice mockInvoice;
    private Core mockCore;

    @Before
    public void createMocks() {
        mockInvoice=mock(Invoice.class);
        when(mockInvoice.getInvoiceNumber()).thenReturn(null);
        when(mockInvoice.getProductTypeId()).thenReturn("productTypeId");

        args=mock(AddInvoiceNumberArgument.class);
        when(args.getInvoiceArgRet()).thenReturn(mockInvoice);

        mockCore=mock(Core.class);

        GenerateUniqueKeyCommand mockGenerateUniqueKeyCommand=mock(GenerateUniqueKeyCommand.class);
        when(mockGenerateUniqueKeyCommand.getKeyRet()).thenReturn(20L);
        when(mockCore.newCommand(eq(GenerateUniqueKeyCommand.class))).thenReturn(mockGenerateUniqueKeyCommand);

        GenerateInvoiceNumberCommand mockGenerateInvoiceNumberRuleCommand=mock(GenerateInvoiceNumberCommand.class);
        when(mockGenerateInvoiceNumberRuleCommand.getInvoiceNumberRet()).thenReturn("123");
        when(mockCore.newCommand(eq(GenerateInvoiceNumberCommand.class))).thenReturn(mockGenerateInvoiceNumberRuleCommand);

        sut=new AddInvoiceNumberService();
        sut.setCore(mockCore);
        sut.setArgs(args);
    }

    @Test(expected=PreconditionException.class)
    public void testInvoicePrecondition() throws Exception{
        when(args.getInvoiceArgRet()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testProductTypeIdPrecondition() throws Exception {
        when(mockInvoice.getProductTypeId()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testInvoiceNumberPrecondition() throws Exception {
        when(mockInvoice.getInvoiceNumber()).thenReturn("INV1234");
        sut.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void testInvoiceNumberPostcondition() throws Exception {
        when(mockInvoice.getInvoiceNumber()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void testHappyPath() throws BaseException {
        when(mockInvoice.getInvoiceNumber()).thenReturn(null, "123");
        sut.invoke();
        verify(mockInvoice).setInvoiceNumber("123");
        assertEquals("123", args.getInvoiceArgRet().getInvoiceNumber());
    }
}
