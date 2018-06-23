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

/**
 * This type of assessment line automatically refers a quotation if a line's value falls outside
 * defined limits. The line defines a minimum and maximum value and the name of the line that the
 * limit applies to. If the line's value is found to be outside of these limits a referral line
 * is added to record the fact.</p>
 * By default, this line executes during the AFTER_RATING stage (see {@link AssessmentStage}.)
 * @since Insurance 2.4, OpenQuote 1.3
 */
@TypeDefinition
public class ReferValueOutOfBounds extends ControlLine {
    private static final long serialVersionUID = -4519518497757725779L;
    private CurrencyAmount minimum;
    private CurrencyAmount maximum;

    public ReferValueOutOfBounds() {
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param currencyAmount2
     * @param currencyAmount
     */
    public ReferValueOutOfBounds(String id, String reason, String relatesTo, CurrencyAmount minimum, CurrencyAmount maximum) {
        super(id, reason, new Reference(AssessmentLine.class, relatesTo), AssessmentStage.AFTER_RATING);
        this.minimum=minimum;
        this.maximum=maximum;
    }

    @Override
    public void execute(AssessmentSheet sheet, CalculationLine line) {
        if (hasFired()) {
            return;
        }
        if (line.getId()!=null && getRelatesTo()!=null && line.getId().equals(getRelatesTo().getId())) {
            if (sheet.getAssessmentStage().equals(getAssessmentStage())) {
                if (ControlLineType.OUTSIDE.equals(getControlLineType())) {
                    if (line.getAmount().greaterThan(getMaximum())) {
                        line.getAssessmentSheet().addReferral("Value of line:"+line.getId()+" exceeded limit of "+getMaximum()+" defined by control line: "+getId());
                        setFired(true);
                    }
                    else if (line.getAmount().lessThan(getMinimum())) {
                        line.getAssessmentSheet().addReferral("Value of line:"+line.getId()+" fell below the minimum of "+getMinimum()+" defined by control line: "+getId());
                        setFired(true);
                    }
                }
                else {
                    if (line.getAmount().greaterThan(getMinimum()) && line.getAmount().lessThan(getMaximum())) {
                        line.getAssessmentSheet().addReferral("Value for line:"+line.getId()+" falls within the refer range defined by the '"+getId()+"' control line.");
                        setFired(true);
                    }
                }
            }
        }
    }

    public CurrencyAmount getMinimum() {
        return minimum;
    }

    public void setMinimum(CurrencyAmount minimum) {
        this.minimum = minimum;
    }

    public CurrencyAmount getMaximum() {
        return maximum;
    }

    public void setMaximum(CurrencyAmount maximum) {
        this.maximum = maximum;
    }
}
