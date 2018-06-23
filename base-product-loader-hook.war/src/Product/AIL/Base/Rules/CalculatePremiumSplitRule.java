
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

import java.util.HashMap;
import java.util.Map;

import com.ail.insurance.payment.CalculatePremiumSplitService.CalculatePremiumSplitArgument;
import com.ail.party.Party;
import com.ail.util.Rate;

/**
 * Where the premium for a policy should be split across multiple parties, this
 * product specific service determines which party gets which proportion of the
 * premium. Typically this is invoked as party of a regular (batch) payment
 * processing job as part of the Journal generation logic.
 * 
 * The expectation is that products will override this Base implementation with
 * more complex logic which is specific to their Journal pattern needs. This
 * implementation simply apportions all of the premium to the broker associated
 * with the policy.
 */
public class CalculatePremiumSplitRule {
    public static void invoke(CalculatePremiumSplitArgument args) {
        Map<Party, Rate> split = new HashMap<Party, Rate>();

        split.put(args.getPolicyArg().getBroker(), new Rate("100%"));

        args.setSplitRet(split);
    }
}
