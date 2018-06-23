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

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.CoreContext.getProductName;
import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.core.CoreContext.getResponseWrapper;
import static com.ail.core.Functions.hideNull;
import static com.ail.core.Functions.isEmpty;
import static com.ail.core.MimeType.APPLICATION_PDF;
import static com.ail.core.context.RequestWrapper.UPLOAD_RESOURCE_ID;
import static com.ail.core.document.DocumentType.COMBINED;
import static com.ail.core.document.DocumentType.forName;
import static com.ail.core.document.DocumentType.isValid;
import static com.ail.core.document.Multiplicity.ONE;
import static com.ail.core.product.Product.BASE_PRODUCT_TYPE_ID;
import static com.ail.pageflow.PageFlowContext.flagActionAsProcessed;
import static com.ail.pageflow.PageFlowContext.getOperationParameters;
import static com.ail.pageflow.PageFlowContext.getRequestedOperation;
import static com.ail.pageflow.PageFlowContext.getRequestedOperationId;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.removeErrorMarkers;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.HasDocuments;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.product.HasProduct;
import com.ail.pageflow.util.Choice;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Widget to provide users with the ability to manage the documents associated
 * with a policy.
 */
public class DocumentManager extends PageSection {
    private static final long serialVersionUID = 7575333161892813599L;
    private static final String UPLOAD_FILE_NAME = "UPLOAD_FILE_NAME";
    private static final Pattern MATCH_ALL = Pattern.compile(".*");

    private List<LabelDetails> labelDetails = new ArrayList<>();

    private String fileTypes = "pdf";
    private String validDocumentTypes = ".*";
    private Pattern validDocumentTypesPattern = MATCH_ALL;
    private String validLabels = ".*";
    private Pattern validLabelsPattern = MATCH_ALL;

    public String getValidDocumentTypes() {
        return validDocumentTypes;
    }

    public void setValidDocumentTypes(String validDocumentTypes) {
        this.validDocumentTypes = validDocumentTypes;
        this.validDocumentTypesPattern = Pattern.compile(validDocumentTypes);
    }

    public String getValidLabels() {
        return validLabels;
    }

    public void setValidLabels(String validLabels) {
        this.validLabels = validLabels;
        this.validLabelsPattern = Pattern.compile(validLabels);
    }

    public LabelDetails getLabelDetails(int i) {
        if (i >= labelDetails.size()) {
            for (int x = labelDetails.size(); x <= i; x++) {
                LabelDetails ld = new LabelDetails();
                ld.applyElementId(getId() + "-ld-" + x);
                labelDetails.add(ld);
            }
        }
        return labelDetails.get(i);
    }

    public void setLabelDetails(int i, LabelDetails labelDetails) {
        this.labelDetails.set(i, labelDetails);
    }

