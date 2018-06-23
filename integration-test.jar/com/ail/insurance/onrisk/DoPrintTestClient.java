package com.ail.insurance.onrisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;

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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.NodeList;


public class DoPrintTestClient {
    private static final String DO_PRINT_PASSWORD = "Password3!";

    private static final String DO_PRINT_USERNAME = "brokerstudiouser";

    private static final String BASE_PATH = "/Users/richarda/Devel/trunk/openunderwriter/integration-test.jar/com/ail/insurance/onrisk";

    private static final String URL = "https://sandboxdoprint.lloyds.com:2152/ContractValidationEngine.xamlx";

    private static final String[] ATTACHMENTS = new String[]{
                                                BASE_PATH+"/Attachment1.pdf"};//,
                                                //BASE_PATH+"/Attachment2.pdf",
                                                //BASE_PATH+"/Attachment3.pdf"};

    private static final String XML_VALIDATION_FILE = BASE_PATH+"/Certificate.xml";


    @Test
    public void call() {
        SOAPConnectionFactory soapConnectionFactory;
        try {
            soapConnectionFactory = SOAPConnectionFactory.newInstance();

            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            String type = "HandShake";
            SOAPMessage handShakeResponse = soapConnection.call(createHandShakeSOAPRequest(type), URL);
            String sessionToken = logResponse(handShakeResponse, type, "a:SessionToken");

            type = "Validation";
            SOAPMessage validationResponse = soapConnection.call(createValidationSOAPRequest(sessionToken, type), URL);
            logResponse(validationResponse, type, "a:IsValid");

            type = "Upload";
            for (int i = 0; i < ATTACHMENTS.length; i++) {
                SOAPMessage uploadResponse = soapConnection.call(createUploadSOAPRequest(sessionToken, type, ATTACHMENTS[i]), URL);
                logResponse(uploadResponse, type, "a:IsValid");
            }


            type = "Transaction";
            SOAPMessage transactioResponse = soapConnection.call(createTransactionSOAPRequest(sessionToken, type), URL);
            logResponse(transactioResponse, type, "a:DocumentsReady");


            type = "Download";
            SOAPMessage downloadResponse = soapConnection.call(createDownloadSOAPRequest(sessionToken, type), URL);
            logResponse(downloadResponse, type, "a:Document");


            soapConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private  SOAPMessage createDownloadSOAPRequest(String sessionToken, String type) throws Exception {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        /*
        <soapenv:Envelope
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:tem="http://tempuri.org/"
            xmlns:m="http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
           <soapenv:Header>
            <tem:MessageType>Download</tem:MessageType>
           </soapenv:Header>
           <soapenv:Body>
            <tem:Message xsi:type="m:CVPDownloadRequest">
                <m:SessionToken>FZKWXEMONPPIKKMZZYZIWGQUQEHZKTXY</m:SessionToken>
            </tem:Message>
           </soapenv:Body>

        </soapenv:Envelope>
         */

        // SOAP Header

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        // SOAP Body

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPDownloadRequest");

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private SOAPMessage createTransactionSOAPRequest(String sessionToken, String type) throws Exception {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        /*
        <soapenv:Envelope
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:tem="http://tempuri.org/"
            xmlns:m="http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
           <soapenv:Header>
            <tem:MessageType>Transaction</tem:MessageType>
           </soapenv:Header>
           <soapenv:Body>
            <tem:Message xsi:type="m:CVPTransactionRequest">
                <m:SessionToken>FZKWXEMONPPIKKMZZYZIWGQUQEHZKTXY</m:SessionToken>
            </tem:Message>
           </soapenv:Body>

        </soapenv:Envelope>
         */

        // SOAP Header

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        // SOAP Body

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPTransactionRequest");

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }


    private  SOAPMessage createUploadSOAPRequest(String sessionToken, String type, String attachment) throws Exception {


        byte[] fileBytes = fileToByteArray(attachment);

        String pdfFileHash = getPdfHash(fileBytes);

//        String pdfFile = DatatypeConverter.printBase64Binary(fileBytes);
//        String pdfFile = Base64.encodeBase64String(fileBytes);


        byte[] pdfFile = Base64.encodeBase64(fileBytes);

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPEnvelope envelope = getEnvelope(soapMessage);

        /*
        <soapenv:Envelope
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:tem="http://tempuri.org/"
            xmlns:m="http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
           <soapenv:Header>
            <tem:MessageType>Upload</tem:MessageType>
           </soapenv:Header>
           <soapenv:Body>
            <tem:Message xsi:type="m:CVPUploadRequest">
                <m:Document>[DOC]</m:Document>
                <m:DocumentHash>HTiQHr416ZlI0ITiVbTZSw==</m:DocumentHash>
                <m:SessionToken>FZKWXEMONPPIKKMZZYZIWGQUQEHZKTXY</m:SessionToken>
            </tem:Message>
           </soapenv:Body>

        </soapenv:Envelope>
         */

        // SOAP Header

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        // SOAP Body

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPUploadRequest");

        SOAPElement documentElem = messageElem.addChildElement("Document", "q1");
        documentElem.addTextNode(new String(pdfFile));

        SOAPElement documentHashElem = messageElem.addChildElement("DocumentHash", "q1");
        documentHashElem.addTextNode(pdfFileHash);

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

//    private byte[] getPdfBytes() throws IOException, FileNotFoundException {

//        return fileToByteArray(attachments);
//        return IOUtils.toByteArray(new FileReader(new File(pdfFile)), "UTF-8");
//    }

    private String getPdfHash(byte[] fileBytes) throws Exception {

//        return Base64.encodeBase64String(getHash(fileBytes).getBytes());

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest(fileBytes);

//        String pdfFileHash = DatatypeConverter.printBase64Binary(md5);

        return Base64.encodeBase64String(md5);
    }

    private SOAPMessage createValidationSOAPRequest(String sessionToken, String type) throws Exception {

        String fileString = IOUtils.toString(new FileReader(new File(XML_VALIDATION_FILE)));
        fileString = fileString.replaceAll("\\n", "");

        for (int i = 0; i < ATTACHMENTS.length; i++) {

            fileString = fileString.replaceFirst("<mstns:FileRef />", "<mstns:FileRef>" +
                    ATTACHMENTS[i] + "</mstns:FileRef>");
            fileString = fileString.replaceFirst("<mstns:FileHash />", "<mstns:FileHash>" +
                    getPdfHash(fileToByteArray(ATTACHMENTS[i])) + "</mstns:FileHash>");

        }
        System.out.println("XML:" + fileString);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest(fileString.getBytes());

        String xmlFile = Base64.encodeBase64String(fileString.getBytes());
        String xmlFileHash = Base64.encodeBase64String(md5);

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        /*
        <soapenv:Envelope
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:tem="http://tempuri.org/"
            xmlns:m="http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
           <soapenv:Header>
            <tem:MessageType>Validation</tem:MessageType>
           </soapenv:Header>
           <soapenv:Body>
            <tem:Message xsi:type="m:CVPValidationRequest">
                <m:Document>[DOC]</m:Document>
                <m:DocumentHash>H8ULRZJmoViwK5SCWWCd1Q==</m:DocumentHash>
                <m:SessionToken>FZKWXEMONPPIKKMZZYZIWGQUQEHZKTXY</m:SessionToken>
            </tem:Message>
           </soapenv:Body>

        </soapenv:Envelope>
         */

        // SOAP Header

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);

        // SOAP Body

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPValidationRequest");

        SOAPElement documentElem = messageElem.addChildElement("Document", "q1");
        documentElem.addTextNode(xmlFile);

        SOAPElement documentHashElem = messageElem.addChildElement("DocumentHash", "q1");
        documentHashElem.addTextNode(xmlFileHash);

        SOAPElement sessionTokenElem = messageElem.addChildElement("SessionToken", "q1");
        sessionTokenElem.addTextNode(sessionToken);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private SOAPMessage createHandShakeSOAPRequest(String type) throws Exception {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = getEnvelope(soapMessage);

        /*
        <soapenv:Envelope
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:tem="http://tempuri.org/"
            xmlns:q1="http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
           <soapenv:Header>
            <tem:APIKey>Password2!</tem:APIKey>
            <tem:MessageType>HandShake</tem:MessageType>
             <tem:RequestorID>brokerstudiouser</tem:RequestorID>
           </soapenv:Header>
           <soapenv:Body>
            <tem:Message xsi:type="q1:CVPHandshakeRequest">
                <q1:APIKey>Password2!</q1:APIKey>
                <q1:ClientVersion xsi:nil="true" />
                <q1:RequestorID>brokerstudiouser</q1:RequestorID>
            </tem:Message>
           </soapenv:Body>

        </soapenv:Envelope>
        */

        // SOAP Header

        SOAPHeader soapHeader = envelope.getHeader();

        SOAPElement apiKeyHeaderElem = soapHeader.addChildElement("APIKey", "tem");
        apiKeyHeaderElem.addTextNode(DO_PRINT_PASSWORD);
        SOAPElement messageTypeHeaderElem = soapHeader.addChildElement("MessageType", "tem");
        messageTypeHeaderElem.addTextNode(type);
        SOAPElement requestorIdHeaderElem = soapHeader.addChildElement("RequestorID", "tem");
        requestorIdHeaderElem.addTextNode(DO_PRINT_USERNAME);
        // SOAP Body

        SOAPBody soapBody = envelope.getBody();

        SOAPElement messageElem = soapBody.addChildElement("Message", "tem");
        messageElem.addAttribute(new QName("xsi:type"), "q1:CVPHandshakeRequest");

        SOAPElement apiKeyElem = messageElem.addChildElement("APIKey", "q1");
        apiKeyElem.addTextNode(DO_PRINT_PASSWORD);

        SOAPElement clientVersionElem = messageElem.addChildElement("ClientVersion", "q1");
        clientVersionElem.addAttribute(new QName("xsi:nil"), "true");

        SOAPElement requestorIdElem = messageElem.addChildElement("RequestorID", "q1");
        requestorIdElem.addTextNode(DO_PRINT_USERNAME);

        prepMessage(soapMessage, type);

        return soapMessage;
    }

    private String getResponseValue(SOAPMessage soapResponse, String valueName) throws SOAPException {

        SOAPBody body = soapResponse.getSOAPBody();
        NodeList returnList = body.getElementsByTagName("Message");

        for (int k = 0; k < returnList.getLength(); k++) {
            NodeList innerResultList = returnList.item(k).getChildNodes();
            for (int l = 0; l < innerResultList.getLength(); l++) {
                if (innerResultList.item(l).getNodeName().equalsIgnoreCase(valueName)) {
                    return innerResultList.item(l).getTextContent().trim();
                }
            }
        }
        return "RESPONSE NOT FOUND";
    }

    private void prepMessage(SOAPMessage soapMessage, String type) throws SOAPException, IOException {
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "http://tempuri.org/IContractValidation/RouteMessage");

        soapMessage.saveChanges();

        System.out.print("\n" + type + " Request:");
        soapMessage.writeTo(System.out);
        System.out.println();
    }

    private SOAPEnvelope getEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");
        envelope.addNamespaceDeclaration("q1", "http://schemas.datacontract.org/2004/07/Euseco.Mokadom.CVP.Workflow.MessageContracts");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        return envelope;
    }

    private String logResponse(SOAPMessage validationResponse, String type, String arg) throws SOAPException, IOException {

        System.out.print(type + " Response:");
        validationResponse.writeTo(System.out);
        System.out.println();

        String res = getResponseValue(validationResponse, arg);

        if ("a:Document".equals(arg)) {
            String newFile = XML_VALIDATION_FILE + ".pdf";
            System.out.println(type + ": '" + arg + "': " + newFile);
            FileUtils.writeByteArrayToFile(new File(newFile), Base64.decodeBase64(res));
        } else {
            System.out.println(type + ": '" + arg + "': " + res);
        }

        return res;
    }

    public static byte[] fileToByteArray(String fileName) {
        try {
            File f = new File(fileName);
            FileInputStream in = new FileInputStream(f);
            byte[] bytes = new byte[(int) f.length()];
            int c = -1;
            int ix = 0;
            while ((c = in.read()) > -1) {
                bytes[ix] = (byte) c;
                ix++;
            }
            in.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}