//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:35:45 AM GMT 
//


package com.ail.eft.bacs.output.reports;

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
 *         &lt;element ref="{}Header"/>
 *         &lt;element ref="{}AddresseeInformation" minOccurs="0"/>
 *         &lt;element ref="{}Message" minOccurs="0"/>
 *         &lt;element ref="{}Submission"/>
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
    "header",
    "addresseeInformation",
    "message",
    "submission"
})
@XmlRootElement(name = "ArrivalReport")
public class ArrivalReport {

    @XmlElement(name = "Header", required = true)
    protected Header header;
    @XmlElement(name = "AddresseeInformation")
    protected AddresseeInformation addresseeInformation;
    @XmlElement(name = "Message")
    protected Message message;
    @XmlElement(name = "Submission", required = true)
    protected Submission submission;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link Header }
     *     
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link Header }
     *     
     */
    public void setHeader(Header value) {
        this.header = value;
    }

    /**
     * Gets the value of the addresseeInformation property.
     * 
     * @return
     *     possible object is
     *     {@link AddresseeInformation }
     *     
     */
    public AddresseeInformation getAddresseeInformation() {
        return addresseeInformation;
    }

    /**
     * Sets the value of the addresseeInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddresseeInformation }
     *     
     */
    public void setAddresseeInformation(AddresseeInformation value) {
        this.addresseeInformation = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link Message }
     *     
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link Message }
     *     
     */
    public void setMessage(Message value) {
        this.message = value;
    }

    /**
     * Gets the value of the submission property.
     * 
     * @return
     *     possible object is
     *     {@link Submission }
     *     
     */
    public Submission getSubmission() {
        return submission;
    }

    /**
     * Sets the value of the submission property.
     * 
     * @param value
     *     allowed object is
     *     {@link Submission }
     *     
     */
    public void setSubmission(Submission value) {
        this.submission = value;
    }

}
