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
package com.ail.insurance.policy;

import static com.ail.party.EmailAddress.MAIN_EMAIL_ADDRESS;
import static com.ail.party.PhoneNumber.MAIN_PHONE_NUMBER;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.party.EmailAddress;
import com.ail.party.Organisation;
import com.ail.party.PhoneNumber;

/**
 * Type represents a broker who owns products in the system. It acts as a
 * repository for all the contact information (etc) which is needed to identify
 * the organisation, and defines values for various standard fields.
 */
@TypeDefinition
@Audited
@Entity
public class Broker extends Organisation {
    private static final long serialVersionUID = -4521508279619758949L;

    public final static String QUOTE_TELEPHONE_NUMBER = "i18n_contact_system_broker_quote_phone_number";
    public final static String PAYMENT_TELEPHONE_NUMBER = "i18n_contact_system_broker_payment_phone_number";
    public final static String CLAIM_TELEPHONE_NUMBER = "i18n_contact_system_broker_claim_phone_number";
    public final static String QUOTE_EMAIL_ADDRESS = "i18n_contact_system_broker_quote_email_address";
    public final static String POLICY_EMAIL_ADDRESS = "i18n_contact_system_broker_policy_email_address";

    /**
     * Trading name if different from legal name. Leave as null if the two are the
     * same
     */
    private String tradingName;

    /** Direct debit ID number if direct debit payments are to be received */
    private String directDebitIdentificationNumber;

    public Broker() {
    }

    public String getDirectDebitIdentificationNumber() {
        return directDebitIdentificationNumber;
    }

    public void setDirectDebitIdentificationNumber(String directDebitIdentificationNumber) {
        this.directDebitIdentificationNumber = directDebitIdentificationNumber;
    }

    public String getPaymentTelephoneNumber() {
        return fetchContactSystem(PhoneNumber.class, PAYMENT_TELEPHONE_NUMBER, MAIN_PHONE_NUMBER).map(PhoneNumber::getPhoneNumber).orElse(null);
    }

    public void setPaymentTelephoneNumber(String paymentTelephoneNumber) {
        addContactSystem(new PhoneNumber(PAYMENT_TELEPHONE_NUMBER, paymentTelephoneNumber));
    }

    public String getQuoteEmailAddress() {
        return fetchContactSystem(EmailAddress.class, QUOTE_EMAIL_ADDRESS, MAIN_EMAIL_ADDRESS).map(EmailAddress::getEmailAddress).orElse(null);
    }

    public void setQuoteEmailAddress(String quoteEmailAddress) {
        addContactSystem(new EmailAddress(QUOTE_EMAIL_ADDRESS, quoteEmailAddress));
    }

    public String getPolicyEmailAddress() {
        return fetchContactSystem(EmailAddress.class, POLICY_EMAIL_ADDRESS, MAIN_EMAIL_ADDRESS).map(EmailAddress::getEmailAddress).orElse(null);
    }

    public void setPolicyEmailAddress(String policyEmailAddress) {
        addContactSystem(new EmailAddress(POLICY_EMAIL_ADDRESS, policyEmailAddress));
    }

    public String getQuoteTelephoneNumber() {
        return fetchContactSystem(PhoneNumber.class, QUOTE_TELEPHONE_NUMBER, MAIN_PHONE_NUMBER).map(PhoneNumber::getPhoneNumber).orElse(null);
    }

    public void setQuoteTelephoneNumber(String quoteTelephoneNumber) {
        addContactSystem(new PhoneNumber(QUOTE_TELEPHONE_NUMBER, quoteTelephoneNumber));
    }

    public String getTradingName() {
        if (tradingName == null) {
            return getLegalName();
        }

        return tradingName;
    }

    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }

    public String getClaimTelephoneNumber() {
        return fetchContactSystem(PhoneNumber.class, CLAIM_TELEPHONE_NUMBER, MAIN_PHONE_NUMBER).map(PhoneNumber::getPhoneNumber).orElse(null);
    }

    public void setClaimTelephoneNumber(String claimTelephoneNumber) {
        addContactSystem(new PhoneNumber(CLAIM_TELEPHONE_NUMBER, claimTelephoneNumber));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((directDebitIdentificationNumber == null) ? 0 : directDebitIdentificationNumber.hashCode());
        result = prime * result + ((tradingName == null) ? 0 : tradingName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Broker other = (Broker) obj;
        if (directDebitIdentificationNumber == null) {
            if (other.directDebitIdentificationNumber != null)
                return false;
        } else if (!directDebitIdentificationNumber.equals(other.directDebitIdentificationNumber))
            return false;

        if (tradingName == null) {
            if (other.tradingName != null)
                return false;
        } else if (!tradingName.equals(other.tradingName))
            return false;
        return super.equals(obj);
    }
}
