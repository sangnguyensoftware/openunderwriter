package com.ail.pageflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.ail.core.Attribute;

public class QuestionWithSubSectionTest {

    @Mock
    private Attribute attribute;
    private QuestionWithSubSection sut;
    
    @Before
    public void setup() {
        initMocks(this);
        sut=new QuestionWithSubSection();
        doReturn(attribute).when(attribute).xpathGet(anyString());
    }
    
    @Test
    public void testDefaultEnableCondition() {
        doReturn("Yes").when(attribute).getValue();
        assertTrue(sut.isDetailsEnabled(attribute));

        doReturn("No").when(attribute).getValue();
        assertFalse(sut.isDetailsEnabled(attribute));
    }
    
    @Test
    public void testSpecifiedEnableConditions() {
        sut.setDetailsEnabledFor("Red;Green;Blue");

        doReturn("Red").when(attribute).getValue();
        assertTrue(sut.isDetailsEnabled(attribute));

        doReturn("Green").when(attribute).getValue();
        assertTrue(sut.isDetailsEnabled(attribute));

        doReturn("Blue").when(attribute).getValue();
        assertTrue(sut.isDetailsEnabled(attribute));

        doReturn("G").when(attribute).getValue();
        assertFalse(sut.isDetailsEnabled(attribute));

        doReturn("Yellow").when(attribute).getValue();
        assertFalse(sut.isDetailsEnabled(attribute));

        doReturn("Yes").when(attribute).getValue();
        assertFalse(sut.isDetailsEnabled(attribute));
    }
}