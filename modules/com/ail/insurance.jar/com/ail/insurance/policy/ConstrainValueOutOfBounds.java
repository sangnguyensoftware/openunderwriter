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
 * This type of assessment line limits the value that another line may hold. The line defines a
 * minimum and maximum value and the name of the line that the limit applies to. That line's value
 * will never be allowed to exceed these limits. If the value of that line becomes greater than the
 * maximum allowed, the value will be set to that maximum. If the value becomes lower than the
 * minimum it will be set to the minimum.<p/>
 * If a line's value is modified an AssessmentNote will be added to the sheet to record the fact.<p/>
 * By default, this line executes during the RATING stage (see {@link AssessmentStage}.)
 * @since Insurance 2.4, OpenQuote 1.3
 */
@TypeDefinition
public class ConstrainValueOutOfBounds extends ControlLine {
    private static final long serialVersionUID = -4519518497757725779L;
    private CurrencyAmount minimum;
    private CurrencyAmount maximum;

    public ConstrainValueOutOfBounds() {
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker.
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param minimum The minimum value that the line <i>line</i> will be allowed to hold.
     * @param maximum The maximum value that the line <i>line</i> will be allowed to hold.
     */
    public ConstrainValueOutOfBounds(String id, String reason, String relatesTo, CurrencyAmount minimum, CurrencyAmount maximum) {
        this(id, reason, relatesTo, AssessmentStage.RATING, minimum, maximum);
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param stage The stage during rating that this line should be applied.
     * @param minimum The minimum value that the line <i>line</i> will be allowed to hold.
     * @param maximum The maximum value that the line <i>line</i> will be allowed to hold.
     */
    public ConstrainValueOutOfBounds(String id, String reason, String relatesTo, AssessmentStage stage, CurrencyAmount minimum, CurrencyAmount maximum) {
        super(id, reason, new Reference(AssessmentLine.class, relatesTo), stage);
        setMinimum(minimum);
        setMaximum(maximum);
    }

    public void setMinimum(CurrencyAmount minimum) {
        this.minimum = minimum;
    }

    public CurrencyAmount getMinimum() {
        return minimum;
    }

    public void setMaximum(CurrencyAmount maximum) {
        this.maximum = maximum;
    }

    public CurrencyAmount getMaximum() {
        return maximum;
    }

    @Override
    public void execute(AssessmentSheet sheet, CalculationLine line) {
        if (line.getId()!=null && getRelatesTo()!=null && line.getId().equals(getRelatesTo().getId())) {
            if (sheet.getAssessmentStage().equals(getAssessmentStage())) {
                if (line.getAmount().greaterThan(getMaximum())) {
                    line.getAssessmentSheet().addAssessmentNote("Maximum value for line:'"+line.getId()+"' exceeded limits defined by the '"+getId()+"' control line. Value of "+line.getAmount()+" adjusted to "+getMaximum()+".");
                    line.setAmount(getMaximum());
                }
                else if (line.getAmount().lessThan(getMinimum())) {
                    line.getAssessmentSheet().addAssessmentNote("Minimum value for line:"+line.getId()+" exceeded limits defined by the '"+getId()+"' control line. Value of "+line.getAmount()+" adjusted to "+getMinimum()+".");
                    line.setAmount(getMinimum());
                }
            }
        }
    }
}
