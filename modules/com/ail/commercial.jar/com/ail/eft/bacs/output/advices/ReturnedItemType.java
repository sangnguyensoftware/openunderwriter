//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:36:20 AM GMT 
//


package com.ail.eft.bacs.output.advices;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ReturnedItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReturnedItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="PayerAccount" type="{}AccountType"/>
 *         &lt;element name="ReceiverAccount" type="{}AccountType"/>
 *       &lt;/choice>
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="returnCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="returnDescription" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="originalProcessingDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="uacName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="valueOf" use="required" type="{}ValueType" />
 *       &lt;attribute name="currency" use="required" type="{}CurrencyType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnedItemType", propOrder = {
    "payerAccount",
    "receiverAccount"
})
public class ReturnedItemType {

    @XmlElement(name = "PayerAccount")
    protected AccountType payerAccount;
    @XmlElement(name = "ReceiverAccount")
    protected AccountType receiverAccount;
    @XmlAttribute(name = "ref", required = true)
    protected String ref;
    @XmlAttribute(name = "transCode", required = true)
    protected String transCode;
    @XmlAttribute(name = "returnCode", required = true)
    protected String returnCode;
    @XmlAttribute(name = "returnDescription", required = true)
    protected String returnDescription;
    @XmlAttribute(name = "originalProcessingDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar originalProcessingDate;
    @XmlAttribute(name = "uacName")
    protected String uacName;
    @XmlAttribute(name = "valueOf", required = true)
    protected BigDecimal valueOf;
    @XmlAttribute(name = "currency", required = true)
    protected String currency;

    /**
     * Gets the value of the payerAccount property.
     * 
     * @return
     *     possible object is
     *     {@link AccountType }
     *     
     */
    public AccountType getPayerAccount() {
        return payerAccount;
    }

    /**
     * Sets the value of the payerAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountType }
     *     
     */
    public void setPayerAccount(AccountType value) {
        this.payerAccount = value;
    }

    /**
     * Gets the value of the receiverAccount property.
     * 
     * @return
     *     possible object is
     *     {@link AccountType }
     *     
     */
    public AccountType getReceiverAccount() {
        return receiverAccount;
    }

    /**
     * Sets the value of the receiverAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountType }
     *     
     */
    public void setReceiverAccount(AccountType value) {
        this.receiverAccount = value;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

    /**
     * Gets the value of the transCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransCode() {
        return transCode;
    }

    /**
     * Sets the value of the transCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransCode(String value) {
        this.transCode = value;
    }

    /**
     * Gets the value of the returnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnCode() {
        return returnCode;
    }

    /**
     * Sets the value of the returnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnCode(String value) {
        this.returnCode = value;
    }

    /**
     * Gets the value of the returnDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnDescription() {
        return returnDescription;
    }

    /**
     * Sets the value of the returnDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnDescription(String value) {
        this.returnDescription = value;
    }

    /**
     * Gets the value of the originalProcessingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOriginalProcessingDate() {
        return originalProcessingDate;
    }

    /**
     * Sets the value of the originalProcessingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOriginalProcessingDate(XMLGregorianCalendar value) {
        this.originalProcessingDate = value;
    }

    /**
     * Gets the value of the uacName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUacName() {
        return uacName;
    }

    /**
     * Sets the value of the uacName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUacName(String value) {
        this.uacName = value;
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

}
