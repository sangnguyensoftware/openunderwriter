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
 * Record includes the service user number of the originator.
 */
@PositionalRecord
public class HeaderLabel1 {

    /**
     * Must be HDR.
     */
    private String labelIdentifier = "HDR";

    /**
     * Must be 1.
     */
    private String labelNumber = "1";

    /**
     * Must be A.
     */
    private String fileIdentifier1 = "A";

    /**
     * sfs • Must be a valid service user number.
     *     • Must be the same as VOL1 ({#link VolumeHeaderLabel1}) characters 42-47 (in field 7).
     *     • If file is live, must have a processing status of live.
     * mfs • Must be all numeric
     *     • Must be a valid service user number.
     *     • Must be valid relationship set up for the Bacs service between the originator and submitting bureau.
     *     • If file is live, must have a processing status of live.
     */
    private String fileIdentifier2;

    /**
     * Must be S.
     */
    private String fileIdentifier3 = "S";

    /**
     * Can be any valid characters.
     */
    private String fileIdentifier4;

    /**
     * sfs Must be a blank space or 1.
     * mfs Must be file serial number of next file. Should start at 1 and increment by 1 for each file for that service user. At 9, numbering should restart at 1.
     */
    private String fileIdentifier5;

    /**
     * sfs Must be blank spaces or the same as VOL1 ({#link VolumeHeaderLabel1}) characters 42-47 (in field 7).
     * mfs Must be the same as VOL1 ({#link VolumeHeaderLabel1}) characters 42-47 (in field 7).
     */
    private String fileIdentifier6;

    /**
     * Must be the same as the submission serial number in VOL1 ({#link VolumeHeaderLabel1}) field 3.
     */
    private String setIdentification;

    /**
     * Must be 0001.
     */
    private String fileSectionNumber = "0001";

    /**
     * sfs Must be 0001.
     * mfs Must be numeric and in sequence (and consecutive) but does not need to start at 0001. Arrival check only performed for ETS and STS submissions.
     */
    private String fileSequenceNumber = "";

    /**
     * Must be all numeric or blank.
     */
    private String generationNumber = "";

    /**
     * Must be all numeric or blank.
     */
    private String generationVersionNumber = "";

    /**
     * • Must be in the format bYYDDD
     * • Must be earlier than or the same as the processing day in UHL1 field 3.
     */
    private String creationDate;

    /**
     * • Must be in the format bYYDDD
     * • Must be greater than processing day in UHL1 in field 3
     * ...and if it is a multiprocessing day file...
     * Must also be later than the processing day of all payment instructions in this payment file.
     */
    private String expirationDate;

    /**
     * Must be a blank space or a zero.
     */
    private String accessibilityIndicator = "";

    /**
     * Must be zero filled.
     */
    private String blockCount = "";

    /**
     * Can be any valid characters.
     */
    private String systemCode = "";

    /**
     * Must be blank.
     */
    private String reserved = "";

    public HeaderLabel1() {
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

    @PositionalField(initialPosition = 5, finalPosition = 5)
    public String getFileIdentifier1() {
        return fileIdentifier1;
    }

    public void setFileIdentifier1(String fileIdentifier1) {
        this.fileIdentifier1 = fileIdentifier1;
    }

    @PositionalField(initialPosition = 6, finalPosition = 11)
    public String getFileIdentifier2() {
        return fileIdentifier2;
    }

    public void setFileIdentifier2(String fileIdentifier2) {
        this.fileIdentifier2 = fileIdentifier2;
    }

    @PositionalField(initialPosition = 12, finalPosition = 12)
    public String getFileIdentifier3() {
        return fileIdentifier3;
    }

    public void setFileIdentifier3(String fileIdentifier3) {
        this.fileIdentifier3 = fileIdentifier3;
    }

    @PositionalField(initialPosition = 13, finalPosition = 14)
    public String getFileIdentifier4() {
        return fileIdentifier4;
    }

    public void setFileIdentifier4(String fileIdentifier4) {
        this.fileIdentifier4 = fileIdentifier4;
    }

    @PositionalField(initialPosition = 15, finalPosition = 15)
    public String getFileIdentifier5() {
        return fileIdentifier5;
    }

    public void setFileIdentifier5(String fileIdentifier5) {
        this.fileIdentifier5 = fileIdentifier5;
    }

    @PositionalField(initialPosition = 16, finalPosition = 21, paddingCharacter = '0', paddingAlign = PaddingAlign.RIGHT)
    public String getFileIdentifier6() {
        return fileIdentifier6;
    }

    public void setFileIdentifier6(String fileIdentifier6) {
        this.fileIdentifier6 = fileIdentifier6;
    }

    @PositionalField(initialPosition = 22, finalPosition = 27)
    public String getSetIdentification() {
        return setIdentification;
    }

    public void setSetIdentification(String setIdentification) {
        this.setIdentification = setIdentification;
    }

    @PositionalField(initialPosition = 28, finalPosition = 31)
    public String getFileSectionNumber() {
        return fileSectionNumber;
    }

    public void setFileSectionNumber(String fileSectionNumber) {
        this.fileSectionNumber = fileSectionNumber;
    }

    @PositionalField(initialPosition = 32, finalPosition = 35)
    public String getFileSequenceNumber() {
        return fileSequenceNumber;
    }

    public void setFileSequenceNumber(String fileSequenceNumber) {
        this.fileSequenceNumber = fileSequenceNumber;
    }

    @PositionalField(initialPosition = 36, finalPosition = 39, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(String generationNumber) {
        this.generationNumber = generationNumber;
    }

    @PositionalField(initialPosition = 40, finalPosition = 41, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getGenerationVersionNumber() {
        return generationVersionNumber;
    }

    public void setGenerationVersionNumber(String generationVersionNumber) {
        this.generationVersionNumber = generationVersionNumber;
    }

    @PositionalField(initialPosition = 42, finalPosition = 47, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @PositionalField(initialPosition = 48, finalPosition = 53, paddingCharacter = ' ', paddingAlign = PaddingAlign.RIGHT)
    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @PositionalField(initialPosition = 54, finalPosition = 54, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getAccessibilityIndicator() {
        return accessibilityIndicator;
    }

    public void setAccessibilityIndicator(String accessibilityIndicator) {
        this.accessibilityIndicator = accessibilityIndicator;
    }

    @PositionalField(initialPosition = 55, finalPosition = 60, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(String blockCount) {
        this.blockCount = blockCount;
    }

    @PositionalField(initialPosition = 61, finalPosition = 73, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @PositionalField(initialPosition = 74, finalPosition = 80, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }
}