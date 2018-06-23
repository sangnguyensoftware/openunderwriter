/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import java.util.StringTokenizer;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.RefreshAssessmentSheetCache;

/**
 * The totalizer calculation line sums the value of any number of other named lines up to make a
 * total and optionally contributes that total to another line. The totalizer's value will only
 * calculate when all of the lines which it depends on themselves have values. If one or more of
 * them does not exist, it will not perform its calculation.
 */
@TypeDefinition
public class Totalizer extends CalculationLine {
    private static final long serialVersionUID = 1048697316607294623L;
    private String dependsOn=null;

    public Totalizer() {
    }

    /**
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param dependsOn A comma separated list of the IDs of the lines that this one depends on (will sum).
     */
    public Totalizer(String id, String reason, Reference relatesTo, String contributesTo, String dependsOn) {
        super(id, reason, relatesTo, contributesTo, null);
        this.dependsOn=dependsOn;
    }

    /**
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param dependsOn A comma separated list of the IDs of the lines that this one depends on (will sum).
     * @param priority The priority of this line wrt other lines in the same sheet (lines with higher priority values are processed first)
     */
    public Totalizer(String id, String reason, Reference relatesTo, String contributesTo, String dependsOn, int priority) {
      super(id, reason, relatesTo, contributesTo, null, priority);
      this.dependsOn=dependsOn;
    }

    /**
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param dependsOn A comma separated list of the IDs of the lines that this one depends on (will sum).
     */
    public Totalizer(String id, String reason, String dependsOn) {
        super(id, reason, null, null, null);
        this.dependsOn=dependsOn;
    }

    /**
     * Get the Ids of the lines that this totalizer depends on. The Ids
     * are returned as a comma separated list.
     * @return List of the IDs of the lines that this line depends on.
     */
    public String getDependsOn() {
        return dependsOn;
    }

    /**
     * Set the Ids of the lines that this totalizer depends on. The Ids
     * are passed as a comma separated list.
     * @param dependsOn List of the IDs of the lines that this line depends on.
     */
    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public boolean calculate(RefreshAssessmentSheetCache sheets, AssessmentSheet sheet) {
        CurrencyAmount total=null;
        CalculationLine cl=null;

        // loop through all the named dependsOn lines, and add their values to the total
        for(StringTokenizer st=new StringTokenizer(dependsOn, ",") ; st.hasMoreTokens() ; ) {
            cl=(CalculationLine)sheets.findAssessmentLine(st.nextToken().trim(), sheet);

            if (cl==null || cl.getAmount()==null) {
                return false;
            }

            if (total==null) {
                total=new CurrencyAmount(cl.getAmount());
            }
            else {
                total=total.add(cl.getAmount());
            }
        }

        // no baseCurrency means no lines found - could be an empty dependsOn?
        if (total==null) {
            return false;
        }

        // set the amount base on the values just parsed
        setAmount(total);

        // mark the line with an order index so that the order lines were processed in is known
        setProcessedOrder(sheet.getNextProcessOrderIndex());

        // if this line contributes to another...
        if (getContributesTo()!=null) {
            // try to get the line that this on contributes to.
            CalculationLine conTo=(CalculationLine)sheets.findAssessmentLine(getContributesTo(), sheet);

            // if it doesn't exist yet, create it.
            if (conTo==null) {
                conTo=new FixedSum(getContributesTo(), "calculated", null, null, new CurrencyAmount(total.getAmount(), total.getCurrency()));
                sheets.addAssessmentLine(conTo, sheet);
                conTo.setAssessmentSheet(sheet);
            }
            else {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
            }
        }

        return true;
    }
}
