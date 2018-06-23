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

package com.ail.insurance.policy;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;

/**
 * A marker assessment line indicates referral and decline conditions. When risk assessment rules detect a condition
 * that calls for a referral or decline, a line of this type is added to the assessment sheet. The presents of these
 * lines does not necessarily stop premium calculation, but other services may refuse to process quotations when 
 * any of these markers are present. For example, {@link com.ail.insurance.quotation.CalculatePremiumService
 * CalculatePremium} will not move a policy to QUOTATION status if any of these markers are present.
 * @see MarkerResolution
 */
@TypeDefinition
public class Marker extends AssessmentLine {
    private static final long serialVersionUID = -5791683969972762091L;

    /** This property describes the type of marker represented (e.g. refer, decline). */
    private MarkerType type = null;

    /**
     * Default constructor
     */
    public Marker() {
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for why this marker was applied.
     * @param relatesTo Optional reference to the part of the policy that caused the marker.
     * @param type This marker's type.
     */
    public Marker(String id, String reason, Reference relatesTo, MarkerType type) {
        super(id, reason, relatesTo);
        this.type=type;
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for why this marker was applied.
     * @param relatesTo Optional reference to the part of the policy that caused the marker.
     * @param type This marker's type.
     * @param priority The priority of this line wrt other lines in the same sheet (lines with higher priority values are processed first)
     */
    public Marker(String id, String reason, Reference relatesTo, MarkerType type, int priority) {
        super(id, reason, relatesTo, priority);
        this.type=type;
    }

    /**
     * Set the type property. This property describes the type of marker represented (e.g. refer, decline).
     * @param type New value for type property.
     */
    public void setType(MarkerType type) {
        this.type = type;
    }

    /**
     * Set the type property from a String. This property describes the type of marker represented (e.g. refer, decline).
     * The argument passed must represent a valid value for calling MarkerType.forName(arg).
     * @see MarkerType#forName
     * @see #setType
     * @throws IndexOutOfBoundsException If type is not a valid string representation of MarkerType.
     * @param type New value for type property.
     */
    public void setTypeAsString(String type) {
        this.type = MarkerType.valueOf(type);
    }

    /**
     * Get the value of the type property. This property describes the type of marker represented (e.g. refer, decline).
     * @return Value of type
     */
    public MarkerType getType() {
        return this.type;
    }

    /**
     * Get the current value of the type property as a String.
     * @see #getType
     * @return Value of type property as a String, or null if type is null.
     */
    public String getTypeAsString() {
        if (type != null) {
            return type.name();
        }
        return null;
    }
}
