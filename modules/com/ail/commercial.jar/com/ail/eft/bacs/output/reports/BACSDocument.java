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
 *         &lt;element name="Data">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{}InputReport"/>
 *                   &lt;element ref="{}WithdrawalReport"/>
 *                   &lt;element ref="{}ArrivalReport"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SignatureMethod" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Signature" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
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
    "data",
    "signatureMethod",
    "signature"
})
@XmlRootElement(name = "BACSDocument")
public class BACSDocument {

    @XmlElement(name = "Data", required = true)
    protected BACSDocument.Data data;
    @XmlElement(name = "SignatureMethod", required = true)
    protected String signatureMethod;
    @XmlElement(name = "Signature", required = true)
    protected byte[] signature;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link BACSDocument.Data }
     *     
     */
    public BACSDocument.Data getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link BACSDocument.Data }
     *     
     */
    public void setData(BACSDocument.Data value) {
        this.data = value;
    }

    /**
     * Gets the value of the signatureMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignatureMethod() {
        return signatureMethod;
    }

    /**
     * Sets the value of the signatureMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignatureMethod(String value) {
        this.signatureMethod = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSignature(byte[] value) {
        this.signature = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element ref="{}InputReport"/>
     *         &lt;element ref="{}WithdrawalReport"/>
     *         &lt;element ref="{}ArrivalReport"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "inputReport",
        "withdrawalReport",
        "arrivalReport"
    })
    public static class Data {

        @XmlElement(name = "InputReport")
        protected InputReport inputReport;
        @XmlElement(name = "WithdrawalReport")
        protected WithdrawalReport withdrawalReport;
        @XmlElement(name = "ArrivalReport")
        protected ArrivalReport arrivalReport;

        /**
         * Gets the value of the inputReport property.
         * 
         * @return
         *     possible object is
         *     {@link InputReport }
         *     
         */
        public InputReport getInputReport() {
            return inputReport;
        }

        /**
         * Sets the value of the inputReport property.
         * 
         * @param value
         *     allowed object is
         *     {@link InputReport }
         *     
         */
        public void setInputReport(InputReport value) {
            this.inputReport = value;
        }

        /**
         * Gets the value of the withdrawalReport property.
         * 
         * @return
         *     possible object is
         *     {@link WithdrawalReport }
         *     
         */
        public WithdrawalReport getWithdrawalReport() {
            return withdrawalReport;
        }

        /**
         * Sets the value of the withdrawalReport property.
         * 
         * @param value
         *     allowed object is
         *     {@link WithdrawalReport }
         *     
         */
        public void setWithdrawalReport(WithdrawalReport value) {
            this.withdrawalReport = value;
        }

        /**
         * Gets the value of the arrivalReport property.
         * 
         * @return
         *     possible object is
         *     {@link ArrivalReport }
         *     
         */
        public ArrivalReport getArrivalReport() {
            return arrivalReport;
        }

        /**
         * Sets the value of the arrivalReport property.
         * 
         * @param value
         *     allowed object is
         *     {@link ArrivalReport }
         *     
         */
        public void setArrivalReport(ArrivalReport value) {
            this.arrivalReport = value;
        }

    }

}
