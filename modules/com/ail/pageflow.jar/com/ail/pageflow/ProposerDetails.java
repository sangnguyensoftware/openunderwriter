/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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

import static com.ail.core.Functions.isEmpty;
import static com.ail.pageflow.util.Functions.addError;
import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.util.regex.Pattern;

import com.ail.core.CoreContext;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.insurance.policy.CommercialProposer;
import com.ail.insurance.policy.Proposer;
import com.ail.pageflow.util.Functions;
import com.ail.party.Title;

/**
 * <p>Page element to capture the proposer's party details. The element has two distinct modes: Personal or Commercial. The actual mode
 * used for a given quotation depends on the type of the class returned by <code>model.xpathGet(binding)</code>, where binding defaults to
 * "/proposer".</p>
 * <p><img src="doc-files/ProposerDetails.png"/></p>
 * <p><img src="doc-files/ProposerDetailsCommercial.png"/></p>
 * <p>This page element applies the following validation rules:<ul>
 * <li>A value (other than "Title?") must be selected in the "Title" drop-down.</li>
 * <li>If a "Title" of "Other" is selected in the drop down, a value must be entered in "Other title".</li>
 * <li>Both "First name" and "Surname" must be supplied and must match the regular expression: ^[\\p{L}\\p{N}-,. ()]*$</li>
 * <li>Address lines 1 & 2 must be supplied and must match the regular expression above.</li>
 * <li>Address lines 3 & 4 are optional, but if supplied must match the regular expression above.</li>
 * <li>Postcode must be supplied, and must match the regular expression: ^[a-zA-Z0-9 -]*$</li>
 * <li>Telephone number must be supplied, and must match the regular expression: (^[+()0-9 -]*$)|(^[+()0-9 -]*[extEXT]{0,3}[ ()0-9]*$)</li>
 * <li>Mobile phone number are optional and must match the regular expression: (^[+()0-9 -]*$)</li>
 * <li>Email address must be supplied, and must match the regular expression: ^[0-9a-zA-Z.-]*@[0-9a-zA-Z.-]*[.][0-9a-zA-Z.-]*$</li>
 * <li>If in Commercial mode, company name must be supplied and must match the regular expression: ^[\\p{L}\\p{N}-,. ()]*$.</li>
 * </ul>
 */
