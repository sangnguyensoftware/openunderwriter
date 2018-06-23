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

import static com.ail.core.CoreContext.getFailureRedirect;
import static com.ail.core.CoreContext.getSuccessRedirect;
import static com.ail.core.CoreContext.setFailureRedirect;
import static com.ail.core.CoreContext.setSuccessRedirect;
import static com.ail.core.document.DocumentType.ATTACHMENT;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.NodeList;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.document.MergeDataService.MergeDataCommand;
import com.ail.core.document.RenderDocumentService.RenderDocumentArgument;
import com.ail.core.product.HasProduct;
import com.ail.core.product.Product;

/**
 * This class provides an implementation of the render document service which
 * renders to PDF using the Lloyds DoPrint service.
 * <p/>
 */
@ServiceImplementation
public class RenderDoPrintDocumentService extends Service<RenderDocumentArgument> {

    public static final String DOPRINT_REQUEST_DOCUMENT_FILENAME = "doprint request.xml";
    public static final String DOPRINT_REQUEST_DOCUMENT_TITLE = "DoPrint Request";

    @Override
    public void invoke() throws PreconditionException, PostconditionException, RenderException {
        if (args.getSourceDataArg() == null) {
            throw new PreconditionException("args.getSourceDataArg()==null");
        }

        if (args.getRenderOptionsArg() == null || args.getRenderOptionsArg().length() == 0) {
            throw new PreconditionException("args.getRenderOptionsArg() == null || args.getRenderOptionsArg().length() == 0");
        }

        if (fetchServiceUrl() == null) {
            throw new PreconditionException("CoreContext.getCoreProxy().getParameterValue('DoPrintServiceUrl') == null");
        }

        if (fetchUsername() == null) {
            throw new PreconditionException("CoreContext.getCoreProxy().getParameterValue('DoPrintUsername') == null");
        }

        if (fetchPassword() == null) {
            throw new PreconditionException("CoreContext.getCoreProxy().getParameterValue('DoPrintPassword') == null");
        }

        Type model = fetchPolicy();

        performDoPrintInteraction(model, args.getAttachmentArgRet());

        if (args.getRenderedDocumentRet() == null || args.getRenderedDocumentRet().length == 0) {
            throw new PostconditionException("args.getRenderedDocumentRet()==null || args.getRenderedDocumentRet().length==0");
        }
    }

