/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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
package com.ail.core.document;

import static com.ail.core.document.DocumentRequestType.GENERATE_AND_DOWNLOAD;
import static javax.persistence.EnumType.STRING;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * A document request represents a system's request for a document.
 * <p>
 * The request is represented externally to OpenUnderwriter by a request ID
 * (UUID) which only identifies the request and does not reveal the document's
 * type or any identifying characteristic of the document itself. A document
 * request has a limited life. It is expected to be used only once and will be
 * deleted once the request has been fulfilled.
 * </p>
 * <p>
 * A typical usage is in the generation of URLs for uses to download documents
 * (policies, quotes etc). In this situation we do not want the URL to address
 * the document directly by including its ID as this would allow other documents
 * to be downloaded maliciously by guessing their IDs. Instead a document
 * request is created and the ID of that request is included in the URL.
 * Assuming the URL is used, the system can retrieve the information related to
 * the request and return the document to the user without there ever being a
 * chance of a user hijacking the URL to download another document.
 * </p>
 * <p>
 * Two types of document request are supported:
 * <ol>
 * <li>A request for a document that needs to be generated. In this case the
 * documentType and sourceUID must be specified (document UID is null).</li>
 * <li>A request for a document that already exists. Only the documentUID need
 * be specified.</li>
 * </ol>
 * </p>
 */
@Entity
@TypeDefinition
@NamedQueries({
    @NamedQuery(name = "get.documentRequest.by.requestId", query = "select dre from DocumentRequest dre where dre.requestId = ?"),
    @NamedQuery(name = "delete.documentRequests.by.createdDate", query = "delete from DocumentRequest dre where dre.createdDate < ?")
})
public class DocumentRequest extends Type {
    @Enumerated(STRING)
    private DocumentRequestType requestType;

    private String requestId;

    private String documentType;

    private Long sourceUID;

    private Long documentUID;

    public DocumentRequest() {
        this.requestId = UUID.randomUUID().toString();
    }

    /**
     * Create a request for a document that may require generation. For example,
     * this request may in effect say
     * "I want the Quotation document for this policy" - for which
     * "documentType" would be "Quotation" and sourceUID would be the UID of the
     * policy.
     */
    public DocumentRequest(String documentType, Long sourceUID, String title, String filename) {
        this();
        this.requestType = GENERATE_AND_DOWNLOAD;
        this.documentType = documentType;
        this.sourceUID = sourceUID;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Long getSourceUID() {
        return sourceUID;
    }

    public void setSourceUID(Long sourceUID) {
        this.sourceUID = sourceUID;
    }

    public Long getDocumentUID() {
        return documentUID;
    }

    public void setDocumentUID(Long documentUID) {
        this.documentUID = documentUID;
    }

    public DocumentRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(DocumentRequestType requestType) {
        this.requestType = requestType;
    }
}
