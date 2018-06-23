/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
 * Create a BodyPart holding a document attached to the policy and attach it to
 * the specified MimeMultipart. The attachment will only be added if the
 * document already exists. If it does not, no changes are made.
 */
@Service
public class AttachPolicyDocumentToEmail {
    public static void invoke(Core core, Policy policy, List<Document> attachments, String templateName) throws BaseException, MessagingException {
        // Only attached the document if it has already been generated - don't generate it
        if (policy.retrieveCertificateDocument() != null) {
            FetchDocumentCommand cmd = core.newCommand("Fetch" + templateName + "Document", FetchDocumentCommand.class);
            cmd.setModelIDArg(policy.getSystemId());
            cmd.invoke();

            attachments.add(cmd.getDocumentRet());
        }
    }
}