    public String getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String fileTypes) {
        this.fileTypes = fileTypes;
    }

    public String getUploadUrl() {
        MimeResponse response = (MimeResponse) getResponseWrapper().getPortletResponse();
        ResourceURL resourceUrl = response.createResourceURL();
        resourceUrl.setResourceID(UPLOAD_RESOURCE_ID + ":" + getId());
        return resourceUrl.toString();
    }

    public String getDownloadUrl(Document document) {
        return format("%s://%s:%d/pageflow-portlet/fetchDocument?reference=%s", getRequestWrapper().getScheme(), getRequestWrapper().getServerName(), getRequestWrapper().getServerPort(),
                document.getExternalSystemId());
    }

    public boolean isNewRow(Date createdDate) {
        Date cutOff = new Date(System.currentTimeMillis() - (3 * 1000));

        return createdDate != null && createdDate.after(cutOff);
    }

    public List<String> getDocumentTypeOptions() {
        String productName = getProductName();

        Choice choice = getCoreProxy().newProductType(productName, "DocumentTypes", Choice.class);

        return choice.getChoice().stream().map(Choice::getName).filter(n -> validDocumentTypesPattern.matcher(n).matches()).sorted().collect(Collectors.toList());
    }

    /**
     * Determine if the document is valid WRT {@link #validDocumentTypes} and
     * {@link #validLabels}.
     *
     * @param document
     * @return true, if the document passes both validation checks; false,
     *         otherwise.
     */
    public boolean isDocumentTypeAndLabelsValid(Document document) {
        if (validDocumentTypesPattern.matcher(document.getType()).matches()) {
            if (document.getLabel().size() == 0) {
                return true;
            } else {
                if (document.getLabel().stream().allMatch(l -> validLabelsPattern.matcher(l).matches())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determine if the document is valid WRT {@link #validDocumentTypes} and
     * {@link #validLabels}.
     *
     * @param document
     * @return true, if the document passes both validation checks; false,
     *         otherwise.
     */
    public boolean isDocumentTypeAndLabelsValid(DocumentPlaceholder documentPlaceholder) {
        if (validDocumentTypesPattern.matcher(documentPlaceholder.getType()).matches()) {
            return true;
        }
        return false;
    }

    public boolean isProcessingDocumentUpload() {
        return getRequestWrapper().isDocumentBeingUploaded(UPLOAD_RESOURCE_ID + ":" + getId());
    }

    public String getUploadFileName() {
        return UPLOAD_FILE_NAME;
    }

    @Override
    public boolean processValidations(Type model) {

        model = super.applyRequestValues(model);

        Type boundModel = (Type) fetchBoundObject(model, model);

        if (!(boundModel instanceof HasDocuments)) {
            return false;
        }

        HasDocuments documentModel = (HasDocuments) boundModel;

        AtomicBoolean errorsFound = new AtomicBoolean(false);
        Map<String, Integer> typeUsage = new HashMap<>();

        for (Document document : documentModel.getDocument()) {
            removeErrorMarkers(document);
            if (isEmpty(document.getFileName())) {
                addError("fileName", "i18n_document_manager_filename_required", document);
                errorsFound.set(true);
            }
            String type = document.getType();
            typeUsage.put(type, (typeUsage.containsKey(type) ? typeUsage.get(type) + 1 : 1));
        }

        typeUsage.forEach((type, count) -> {
            if (count > 1 && isValid(type) && forName(type).getMultiplicity() == ONE) {
                documentModel.getDocument().stream().filter(d -> type.equals(d.getType())).forEach(d -> {
                    addError("type", "i18n_document_manager_duplicate_type_error", d);
                });
                errorsFound.set(true);
            }
        });

        return errorsFound.get();
    }

    public String encodeId(String fieldName, int row) {
        return encodeId("document", fieldName, row);
    }

    public String encodeId(String type, String fieldName, int row) {
        return encodeId(getId() + hideNull(binding) + "/" + type + "[" + row + "]/" + fieldName);
    }

    private void applyRequestValue(HasDocuments model, String fieldName, int row) {
        RequestWrapper request = getRequestWrapper();

        String paramName = encodeId(fieldName, row + 1);
        String paramValue = request.getParameter(paramName);
        if (paramValue != null) {
            Document document = model.getDocument().get(row);
            removeErrorMarkers(document);
            model.xpathSet("/document[" + (row + 1) + "]/" + fieldName, paramValue);
        }
    }

    @Override
    public Type applyRequestValues(Type model) {

        model = super.applyRequestValues(model);

        Type boundModel = (Type) fetchBoundObject(model, model);

        if (!(boundModel instanceof HasDocuments)) {
            return model;
        }

        HasDocuments documentModel = (HasDocuments) boundModel;

        for (int i = 0; i < documentModel.getDocument().size(); i++) {
            applyRequestValue(documentModel, "title", i);
            applyRequestValue(documentModel, "description", i);
            applyRequestValue(documentModel, "fileName", i);
            applyRequestValue(documentModel, "type", i);
            getLabelDetails(i).applyRequestValues(documentModel.getDocument().get(i));
        }

        getCoreProxy().flush();

        return model;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        Type boundModel = (Type) fetchBoundObject(model, model);

        if (!(boundModel instanceof HasDocuments)) {
            return model;
        }

        HasDocuments hasDocuments = (HasDocuments) boundModel;
        if (isProcessingDocumentUpload()) {
            processUpload(hasDocuments);
        } else {
            Properties opParams = getOperationParameters();
            String op = getRequestedOperation();
            String opId = getRequestedOperationId();

            if (id.equals(opId)) {
                handleDelete(hasDocuments, opParams, op);

                handleRestore(hasDocuments, opParams, op);

                handleCombine(boundModel, op);
            }

            handleLabels(hasDocuments);

            getCoreProxy().flush();
        }
        return model;
    }

    private void handleLabels(HasDocuments hasDocuments) throws BaseException {
        for (int i = 0; i < hasDocuments.getDocument().size(); i++) {
            getLabelDetails(i).processActions(hasDocuments.getDocument().get(i));
        }
    }

    private void handleCombine(Type boundModel, String op) throws BaseException {
        if ("combine".equals(op)) {
            generateCombinedDocument(boundModel);
            flagActionAsProcessed();
        }
    }

    private void handleRestore(HasDocuments hasDocuments, Properties opParams, String op) {
        if ("restore".equals(op)) {
            int row = parseInt(opParams.getProperty("row")) - 1;

            Document document = hasDocuments.getArchivedDocument().get(row);
            hasDocuments.restoreDocument(document);

            if (isEmpty(document.getFileName())) {
                addError("fileName", "i18n_document_manager_filename_required", document);
            }

            if (document.isOfBaseType() && document.getTypeAsEnum().getMultiplicity() == ONE) {
                List<Document> docs = filter(having(on(Document.class).getTypeAsEnum(), is(document.getTypeAsEnum())), hasDocuments.getDocument());
                if (docs.size() > 1) {
                    for (Document doc : docs) {
                        addError("type", "i18n_document_manager_duplicate_type_error", doc);
                    }
                }
            }
            flagActionAsProcessed();
        }
    }

    private void handleDelete(HasDocuments hasDocuments, Properties opParams, String op) {
        if ("delete".equals(op)) {
            int row = parseInt(opParams.getProperty("row")) - 1;

            Document document = hasDocuments.getDocument().get(row);
            hasDocuments.archiveDocument(document);
            flagActionAsProcessed();
        }
    }

    private void generateCombinedDocument(Type model) throws BaseException {

        HasDocuments documentModel = (HasDocuments) model;

        if (documentModel.getDocument().size() == 0) {
            return;
        }

        for (Iterator<Document> iter = documentModel.getDocument().listIterator(); iter.hasNext();) {
            Document document = iter.next();
            if (COMBINED.getLongName().equals(document.getType())) {
                iter.remove();
            }
        }

        com.itextpdf.text.Document combinedPdf = new com.itextpdf.text.Document();
        Set<String> combinedLabels = new HashSet<>();

        try {
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfCopy copy = new PdfCopy(combinedPdf, pdfOutputStream);
            combinedPdf.open();

            for (Document document : documentModel.getDocument()) {
                if (isDocumentTypeAndLabelsValid(document)) {
                    combinedLabels.addAll(document.getLabel());
                    if (APPLICATION_PDF.equals(document.getMimeType())) {
                        addPdf(copy, document.getDocumentContent().getContent());
                    } else {
                        addPdf(copy, getImagePdf(document));
                    }
                }
            }

            combinedPdf.close();

            String filename = "Combined " + model.getReferenceNumber() + ".pdf";
            String product = (model instanceof HasProduct) ? ((HasProduct) model).getProductTypeId() : BASE_PRODUCT_TYPE_ID;

            PdfWriter.getInstance(combinedPdf, pdfOutputStream);
            Document combinedDocument = new Document(COMBINED, pdfOutputStream.toByteArray(), "Combined", filename, APPLICATION_PDF, product);
            combinedDocument.getLabel().addAll(combinedLabels);
            documentModel.getDocument().add(combinedDocument);

        } catch (Exception e) {

            getCoreProxy().logError("Failed to generate combined documents", e);

            if (combinedPdf != null && combinedPdf.isOpen()) {
                combinedPdf.close();
            }
        }

    }

    private byte[] getImagePdf(Document document) throws DocumentException, BadElementException, MalformedURLException, IOException {
        com.itextpdf.text.Document imgDoc = new com.itextpdf.text.Document();
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(imgDoc, imgOutputStream);
        imgDoc.open();
        Image image = Image.getInstance(document.getDocumentContent().getContent());

        float documentWidth = imgDoc.getPageSize().getWidth() - imgDoc.leftMargin() - imgDoc.rightMargin();
        float documentHeight = imgDoc.getPageSize().getHeight() - imgDoc.topMargin() - imgDoc.bottomMargin();
        image.scaleToFit(documentWidth, documentHeight);
        imgDoc.add(image);
        imgDoc.close();
        return imgOutputStream.toByteArray();
    }

    private void addPdf(PdfCopy copy, byte[] pdf) throws IOException, BadPdfFormatException {
        PdfReader reader = new PdfReader(pdf);
        copy.addPage(copy.getImportedPage(reader, 1));
        copy.freeReader(reader);
        reader.close();
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("DocumentManager", model);
    }

    private void processUpload(HasDocuments hasDocuments) {
        try {
            Document document = CoreContext.getRequestWrapper().getDocumentBeingUploaded(UPLOAD_FILE_NAME);
            hasDocuments.getDocument().add(document);
        } catch (IOException e) {
            throw new DocumentUploadError("Document upload failure", e);
        }
    }
}
