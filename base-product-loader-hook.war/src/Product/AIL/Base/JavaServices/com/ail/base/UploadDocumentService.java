package com.ail.base;
/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
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
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Map;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.HasDocuments;
import com.ail.core.PreconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.language.I18N;
import com.ail.core.product.ProductServiceCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

@ProductServiceCommand(serviceName = "UploadDocumentService", commandName = "UploadDocument")
public class UploadDocumentService extends RestfulServiceInvoker {

	public static void invoke(ExecutePageActionArgument args) throws BaseException {
		new UploadDocumentService().invoke(Argument.class);
	}

	public RestfulServiceReturn service(Argument argument) throws PreconditionException {
		
		if (PageFlowContext.getPolicy() == null) {
			throw new PreconditionException("PageFlowContext.getPolicy() == null");
		}

		Document document = addDocumentToTarget(argument);
		if (document != null && argument.documentPlaceholderId != null && argument.documentPlaceholderId.length() > 0) {
		    removeDocumentPlaceholder(argument);
		}

        PageFlowContext.getCoreProxy().flush();
		
		return new Return(HTTP_OK, document.getExternalSystemId());
	}

	private Document addDocumentToTarget(Argument arg) {
		Map<String, Object> fileMap = PageFlowContext.getRestfulRequestAttachment();

		Document doc = new Document(arg.documentType, (byte[]) fileMap.get("file"), I18N.i18n(arg.documentType),
				(String) fileMap.get("fileName"), (String) fileMap.get("mimeType"), CoreContext.getProductName());

		if (arg.title != null) {
			doc.setTitle(arg.title);
		}

		doc.setDescription(arg.description);

        getTarget(arg).getDocument().add(doc);
		return doc;
	}
	
	private void removeDocumentPlaceholder(Argument arg) {
        HasDocuments hasDocuments = getTarget(arg);
        
        if (hasDocuments.getDocumentPlaceholder() != null) {
            for (DocumentPlaceholder dp : hasDocuments.getDocumentPlaceholder()) {
                if (dp.getExternalSystemId().equals(arg.documentPlaceholderId)) {
                    hasDocuments.getDocumentPlaceholder().remove(dp);
                    break;
                }
            }
        }
	}
	
	private HasDocuments getTarget(Argument arg) {
       if (arg.target != null && arg.target.length() > 0) {
            return (HasDocuments)PageFlowContext.getPolicy().xpathGet(arg.target);
        } else {
            return PageFlowContext.getPolicy();
        }
	}

	public static class Argument {
		String documentType;
		String title;
		String description;
		String target;
		String documentPlaceholderId;

		public Argument() {
		}

		public Argument(String documentType, String title, String description, String target, String documentPlaceholderId) {
			this.documentType = documentType;
			this.title = title;
			this.description = description;
			this.target = target;
			this.documentPlaceholderId = documentPlaceholderId;
		}
	}

	public static class Return extends RestfulServiceReturn {
		String documentId;

		public Return(int status, String documentId) {
			super(status);
			this.documentId = documentId;
		}
	}
}