package com.ail.core.document.model;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Type;
import com.ail.core.TypeXPathException;

public class ModelDataTest {

    private ModelData sut;

    @Mock
    private RenderContext context;
    @Mock
    private Type model;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private TypeXPathException typeXPathException;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        sut=new ModelData();
        sut.setBinding("binding");
        
        doReturn(model).when(context).getModel();
        doReturn("value").when(model).xpathGet(eq("binding"), eq(String.class));
        doReturn(printWriter).when(context).getOutput();
    }
    
    @Test
    public void testHappyPath() {
        sut.render(context);
        verify(printWriter, times(1)).printf(eq("<itemData%s%s%s%s>%s</itemData>"),eq(""), eq(""), eq(" class=\"string\""), eq(""), eq("value"));
    }

    @Test
    public void testXpathError() {
        doThrow(typeXPathException).when(model).xpathGet(eq("binding"), eq(String.class));
        sut.render(context);
        verify(printWriter, times(1)).printf(eq("<itemData%s%s%s%s>%s</itemData>"),eq(""), eq(""), eq(" class=\"string\""), eq(""), eq("undefined: binding"));
    }
    
    @Test
    public void testNullXpathResultRendersAsBlank() {
        doReturn(null).when(model).xpathGet(eq("binding"), eq(String.class));
        sut.render(context);
        verify(printWriter, times(1)).printf(eq("<itemData%s%s%s%s>%s</itemData>"),eq(""), eq(""), eq(" class=\"string\""), eq(""), eq(""));
        
    }
}
