//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 10:36:20 AM GMT 
//


package com.ail.eft.bacs.output.advices;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 *       &lt;attribute name="numberOf" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
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
@XmlType(name = "")
@XmlRootElement(name = "Totals")
public class Totals {

    @XmlAttribute(name = "numberOf", required = true)
    protected BigInteger numberOf;
    @XmlAttribute(name = "valueOf", required = true)
    protected BigDecimal valueOf;
    @XmlAttribute(name = "currency", required = true)
    protected String currency;

    /**
     * Gets the value of the numberOf property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOf() {
        return numberOf;
    }

    /**
     * Sets the value of the numberOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOf(BigInteger value) {
        this.numberOf = value;
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
