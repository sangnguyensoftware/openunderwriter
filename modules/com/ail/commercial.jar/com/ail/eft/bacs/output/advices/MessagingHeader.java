//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:36:20 AM GMT 
//


package com.ail.eft.bacs.output.advices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="document-number" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="advice-type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="subject-first-aosn" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="subject-last-aosn" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="user-number" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="stream-identifier" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="reprint-indicator" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="envelope-sequence-number" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="report-generation-date" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="user-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="report-type" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "MessagingHeader")
public class MessagingHeader {

    @XmlAttribute(name = "document-number", required = true)
    protected int documentNumber;
    @XmlAttribute(name = "advice-type", required = true)
    protected String adviceType;
    @XmlAttribute(name = "subject-first-aosn", required = true)
    protected int subjectFirstAosn;
    @XmlAttribute(name = "subject-last-aosn", required = true)
    protected int subjectLastAosn;
    @XmlAttribute(name = "user-number", required = true)
    protected int userNumber;
    @XmlAttribute(name = "stream-identifier", required = true)
    protected long streamIdentifier;
    @XmlAttribute(name = "reprint-indicator")
    protected String reprintIndicator;
    @XmlAttribute(name = "envelope-sequence-number", required = true)
    protected int envelopeSequenceNumber;
    @XmlAttribute(name = "report-generation-date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reportGenerationDate;
    @XmlAttribute(name = "user-name", required = true)
    protected String userName;
    @XmlAttribute(name = "report-type", required = true)
    protected int reportType;

    /**
     * Gets the value of the documentNumber property.
     * 
     */
    public int getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the value of the documentNumber property.
     * 
     */
    public void setDocumentNumber(int value) {
        this.documentNumber = value;
    }

    /**
     * Gets the value of the adviceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdviceType() {
        return adviceType;
    }

    /**
     * Sets the value of the adviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdviceType(String value) {
        this.adviceType = value;
    }

    /**
     * Gets the value of the subjectFirstAosn property.
     * 
     */
    public int getSubjectFirstAosn() {
        return subjectFirstAosn;
    }

    /**
     * Sets the value of the subjectFirstAosn property.
     * 
     */
    public void setSubjectFirstAosn(int value) {
        this.subjectFirstAosn = value;
    }

    /**
     * Gets the value of the subjectLastAosn property.
     * 
     */
    public int getSubjectLastAosn() {
        return subjectLastAosn;
    }

    /**
     * Sets the value of the subjectLastAosn property.
     * 
     */
    public void setSubjectLastAosn(int value) {
        this.subjectLastAosn = value;
    }

    /**
     * Gets the value of the userNumber property.
     * 
     */
    public int getUserNumber() {
        return userNumber;
    }

    /**
     * Sets the value of the userNumber property.
     * 
     */
    public void setUserNumber(int value) {
        this.userNumber = value;
    }

    /**
     * Gets the value of the streamIdentifier property.
     * 
     */
    public long getStreamIdentifier() {
        return streamIdentifier;
    }

    /**
     * Sets the value of the streamIdentifier property.
     * 
     */
    public void setStreamIdentifier(long value) {
        this.streamIdentifier = value;
    }

    /**
     * Gets the value of the reprintIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReprintIndicator() {
        return reprintIndicator;
    }

    /**
     * Sets the value of the reprintIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReprintIndicator(String value) {
        this.reprintIndicator = value;
    }

    /**
     * Gets the value of the envelopeSequenceNumber property.
     * 
     */
    public int getEnvelopeSequenceNumber() {
        return envelopeSequenceNumber;
    }

    /**
     * Sets the value of the envelopeSequenceNumber property.
     * 
     */
    public void setEnvelopeSequenceNumber(int value) {
        this.envelopeSequenceNumber = value;
    }

    /**
     * Gets the value of the reportGenerationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReportGenerationDate() {
        return reportGenerationDate;
    }

    /**
     * Sets the value of the reportGenerationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReportGenerationDate(XMLGregorianCalendar value) {
        this.reportGenerationDate = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the reportType property.
     * 
     */
    public int getReportType() {
        return reportType;
    }

    /**
     * Sets the value of the reportType property.
     * 
     */
    public void setReportType(int value) {
        this.reportType = value;
    }

}