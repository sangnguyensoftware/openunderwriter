package com.ail.core.document;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Type;
import com.ail.core.XMLString;
import com.ail.core.document.RenderDocumentService.RenderDocumentArgument;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfStamper;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RenderItextPdfDocumentService.class)
public class RenderItextPdfDocumentServiceTest {
    private static final String FIELD_NAME = "FIELD_ONE";
    private static final String XPATH_EXPRESSION = "XPATH";
    private Core mockCore;
    private RenderItextPdfDocumentService sut;
    private RenderDocumentArgument mockArgs;
    private XMLString mockXMLString;
    private byte[] mockRenderedDocument;

    @Before
    public void setupMocks() {
        mockCore=mock(Core.class);
        mockArgs=mock(RenderDocumentArgument.class);
        mockXMLString=mock(XMLString.class);
        mockRenderedDocument=new byte[10];

        String samplePDFSource=this.getClass().getResource("SampleTemplate.pdf").toString();

        sut=new RenderItextPdfDocumentService();
        sut.setCore(mockCore);
        sut.setArgs(mockArgs);
        when(mockArgs.getSourceDataArg()).thenReturn(mockXMLString);
        when(mockArgs.getTemplateUrlArg()).thenReturn(samplePDFSource);
        when(mockArgs.getRenderedDocumentRet()).thenReturn(mockRenderedDocument);
    }

    @Test(expected=PreconditionException.class)
    public void testSourceDataPrecondition() throws PreconditionException, PostconditionException, RenderException  {
        when(mockArgs.getSourceDataArg()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testTranslationURLPrecondition() throws PreconditionException, PostconditionException, RenderException  {
        when(mockArgs.getTemplateUrlArg()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void testRenderedDocumentPostcondition() throws PreconditionException, PostconditionException, RenderException {
        when(mockArgs.getRenderedDocumentRet()).thenReturn(null);
        sut.invoke();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void xpathEvaluationFailuesMustIncludeXpathInMessage() throws Exception {
        PdfStamper pdfStamper = mock(PdfStamper.class);
        whenNew(PdfStamper.class).withAnyArguments().thenReturn(pdfStamper);

        AcroFields acroFields = mock(AcroFields.class);
        doReturn(acroFields).when(pdfStamper).getAcroFields();

        Map<String,Item> fields = mock(HashMap.class);
        doReturn(fields).when(acroFields).getFields();

        Set<String> fieldNames=new HashSet<>();
        fieldNames.add(FIELD_NAME);
        doReturn(fieldNames).when(fields).keySet();

        doReturn(XPATH_EXPRESSION).when(acroFields).getField(eq(FIELD_NAME));

        Type data = mock(Type.class);
        doReturn(data).when(mockCore).fromXML((Class)anyObject(), (XMLString)anyObject());

        NullPointerException exception = mock(NullPointerException.class);
        doThrow(exception).when(data).xpathGet(eq(XPATH_EXPRESSION));

        try {
            sut.invoke();
            fail("Invalid XPATH should have thrown an exeception");
        }
        catch(PreconditionException e) {
            assertThat(e.toString(), containsString(XPATH_EXPRESSION));
        }
    }
}