public class ProposerDetails extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;
    private static final Pattern NAME_PATTERN = compile("^[\\p{L}\\p{N}-,. &()]*$");
    private static final Pattern POSTCODE_PATTERN = compile("^[a-zA-Z0-9 -]*$");
    private static final Pattern EMAIL_PATTERN = compile("^[0-9a-zA-Z.-]*@[0-9a-zA-Z.-]*[.][0-9a-zA-Z.-]*$");
    private static final Pattern MOBILE_PATTERN = compile("(^[+()0-9 -]*$)");
    private static final Pattern PHONE_PATTERN = compile("(^[+()0-9 -]*$)|(^[+()0-9 -]*[extEXT]{0,3}[ ()0-9]*$)");
    private static final String DEFAULT_BINDING = "/proposer";
    private static final String DEFAULT_MANDATORY_ADDRESS_FIELDS="address1|postcode";

    private String mandatoryAddressFields = DEFAULT_MANDATORY_ADDRESS_FIELDS;
    private transient Pattern mandatoryAddressFieldsPattern = compile(mandatoryAddressFields);

    public ProposerDetails() {
        super();
        setBinding(DEFAULT_BINDING);
    }

    public String getMandatoryAddressFields() {
        return mandatoryAddressFields;
    }

    public void setMandatoryAddressFields(String mandatoryAddressFields) {
        this.mandatoryAddressFields = mandatoryAddressFields;
        this.mandatoryAddressFieldsPattern = compile(mandatoryAddressFields);
    }

    public boolean isFieldMandatory(String fieldName) {
        return mandatoryAddressFieldsPattern.matcher(fieldName).matches();
    }

    @Override
    public Type applyRequestValues(Type model) {
        String value;
        RequestWrapper request = CoreContext.getRequestWrapper();

        Proposer proposer = model.xpathGet(getBinding(), Proposer.class);

        value = request.getParameter("title");
        if (value != null) {
            proposer.setTitle(Title.forName(value));
        }

        value = request.getParameter("otherTitle");
        if (value != null) {
            proposer.setOtherTitle(request.getParameter("otherTitle"));
        }

        value = request.getParameter("firstname");
        if (value != null) {
            proposer.setFirstName(request.getParameter("firstname"));
        }

        value = request.getParameter("surname");
        if (value != null) {
            proposer.setSurname(request.getParameter("surname"));
        }

        value = request.getParameter("address1");
        if (value != null) {
            proposer.getAddress().setLine1(request.getParameter("address1"));
        }

        value = request.getParameter("address2");
        if (value != null) {
            proposer.getAddress().setLine2(request.getParameter("address2"));
        }

        value = request.getParameter("address3");
        if (value != null) {
            proposer.getAddress().setLine3(request.getParameter("address3"));
        }

        value = request.getParameter("address4");
        if (value != null) {
            proposer.getAddress().setLine4(request.getParameter("address4"));
        }

        value = request.getParameter("town");
        if (value != null) {
            proposer.getAddress().setTown(request.getParameter("town"));
        }

        value = request.getParameter("county");
        if (value != null) {
            proposer.getAddress().setCounty(request.getParameter("county"));
        }

        value = request.getParameter("postcode");
        if (value != null) {
            proposer.getAddress().setPostcode(request.getParameter("postcode"));
        }

        value = request.getParameter("phone");
        if (value != null) {
            proposer.setTelephoneNumber(request.getParameter("phone"));
        }

        value = request.getParameter("mobile");
        if (value != null) {
            proposer.setMobilephoneNumber(request.getParameter("mobile"));
        }

        value = request.getParameter("email");
        if (value != null) {
            proposer.setEmailAddress(request.getParameter("email"));
        }

        if (proposer instanceof CommercialProposer) {
            value = request.getParameter("companyName");
            if (value != null) {
                ((CommercialProposer)proposer).setCompanyName(value);
            }
        }

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        Proposer proposer = model.xpathGet(getBinding(), Proposer.class);

        Functions.removeErrorMarkers(proposer.getInstance());

        // Check the proposer for errors.
        if (Title.UNDEFINED.equals(proposer.getTitle())) {
            addError("title", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (Title.OTHER.equals(proposer.getTitle()) && isEmpty(proposer.getOtherTitle())) {
            addError("otherTitle", i18n("i18n_required_error"), proposer.getInstance());
        }

        if (isEmpty(proposer.getFirstName())) {
            addError("firstName", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (!NAME_PATTERN.matcher(proposer.getFirstName()).find()) {
            addError("firstName", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isEmpty(proposer.getSurname())) {
            addError("surname", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (!NAME_PATTERN.matcher(proposer.getSurname()).find()) {
            addError("surname", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isFieldMandatory("address1") && isEmpty(proposer.getAddress().getLine1())) {
            addError("address1", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (!NAME_PATTERN.matcher(proposer.getAddress().getLine1()).find()) {
            addError("address1", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isFieldMandatory("address2") && isEmpty(proposer.getAddress().getLine2())) {
            addError("address2", i18n("i18n_required_error"), proposer.getInstance());
        }
        if (!isEmpty(proposer.getAddress().getLine2()) && !NAME_PATTERN.matcher(proposer.getAddress().getLine2()).find()) {
            addError("address2", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isFieldMandatory("address3") && isEmpty(proposer.getAddress().getLine3())) {
            addError("address3", i18n("i18n_required_error"), proposer.getInstance());
        }
        if (!isEmpty(proposer.getAddress().getLine3()) && !NAME_PATTERN.matcher(proposer.getAddress().getLine3()).find()) {
            addError("address3", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isFieldMandatory("address4") && isEmpty(proposer.getAddress().getLine4())) {
            addError("address4", i18n("i18n_required_error"), proposer.getInstance());
        }
        if (!isEmpty(proposer.getAddress().getLine4()) && !NAME_PATTERN.matcher(proposer.getAddress().getLine4()).find()) {
            addError("address4", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isFieldMandatory("town") && isEmpty(proposer.getAddress().getTown())) {
            addError("town", i18n("i18n_required_error"), proposer.getInstance());
        }

        if (isFieldMandatory("county") && isEmpty(proposer.getAddress().getCounty())) {
            addError("county", i18n("i18n_required_error"), proposer.getInstance());
        }

        if (isFieldMandatory("postcode") && isEmpty(proposer.getAddress().getPostcode())) {
            addError("postcode", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (!POSTCODE_PATTERN.matcher(proposer.getAddress().getPostcode()).find()) {
            addError("postcode", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isEmpty(proposer.getTelephoneNumber())) {
            addError("phone", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (!PHONE_PATTERN.matcher(proposer.getTelephoneNumber()).find()) {
            addError("phone", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (!MOBILE_PATTERN.matcher(proposer.getMobilephoneNumber()).find()) {
            addError("mobile", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (isEmpty(proposer.getEmailAddress())) {
            addError("email", i18n("i18n_required_error"), proposer.getInstance());
        }
        else if (!EMAIL_PATTERN.matcher(proposer.getEmailAddress()).find()) {
            addError("email", i18n("i18n_invalid_error"), proposer.getInstance());
        }

        if (proposer instanceof CommercialProposer) {
            String companyName=((CommercialProposer)proposer).getCompanyName();
            if (isEmpty(companyName)) {
                addError("companyName", i18n("i18n_required_error"), proposer.getInstance());
            }
            else if (!NAME_PATTERN.matcher(companyName).find()) {
                addError("companyName", i18n("i18n_invalid_error"), proposer.getInstance());
            }
        }
        return Functions.hasErrorMarkers(proposer.getInstance());
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("ProposerDetails", model);
    }
}
