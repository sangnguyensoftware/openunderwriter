package com.ail.core.document.model;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Type;

public class BlockDataTest {

    BlockData sut;
    @Mock
    private RenderContext renderContext;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private Type model;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new BlockData();
        
        doReturn(printWriter).when(renderContext).getOutput();
        doReturn(model).when(renderContext).getModel();
        
    }

    @Test
    public void checkThatTrueConditionProducesOutput() {
        sut = spy(sut);

        doReturn(true).when(sut).conditionIsMet(eq(model));
        
        sut.render(renderContext);
        
        verify(renderContext, times(2)).getOutput();
    }

    @Test
    public void checkThatFalseConditionSurpressesOutput() {
        sut = spy(sut);

        doReturn(false).when(sut).conditionIsMet(eq(model));
        
        sut.render(renderContext);
        
        verify(renderContext, never()).getOutput();
    }
}
