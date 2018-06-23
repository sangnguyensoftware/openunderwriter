package com.ail.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.jxpath.JXPathContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.data.XPath;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XPath.class})
public class FunctionsTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(XPath.class);
    }

    @Test
    public void testObjectListToLineSeperatedString() {
        Collection<Object> collection=new ArrayList<>();

        collection.add("Hello");
        collection.add("World!!");
        collection.add(new Integer(21));

        String value=Functions.collectionAsLineSeparatedString(collection);

        assertNotNull(value);
        assertEquals("Hello\nWorld!!\n21", value);
    }

    @Test
    public void testObjectListToLineSeperatedStringWithEmptyList() {
        Collection<Object> collection=new ArrayList<>();

        String value=Functions.collectionAsLineSeparatedString(collection);

        assertNotNull(value);
        assertEquals("", value);
    }

    @Test
    public void testStringExpand() throws Exception {
        Version v=spy(new Version());

        doReturn(JXPathContext.newContext(v)).when(v).fetchJXPathContext();

        v.setAuthor("H.G. Wells");
        v.setComment("Nice House, good tea");
        v.setCopyright("(c) me");

        when(XPath.xpath("/author")).thenReturn("/author");
        when(XPath.xpath("/comment")).thenReturn("/comment");
        when(XPath.xpath("/copyright")).thenReturn("/copyright");

        String st=Functions.expand("Author: ${/author}, '${/comment}'. ${/copyright}", v);
        assertEquals("Author: H.G. Wells, 'Nice House, good tea'. (c) me", st);
    }

}
