/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
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
 */package com.ail.core;

import java.util.List;

import com.ail.core.document.Document;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.document.DocumentType;

/**
 * Interface describing model objects that have references to documents. Any given
 * object may have 0..* references documents and must also manage an associated
 * list of archived documents (logically deleted documents).
 */
public interface HasDocuments extends SupportsXpath {
    List<Document> getDocument();

    void setDocument(List<Document> document);

    List<Document> getArchivedDocument();

    void setArchivedDocument(List<Document> archivedDocument);

    void archiveDocument(Document document);

    void restoreDocument(Document document);

    Document retrieveDocumentOfType(DocumentType type);

    Document retrieveDocumentOfType(String type);

    List<DocumentPlaceholder> getDocumentPlaceholder();

    void setDocumentPlaceholder(List<DocumentPlaceholder> documentPlaceholder);
}
