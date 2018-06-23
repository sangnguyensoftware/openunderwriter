/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
package com.ail.insurance.quotation;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.claim.Claim;

@ServiceInterface
public interface GenerateClaimNumberService {

    @ServiceArgument
    public interface GenerateClaimNumberArgument extends Argument {
        /**
         * Getter for the claimArg property. The claim to generate a number for
         * @return Value of claimArg, or null if it is unset
         */
        Claim getClaimArg();

        /**
         * Setter for the claimArg property. * @see #getClaimArg
         * @param claimArg new value for property.
         */
        void setClaimArg(Claim claimArg);

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
         * Getter for the claimNumberRet property. The generated claim number.
         * @return Value of claimNumberRet, or null if it is unset
         */
        String getClaimNumberRet();

        /**
         * Setter for the claimNumberRet property. * @see #getClaimNumberRet
         * @param claimNumberRet new value for property.
         */
        void setClaimNumberRet(String claimNumberRet);
    }

    @ServiceCommand
    public interface GenerateClaimNumberCommand extends Command, GenerateClaimNumberArgument {
    }
}
