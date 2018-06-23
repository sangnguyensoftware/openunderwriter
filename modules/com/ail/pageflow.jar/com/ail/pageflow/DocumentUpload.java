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
package com.ail.pageflow;

import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.core.context.RequestWrapper.UPLOAD_RESOURCE_ID;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.removeErrorMarkers;
import static java.lang.String.format;

import java.io.IOException;

import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.document.Document;

/**
 * Upload a local file.
 */
public class DocumentUpload extends PageElement {

    private static final long serialVersionUID = -1320299722728499324L;

    private static final String UPLOAD_FILE_NAME = "UPLOAD_FILE_NAME";

    private String documentType = "i18n_Unknown";

    private String fileTypes = "pdf";

    private String documentTitle = "";

	public DocumentUpload() {
		super();
	}

	@Override
    public Type applyRequestValues(Type model) {
		return model;
	}

	@Override
    public boolean processValidations(Type model) {
	    removeErrorMarkers(PageFlowContext.getPolicy());
	    for (Document document : PageFlowContext.getPolicy().getDocument()) {
            if (documentTitle.equals(document.getTitle())) {
                return false;
            }
	    }
	    addError(documentTitle, "i18n_required_error", PageFlowContext.getPolicy());
	    return true;
	}

	@Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
		return executeTemplateCommand("DocumentUpload", model);
	}

    @Override
    public Type processActions(Type model) throws BaseException {
        if (getRequestWrapper().isDocumentBeingUploaded(UPLOAD_RESOURCE_ID + ":" + getId())) {
            processUpload();
        }
        return model;
    }

	public String getUploadFileName() {
        return UPLOAD_FILE_NAME;
    }

	private void processUpload() {

	    removeErrorMarkers(PageFlowContext.getPolicy());

	    removePreviousDocument();

	    try {
    	    Document newDocument = PageFlowContext.getRequestWrapper().getDocumentBeingUploaded(UPLOAD_FILE_NAME);
            PageFlowContext.getPolicy().getDocument().add(newDocument);
	    }
	    catch(IOException e) {
	        throw new DocumentUploadError("Document upload failure", e);
	    }
    }

	private void removePreviousDocument() {
	    Document documentToRemove = null;
	    for(Document document : PageFlowContext.getPolicy().getDocument()) {
	        if (this.documentTitle.equals(document.getTitle())) {
	            documentToRemove = document;
	            break;
	        }
	    }
	    if (documentToRemove != null) {
	        PageFlowContext.getPolicy().archiveDocument(documentToRemove);
	    }
	}

	public String getUploadUrl() {
        MimeResponse response = (MimeResponse)PageFlowContext.getResponseWrapper().getPortletResponse();
        ResourceURL resourceUrl = response.createResourceURL();
        resourceUrl.setResourceID(UPLOAD_RESOURCE_ID + ":" + getId());
        return resourceUrl.toString();
    }

	public String getServiceUploadUrl() {
	    HttpServletRequest request = PageFlowContext.getRequestWrapper().getServletRequest();
        return format("%s://%s:%d/policy/%s/UploadDocument",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                PageFlowContext.getPolicy().getExternalSystemId());
	}

    public String getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String fileTypes) {
        this.fileTypes = fileTypes;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFileTitle() {
        return documentTitle;
    }

    public void setFileTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

}
