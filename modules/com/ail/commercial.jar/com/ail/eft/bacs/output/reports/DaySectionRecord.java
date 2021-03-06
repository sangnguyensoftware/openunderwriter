//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:35:45 AM GMT 
//


package com.ail.eft.bacs.output.reports;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element ref="{}Credit"/>
 *         &lt;element ref="{}Debit"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dayRef" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="recordDate" type="{}UserInputDate" />
 *       &lt;attribute name="processingDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "credit",
    "debit"
})
@XmlRootElement(name = "DaySectionRecord")
public class DaySectionRecord {

    @XmlElement(name = "Credit", required = true)
    protected Credit credit;
    @XmlElement(name = "Debit", required = true)
    protected Debit debit;
    @XmlAttribute(name = "dayRef")
    protected String dayRef;
    @XmlAttribute(name = "recordDate")
    protected String recordDate;
    @XmlAttribute(name = "processingDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar processingDate;

    /**
     * Gets the value of the credit property.
     * 
     * @return
     *     possible object is
     *     {@link Credit }
     *     
     */
    public Credit getCredit() {
        return credit;
    }

    /**
     * Sets the value of the credit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credit }
     *     
     */
    public void setCredit(Credit value) {
        this.credit = value;
    }

    /**
     * Gets the value of the debit property.
     * 
     * @return
     *     possible object is
     *     {@link Debit }
     *     
     */
    public Debit getDebit() {
        return debit;
    }

    /**
     * Sets the value of the debit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Debit }
     *     
     */
    public void setDebit(Debit value) {
        this.debit = value;
    }

    /**
     * Gets the value of the dayRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayRef() {
        return dayRef;
    }

    /**
     * Sets the value of the dayRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayRef(String value) {
        this.dayRef = value;
    }

    /**
     * Gets the value of the recordDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordDate() {
        return recordDate;
    }

    /**
     * Sets the value of the recordDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordDate(String value) {
        this.recordDate = value;
    }

    /**
     * Gets the value of the processingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProcessingDate() {
        return processingDate;
    }

    /**
     * Sets the value of the processingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProcessingDate(XMLGregorianCalendar value) {
        this.processingDate = value;
    }

}
