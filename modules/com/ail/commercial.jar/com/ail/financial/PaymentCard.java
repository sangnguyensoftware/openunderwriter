/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.financial;

import static javax.persistence.EnumType.STRING;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

/**
 * Represents the details of a payment card. Payment card encompasses both credit and debit cards.
 */
@TypeDefinition
@Audited
@Entity
public class PaymentCard extends PaymentMethod {
    private static final long serialVersionUID = 1L;

    @Transient
    private transient DateFormat dateFormatter;

    /** The type of card (issuer) */
    @Enumerated(STRING)
    @Column(name="pmeIssuer")
    private CardIssuer issuer;

    /** The credit card's number */
    @Column(name="pmeCardNumber")
    private String cardNumber;

    /** The card holder's name as it appears on the card */
    @Column(name="pmeCardHoldersName")
    private String cardHoldersName;

    /** The card's issues number (optional based on card type) */
    @Column(name="pmeIssueNumber")
    private String issueNumber;

    /** Date (month & year only) when the card expires */
    @Column(name="pmeExpiryDate")
    private Date expiryDate;

    /** Date (month & year only) when the card starts */
    @Column(name="pmeStartDate")
    private Date startDate;

    /** The security code which appears on the back of the card (optional based on card type) */
    @Column(name="pmeSecurityCode")
    private String securityCode;

    public PaymentCard() {
        super();
    }

    public PaymentCard(CardIssuer issuer, String cardNumber, String cardHoldersName, String issueNumber, String securityCode) {
        super();
        this.issuer = issuer;
        this.cardNumber = cardNumber;
        this.cardHoldersName = cardHoldersName;
        this.issueNumber = issueNumber;
        this.securityCode = securityCode;
    }

    public String getCardHoldersName() {
        return cardHoldersName;
    }

    public void setCardHoldersName(String cardHoldersName) {
        this.cardHoldersName = cardHoldersName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Get the card number with the first part masked for security. This
     * will return a string in the form '**** **** **** 1234"
     * @return masked card number
     */
    public String getMaskedCardNumber() {
        if (cardNumber!=null && cardNumber.length() > 4) {
            return "**** **** **** "+cardNumber.substring(cardNumber.length()-4);
        }
        else {
            return "**** **** **** ****";
        }
    }

    @Override
    public String getId() {
        return getMaskedCardNumber();
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public CardIssuer getIssuer() {
        return issuer;
    }

    public void setIssuer(CardIssuer issuer) {
        this.issuer = issuer;
    }

    public String getIssuerAsString() {
        if (issuer!=null) {
            return issuer.toString();
        }
        else {
            return null;
        }
    }

    public void setIssuerAsString(String issuer) throws IndexOutOfBoundsException {
        this.issuer=CardIssuer.valueOf(issuer);
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Fetch a formatted string representation of the expiry date. This method
     * will return a string in the format "MM/yy"
     * @return formatted date, or an empty string if expiryDate is null
     */
    public String getFormattedExpiryDate() {
        if (expiryDate==null) {
            return "";
        }

        return dateFormatter().format(expiryDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Fetch a formatted string representation of the start date. This method
     * will return a string in the format "MM/yy"
     * @return formatted date, or an empty string if expiryDate is null
     */
    public String getFormattedStartDate() {
        if (startDate==null) {
            return "";
        }

        return dateFormatter().format(startDate);
    }

    private DateFormat dateFormatter() {
        if (dateFormatter==null) {
            dateFormatter=new SimpleDateFormat("MM/yy");
        }
        return dateFormatter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cardHoldersName == null) ? 0 : cardHoldersName.hashCode());
        result = prime * result + ((cardNumber == null) ? 0 : cardNumber.hashCode());
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((issueNumber == null) ? 0 : issueNumber.hashCode());
        result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
        result = prime * result + ((securityCode == null) ? 0 : securityCode.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
        if (!super.equals(obj))
            return false;
        PaymentCard other = (PaymentCard) obj;
        if (cardHoldersName == null) {
            if (other.cardHoldersName != null)
                return false;
        } else if (!cardHoldersName.equals(other.cardHoldersName))
            return false;
        if (cardNumber == null) {
            if (other.cardNumber != null)
                return false;
        } else if (!cardNumber.equals(other.cardNumber))
            return false;
        if (expiryDate == null) {
            if (other.expiryDate != null)
                return false;
        } else if (!expiryDate.equals(other.expiryDate))
            return false;
        if (issueNumber == null) {
            if (other.issueNumber != null)
                return false;
        } else if (!issueNumber.equals(other.issueNumber))
            return false;
        if (issuer != other.issuer)
            return false;
        if (securityCode == null) {
            if (other.securityCode != null)
                return false;
        } else if (!securityCode.equals(other.securityCode))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentCard [issuer=" + issuer + ", cardNumber=" + cardNumber + ", cardHoldersName=" + cardHoldersName + ", issueNumber=" + issueNumber + ", expiryDate=" + expiryDate + ", startDate="
                + startDate + ", securityCode=" + securityCode + ", getId()=" + getId() + "]";
    }
}
