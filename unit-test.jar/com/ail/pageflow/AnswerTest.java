package com.ail.pageflow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Type;
import com.ail.pageflow.render.RenderService.RenderArgument;

public class AnswerTest {

    Answer sut = null;
    @Mock
    private RenderArgument renderArgument;
    @Mock
    private Type model;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = spy(new Answer());
    }

    @Test
    public void testFormattedAnswerWhichEvlaualtesToNull() {
        doReturn(model).when(renderArgument).getModelArgRet();
        doReturn(null).when(sut).fetchBoundObject(eq(model), eq(""));

        assertThat(sut.formattedAnswer(renderArgument), is(""));
    }
}
