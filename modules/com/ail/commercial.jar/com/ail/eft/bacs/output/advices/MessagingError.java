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
 *       &lt;attribute name="line1" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="line2" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="line3" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "MessagingError")
public class MessagingError {

    @XmlAttribute(name = "line1")
    protected String line1;
    @XmlAttribute(name = "line2")
    protected String line2;
    @XmlAttribute(name = "line3")
    protected String line3;

    /**
     * Gets the value of the line1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine1() {
        return line1;
    }

    /**
     * Sets the value of the line1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine1(String value) {
        this.line1 = value;
    }

    /**
     * Gets the value of the line2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine2() {
        return line2;
    }

    /**
     * Sets the value of the line2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine2(String value) {
        this.line2 = value;
    }

    /**
     * Gets the value of the line3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine3() {
        return line3;
    }

    /**
     * Sets the value of the line3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine3(String value) {
        this.line3 = value;
    }

}
