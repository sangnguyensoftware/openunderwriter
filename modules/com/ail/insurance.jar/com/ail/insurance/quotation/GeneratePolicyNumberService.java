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

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.Policy;

@ServiceInterface
public interface GeneratePolicyNumberService {

    @ServiceArgument
    public interface GeneratePolicyNumberArgument extends Argument {
        /**
         * Getter for the policyArg property. The policy to generate a number for
         * @return Value of policyArg, or null if it is unset
         */
        Policy getPolicyArg();

        /**
         * Setter for the policyArg property. * @see #getPolicyArg
         * @param policyArg new value for property.
         */
        void setPolicyArg(Policy policyArg);

        /**
         * Getter for the uniqueNumberArg property. A unique number that may be used by the number generation service. This number
         * is guaranteed to be unique for each invocation of the service.
         * @return Value of uniqueNumberArg, or null if it is unset
         */
        Long getUniqueNumberArg();

        /**
         * Setter for the uniqueNumberArg property. * @see #getUniqueNumberArg
         * @param uniqueNumberArg new value for property.
         */
        void setUniqueNumberArg(Long uniqueNumberArg);

        /**
         * Getter for the policyNumberRet property. The generated policy number.
         * @return Value of policyNumberRet, or null if it is unset
         */
        String getPolicyNumberRet();

        /**
         * Setter for the policyNumberRet property. * @see #getPolicyNumberRet
         * @param policyNumberRet new value for property.
         */
        void setPolicyNumberRet(String policyNumberRet);
    }

    @ServiceCommand
    public interface GeneratePolicyNumberCommand extends Command, GeneratePolicyNumberArgument {
    }
}
