/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PolicyLinkTest {

    Policy policy;

    @Before
    public void setupPolicy() {
        policy=new Policy();
    }

    @Test
    public void testThatLinksCanBeAdded() {
        policy.getPolicyLink().add(new PolicyLink(PolicyLinkType.MTA_FROM, 1234L));
        policy.getPolicyLink().add(new PolicyLink(PolicyLinkType.DISAGGREGATED_FROM, 4321L));

//        // adding a duplicate - should be ignored.
//        policy.getPolicyLink().add(new PolicyLink(PolicyLinkType.MTA_FROM, 1234L));

        assertThat(policy.getPolicyLink().size(), is(2));
    }
}