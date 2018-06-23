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

package com.ail.core.document;

import static com.ail.core.document.Multiplicity.MANY;
import static com.ail.core.document.Multiplicity.ONE;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

/**
 * "System" document types. This list defines the types of document which OpenUnderwriter considers to
 * be standard - which means that they are referred to by system code. A product author may define
 * other document types as necessary (see notes in <Product>/Base/Model/DocumentTypes.xml). However,
 * the list defined here is immutable and must have corresponding entries in the DocumentTypes.xml file.
 */
public enum DocumentType implements TypeEnum {
    ATTACHMENT("i18n_Attachment", MANY),
    CERTIFICATE("i18n_Certificate", ONE),
    INVOICE("i18n_Invoice", ONE),
    OTHER("i18n_Other", MANY),
    POLICY("i18n_Policy", ONE),
    QUOTATION("i18n_Quotation", ONE),
    SCHEDULE("i18n_Schedule", ONE),
    UNKNOWN("i18n_Unknown", MANY),
    WORDING("i18n_Wording", ONE),
    ENDORSEMENT("i18n_Endorsement", ONE),
    CANCELLATION("i18n_Cancellation", ONE),
    COMBINED("i18n_Combined", ONE);

    private final String longName;
    private final Multiplicity multiplicity;

    DocumentType(String longName, Multiplicity multiplicity) {
        this.longName=longName;
        this.multiplicity=multiplicity;
    }

    @Override
    public String longName() {
        return longName;
    }
    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLongName() {
        return longName;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static DocumentType forName(String name) {
        return (DocumentType)Functions.enumForName(name, values());
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    public static boolean isValid(String name) {
        for(DocumentType type: values()) {
            if (type.longName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
