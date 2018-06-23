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

import com.ail.core.BaseException;
import com.ail.core.HasDocuments;
import com.ail.core.PreconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.language.I18N;
import com.ail.core.product.ProductServiceCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

@ProductServiceCommand(serviceName = "UploadDocumentPlaceholderService", commandName = "UploadDocumentPlaceholder")
public class UploadDocumentPlaceholderService extends RestfulServiceInvoker {

	public static void invoke(ExecutePageActionArgument args) throws BaseException {
		new UploadDocumentPlaceholderService().invoke(Argument.class);
	}

	public RestfulServiceReturn service(Argument argument) throws PreconditionException {
		
		if (PageFlowContext.getPolicy() == null) {
			throw new PreconditionException("PageFlowContext.getPolicy() == null");
		}

		DocumentPlaceholder documentPlaceholder = addDocumentPlaceholderToPolicy(argument);

		return new Return(HTTP_OK, documentPlaceholder.getExternalSystemId());
	}

	private DocumentPlaceholder addDocumentPlaceholderToPolicy(Argument arg) {

	    DocumentPlaceholder docPlaceholder = new DocumentPlaceholder(arg.documentType, I18N.i18n(arg.documentType));

		if (arg.title != null) {
		    docPlaceholder.setTitle(arg.title);
		}

		docPlaceholder.setDescription(arg.description);

		if (arg.target != null && arg.target.length() > 0) {
		    HasDocuments hasDocuments = (HasDocuments)PageFlowContext.getPolicy().xpathGet(arg.target);
		    hasDocuments.getDocumentPlaceholder().add(docPlaceholder);
		} else {
		    PageFlowContext.getPolicy().getDocumentPlaceholder().add(docPlaceholder);
		}

		PageFlowContext.getCoreProxy().flush();

		return docPlaceholder;
	}

	public static class Argument {
		String documentType;
		String title;
		String description;
		String target;

		public Argument() {
		}

		public Argument(String documentType, String title, String description, String target) {
			this.documentType = documentType;
			this.title = title;
			this.description = description;
			this.target = target;
		}
	}

	public static class Return extends RestfulServiceReturn {
		String documentPlaceholderId;

		public Return(int status, String documentPlaceholderId) {
			super(status);
			this.documentPlaceholderId = documentPlaceholderId;
		}
	}
}