/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

import java.math.BigDecimal;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.CalculationLine;
import com.ail.insurance.policy.FixedSum;
import com.ail.insurance.policy.RateBehaviour;

@ServiceImplementation
public class AddManualLoadingService extends Service<AddManualLoadingService.AddManualLoadingArgument> {
    
    private static final long serialVersionUID = 7959054258477631251L;


    @ServiceArgument
    public interface AddManualLoadingArgument extends Argument {
        
        /**
         * Fetch the assessment sheet for which the calculation line is to be added.
         * @return value of assessment
         */
        AssessmentSheet getAssessmentSheetArg();

        /**
         * Set the assessment for which the calculation line is to be added.
         * @param sheet New value for assessment argument.
         */
        void setAssessmentSheetArg(AssessmentSheet sheet);
        
        /**
         * Set the calculation line to add as a loading.
         * @param amount
         */
        void setCalculationLineArg(CalculationLine line);
        
        /**
         * Return the calculation line to add as a loading.
         * @return
         */
        CalculationLine getCalculationLineArg();
    }

    @ServiceCommand(defaultServiceClass=AddManualLoadingService.class)
    public interface AddManualLoadingCommand extends Command, AddManualLoadingArgument {
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        
        AssessmentSheet sheet = args.getAssessmentSheetArg();
        CalculationLine line = args.getCalculationLineArg();

        if (sheet == null) {
            throw new PreconditionException("sheet==null");
        }
        
        if (line == null) {
            throw new PreconditionException("line==null");
        }
        
        sheet.setLockingActor("User");
        sheet.removeLine(line);
            
        if (line instanceof RateBehaviour) {
            RateBehaviour rateBehaviour = (RateBehaviour)line;
            if (rateBehaviour.getRate() != null) {
                if (BigDecimal.ZERO.compareTo(rateBehaviour.getRate().getNominator()) != 0) {
                    sheet.addLine(line);
                }
            }
        } else if (line instanceof FixedSum) {
            if (BigDecimal.ZERO.compareTo(line.getAmount().getAmount()) != 0) {
                sheet.addLine(line);
            }
        }
        sheet.clearLockingActor();
    }
    
}


