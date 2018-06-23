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

import com.ail.core.Reference;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.RefreshAssessmentSheetCache;

/**
 * A behaviour is a type of assessment line, specifically one that has an effect on a calculated amount.
 * A behaviour may "load" or "discount" and may do so with relation to a "rate" or a fixed sum. The 
 * behaviour's type gives more detail about the behaviour, e.g. is it a loading, a discount, a tax, etc.
 * @stereotype type 
 */
public abstract class Behaviour extends CalculationLine {
    private static final long serialVersionUID = 4371815020594784515L;

    /** The type property describes type of behaviour being represented (e.g. Load, Discount). */
    private BehaviourType type = null;

    /**
     * Default constructor
     */
    public Behaviour() {
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this line contributes to.
     * @param type The type of behaviour being represented.
     * @param amount The amount (value) represented by this line.
     */
    public Behaviour(String id, String reason, Reference relatesTo, String contributesTo, BehaviourType type, CurrencyAmount amount) {
        super(id, reason, relatesTo, contributesTo, amount);
        this.type=type;
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this line contributes to.
     * @param type The type of behaviour being represented.
     * @param amount The amount (value) represented by this line.
     * @param priority The priority of this line wrt other lines in the same sheet (lines with higher priority values are processed first)
     */
    public Behaviour(String id, String reason, Reference relatesTo, String contributesTo, BehaviourType type, CurrencyAmount amount, int priority) {
        super(id, reason, relatesTo, contributesTo, amount, priority);
        this.type=type;
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this line contributes to.
     * @param type The type of behaviour being represented.
     */
    public Behaviour(String id, String reason, Reference relatesTo, String contributesTo, BehaviourType type) {
        this(id, reason, relatesTo, contributesTo, type, null);
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this line contributes to.
     * @param type The type of behaviour being represented.
     * @param priority The priority of this line wrt other lines in the same sheet (lines with higher priority values are processed first)
     */
    public Behaviour(String id, String reason, Reference relatesTo, String contributesTo, BehaviourType type, int priority) {
        this(id, reason, relatesTo, contributesTo, type, null, priority);
    }

    /**
     * Set the type property. The type property describes type of behaviour being represented (e.g. Load, Discount).
     * @param type New value for type property.
     */
    public void setType(BehaviourType type) {
        this.type = type;
    }

    /**
     * Set the type property from a String. The type property describes type of behaviour being represented (e.g. Load, Discount).
     * The argument passed must represent a valid value for calling BehaviourType.forName(arg).
     * @see BehaviourType#forName
     * @see #setType
     * @throws IndexOutOfBoundsException If type is not a valid string representation of BehaviourType.
     * @param type New value for type property.
     */
    public void setTypeAsString(String type) {
        this.type = BehaviourType.valueOf(type);
    }

    /**
     * Get the value of the type property. The type property describes type of behaviour being
     * represented (e.g. Load, Discount).
     * @return Value of type
     */
    public BehaviourType getType() {
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
    
    protected void contribute(RefreshAssessmentSheetCache sheets, AssessmentSheet sheet) {
        // A line may not contribute to anything. For example, tax may have been included in the
        // base rates - but the tax line still needs to appear in the assessment sheet. In this case
        // the contributesTo will be null.
        if (getContributesTo()!=null) {
            // try to get the line that this one contributes to.
            CalculationLine conTo=(CalculationLine)sheets.findAssessmentLine(getContributesTo(), sheet);

            // if it doesn't exist, create it.
            if (conTo==null) {
                conTo=new FixedSum(getContributesTo(), "calculated", null, null, new CurrencyAmount(0, getAmount().getCurrency()));
                sheets.addAssessmentLine(conTo, sheet);
                conTo.setAssessmentSheet(sheet);
            }

            // If we're loading add our calculated amount to contributed to,
            // if we are a discount subtract it.
            if (getType().equals(BehaviourType.LOAD)) {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
                conTo.setProcessedOrder(sheet.getNextProcessOrderIndex());
            }
            else if (getType().equals(BehaviourType.TAX)) {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
                conTo.setProcessedOrder(sheet.getNextProcessOrderIndex());
            }
            else if (getType().equals(BehaviourType.COMMISSION)) {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
                conTo.setProcessedOrder(sheet.getNextProcessOrderIndex());
            }
            else if (getType().equals(BehaviourType.BROKERAGE)) {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
                conTo.setProcessedOrder(sheet.getNextProcessOrderIndex());
            }
            else if (getType().equals(BehaviourType.MANAGEMENT_CHARGE)) {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
                conTo.setProcessedOrder(sheet.getNextProcessOrderIndex());
            }
            else if (getType().equals(BehaviourType.DISCOUNT)) {
                conTo.setAmount(conTo.getAmount().subtract(getAmount()));
                conTo.setProcessedOrder(sheet.getNextProcessOrderIndex());
            }
        }
    }
}
