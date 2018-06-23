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
import com.ail.core.HasDocuments;
import com.ail.core.document.Document;

/**
 * Find a document on the model by title and add it to the attachments list
 */
@Service
public class AttachDocumentToEmailByTitle {
    public static boolean invoke(Core core, List<Document> attachments, HasDocuments model, String documentTitle) throws BaseException, MessagingException {
        for (Document document : model.getDocument()) {
            if (document.getTitle().equals(documentTitle)) {
                attachments.add(document);
                return true;
            }
        }

        return false;
    }
}