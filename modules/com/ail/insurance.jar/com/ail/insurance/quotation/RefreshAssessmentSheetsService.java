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

package com.ail.insurance.quotation;

import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.AssessmentLine;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.AssessmentStage;
import com.ail.insurance.policy.CalculationLine;
import com.ail.insurance.policy.Policy;

@ServiceImplementation
public class RefreshAssessmentSheetsService extends Service<RefreshAssessmentSheetsService.RefreshAssessmentSheetsArgument> {
    private static final long serialVersionUID = 3642886757932052003L;

    @ServiceArgument
    public interface RefreshAssessmentSheetsArgument extends Argument {
        /**
         * Getter for the policyArgRet property. The policy to refresh assessment sheets on.
         * @return Value of policyArgRet, or null if it is unset
         */
        Policy getPolicyArgRet();

        /**
         * Setter for the policyArgRet property. * @see #getPolicyArgRet
         * @param policyArgRet new value for property.
         */
        void setPolicyArgRet(Policy policyArgRet);

        /**
         * Getter for the originArg property. Used to tie assessment sheet entries to the service (owner) which originated them.
         * @return Value of originArg, or null if it is unset
         */
        String getOriginArg();

        /**
         * Setter for the originArg property. * @see #getOriginArg
         * @param originArg new value for property.
         */
        void setOriginArg(String originArg);
        
        /**
         * For aggregator policies (getPolicyArgRet().isAggregator()==true), the
         * product that the assessment sheets should be refreshed for.
         * <p>This argument is ignored for non-aggregator policies.</p>
         * @since 3.0 
         * @return product type id
         */
        String getProductTypeIdArg();
        
        /**
         * @see #getProductTypeIdArg()
         * @param productTypeIdArg ID of the product to assess risk for.
         */
        void setProductTypeIdArg(String productTypeIdArg);
    }

    @ServiceCommand(defaultServiceClass=RefreshAssessmentSheetsService.class)
    public interface RefreshAssessmentSheetsCommand extends Command, RefreshAssessmentSheetsArgument {
    }

    private String determineAssessmentSheetName() {
        return args.getPolicyArgRet().isAggregator() ? args.getProductTypeIdArg() : DEFAULT_SHEET_NAME;
    }

    /**
     * Process the contents of an AssessmentSheet. Run through the contents of the sheet checking each line
     * in order of priority. If a line hasn't already been calculated (i.e. it doesn't appear in the <i>processed</i>
     * collection) then it's <i>calculate()</i> method is called. If that call returns <i>true</i> the line is assumed
     * to have been processed and will be added to the <i>processed<i> list and marked with an integer indicating the 
     * order it was processed in relation to other lines.
     * @param sheet The sheet to process
     * @param sheets Collection of all sheets being processed
     * @param processed Collection of the AssessmentLines that have been processed so far.
     * @param calculationCountOffset Index to start numbering processed lines from. 
     * @return true If any line is processed, false otherwise
     */
    private int processSheet(AssessmentSheet sheet, RefreshAssessmentSheetCache sheets, Collection<AssessmentLine> processed, int calculationCountOffset) {
        CalculationLine cl=null;
        ArrayList<CalculationLine> sortedLines=null;

        // seed the "processed order" counter, as lines calculate() they will increment this counter and use
        // its value at the time to define their processed order.
        sheet.setProcessedOrderCounter(calculationCountOffset+1);
        
        // get the CalculationLines in priority order
        sortedLines=new ArrayList<CalculationLine>(sheet.getLinesOfType(CalculationLine.class).values());
        Collections.sort(sortedLines);
        
        // make sure that each line knows about the assessment sheet it is in
        for(CalculationLine line: sortedLines) {
            line.setAssessmentSheet(sheet);
        }
        
        // iterate through the lines
        for(Iterator<CalculationLine> it=sortedLines.iterator() ; it.hasNext() ; ) {
            cl=it.next();

            // if we've processed this line already, skip to the next.
            if (processed.contains(cl)) {
                continue;
            }

            // calculate the line, if we get a true back then the line has been done
            if (cl.calculate(sheets, sheet)==true) {
                processed.add(cl);
            }
        }

        return sheet.getProcessedOrderCounter();
    }

    private int iterateOverSheets(RefreshAssessmentSheetCache sheets, Collection<AssessmentLine> processed, int calculationOrder) {
        boolean again=false;

        do {
            again=false;

            for(Iterator<AssessmentSheet> it=sheets.getSheets().iterator() ; it.hasNext() ; ) {
                
                int count=processSheet(it.next(), sheets, processed, calculationOrder);
                
                if (count!=0) {
                    calculationOrder+=count;
                    again=true;
                }
            }
        } while(again==true);

        return calculationOrder;
    }
    
    private void processAssessmentSheets() {
        int calculationOrder=0;
        RefreshAssessmentSheetCache sheets=new RefreshAssessmentSheetCache(args.getPolicyArgRet(), determineAssessmentSheetName());
        Collection<AssessmentLine> processed=new ArrayList<AssessmentLine>();

        // execute all the control lines appropriate to the before phase
        sheets.executeControlLinesForAssessmentStage(AssessmentStage.BEFORE_RATING);

        // the following loop performs all the rating calcs, so set the phase accordingly
        sheets.setAssessmentStage(AssessmentStage.RATING);
        
        calculationOrder=iterateOverSheets(sheets, processed, calculationOrder);

        // execute all the control lines appropriate to the after phase
        sheets.executeControlLinesForAssessmentStage(AssessmentStage.AFTER_RATING);

        calculationOrder=iterateOverSheets(sheets, processed, calculationOrder);
    }

    @Override
    public void invoke() throws PreconditionException {
        // check preconditions
        if (args.getPolicyArgRet() == null) {
            throw new PreconditionException("args.getPolicyArgRet() == null");
        }

        if (args.getOriginArg() == null || args.getOriginArg().length() == 0) {
            throw new PreconditionException("args.getOriginArg() == null || args.getOriginArg().length() == 0");
        }
        
        if (args.getPolicyArgRet().isAggregator() && (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0)) {
            throw new PreconditionException("args.getPolicyArgRet().isAggregator() && (args.getProductTypeIdArg() == null || args.getProductTypeIdArg().length() == 0)");
        }

        String sheetName = determineAssessmentSheetName();
        
        Policy policy = args.getPolicyArgRet();

        // remove only lines that this origin added in the past
        policy.removeAssessmentLinesByOrigin(sheetName, args.getOriginArg());

        // Lines may have been fired in a previous run, reset them so they can fire again.
        policy.resetAssessmentControlLines(sheetName);
        
        // lock the sheets to this origin
        policy.lockAllAssessmentSheets(sheetName, args.getOriginArg());

        // perform the refresh
        processAssessmentSheets();

        // unlock the assessment sheets
        policy.unlockAllAssessmentSheets(sheetName);
    }
}


