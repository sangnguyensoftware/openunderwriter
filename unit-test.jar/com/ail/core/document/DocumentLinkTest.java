package com.ail.core.document;

import static org.junit.Assert.assertNotNull;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import com.ail.core.CoreContext;

public class DocumentLinkTest {
    DocumentLink sut;

    @Test
    public void checkCalculateHashDoesNotReturnNull() throws NoSuchAlgorithmException {
        CoreContext.initialise();
        CoreContext.setProductName("TEST");
        sut=new DocumentLink(DocumentType.ATTACHMENT, "http://www.google.com", "TEST");
        assertNotNull(sut.calculateMD5Hash());
    }
}
