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

import static com.ail.insurance.policy.AssessmentStage.AFTER_RATING;

import java.math.BigDecimal;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;
import com.ail.financial.CurrencyAmount;

/**
 * This type of assessment line applies a rounding to the value of another line. The line defines a
 * scale and rounding mode and the ID of the line that the control applies to. See {@link java.math.BigDecimal BigDecimal} for
 * a definition of how the values of scale and rounding mode affect how rounding is applied.<p/>
 * If a line's value is modified an AssessmentNote will be added to the sheet to record the fact.<p/>
 * By default, this line executes during the AFTER_RATING stage (see {@link AssessmentStage}).
 */
@TypeDefinition
public class RoundValue extends ControlLine {
    private static final long serialVersionUID = -4519518499592925779L;
    private int roundingMode = BigDecimal.ROUND_HALF_UP;
    private int scale = 0;
    private boolean executing = false;

    public RoundValue() {
        setAssessmentStage(AFTER_RATING);
    }

    /**
     * Constructor. Rounding method defaults to {@link java.math.BigDecimal#ROUND_HALF_UP HALF_ROUND_UP}.
     * @param id The Id for this line.
     * @param reason Free text reason for the marker.
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param scale Positive value indicates digits to the right of the decimal place, negative values represent digits to the left.
     */
    public RoundValue(String id, String reason, String relatesTo, int scale) {
        this(id, reason, relatesTo, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker.
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param scale Positive value indicates digits to the right of the decimal place, negative values represent digits to the left.
     * @param roundingMode How to apply rounding. Values from  {@link java.math.BigDecimal BigDecimal}, defaults to  {@link java.math.BigDecimal#ROUND_HALF_UP ROUND_HALF_UP}
     */
    public RoundValue(String id, String reason, String relatesTo, int scale, int roundingMode) {
        this(id, reason, relatesTo, AFTER_RATING, scale, roundingMode);
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param stage The stage during rating that this line should be applied.
     * @param scale Positive value indicates digits to the right of the decimal place, negative values represent digits to the left.
     * @param roundingMode How to apply rounding. Values from  {@link java.math.BigDecimal BigDecimal}, defaults to  {@link java.math.BigDecimal#ROUND_HALF_UP ROUND_HALF_UP}
     */
    public RoundValue(String id, String reason, String relatesTo, AssessmentStage stage, int scale, int roundingMode) {
        super(id, reason, new Reference(AssessmentLine.class, relatesTo), stage);
        this.roundingMode = roundingMode;
        this.scale = scale;
    }

    @Override
    public void execute(AssessmentSheet sheet, CalculationLine line) {
        if (!executing && line.getId() != null && getRelatesTo() != null && line.getId().equals(getRelatesTo().getId())) {
            if (sheet.getAssessmentStage().equals(getAssessmentStage())) {
                executing = true;
                BigDecimal amount = line.getAmount().getAmount().setScale(scale, roundingMode);
                line.setAmount(new CurrencyAmount(amount, line.getAmount().getCurrency()));
                line.getAssessmentSheet().addAssessmentNote("Value for line:'"+line.getId()+"' ("+line.getAmount()+") rounded by the '"+getId()+"' control line.");
                setFired(true);
                executing = false;
            }
        }
    }

    public int getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(int roundingMode) {
        this.roundingMode = roundingMode;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
