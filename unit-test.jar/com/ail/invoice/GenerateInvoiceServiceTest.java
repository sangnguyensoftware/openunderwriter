package com.ail.invoice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.document.RenderDocumentService.RenderDocumentCommand;
import com.ail.core.document.model.DocumentDefinition;
import com.ail.financial.Invoice;
import com.ail.financial.MoneyProvision;
import com.ail.party.Party;
import com.ail.invoice.GenerateInvoiceService.GenerateInvoiceArgument;

public class GenerateInvoiceServiceTest {
    private GenerateInvoiceService sut;
    private Core mockCore;
    private GenerateInvoiceArgument mockArgs;
    private Invoice mockInvoice;
    private Party mockFrom;
    private Party mockAddressee;
    private List<MoneyProvision> mockFinancialAmounts;
    private MoneyProvision mockFinancialAmount;
    private Party mockRegisteredOffice;
    private byte[] document;
    private DocumentDefinition mockDocumentDef;
    private RenderDocumentCommand mockRenderCommand;

    @Before
    public void setupMocks() {
        mockCore = mock(Core.class);
        mockArgs = mock(GenerateInvoiceArgument.class);
        mockInvoice = mock(Invoice.class);
        mockFrom = mock(Party.class);
        mockAddressee = mock(Party.class);
        mockRegisteredOffice = mock(Party.class);
        mockDocumentDef=mock(DocumentDefinition.class);
        document=new byte[10];
        mockFinancialAmounts = new ArrayList<MoneyProvision>();
        mockFinancialAmount = mock(MoneyProvision.class);
        mockFinancialAmounts.add(mockFinancialAmount);
        mockRenderCommand = mock(RenderDocumentCommand.class);

        sut = new GenerateInvoiceService();
        sut.setCore(mockCore);
        sut.setArgs(mockArgs);

        when(mockArgs.getDocumentRet()).thenReturn(document);
        when(mockArgs.getInvoiceArg()).thenReturn(mockInvoice);
        when(mockInvoice.getProductTypeId()).thenReturn("productTypeId");
        when(mockInvoice.getFrom()).thenReturn(mockFrom);
        when(mockInvoice.getAddressee()).thenReturn(mockAddressee);
        when(mockInvoice.getRegisteredOffice()).thenReturn(mockRegisteredOffice);
        when(mockInvoice.getFinancialAmounts()).thenReturn(mockFinancialAmounts);
        when(mockInvoice.getInvoiceNumber()).thenReturn("1234");
        when(mockInvoice.getNarative()).thenReturn("Narative");
        when(mockCore.newProductType("productTypeId", "InvoiceDocument")).thenReturn(mockDocumentDef);
        when(mockDocumentDef.getRenderCommand()).thenReturn("RenderCommand");
        when(mockCore.newCommand("RenderCommand", RenderDocumentCommand.class)).thenReturn(mockRenderCommand);
        when(mockRenderCommand.getRenderedDocumentRet()).thenReturn(document);
    }

    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testProductTypeIdPrecondition() throws BaseException {
        when(mockInvoice.getProductTypeId()).thenReturn("");
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testInvoiceNumberPrecondition() throws BaseException {
        when(mockInvoice.getInvoiceNumber()).thenReturn("");
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testAddresseePrecondition() throws BaseException {
        when(mockInvoice.getAddressee()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testFromPrecondition() throws BaseException {
        when(mockInvoice.getFrom()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testNarativePrecondition() throws BaseException {
        when(mockInvoice.getNarative()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testFinancialAmountsPrecondition() throws BaseException {
        when(mockInvoice.getFinancialAmounts()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testRegisteredOfficePrecondition() throws BaseException {
        when(mockInvoice.getRegisteredOffice()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void testDocumentRetPostcondition() throws BaseException {
        when(mockArgs.getDocumentRet()).thenReturn(null);
        sut.invoke();
    }
}
