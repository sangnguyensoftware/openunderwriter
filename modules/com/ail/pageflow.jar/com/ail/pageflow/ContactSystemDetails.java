/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
import static com.ail.core.Functions.isEmpty;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.removeErrorMarkers;
import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.ail.core.Type;
import com.ail.party.Address;
import com.ail.party.ContactSystem;
import com.ail.party.EmailAddress;
import com.ail.party.Party;
import com.ail.party.PhoneNumber;

/**
 * Page element to display/edit the details of a contact system (Address, Phone number, etc).
 */
public class ContactSystemDetails extends PageElement {
    private static final long serialVersionUID = -4810848163554021748L;
    private static final Pattern ADDRESS_PATTERN = compile("^[\\p{L}\\p{N}-,. &()]*$");
    private static final Pattern POSTCODE_PATTERN = compile("^[a-zA-Z0-9 -]*$");
    private static final Pattern EMAIL_PATTERN = compile("^[0-9a-zA-Z.-]*@[0-9a-zA-Z.-]*[.][0-9a-zA-Z.-]*$");
    private static final Pattern PHONE_PATTERN = compile("(^[+()0-9 -]*$)|(^[+()0-9 -]*[extEXT]{0,3}[ ()0-9]*$)");
    private static final Pattern MATCH_ALL = compile(".*");
    private static final String DEFAULT_MANDATORY_ADDRESS_FIELDS="address1|postcode";

    private String mandatoryAddressFields = DEFAULT_MANDATORY_ADDRESS_FIELDS;
    private transient Pattern mandatoryAddressFieldsPattern = compile(mandatoryAddressFields);
    private String classes=".*";
    private transient Pattern classesPattern = MATCH_ALL;
    private String types=".*";
    private transient Pattern typesPattern = MATCH_ALL;

    public ContactSystemDetails() {
        super();
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
        this.typesPattern = null;
    }

    Pattern getTypesPattern() {
        if (typesPattern == null) {
            typesPattern = compile(types);
        }
        return typesPattern;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
        this.classesPattern = null;
    }

    Pattern getClassesPattern() {
        if (classesPattern == null) {
            classesPattern = compile(classes);
        }
        return classesPattern;
    }

    public String getMandatoryAddressFields() {
        return mandatoryAddressFields;
    }

    public void setMandatoryAddressFields(String mandatoryAddressFields) {
        this.mandatoryAddressFields = mandatoryAddressFields;
    }

    private Pattern getMandatoryAddressFieldsPattern() {
        if (mandatoryAddressFieldsPattern == null) {
            mandatoryAddressFieldsPattern = compile(getMandatoryAddressFields());
        }
        return mandatoryAddressFieldsPattern;
    }

    public List<ContactSystem> fetchContactSystems(Type model) {
        List<ContactSystem> results = new ArrayList<>();

        Party party = fetchBoundObject(model, null, Party.class);

        if (party != null) {
            results.addAll(party.fetchContactSystems(new Date(), getClassesPattern(), getTypesPattern()));
        }

        return results;
    }

    public boolean isFieldMandatory(String fieldName) {
        return getMandatoryAddressFieldsPattern().matcher(fieldName).matches();
    }

    public String fieldId(ContactSystem contactSystem, String fieldName) {
        return encodeId(contactSystem.getExternalSystemId() + fieldName);
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (!conditionIsMet(model)) {
            return model;
        }

        try {
            for(ContactSystem cs: fetchContactSystems(model)) {
                if (cs instanceof Address) {
                    applyRquestValue(cs, "address1", "setLine1");
                    applyRquestValue(cs, "address2", "setLine2");
                    applyRquestValue(cs, "address3", "setLine3");
                    applyRquestValue(cs, "address4", "setLine4");
                    applyRquestValue(cs, "town", "setTown");
                    applyRquestValue(cs, "county", "setCounty");
                    applyRquestValue(cs, "country", "setCountry");
                    applyRquestValue(cs, "postcode", "setPostcode");
                }
                if (cs instanceof PhoneNumber) {
                    applyRquestValue(cs, "phone", "setPhoneNumber");
                }
                if (cs instanceof EmailAddress) {
                    applyRquestValue(cs, "email", "setEmailAddress");
                }
            }
        } catch (SecurityException | ReflectiveOperationException e) {
            throw new ValidationError("Could not apply request values", e);
        }

        return model;
    }

    private void applyRquestValue(ContactSystem cs, String field, String setter) throws ReflectiveOperationException, SecurityException {
        String param = getRequestWrapper().getParameter(fieldId(cs, field));
        if (param != null) {
            cs.getClass().getMethod(setter, String.class).invoke(cs,  param);
        }
    }

    @Override
    public boolean processValidations(Type model) {
        if (!conditionIsMet(model)) {
            return false;
        }

        try {
            for(ContactSystem cs: fetchContactSystems(model)) {
                if (cs instanceof Address) {
                    validateValue(cs, "address1", "getLine1", ADDRESS_PATTERN);
                    validateValue(cs, "address2", "getLine2", ADDRESS_PATTERN);
                    validateValue(cs, "address3", "getLine3", ADDRESS_PATTERN);
                    validateValue(cs, "address4", "getLine4", ADDRESS_PATTERN);
                    validateValue(cs, "town", "getTown", ADDRESS_PATTERN);
                    validateValue(cs, "county", "getCounty", ADDRESS_PATTERN);
                    validateValue(cs, "country", "getCountry", ADDRESS_PATTERN);
                    validateValue(cs, "postcode", "getPostcode", POSTCODE_PATTERN);
                }
                if (cs instanceof PhoneNumber) {
                    validateValue(cs, "phone", "getPhoneNumber", PHONE_PATTERN);
                }
                if (cs instanceof EmailAddress) {
                    validateValue(cs, "email", "getEmailAddress", EMAIL_PATTERN);
                }
            }
        } catch (SecurityException | ReflectiveOperationException e) {
            throw new ValidationError("Could not validate ContactSystem values", e);
        }

        return super.processValidations(model);
    }

    private void validateValue(ContactSystem cs, String fieldName, String method, Pattern validation) throws ReflectiveOperationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String value = (String) cs.getClass().getMethod(method).invoke(cs);

        if (isEmpty(value)) {
            if (isFieldMandatory(fieldName)) {
                addError(fieldName, "i18n_required_error", cs);
            }
            else {
                removeErrorMarkers(cs, fieldName);
            }
        }
        else if (!validation.matcher(value).matches()) {
            addError(fieldName, "i18n_invalid_error", cs);
        }
        else {
           removeErrorMarkers(cs, fieldName);
        }
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("ContactSystemDetails", model);
    }
}
