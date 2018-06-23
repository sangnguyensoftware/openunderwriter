package com.ail.pageflow.render;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.document.Document;
import com.ail.core.document.FetchDocumentService.FetchDocumentCommand;
import com.ail.insurance.policy.Policy;

/**
 * Create a BodyPart holding the certificate document (PDF) and attach it to
 * the specified MimeMultipart. The attachment will only be added if the
 * certificate document already exists. If it does not, no changes are made.
 */
@Service
public class AttachCertificateToEmail {
    public static void invoke(Core core, Policy policy, List<Document> attachments) throws BaseException, MessagingException {
        // Only attached the document if it has already been generated - don't generate it
        if (policy.retrieveCertificateDocument() != null) {
            FetchDocumentCommand cmd = core.newCommand("FetchCertificateDocument", FetchDocumentCommand.class);
            cmd.setModelIDArg(policy.getSystemId());
            cmd.invoke();

            attachments.add(cmd.getDocumentRet());
        }
    }
}