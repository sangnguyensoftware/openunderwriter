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
package com.ail.eft.bacs;

import com.github.ffpojo.metadata.positional.PaddingAlign;
import com.github.ffpojo.metadata.positional.annotation.PositionalField;
import com.github.ffpojo.metadata.positional.annotation.PositionalRecord;

/**
 *  Record includes the file processing date, the currency code, the work code and the audit print identifier.
 */
@PositionalRecord
public class UserHeaderLabel1 {

    /**
     * Must be UHL.
     */
    private String labelIdentifier = "UHL";

    /**
     * Must be 1.
     */
    private String labelNumber = "1";

    /**
     * If file contains DDIs, must be current day.
     * spd • Must be a valid processing day
     *     • Must be in the format bYYDDD
     *     • Must be equal to or later than the current processing day
     *     • Must not be more than 31 days later than the current processing day
     *     • For a standing order recall file, must be current day
     * mpd • Must be a valid processing day
     *     • Must be in the format bYYDDD
     *     • Must be equal to or later than the current processing day
     *     • Must not be more than 31 days later than the current processing day.
     */
    private String processingDate;

    /**
     * Must be 999999.
     */
    private String identifyingNumberOfReceivingParty1 = "999999";

    /**
     * Must be blank spaces.
     */
    private String identifyingNumberOfReceivingParty2 = "";

    /**
     * For sterling must be 00 (zeros) or blanks.
     */
    private String currencyCode = "";

    /**
     * Must be 000000 (zeros).
     */
    private String countryCode = "000000";

    /**
     * Must be:
     * • 1bDAILYbb for an spd payment file; or
     * • 4bMULTIbb for an mpd payment file; or
     * • 5bRECALLS for a standing order recall file, see sec 4.3.4, pg 39
     */
    private String workCode = "4 MULTI  ";

    /**
     * Must be all numeric
     */
    private String fileNumber;

    /**
     * Must be blank spaces.
     */
    private String reserved1 = "";

    /**
     * To get audit prints for input reports, must be AUDnnnn where nnnn is the frequency of the audit prints, otherwise it should be blank.
     */
    private String auditPrintIdentifier;

    /**
     * Can be any valid characters.
     */
    private String reserved2 = "";

    public UserHeaderLabel1() {
    }

    @PositionalField(initialPosition = 1, finalPosition = 3)
    public String getLabelIdentifier() {
        return labelIdentifier;
    }

    public void setLabelIdentifier(String labelIdentifier) {
        this.labelIdentifier = labelIdentifier;
    }

    @PositionalField(initialPosition = 4, finalPosition = 4)
    public String getLabelNumber() {
        return labelNumber;
    }

    public void setLabelNumber(String labelNumber) {
        this.labelNumber = labelNumber;
    }

    @PositionalField(initialPosition = 5, finalPosition = 10, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

    @PositionalField(initialPosition = 11, finalPosition = 16, paddingCharacter = '9', paddingAlign = PaddingAlign.LEFT)
    public String getIdentifyingNumberOfReceivingParty1() {
        return identifyingNumberOfReceivingParty1;
    }

    public void setIdentifyingNumberOfReceivingParty1(String identifyingNumberOfReceivingParty1) {
        this.identifyingNumberOfReceivingParty1 = identifyingNumberOfReceivingParty1;
    }

    @PositionalField(initialPosition = 17, finalPosition = 20, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getIdentifyingNumberOfReceivingParty2() {
        return identifyingNumberOfReceivingParty2;
    }

    public void setIdentifyingNumberOfReceivingParty2(String identifyingNumberOfReceivingParty2) {
        this.identifyingNumberOfReceivingParty2 = identifyingNumberOfReceivingParty2;
    }

    @PositionalField(initialPosition = 21, finalPosition = 22, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @PositionalField(initialPosition = 23, finalPosition = 28, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @PositionalField(initialPosition = 29, finalPosition = 37)
    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    @PositionalField(initialPosition = 38, finalPosition = 40)
    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    @PositionalField(initialPosition = 41, finalPosition = 47, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    @PositionalField(initialPosition = 48, finalPosition = 54)
    public String getAuditPrintIdentifier() {
        return auditPrintIdentifier;
    }

    public void setAuditPrintIdentifier(String auditPrintIdentifier) {
        this.auditPrintIdentifier = auditPrintIdentifier;
    }

    @PositionalField(initialPosition = 55, finalPosition = 80, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

}