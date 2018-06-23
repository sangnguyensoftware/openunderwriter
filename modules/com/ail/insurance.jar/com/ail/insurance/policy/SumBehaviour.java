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
 * An assessment line which contributes a fixed sum to another line. This type of line applies a fixed amount to the
 * value of another line. How the amount is applied depends upon the behaviour type of this line. For example, if this
 * line is a loading (i.e. {@link #getType() type}=={@link BehaviourType#LOAD}) then the amount will be added to the value
 * of the line indicated by {@link #getContributesTo() contributesTo}. If this line is a discount (i.e. {@link #getType()
 * type}=={@link BehaviourType#DISCOUNT}) the amount will be subtracted from the indicated line.
 * @see FixedSum
 */
@TypeDefinition
public class SumBehaviour extends Behaviour {
    private static final long serialVersionUID = 264541471718824407L;

    /**
     * Default constructor
     */
    public SumBehaviour() {
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
    public SumBehaviour(String id, String reason, Reference relatesTo, String contributesTo, BehaviourType type, CurrencyAmount amount) {
        super(id, reason, relatesTo, contributesTo, type, amount);
    }

    /**
     * Constructor
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this line contributes to.
     * @param type The type of behaviour being represented.
     * @param amount The amount (value) represented by this line.
     * @param priority The priority of this line WRT other lines in the same sheet (lines with higher priority values are processed first)
     */
    public SumBehaviour(String id, String reason, Reference relatesTo, String contributesTo, BehaviourType type, CurrencyAmount amount, int priority) {
        super(id, reason, relatesTo, contributesTo, type, amount, priority);
    }

    /**
     * Perform the calculation. For a FixedSum line this simply means take the amount property and
     * apply it to the assessment line indicated by the contributeTo property. If the behaviour
     * type is Load then amount will be added to the target line, if it is Discount it will be
     * subtracted from it.
     * @param sheets The sheets being evaluated
     * @param sheet The sheet that this line is a member of.
     * @return always true. FixedSum lines cannot fail to calculate.
     */
    public boolean calculate(RefreshAssessmentSheetCache sheets, AssessmentSheet sheet) {
        // mark the line with an order index so that the order lines were processed in is known
        setProcessedOrder(sheet.getNextProcessOrderIndex());

        // Pass on the result of this calculation to any 'contributeTo' line
        contribute(sheets, sheet);

        return true;
    }
}
