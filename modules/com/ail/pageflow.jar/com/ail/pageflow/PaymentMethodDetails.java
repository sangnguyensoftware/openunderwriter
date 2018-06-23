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

import static com.ail.core.Functions.isEmpty;
import static com.ail.pageflow.PageFlowContext.getRequestedOperation;
import static com.ail.pageflow.PageFlowContext.getRequestedOperationId;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.defaultDateFormat;
import static com.ail.pageflow.util.Functions.removeErrorMarkers;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.CardIssuer;
import com.ail.financial.DirectDebit;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentMethod;
import com.ail.pageflow.util.Functions;
import com.ail.party.Party;
/**
 * Widget for editing PaymentMethods on a Party. Currently supports DirectDebit and PaymentCard types.
 *
 */
public class PaymentMethodDetails extends PageContainer {
    private static final long serialVersionUID = -4810599045554021748L;

    /** Set to true if the user should be able to add and remove payment methods */
    private boolean addAndDeleteEnabled = true;
    private boolean readOnly = false;

    /**
     * PaymentMethod type options as a comma separated list
     * Any valid subclasses of {@link PaymentMethod} should be supported.
     */
    private String[] paymentMethodOptions = {DirectDebit.class.getSimpleName(), PaymentCard.class.getSimpleName()};

    private String addPaymentMethod;

    public PaymentMethodDetails() {
        super();
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PaymentMethodDetails", model);
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return model;
        }

        if (!conditionIsMet(model)) {
            return model;
        }

        setAddPaymentMethod(CoreContext.getRequestWrapper().getParameter(encodeId("addPaymentMethod")));

        handlePaymentMethodUpdates(model);

