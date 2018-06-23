//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:36:20 AM GMT 
//


package com.ail.eft.bacs.output.advices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="ServiceLicenseInformation" type="{}ServiceLevelLicenseInformation" minOccurs="0"/>
 *         &lt;element name="OriginatingAccount" type="{}AccountType"/>
 *         &lt;choice>
 *           &lt;element ref="{}ReturnedDebitItem" maxOccurs="unbounded"/>
 *           &lt;element ref="{}ReturnedCreditItem" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element ref="{}Totals" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceLicenseInformation",
    "originatingAccount",
    "returnedDebitItem",
    "returnedCreditItem",
    "totals"
})
@XmlRootElement(name = "OriginatingAccountRecord")
public class OriginatingAccountRecord {

    @XmlElement(name = "ServiceLicenseInformation")
    protected ServiceLevelLicenseInformation serviceLicenseInformation;
    @XmlElement(name = "OriginatingAccount", required = true)
    protected AccountType originatingAccount;
    @XmlElement(name = "ReturnedDebitItem")
    protected List<ReturnedItemType> returnedDebitItem;
    @XmlElement(name = "ReturnedCreditItem")
    protected List<ReturnedItemType> returnedCreditItem;
    @XmlElement(name = "Totals", required = true)
    protected List<Totals> totals;

    /**
     * Gets the value of the serviceLicenseInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelLicenseInformation }
     *     
     */
    public ServiceLevelLicenseInformation getServiceLicenseInformation() {
        return serviceLicenseInformation;
    }

    /**
     * Sets the value of the serviceLicenseInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelLicenseInformation }
     *     
     */
    public void setServiceLicenseInformation(ServiceLevelLicenseInformation value) {
        this.serviceLicenseInformation = value;
    }

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
     * Gets the value of the returnedDebitItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the returnedDebitItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturnedDebitItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReturnedItemType }
     * 
     * 
     */
    public List<ReturnedItemType> getReturnedDebitItem() {
        if (returnedDebitItem == null) {
            returnedDebitItem = new ArrayList<ReturnedItemType>();
        }
        return this.returnedDebitItem;
    }

    /**
     * Gets the value of the returnedCreditItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the returnedCreditItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturnedCreditItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReturnedItemType }
     * 
     * 
     */
    public List<ReturnedItemType> getReturnedCreditItem() {
        if (returnedCreditItem == null) {
            returnedCreditItem = new ArrayList<ReturnedItemType>();
        }
        return this.returnedCreditItem;
    }

    /**
     * Gets the value of the totals property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the totals property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTotals().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Totals }
     * 
     * 
     */
    public List<Totals> getTotals() {
        if (totals == null) {
            totals = new ArrayList<Totals>();
        }
        return this.totals;
    }

}
