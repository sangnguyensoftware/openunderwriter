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
 *  Record includes the service user/bureau number of the submitter.
 */
@PositionalRecord
public class VolumeHeaderLabel1 {

    /**
     * Must be VOL.
     */
    private String labelIdentifier = "VOL";

    /**
     * Must be 1.
     */
    private String labelNumber = "1";

    /**
     * Must be alpha and/or numeric.
     * Must not be all blank spaces or zeros.
     * Must be unique for the input day and the processing day for the submitting service user.
     * */
    private String submissionSerialNumber;

    /**
     * Must be a blank space or a zero.
     */
    private String accessibilityIndicator = "";

    /**
     * Must be blank spaces.
     */
    private String reserved1 = "";

    /**
     * Must be blank spaces.
     */
    private String reserved2 = "";

    /**
     * Must be blank spaces.
     */
    private String ownerIdentificationPrefix = "";

    /**
     * Must be a valid service user or bureau number.
     * For Bacstel-IP must be the same as that in the XML submission header.
     * For a live submission sent over:
     * • ETS – service user must have a live ETS status.
     * • STS – service user must have a live STS status.
     * No check is made on channel status for Bacstel-IP submissions.
     * For ETS and STS submissions, the service user number must be the same as, or linked in
     * reference data to, the transmission service user that signed the ETS or STS data.
     */
    private String ownerIdentification;

    /**
     *  Must be blank spaces.
     */
    private String ownerIdentificationSuffix = "";

    /**
     *  Must be blank spaces.
     */
    private String reserved3 = "";

    /**
     *  Must be 1.
     */
    private String labelStandardLevel = "1";

    public VolumeHeaderLabel1() {
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

    @PositionalField(initialPosition = 5, finalPosition = 10, paddingCharacter = '0', paddingAlign = PaddingAlign.RIGHT)
    public String getSubmissionSerialNumber() {
        return submissionSerialNumber;
    }

    public void setSubmissionSerialNumber(String submissionSerialNumber) {
        this.submissionSerialNumber = submissionSerialNumber;
    }

    @PositionalField(initialPosition = 11, finalPosition = 11, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getAccessibilityIndicator() {
        return accessibilityIndicator;
    }

    public void setAccessibilityIndicator(String accessibilityIndicator) {
        this.accessibilityIndicator = accessibilityIndicator;
    }

    @PositionalField(initialPosition = 12, finalPosition = 31, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    @PositionalField(initialPosition = 32, finalPosition = 37, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    @PositionalField(initialPosition = 38, finalPosition = 41, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getOwnerIdentificationPrefix() {
        return ownerIdentificationPrefix;
    }

    public void setOwnerIdentificationPrefix(String ownerIdentificationPrefix) {
        this.ownerIdentificationPrefix = ownerIdentificationPrefix;
    }

    @PositionalField(initialPosition = 42, finalPosition = 47)
    public String getOwnerIdentification() {
        return ownerIdentification;
    }

    public void setOwnerIdentification(String ownerIdentification) {
        this.ownerIdentification = ownerIdentification;
    }

    @PositionalField(initialPosition = 48, finalPosition = 51, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getOwnerIdentificationSuffix() {
        return ownerIdentificationSuffix;
    }

    public void setOwnerIdentificationSuffix(String ownerIdentificationSuffix) {
        this.ownerIdentificationSuffix = ownerIdentificationSuffix;
    }

    @PositionalField(initialPosition = 52, finalPosition = 79, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved3() {
        return reserved3;
    }

    public void setReserved3(String reserved3) {
        this.reserved3 = reserved3;
    }

    @PositionalField(initialPosition = 80, finalPosition = 80)
    public String getLabelStandardLevel() {
        return labelStandardLevel;
    }

    public void setLabelStandardLevel(String labelStandardLevel) {
        this.labelStandardLevel = labelStandardLevel;
    }
}