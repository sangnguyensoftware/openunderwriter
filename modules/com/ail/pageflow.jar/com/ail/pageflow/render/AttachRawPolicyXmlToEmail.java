package com.ail.pageflow.render;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.ail.insurance.policy.Policy;

/**
 * Create a BodyPart holding the assessment sheet (HTML) and attach it to
 * the specified MimeMultipart.
 */
@Service
public class AttachRawPolicyXmlToEmail {
    public static void invoke(Core core, Policy policy, List<Document> attachments) throws BaseException, MessagingException, IOException {

         Document attachment = core.create(new Document(
                DocumentType.ATTACHMENT.name(), "attachment", core.toXML(policy).toString().getBytes(),
                "raw.xml", policy.getQuotationNumber()+" Raw.xml", "text/xml", policy.getProductTypeId()));

         attachments.add(attachment);
    }
}