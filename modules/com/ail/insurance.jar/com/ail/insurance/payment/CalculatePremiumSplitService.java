/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

package com.ail.insurance.payment;

import java.util.Map;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.insurance.policy.Policy;
import com.ail.party.Party;
import com.ail.util.Rate;

/**
 * Service interface for services which are used to calculate how the premium
 * for a policy should be split between parties.
 */
@ServiceInterface
public interface CalculatePremiumSplitService {

    @ServiceArgument
    public interface CalculatePremiumSplitArgument extends Argument {
        void setPolicyArg(Policy policyArg);

        Policy getPolicyArg();

        void setSplitRet(Map<Party,Rate> splitArg);

        Map<Party,Rate> getSplitRet();
    }

    @ServiceCommand
    public interface CalculatePremiumSplitCommand extends Command, CalculatePremiumSplitArgument {
    }
}