        return model;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return model;
        }

        if (!conditionIsMet(model)) {
            return model;
        }

        handlePaymentMethodAdd(model);

        handlePaymentMethodDelete(model);

        return model;
    }

    private void handlePaymentMethodDelete(Type model) {
        Party party = getTarget(model);
        if (deleteButtonPressed(party.getSystemId())) {
            int row = Integer.parseInt((String)PageFlowContext.getOperationParameters().get("row"));

            party.getPaymentMethod().remove(row);

            PageFlowContext.flagActionAsProcessed();
        }
    }

    private Type handlePaymentMethodUpdates(Type model) {
        RequestWrapper request = CoreContext.getRequestWrapper();
        Party party = getTarget(model);

        for (int i = 0; i < party.getPaymentMethod().size(); i++) {
            PaymentMethod paymentMethod = party.getPaymentMethod().get(i);

            removeErrorMarkers(paymentMethod);

            String fieldValue = null;
            if (DirectDebit.class.isInstance(paymentMethod)) {
                DirectDebit directDebit = (DirectDebit) paymentMethod;

                fieldValue = getFieldValue(request, i, "accountNumber");
                if (!isEmpty(fieldValue)) {
                    directDebit.setAccountNumber(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "sortCode");
                if (!isEmpty(fieldValue)) {
                    directDebit.setSortCode(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "name");
                if (!isEmpty(fieldValue)) {
                    directDebit.setName(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "bankName");
                if (!isEmpty(fieldValue)) {
                    directDebit.setBankName(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "branchName");
                if (!isEmpty(fieldValue)) {
                    directDebit.setBranchName(fieldValue);
                }
            } else if (PaymentCard.class.isInstance(paymentMethod)) {
                PaymentCard paymentCard = (PaymentCard) paymentMethod;

                fieldValue = getFieldValue(request, i, "issuer");
                if (!isEmpty(fieldValue)) {
                    try {
                        paymentCard.setIssuer(CardIssuer.forName(fieldValue));
                    } catch (IllegalArgumentException e) {
                        addError("issuerInvalid-" + i, "i18n_invalid_error", paymentMethod);
                    }
                }

                fieldValue = getFieldValue(request, i, "cardNumber");
                if (!isEmpty(fieldValue)) {
                    paymentCard.setCardNumber(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "cardHoldersName");
                if (!isEmpty(fieldValue)) {
                    paymentCard.setCardHoldersName(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "issueNumber");
                if (!isEmpty(fieldValue)) {
                    paymentCard.setIssueNumber(fieldValue);
                }
                fieldValue = getFieldValue(request, i, "securityCode");
                if (!isEmpty(fieldValue)) {
                    paymentCard.setSecurityCode(fieldValue);
                }

                fieldValue = getFieldValue(request, i, "startDate");
                if (!isEmpty(fieldValue)) {
                    try {
                        Date startDate = new SimpleDateFormat(defaultDateFormat()).parse(fieldValue);
                        paymentCard.setStartDate(startDate);
                    } catch (ParseException e) {
                        addError("startDateInvalid-" + i, "i18n_invalid_error", paymentMethod);
                    }
                }

                fieldValue = getFieldValue(request, i, "expiryDate");
                if (!isEmpty(fieldValue)) {
                    try {
                        Date expiryDate = new SimpleDateFormat(defaultDateFormat()).parse(fieldValue);
                        paymentCard.setExpiryDate(expiryDate);
                    } catch (ParseException e) {
                        addError("expiryDateInvalid-" + i, "i18n_invalid_error", paymentMethod);
                    }
                }
            }
        }

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return false;
        }
        Party party = getTarget(model);

        Functions.removeErrorMarkers(party);

        for (int i = 0; i < party.getPaymentMethod().size(); i++) {
            PaymentMethod paymentMethod = party.getPaymentMethod().get(i);

            if (DirectDebit.class.isInstance(paymentMethod)) {
                DirectDebit directDebit = (DirectDebit) paymentMethod;

                if (isEmpty(directDebit.getAccountNumber())) {
                    addError("accountNumber-" + i, i18n("i18n_required_error"), party);
                }
                if (isEmpty(directDebit.getName())) {
                    addError("name-" + i, i18n("i18n_required_error"), party);
                }
                if (isEmpty(directDebit.getBankName())) {
                    addError("bankName-" + i, i18n("i18n_required_error"), party);
                }
            } else if (PaymentCard.class.isInstance(paymentMethod)) {
                PaymentCard paymentCard = (PaymentCard) paymentMethod;

                if (paymentCard.getIssuer() == null) {
                    addError("issuer-" + i, i18n("i18n_required_error"), party);
                }
                if (isEmpty(paymentCard.getCardNumber())) {
                    addError("cardNumber-" + i, i18n("i18n_required_error"), party);
                }
                if (isEmpty(paymentCard.getCardHoldersName())) {
                    addError("cardHoldersName-" + i, i18n("i18n_required_error"), party);
                }
                if (isEmpty(paymentCard.getSecurityCode())) {
                    addError("securityCode-" + i, i18n("i18n_required_error"), party);
                }
                if (paymentCard.getExpiryDate() == null) {
                    addError("expiryDate-" + i, i18n("i18n_required_error"), party);
                }
            }
        }

        return Functions.hasErrorMarkers(party);
    }

    private String getFieldValue(RequestWrapper request, int i, String fieldName) {
        return request.getParameter(encodeId("/paymentMethod[" + (i + 1) + "]/" + fieldName));
    }

    private void handlePaymentMethodAdd(Type model) {
        Party party = getTarget(model);
        if (addButtonPressed(party.getSystemId())) {
            String paymentMethodType = StringUtils.substringAfter(getRequestedOperation(), this.getClass().getSimpleName() + "-add-");
            if (!isEmpty(paymentMethodType)) {
                setAddPaymentMethod(paymentMethodType);
            }

            if (getAddPaymentMethod() != null) {
                try {
                    party.getPaymentMethod().add((PaymentMethod) Class.forName("com.ail.financial." + getAddPaymentMethod()).newInstance());
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    addError("paymentMethod-0", i18n("i18n_invalid_error"), model);
                }
            }
            PageFlowContext.flagActionAsProcessed();
        }
    }

    private Party getTarget(Type model) {
        return (binding!=null) ? model.xpathGet(binding, Party.class) : (Party)model;
    }

    public String getAddPaymentMethod() {
        return addPaymentMethod;
    }

    public void setAddPaymentMethod(String addPaymentMethod) {
        this.addPaymentMethod = addPaymentMethod;
    }

    public String[] getPaymentMethodOptions() {
        return paymentMethodOptions;
    }

    public void setPaymentMethodOptions(String[] paymentMethodOptions) {
        // do nothing
    }

    public CardIssuer[] getIssuerOptions() {
        return CardIssuer.values();
    }

    public void setIssuerOptions(CardIssuer[] issuerOptions) {
        // do nothing
    }

    private boolean addButtonPressed(long systemId) {
        return getRequestedOperation() != null && getRequestedOperation().startsWith(this.getClass().getSimpleName() + "-add") && parseInt(getRequestedOperationId()) == systemId;
    }

    private boolean deleteButtonPressed(long systemId) {
        return getRequestedOperation() != null && getRequestedOperation().startsWith(this.getClass().getSimpleName() + "-delete") && parseInt(getRequestedOperationId()) == systemId;
    }

    public String paymentMethodId(Type model, int i) {
        String bindingRoot = !isEmpty(binding) ? binding : valueOf(model.getSystemId());
        return encodeId(bindingRoot + "/paymentMethod[" + (i + 1) + "]/id");
    }

    public String deleteOp(Party party, int i) {
        return "op=" + this.getClass().getSimpleName() + "-delete:id=" + party.getSystemId() + ":row=" + i + ":immediate=true";
    }

    public String addOp(Party party) {
        return "op=" + this.getClass().getSimpleName() + "-add:id=" + party.getSystemId() + ":immediate=true";
    }

    public String addOp(Party party, String paymentMethodType) {
        return "op=" + this.getClass().getSimpleName() + "-add-" + paymentMethodType + ":id=" + party.getSystemId() + ":immediate=true";
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isAddAndDeleteEnabled() {
        return addAndDeleteEnabled;
    }

    public void setAddAndDeleteEnabled(boolean addAndDeleteEnabled) {
        this.addAndDeleteEnabled = addAndDeleteEnabled;
    }
}
