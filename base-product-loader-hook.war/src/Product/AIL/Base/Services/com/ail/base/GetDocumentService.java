package com.ail.base;
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
import static com.ail.core.MimeType.APPLICATION_PDF;
import static com.ail.core.document.Multiplicity.ONE;
import static com.ail.core.language.I18N.i18n;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ail.base.data.DataHelper;
import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.HasDocuments;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.Type;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.ail.core.document.FetchDocumentService.FetchDocumentCommand;
import com.ail.core.document.GenerateDocumentService.GenerateDocumentCommand;
import com.ail.core.document.Multiplicity;
import com.ail.core.factory.UndefinedTypeError;
import com.ail.core.product.ProductServiceCommand;
import com.ail.core.workflow.CaseType;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.util.Choice;

/**
 * A general service to call the more specific document generation services. May
 * generate a standard document type and attach it to the policy or a custom
 * named template that can be attached to the appropriate {@link HasDocuments}
 * model - could be Policy, Claim, or Party.
 */
@ProductServiceCommand(serviceName = "GetDocumentService", commandName = "GetDocument")
public class GetDocumentService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new GetDocumentService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws PostconditionException, JSONException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("GetDocumentService argument: " + arg);

        if (StringUtils.isBlank(arg.templateName)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg templateName: " + arg.templateName);
        }
        try {
            CaseType.forName(arg.caseType);
        } catch (Exception e) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg caseType: " + arg.caseType);
        }
        if (StringUtils.isBlank(arg.caseId)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg caseId: " + arg.caseId);
        }

        String documentId = generateAppropriateDocument(arg, coreProxy);

        return new Return(HTTP_OK, documentId);
    }

    /**
     * Generates a document for the given template name. If the template name
     * corresponds to a standard OU template then generate and attach that.
     * Otherwise look for the configuration in the registry to generate a custom
     * document. The registry should contain a top level group called 'documents'
     * with groups underneath named by TemplateName. This group can supply extra
     * parameters to the custom document generation to specify {@link DocumentType},
     * {@link Multiplicity} (which can override the default multiplicity on the
     * document type), 'title' and 'filename'
     * 
     * @param arg
     * @param coreProxy
     * @return
     * @throws PostconditionException
     */
    private String generateAppropriateDocument(Argument arg, CoreProxy coreProxy) throws PostconditionException {
    	String documentId = null;

    	try {
	    	documentId = generateStandardDocument(arg, coreProxy);
	
	    	if (documentId == null) {
	        	documentId = generateCustomDocument(arg, coreProxy);
	        }
        } catch (Exception e) {
            coreProxy.logError("Error generating appropriate document.", e);
        }

        return documentId;
    }

	String generateCustomDocument(Argument arg, CoreProxy coreProxy) throws BaseException {
		Type type = new DataHelper().getType(CaseType.forName(arg.caseType), arg.caseId);

		GenerateDocumentCommand cmd = (GenerateDocumentCommand) coreProxy.newCommand(GenerateDocumentCommand.class);
		cmd.setModelArg(type);
		cmd.setProductNameArg(PageFlowContext.getProductName());
		cmd.setDocumentTypeArg(arg.templateName);
		cmd.invoke();
 
        HasDocuments hasDocumentsType = (HasDocuments) type;

        Properties templateParams = fetchAttributesForTemplateAsProperties(coreProxy, arg.templateName);

        DocumentType documentType = DocumentType.forName(templateParams.getProperty("documentType", "Other"));
		String title = i18n(templateParams.getProperty("title", arg.templateName));
		String filename = i18n(templateParams.getProperty("filename", title + ".pdf"));

		Multiplicity multiplicity = Multiplicity.forName(templateParams.getProperty("multiplicity", documentType.getMultiplicity().getName()));
		if (multiplicity == ONE) {
		    for (Iterator<Document> i = hasDocumentsType.getDocument().iterator(); i.hasNext();) {
		        if (((Document) i.next()).getTitle().equals(title)) {
		            i.remove();
		        }
		    }
		}

		byte[] rawDoc = cmd.getRenderedDocumentRet();
		Document document = (Document) coreProxy.create(new Document(arg.templateName, rawDoc, title, filename, APPLICATION_PDF, PageFlowContext.getProductName()));
		hasDocumentsType.getDocument().add(document);

		return document.getExternalSystemId();
	}

    private Properties fetchAttributesForTemplateAsProperties(CoreProxy core, String templateName) throws PreconditionException {
    	Choice documentTypes = (Choice)core.newType("DocumentTypes");
    	
    	for(Choice documentType: documentTypes.getChoice()) {
    		if (documentType.getName().equals(templateName)) {
    			Properties properties = new Properties();
    			for(Attribute attribute: documentType.getAttribute()) {
    				properties.put(attribute.getId(), attribute.getValue());
    			}
    			return properties;
    		}
    	}
    	
    	throw new PreconditionException("Document type: '"+templateName+"' not found in DocumentTypes model object.");
	}

	/**
     * If the argument template name is a standard document type then generate and
     * attach it to the policy on the context that and return the id
     * 
     * @param arg
     * @param coreProxy
     * @return
     */
    private String generateStandardDocument(Argument arg, CoreProxy coreProxy) {
        try {
            coreProxy.logInfo("Invoking FetchDocumentCommand for " + "Fetch" + arg.templateName + "Document");
            FetchDocumentCommand cmd = (FetchDocumentCommand) coreProxy.newCommand("Fetch" + arg.templateName + "Document", FetchDocumentCommand.class);
            cmd.setModelIDArg(PageFlowContext.getPolicySystemId());
            cmd.invoke();
            Document doc = cmd.getDocumentRet();
            if (doc != null) {
                return doc.getExternalSystemId();
            }
        } catch (Exception e) {
            coreProxy.logInfo("Fetch" + arg.templateName + "DocumentCommand failed " + e.getMessage());
        } catch (UndefinedTypeError u) {
            coreProxy.logInfo("Fetch" + arg.templateName + "DocumentCommand failed " + u.getMessage());
        }

        return null;
    }

    public static class Argument {

        String caseType;
        String caseId;
        String templateName;

    }

    public static class Return extends RestfulServiceReturn {

        String documentId;

        public Return(int status, String documentId) {
            super(status);
            this.documentId = documentId;
        }
    }

}