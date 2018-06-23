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
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.RefreshAssessmentSheetCache;

/**
 * A type of assessment line representing a fixed amount (as opposed to one that is calculated by applying a rate to
 * another line). If the {@link #getContributesTo() contributesTo} property is defined, the value of this line will
 * simply be added to the value of the indicated line.
 * @see SumBehaviour
 */
@TypeDefinition
public class FixedSum extends CalculationLine {
    private static final long serialVersionUID = 399954132621176151L;

    /**
     * Default constructor
     */
    public FixedSum() {
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     */
    public FixedSum(String id, String reason, Reference relatesTo, String contributesTo, CurrencyAmount amount) {
        super(id, reason, relatesTo, contributesTo, amount);
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @param priority The priority of this line WRT other lines in the same sheet (lines with higher priority values are processed first)
     */
    public FixedSum(String id, String reason, Reference relatesTo, String contributesTo, CurrencyAmount amount, int priority) {
      super(id, reason, relatesTo, contributesTo, amount, priority);
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param amount The amount to be contributed.
     * @param priority The priority of this line WRT other lines in the same sheet (lines with higher priority values are processed first)
     */
    public FixedSum(String id, String reason, CurrencyAmount amount) {
        this(id, reason, null, null, amount);
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @param priority The priority of this line WRT other lines in the same sheet (lines with higher priority values are processed first)
     */
    public FixedSum(String id, String reason, String contributesTo, CurrencyAmount amount) {
        this(id, reason, null, contributesTo, amount);
    }

    /**
     * A FixedSum can only either contribute to another FixedSum, or do nothing.
     * @param sheets All the sheets in the current process.
     * @param sheet The sheet that this line is part of.
     * @return true - always
     */
    public boolean calculate(RefreshAssessmentSheetCache sheets, AssessmentSheet sheet) {
        // mark the line with an order index so that the order lines were processed in is known
        setProcessedOrder(sheet.getNextProcessOrderIndex());

        // Do we contribute to anything?
        if (getContributesTo()!=null) {
            // yes, then does the line we contribute to exist?
            CalculationLine conTo=(CalculationLine)sheets.findAssessmentLine(getContributesTo(), sheet);

            // if not, create it.
            if (conTo==null) {
                conTo=new FixedSum(getContributesTo(), "calculated", null, null, new CurrencyAmount(0, getAmount().getCurrency()));
                sheets.addAssessmentLine(conTo, sheet);
                conTo.setAssessmentSheet(sheet);
            }

            // add to the 'contributeTo' line.
            conTo.setAmount(conTo.getAmount().add(getAmount()));
        }

        return true;
    }
}
