package com.ail.insurance;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FunctionsTest {


    @Test
    public void testRemoveHtmlTags() {
        String text = "<p>text</p><p>text</p><p>text</p>";
        assertEquals("texttexttext", Functions.removeHtmlTags(text));
    }
}
