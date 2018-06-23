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
 * Record repeats the information in HDR2.
 */
@PositionalRecord
public class EndOfFileLabel2 {

    /**
     * Must be EOF.
     */
    private String labelIdentifier = "EOF";

    /**
     * Must be 2.
     */
    private String labelNumber = "2";

    /**
     * Must be F.
     */
    private String recordFormat = "F";

    /**
     * Must be five numeric characters.
     */
    private String blockLength = "";

    /**
     * spd Must be 00100.
     * mpd Must be 00106.
     */
    private String recordLength;

    /**
     * Can be any valid characters.
     */
    private String reserved1 = "";

    /**
     * Must be zero filled.
     */
    private String bufferOffset = "";

    /**
     *  Must be blank spaces.
     */
    private String reserved2 = "";

    public EndOfFileLabel2() {
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
    public String getRecordFormat() {
        return recordFormat;
    }

    public void setRecordFormat(String recordFormat) {
        this.recordFormat = recordFormat;
    }

    @PositionalField(initialPosition = 6, finalPosition = 10, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getBlockLength() {
        return blockLength;
    }

    public void setBlockLength(String blockLength) {
        this.blockLength = blockLength;
    }

    @PositionalField(initialPosition = 11, finalPosition = 15)
    public String getRecordLength() {
        return recordLength;
    }

    public void setRecordLength(String recordLength) {
        this.recordLength = recordLength;
    }

    @PositionalField(initialPosition = 16, finalPosition = 50, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    @PositionalField(initialPosition = 51, finalPosition = 52, paddingCharacter = '0', paddingAlign = PaddingAlign.LEFT)
    public String getBufferOffset() {
        return bufferOffset;
    }

    public void setBufferOffset(String bufferOffset) {
        this.bufferOffset = bufferOffset;
    }

    @PositionalField(initialPosition = 53, finalPosition = 80, paddingCharacter = ' ', paddingAlign = PaddingAlign.LEFT)
    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }
}