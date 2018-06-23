package com.ail.pageflow;

import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.core.Functions.isEmpty;
import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.HasDocuments;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.pageflow.util.Choice;
import com.ail.party.HasPartyRoles;
import com.ail.party.Organisation;
import com.ail.party.Party;
import com.ail.party.PartyRole;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class CustomLetterBuilder extends PageContainer {
    private static final String RECIPIENT_FULL_NAME_DOCUMENT_REQUEST_PARAM_NAME = "recipientFullName";
    private static final String RECIPIENT_SHORT_NAME_DOCUMENT_REQUEST_PARAM_NAME = "recipientShortName";
    private static final String RECIPIENT_ADDRESS_DOCUMENT_REQUEST_PARAM_NAME = "recipientAddress";
    private static final String RECIPIENT_ID_DOCUMENT_REQUEST_PARAM_NAME = "recipientId";
    private static final String CUSTOM_CONTENT_DOCUMENT_REQUEST_PARAM_NAME = "customContent";
    private static final String SELECTED_DOCUMENT_TYPE_ATTR_NAME = "selectedDocumentType";
    private static final String SENDER_SIGNATURE_URL_DOCUMENT_REQUEST_PARAM_NAME = "senderSignatureUrl";
    private static final String SENDER_FULL_NAME_URL_DOCUMENT_REQUEST_PARAM_NAME = "senderFullName";
    private static final String SENDER_SHORT_NAME_URL_DOCUMENT_REQUEST_PARAM_NAME = "senderShortName";
    private static final String SENDER_JOB_TITLE_DOCUMENT_REQUEST_PARAM_NAME = "senderJobTitle";

    private static final long serialVersionUID = 7575333161919813599L;

    private static final Pattern MATCH_ALL = compile(".*");

    private String validDocumentTypes = ".*";
    private transient Pattern validDocumentTypesPattern = MATCH_ALL;

    private String validRecipientTypes = ".*";
    private transient Pattern validRecipientTypesPattern = MATCH_ALL;

    private String saveDestinationPageId;
    private String signatureBaseUrl;

    public CustomLetterBuilder() {
        super();
    }

    // Support methods for document types options and selection.

    public List<String> fetchValidDocumentTypes() {
        return PageFlowContext.getCoreProxy().newType("DocumentTypes", Choice.class).getChoice().stream().
                filter(
                        c -> getValidDocumentTypesPattern().matcher(c.getName()).matches() &&
                             c.getAttribute().stream().anyMatch(a -> a.getId().equals("template"))
                ).
                map(Choice::getName).
                collect(Collectors.toList());
    }

    private Pattern getValidDocumentTypesPattern() {
        if (validDocumentTypesPattern == null) {
            validDocumentTypesPattern = compile(validDocumentTypes);
        }
        return validDocumentTypesPattern;
    }

    public String getValidDocumentTypes() {
        return validDocumentTypes;
    }

    public void setValidDocumentTypes(String validDocumentTypes) {
        this.validDocumentTypes = validDocumentTypes;
        this.validDocumentTypesPattern = compile(validDocumentTypes);
    }

    public String getSelectedDocumentType() {
        return (String) getRequestWrapper().getServletRequest().getAttribute(SELECTED_DOCUMENT_TYPE_ATTR_NAME);
    }

    public void setSelectedDocumentType(String selectedDocumentType) {
        getRequestWrapper().getServletRequest().setAttribute(SELECTED_DOCUMENT_TYPE_ATTR_NAME, selectedDocumentType);;
    }

    public String getSelectedDocumentTypeFieldId() {
        return encodeId(getId() + "documentTypeId");
    }

    // Support methods for custom content entry and update.

    public String getCustomContent(Type model, String documentType) {
        return getDocumentRequestParameter(model, documentType, CUSTOM_CONTENT_DOCUMENT_REQUEST_PARAM_NAME);
    }

    public void setCustomContent(Type model, String documentType, String customContent) {
        setDocumentRequestParameter(model, documentType, CUSTOM_CONTENT_DOCUMENT_REQUEST_PARAM_NAME, customContent);
    }

    public String getCustomContentFieldId() {
        return encodeId(getId() + "customContentId");
    }

    // Support methods for recipient options and selection.

    public List<PartyRole> fetchValidRecipients(HasPartyRoles target) {
        return target.fetchPartyRolesForRoleTypes(getValidRecipientTypesPattern());
    }

    private Pattern getValidRecipientTypesPattern() {
        if (validRecipientTypesPattern == null) {
            validRecipientTypesPattern = compile(validRecipientTypes);
        }
        return validRecipientTypesPattern;
    }

    public String getValidRecipientTypes() {
        return validRecipientTypes;
    }

    public void setValidRecipientTypes(String validRecipientTypes) {
        this.validRecipientTypes = validRecipientTypes;
        this.validRecipientTypesPattern = compile(validRecipientTypes);
    }

    public String getSelectedRecipientFieldId() {
        return encodeId(getId() + "recipientTypeId");
    }

    public void setSelectedRecipientId(Type model, String documentType, String recipientId) {
        setDocumentRequestParameter(model, documentType, RECIPIENT_ID_DOCUMENT_REQUEST_PARAM_NAME, recipientId);

        if (recipientId != null && recipientId.length() > 0) {
            Optional<PartyRole> recipientPartyRole = fetchValidRecipients((HasPartyRoles)model).
                                                stream().
                                                filter(pr -> recipientId.equals(pr.getParty().getExternalSystemId())).
                                                findFirst();

            if (recipientPartyRole.isPresent()) {
                Party recipient = recipientPartyRole.get().getParty();
                if (recipient instanceof Organisation) {
                    setDocumentRequestParameter(model, documentType, RECIPIENT_FULL_NAME_DOCUMENT_REQUEST_PARAM_NAME, ((Organisation)recipient).getContactName());
                    setDocumentRequestParameter(model, documentType, RECIPIENT_ADDRESS_DOCUMENT_REQUEST_PARAM_NAME, recipient.getLegalName()+", "+recipient.getAddress().getFullAddress());
                    setDocumentRequestParameter(model, documentType, RECIPIENT_SHORT_NAME_DOCUMENT_REQUEST_PARAM_NAME, recipient.getShortName());
                }
                else {
                    setDocumentRequestParameter(model, documentType, RECIPIENT_FULL_NAME_DOCUMENT_REQUEST_PARAM_NAME, recipient.getLegalName());
                    setDocumentRequestParameter(model, documentType, RECIPIENT_ADDRESS_DOCUMENT_REQUEST_PARAM_NAME, recipient.getAddress().getFullAddress());
                    setDocumentRequestParameter(model, documentType, RECIPIENT_SHORT_NAME_DOCUMENT_REQUEST_PARAM_NAME, recipient.getShortName());
                }
            }
        }
    }

    public String getSelectedRecipientId(Type model, String documentType) {
        return getDocumentRequestParameter(model, documentType, RECIPIENT_ID_DOCUMENT_REQUEST_PARAM_NAME);
    }

    // Support methods for destination page

    public String getSaveDestinationPageId() {
        return saveDestinationPageId;
    }

    public void setSaveDestinationPageId(String saveDestinationPageId) {
        this.saveDestinationPageId = saveDestinationPageId;
    }

    // Support methods for signature base url

    public String getSignatureBaseUrl() {
        return signatureBaseUrl;
    }

    public void setSignatureBaseUrl(String signatureBaseUrl) {
        this.signatureBaseUrl = signatureBaseUrl;
    }

    private void setSender(Type model, String documentType, Long remoteUser) {
        try {
            User user = UserLocalServiceUtil.getUser(remoteUser);

            setDocumentRequestParameter(model, documentType, SENDER_JOB_TITLE_DOCUMENT_REQUEST_PARAM_NAME, user.getJobTitle());
            setDocumentRequestParameter(model, documentType, SENDER_FULL_NAME_URL_DOCUMENT_REQUEST_PARAM_NAME, user.getFullName());
            setDocumentRequestParameter(model, documentType, SENDER_SHORT_NAME_URL_DOCUMENT_REQUEST_PARAM_NAME, user.getFirstName());

            if (!isEmpty(getSignatureBaseUrl())) {
                setDocumentRequestParameter(model, documentType, SENDER_SIGNATURE_URL_DOCUMENT_REQUEST_PARAM_NAME, getSignatureBaseUrl() + "/" + user.getScreenName());
            }
        } catch (PortalException | SystemException e) {
            throw new RenderingError("Failed to gather sender information for Liferay userId: " + remoteUser, e);
        }
    }

    /**
     * Add a document request parameter to a specified Type. Document request parameters are held as an attribute
     * hierarchy in the model object. The top level attribute has an id value set to the document type and each
     * parameter is held as a sub-attribute with an id equal to the paramter's name.
     * @param model The model holding the document request parameter attribute set
     * @param documentType The type of document (generally the documents i18n code).
     * @param paramName The name of the parameter to store.
     * @param paramValue The value to store.
     */
    protected void setDocumentRequestParameter(Type model, String documentType, String paramName, String paramValue) {
        Optional<Attribute> docRequest = model.getAttribute().stream().
                                         filter(attr -> documentType.equals(attr.getId())).
                                         findFirst();

        if (!docRequest.isPresent()) {
            docRequest = Optional.of(new Attribute(documentType, null, "string"));
            model.getAttribute().add(docRequest.get());
        }

        Optional<Attribute> docParam = docRequest.get().getAttribute().stream().
                             filter(attr -> paramName.equals(attr.getId())).
                             findFirst();

        if (!docParam.isPresent()) {
            docParam = Optional.of(new Attribute(paramName, null, "string"));
            docRequest.get().getAttribute().add(docParam.get());
        }

        docParam.get().setValue(paramValue);
    }

    /**
     * @see #setDocumentRequestParameter(Type, String, String, String)
     * @param model The model holding the document request parameter attribute set
     * @param documentType The type of document (generally the documents i18n code).
     * @param paramName The name of the parameter to store.
     * @return The value of the parameter if it is defined or and empty String if it is not.
     */
    protected String getDocumentRequestParameter(Type model, String documentType, String paramName) {
        if (documentType == null) {
            return "";
        }

        Optional<Attribute> attr = model.getAttribute().stream().
                                   filter(at -> documentType.equals(at.getId())).
                                   findFirst();

        if (attr.isPresent()) {
            attr = attr.get().getAttribute().stream().
                        filter(at -> paramName.equals(at.getId())).
                        findFirst();

            return attr.isPresent() ? attr.get().getValue() : "";
        }

        return "";
    }

    @Override
    public Type applyRequestValues(Type model) {

        super.applyRequestValues(model);

        RequestWrapper request = getRequestWrapper();

        Type boundModel = (Type) fetchBoundObject(model, model);

        if (!(boundModel instanceof HasDocuments)) {
            return model;
        }

        String documentType = request.getParameter(getSelectedDocumentTypeFieldId());

        if (documentType != null) {
            setSelectedDocumentType(documentType);
            setCustomContent(boundModel, documentType, request.getParameter(getCustomContentFieldId()));
            setSelectedRecipientId(boundModel, documentType, request.getParameter(getSelectedRecipientFieldId()));
            setSender(boundModel, documentType, PageFlowContext.getRemoteUser());

            PageFlowContext.getSessionTemp().addAttribute(new Attribute(SELECTED_DOCUMENT_TYPE_ATTR_NAME, getSelectedDocumentType(), "string"));
        }

        return model;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        return super.processActions(model);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("CustomLetterBuilder", model);
    }
}
