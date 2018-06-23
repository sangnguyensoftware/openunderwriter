//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:35:45 AM GMT 
//


package com.ail.eft.bacs.output.reports;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}OriginatingAccount"/>
 *         &lt;element ref="{}DestinationAccount"/>
 *       &lt;/sequence>
 *       &lt;attribute name="accountType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="reference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transactionCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="currency" use="required" type="{}CurrencyType" />
 *       &lt;attribute name="valueOf" use="required" type="{}ValueType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "originatingAccount",
    "destinationAccount"
})
@XmlRootElement(name = "Adjustment")
public class Adjustment {

    @XmlElement(name = "OriginatingAccount", required = true)
    protected AccountType originatingAccount;
    @XmlElement(name = "DestinationAccount", required = true)
    protected AccountType destinationAccount;
    @XmlAttribute(name = "accountType", required = true)
    protected String accountType;
    @XmlAttribute(name = "reference")
    protected String reference;
    @XmlAttribute(name = "transactionCode", required = true)
    protected String transactionCode;
    @XmlAttribute(name = "currency", required = true)
    protected String currency;
    @XmlAttribute(name = "valueOf", required = true)
    protected BigDecimal valueOf;

    /**
     * Gets the value of the originatingAccount property.
     * 
     * @return
     *     possible object is
     *     {@link AccountType }
     *     
     */
    public AccountType getOriginatingAccount() {
        return originatingAccount;
    }

    /**
     * Sets the value of the originatingAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountType }
     *     
     */
    public void setOriginatingAccount(AccountType value) {
        this.originatingAccount = value;
    }

    /**
     * Gets the value of the destinationAccount property.
     * 
     * @return
     *     possible object is
     *     {@link AccountType }
     *     
     */
    public AccountType getDestinationAccount() {
        return destinationAccount;
    }

    /**
     * Sets the value of the destinationAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountType }
     *     
     */
    public void setDestinationAccount(AccountType value) {
        this.destinationAccount = value;
    }

    /**
     * Gets the value of the accountType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountType(String value) {
        this.accountType = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the transactionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionCode() {
        return transactionCode;
    }

    /**
     * Sets the value of the transactionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionCode(String value) {
        this.transactionCode = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the valueOf property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValueOf() {
        return valueOf;
    }

    /**
     * Sets the value of the valueOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValueOf(BigDecimal value) {
        this.valueOf = value;
    }

}