    private void performDoPrintInteraction(Type model, List<Document> attachment) throws PreconditionException, RenderException {
        SOAPConnection soapConnection = null;
        SOAPMessage request;
        SOAPMessage response;

        String doPrintServiceUrl = fetchServiceUrl();

        try {
            soapConnection = SOAPConnectionFactory.newInstance().createConnection();

            request = createHandShakeSOAPRequest("HandShake");
            response = soapConnection.call(request, doPrintServiceUrl);
            debug("HandShake", request, response);
            String sessionToken = fetchSessionTokenFromResponse(response);
            updateForwardingUrlsWithPasswordExpiry(response);

            request = createValidationSOAPRequest(sessionToken, model, "Validation", attachment);

            response = soapConnection.call(request, doPrintServiceUrl);
            debug("Validation", request, response);
            validateResponse(response);

            for (Document document : attachment) {
                switch (document.getType()) {
                case "i18n_Signed_Proposal":
                case "i18n_Medical_Report":
                case "i18n_Schedule":
                case "i18n_Wording":
                    request = createUploadSOAPRequest(sessionToken, document);
                    response = soapConnection.call(request, doPrintServiceUrl);
                    validateResponse(response);
                    debug("Upload", request, response);
                }
            }

            request = createTransactionSOAPRequest(sessionToken, "Transaction");
            response = soapConnection.call(request, doPrintServiceUrl);
            debug("Transaction", request, response);
            validateDocumentReadyResponse(response);

            request = createDownloadSOAPRequest(sessionToken, "Download");
            response = soapConnection.call(request, doPrintServiceUrl);
            debug("Download", request, response);
            byte[] document = fetchDocumentFromResponse(response);
            args.setRenderedDocumentRet(document);
        } catch (RenderException e) {
            throw e;
        } catch (Exception e) {
            throw new RenderDoPrintIntegrationError("Integraton with doPrint failed", e);
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    core.logError("failed to close soapConnection: ", e);
                }
            }
        }
    }

    private void debug(String string, SOAPMessage request, SOAPMessage response) throws SOAPException, IOException {
        System.out.print("\n"+string+" request: ");
        request.writeTo(System.out);
        System.out.flush();
        System.out.println("\n");
        System.out.print(string+" response: ");
        response.writeTo(System.out);
        System.out.flush();
        System.out.println("\n");
    }

    private SOAPMessage createHandShakeSOAPRequest(String type) throws Exception {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement apiKeyHeaderElem = soapHeader.addChildElement("APIKey", "tem");
        apiKeyHeaderElem.addTextNode(fetchPassword());
        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);
        SOAPElement requestorIdHeaderElem = soapHeader.addChildElement("RequestorID", "tem");
        requestorIdHeaderElem.addTextNode(fetchUsername());

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPHandshakeRequest");

        SOAPElement apiKeyElem = messageElem.addChildElement("APIKey", "q1");
        apiKeyElem.addTextNode(fetchPassword());

        SOAPElement clientVersionElem = messageElem.addChildElement("ClientVersion", "q1");
        clientVersionElem.addAttribute(new QName("xsi:nil"), "true");

        SOAPElement requestorIdElem = messageElem.addChildElement("RequestorID", "q1");
        requestorIdElem.addTextNode(fetchUsername());

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private SOAPMessage createValidationSOAPRequest(String sessionToken, Type model, String type, List<Document> attachment) throws Exception {
        String validationDataSet = buildMergedValidationDataSet(model);

        addValidationDataSetAttachmentToServiceReturn(attachment, validationDataSet);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest(validationDataSet.getBytes());

        String validationDataSetEncoded = Base64.encodeBase64String(validationDataSet.getBytes());
        String validationDataSetHash = Base64.encodeBase64String(md5);

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPValidationRequest");

        SOAPElement documentElem = messageElem.addChildElement("Document", "q1");
        documentElem.addTextNode(validationDataSetEncoded);

        SOAPElement documentHashElem = messageElem.addChildElement("DocumentHash", "q1");
        documentHashElem.addTextNode(validationDataSetHash);

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private void addValidationDataSetAttachmentToServiceReturn(List<Document> attachment, String validationDataSet) {
        String productTypeId = (this instanceof HasProduct) ? ((HasProduct)this).getProductTypeId() : Product.BASE_PRODUCT_TYPE_ID;

        attachment.add(new Document(ATTACHMENT,
                                    validationDataSet.getBytes(),
                                    DOPRINT_REQUEST_DOCUMENT_TITLE,
                                    DOPRINT_REQUEST_DOCUMENT_FILENAME,
                                    APPLICATION_XML, productTypeId));
    }

    private SOAPMessage createUploadSOAPRequest(String sessionToken, Document document) throws Exception {
        String pdfFileHash = document.calculateMD5Hash();

        byte[] pdfFile = Base64.encodeBase64(document.getDocumentContent().getContent());

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPEnvelope envelope = getEnvelope(soapMessage);

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode("Upload");

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPUploadRequest");

        SOAPElement documentElem = messageElem.addChildElement("Document", "q1");
        documentElem.addTextNode(new String(pdfFile));

        SOAPElement documentHashElem = messageElem.addChildElement("DocumentHash", "q1");
        documentHashElem.addTextNode(pdfFileHash);

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, "Upload");

        return soapMessage;
    }

    private SOAPMessage createTransactionSOAPRequest(String sessionToken, String type) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPTransactionRequest");

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private SOAPMessage createDownloadSOAPRequest(String sessionToken, String type) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPDownloadRequest");

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private String fetchSessionTokenFromResponse(SOAPMessage soapResponse) throws Exception {
        return getResponseValue(soapResponse, "a:SessionToken");
    }

    private String fetchSessionPwExpiryFromResponse(SOAPMessage soapResponse) throws Exception {
        return getResponseValue(soapResponse, "a:PwExpiringDays");
    }

    private String validateResponse(SOAPMessage soapResponse) throws Exception {
        return getResponseValue(soapResponse, "a:IsValid");
    }

    private String validateDocumentReadyResponse(SOAPMessage soapResponse) throws Exception {
        return getResponseValue(soapResponse, "a:DocumentsReady");
    }

    private byte[] fetchDocumentFromResponse(SOAPMessage response) throws Exception {
        String res = getResponseValue(response, "a:Document");
        return (Base64.decodeBase64(res));
    }

    private String getResponseValue(SOAPMessage soapResponse, String valueName) throws Exception {
        Collection<String> errors = new ArrayList<>();
        SOAPBody body = soapResponse.getSOAPBody();
        NodeList elements = body.getElementsByTagName("Message");
        String result = null;

        for (int k = 0 ; k < elements.getLength() ; k++) {
            NodeList innerResultList = elements.item(k).getChildNodes();
            for (int l = 0 ; l < innerResultList.getLength() ; l++) {
                if (innerResultList.item(l).getNodeName().equalsIgnoreCase(valueName)) {
                    result = innerResultList.item(l).getTextContent().trim();
                }
                if ("a:Errors".equals(innerResultList.item(l).getNodeName())) {
                    if (!innerResultList.item(l).getTextContent().isEmpty()) {
                        errors.add(innerResultList.item(l).getTextContent());
                    }
                }
            }
        }

        if (errors.size() != 0) {
            String errorString = Functions.valuesAsSeparatedString(errors, ", ");
            errorString = errorString.replaceAll("Error in", " Error in");
            throw new RenderException(errorString);
        }

        if (result != null) {
            return result;
        }

        throw new PreconditionException("Expected response not found:" + valueName);
    }

    private Type fetchPolicy() throws PreconditionException {
        try {
            Type passedPolicy = (Type)core.fromXML(args.getSourceDataArg().getType(), args.getSourceDataArg());
            return (Type) core.queryUnique("get.policy.by.systemId", passedPolicy.getSystemId());
        } catch (Exception e) {
            throw new PreconditionException("Failed to translate source data into a policy.", e);
        }
    }

    private SOAPEnvelope getEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");
        envelope.addNamespaceDeclaration("q1", "http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        return envelope;
    }

    private void prepMessage(SOAPMessage soapMessage, String type) throws SOAPException, IOException {
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "http://tempuri.org/IContractValidation/RouteMessage");
        soapMessage.saveChanges();
    }

    String fetchServiceUrl() {
        return CoreContext.getCoreProxy().getParameterValue("DoPrintServiceUrl");
    }

    String fetchUsername() {
        return CoreContext.getCoreProxy().getParameterValue("DoPrintUsername");
    }

    String fetchPassword() {
        return CoreContext.getCoreProxy().getParameterValue("DoPrintPassword");
    }

    String buildMergedValidationDataSet(Type model) throws BaseException {
        String mergeCommandName = args.getRenderOptionsArg();
        MergeDataCommand mergeCommand = CoreContext.getCoreProxy().newCommand(mergeCommandName, MergeDataCommand.class);
        mergeCommand.setModelArg(model);
        mergeCommand.invoke();

        return mergeCommand.getMergedDataRet().toString();
    }

    private void updateForwardingUrlsWithPasswordExpiry(SOAPMessage response) throws Exception {
        String pwExpiryDays = fetchSessionPwExpiryFromResponse(response);

        setFailureRedirect(addPasswordExpiryDaysToForwardingUrl(getFailureRedirect(), pwExpiryDays));
        setSuccessRedirect(addPasswordExpiryDaysToForwardingUrl(getSuccessRedirect(), pwExpiryDays));
    }

    private String addPasswordExpiryDaysToForwardingUrl(String existingUrl, String pwExpiryDays) {
        if (existingUrl == null) {
            return null;
        }

        return existingUrl + ((!existingUrl.contains("?")) ? "?" : "&") + "doprint-expiry=" + pwExpiryDays;
    }
}
