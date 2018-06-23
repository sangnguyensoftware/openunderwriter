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
import com.ail.util.Rate;

/**
 * This type of assessment line automatically discounts a quotation if a line's value falls outside
 * defined limits. The line defines a minimum and maximum value and the name of the line that the
 * limit applies to. If the line's value is found to be outside of these limits a discount line
 * is added to record the fact.</p>
 * By default, this line executes during the AFTER_RATING stage (see {@link AssessmentStage}.)
 * @since Insurance 2.4, OpenQuote 1.3
 */
@TypeDefinition
public class DiscountValueOutOfBounds extends ControlLine {
    private static final long serialVersionUID = -4519518497757725779L;
    private CurrencyAmount minimum;
    private CurrencyAmount maximum;
    private CurrencyAmount amount;
    private Rate rate;
    private String contributesTo;
    private String dependsOn;

    public DiscountValueOutOfBounds() {
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param currencyAmount minimum value
     * @param currencyAmount maximum value
     * @param contributesTo the id of the line which the discount should apply to if it is created
     * @param amount Amount to discount by if 'relatesTo' value falls out of bounds
     */
    public DiscountValueOutOfBounds(String id, String reason, String relatesTo, CurrencyAmount minimum, CurrencyAmount maximum, String contributesTo, CurrencyAmount amount) {
        super(id, reason, new Reference(AssessmentLine.class, relatesTo), AssessmentStage.AFTER_RATING);
        this.minimum=minimum;
        this.maximum=maximum;
        this.contributesTo=contributesTo;
        this.amount=amount;
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param currencyAmount minimum value
     * @param currencyAmount maximum value
     * @param contributesTo the id of the line which the discount should apply to if it is created
     * @param dependsOn The id of the line to be used in calculation of the discount (that rate will be applied to)
     * @param rate Rate to discount by if 'relatesTo' value falls out of bounds
     */
    public DiscountValueOutOfBounds(String id, String reason, String relatesTo, CurrencyAmount minimum, CurrencyAmount maximum, String dependsOn, String contributesTo, Rate rate) {
        super(id, reason, new Reference(AssessmentLine.class, relatesTo), AssessmentStage.AFTER_RATING);
        this.minimum=minimum;
        this.maximum=maximum;
        this.contributesTo=contributesTo;
        this.dependsOn=dependsOn;
        this.rate=rate;
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
                        addDiscount(line, "Value of line:"+line.getId()+" exceeded limit of "+getMaximum()+" defined by control line: "+getId());
                        setFired(true);
                    }
                    else if (line.getAmount().lessThan(getMinimum())) {
                        addDiscount(line, "Value of line:"+line.getId()+" fell below the minimum of "+getMinimum()+" defined by control line: "+getId());
                        setFired(true);
                    }
                }
                else {
                    if (line.getAmount().greaterThan(getMinimum()) && line.getAmount().lessThan(getMaximum())) {
                        addDiscount(line, "Value of line:"+line.getId()+" fell within the discount range defined by control line: "+getId());
                        setFired(true);
                    }
                }
            }
        }
    }

    private void addDiscount(CalculationLine line, String description) {
        if (rate!=null) {
            line.getAssessmentSheet().addDiscount(description, contributesTo, dependsOn, rate).setPriority(Integer.MIN_VALUE);
        }
        else {
            line.getAssessmentSheet().addDiscount(description, contributesTo, amount).setPriority(Integer.MIN_VALUE);
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

    public CurrencyAmount getAmount() {
        return amount;
    }

    public void setAmount(CurrencyAmount amount) {
        this.amount = amount;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public String getContributesTo() {
        return contributesTo;
    }

    public void setContributesTo(String contributesTo) {
        this.contributesTo = contributesTo;
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }
}
