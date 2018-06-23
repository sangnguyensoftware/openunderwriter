package com.ail.core.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class DocumentTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void cloneMustWorkWhenContentPropertyIsUninitiliased() {
        Document document = new Document("TYPE", (String) null, "TITLE", "FILENAME", "MIME_TYPE", "product-type-id");

        try {
            document.clone();
        } catch (Exception e) {
            fail("Exception thrown by clone()"+e);
        }
    }

    @Test
    public void jpgMimeTypeMustTransalte() {
        Document document = new Document("TYPE", (String) null, "TITLE", "FILENAME", "image/jpg", "product-type-id");

        assertEquals("image/jpeg", document.getMimeType());
    }

    @Test
    public void nonJpgMimeTypeMustNotTransalte() {
        Document document = new Document("TYPE", (String) null, "TITLE", "FILENAME", "image/png", "product-type-id");

        assertEquals("image/png", document.getMimeType());
    }
}
