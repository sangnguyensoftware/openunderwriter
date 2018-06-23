/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

import static com.ail.core.document.DocumentRequestType.GENERATE_AND_DOWNLOAD;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.document.CreateDocumentRequestService.CreateDocumentRequestCommand;

/**
 * Abstract class for use by all command buttons which generate documents.
 */
public abstract class ViewDocumentButtonAction extends CommandButtonAction {

    public String generateDocumentReference(Type model, String documentType) throws BaseException {
        CreateDocumentRequestCommand cdrc=PageFlowContext.getCoreProxy().newCommand(CreateDocumentRequestCommand.class);
        cdrc.setDocumentRequestTypeArg(GENERATE_AND_DOWNLOAD);
        cdrc.setDocumentTypeArg(documentType);
        cdrc.setSourceUIDArg(model.getSystemId());
        cdrc.invoke();
        return cdrc.getRequestIdRet();
    }
}