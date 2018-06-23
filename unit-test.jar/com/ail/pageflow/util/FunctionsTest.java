package com.ail.pageflow.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.PageElement;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Type.class, PageElement.class})
public class FunctionsTest {

    PageElement sPageElement;
    Type sModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sPageElement = spy(new PageElement() {});
        sModel = spy(new Type() {});
    }

    @Test
    public void testFindErrorsByRegex_ValidateRegex() {
        String TEST_REGEX = "(duration|overlap).*";

        Attribute attrs = new Attribute("model.attrs", "attrs", "string");
        attrs.getAttribute().add(new Attribute("error.duration.something", "Some duration error", "string"));
        attrs.getAttribute().add(new Attribute("error.overlap.something", "Some overlap error", "string"));
        attrs.getAttribute().add(new Attribute("error.overlap", "Some overlap error 2", "string"));
        attrs.getAttribute().add(new Attribute("error.moreTypes", "Some diff type of error", "string"));

        doReturn(attrs.getAttribute()).when(sModel).getAttribute();

        String result = Functions.findErrorsByRegex(TEST_REGEX, sModel, sPageElement);

        assertEquals("" +
                "\"Some duration error\", " +
                "\"Some overlap error\", " +
                "\"Some overlap error 2\"" +
                "",
                result);
    }

    @Test
    public void testFindErrorsByRegex_RemovesDuplicates() {
        String TEST_REGEX = "(duration|overlap).*";

        Attribute attrs = new Attribute("model.attrs", "attrs", "string");
        attrs.getAttribute().add(new Attribute("error.duration.something", "Some duration error", "string"));
        attrs.getAttribute().add(new Attribute("error.duration.something", "Some duration error", "string"));
        attrs.getAttribute().add(new Attribute("error.overlap.something", "Some duration error", "string"));

        doReturn(attrs.getAttribute()).when(sModel).getAttribute();

        String result = Functions.findErrorsByRegex(TEST_REGEX, sModel, sPageElement);

        assertEquals("\"Some duration error\"", result);
    }
}