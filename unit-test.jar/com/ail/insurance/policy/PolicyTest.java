package com.ail.insurance.policy;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.document.Document;
import com.ail.core.document.DocumentLink;

public class PolicyTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void retrieveWordingDocumentShouldReturnNullIfNoWordingDocumentOrWordingUrlIsDefined() {
        Policy policy = new Policy();

        assertThat(policy.retrieveWordingDocument(), is((Document) null));
    }

    @Test
    public void retrieveWordingDocumentShouldReturnWordingUrlIfNoWordingDocumentIsDefined() {
        Policy policy = new Policy();

        Wording wording = mock(Wording.class);
        doReturn(true).when(wording).hasUrl();
        policy.addWording(wording);

        assertThat(policy.retrieveWordingDocument(), instanceOf(DocumentLink.class));
    }

    @Test
    public void retrieveWordingDocumentShouldReturnWordingDocumentInPreferenceToUrl() {
        Policy policy = new Policy();
        byte[] doc = new byte[1];

        Wording wordingLink = mock(Wording.class);
        doReturn(true).when(wordingLink).hasUrl();
        policy.addWording(wordingLink);

        policy.attachWordingDocument(doc);

        assertThat(policy.retrieveWordingDocument(), instanceOf(Document.class));
        assertThat(policy.retrieveWordingDocument().getDocumentContent().getContent(), is(doc));
    }
}
