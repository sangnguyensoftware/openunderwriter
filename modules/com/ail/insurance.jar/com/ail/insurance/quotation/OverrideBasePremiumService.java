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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.CalculationLine;

@ServiceImplementation
public class OverrideBasePremiumService extends Service<OverrideBasePremiumService.OverrideBasePremiumArgument> {
    
    private static final long serialVersionUID = 7959054258477631251L;

    private static final String BASE_PREMIUM_ID = "base premium";

    @ServiceArgument
    public interface OverrideBasePremiumArgument extends Argument {
        
        /**
         * Fetch assessment for sheet which the base premium should be overridden.
         * @return value of assessment
         */
        AssessmentSheet getAssessmentSheetArg();

        /**
         * Set the assessment sheet for which the base premium should be overridden.
         * @param sheet New value for assessment argument.
         */
        void setAssessmentSheetArg(AssessmentSheet sheet);
        
        /**
         * Set the new base premium amount.
         * @param amount
         */
        void setBasePremiumAmountArg(CurrencyAmount amount);
        
        /**
         * Return base premium amount.
         * @return
         */
        CurrencyAmount getBasePremiumAmountArg();
    }

    @ServiceCommand(defaultServiceClass=OverrideBasePremiumService.class)
    public interface OverrideBasePremiumCommand extends Command, OverrideBasePremiumArgument {
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        
        AssessmentSheet sheet = args.getAssessmentSheetArg();
        CurrencyAmount amount = args.getBasePremiumAmountArg();

        if (sheet == null) {
            throw new PreconditionException("sheet==null");
        }
        
        if (amount == null) {
            throw new PreconditionException("amount==null");
        }

        sheet.setLockingActor("User");
        
        CalculationLine line = (CalculationLine)sheet.findLineById(BASE_PREMIUM_ID);
        
        line.setAmount(amount);
        
        sheet.clearLockingActor();
    }
    
}


