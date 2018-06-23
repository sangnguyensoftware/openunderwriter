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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PolicyStatusTest {

    Policy policy;

    @Mock
    AssessmentSheet policyLevelAssessmentSheet;
    @Mock
    AssessmentSheet sectionLevelAssessmentSheet;
    @Mock
    Section section;
    
    @Before
    public void setupPolicy() {
        MockitoAnnotations.initMocks(this);

        doReturn(sectionLevelAssessmentSheet).when(section).getAssessmentSheet();

        policy=new Policy();
        policy.setAssessmentSheet(policyLevelAssessmentSheet);
        policy.addSection(section);
    }
    
    @Test
    public void testGettingStatusByName() {
        PolicyStatus ps=PolicyStatus.valueOf("APPLICATION");
        assertNotNull(ps);
        assertEquals(PolicyStatus.APPLICATION, ps);

        try {
            ps=PolicyStatus.valueOf("AStatusThatDoesntExist");
            fail("found a nonexistant status type!");
        }
        catch(IllegalArgumentException e) {
            // ignore this - its what we want to see.
        }
    }

    @Test
    public void testIsMarkedAsReferred() {
        policy.setAssessmentSheet(null);
        assertThat(policy.isMarkedForRefer(), is(false));
        
        policy.setAssessmentSheet(policyLevelAssessmentSheet);
        doReturn(false).when(policyLevelAssessmentSheet).isMarkedForRefer();
        assertThat(policy.isMarkedForRefer(), is(false));

        doReturn(true).when(policyLevelAssessmentSheet).isMarkedForRefer();
        assertThat(policy.isMarkedForRefer(), is(true));
        
        doReturn(false).when(policyLevelAssessmentSheet).isMarkedForRefer();
        doReturn(false).when(sectionLevelAssessmentSheet).isMarkedForRefer(); 
        assertThat(policy.isMarkedForRefer(), is(false));

        doReturn(true).when(policyLevelAssessmentSheet).isMarkedForRefer();
        doReturn(false).when(sectionLevelAssessmentSheet).isMarkedForRefer(); 
        assertThat(policy.isMarkedForRefer(), is(true));
        
        doReturn(false).when(policyLevelAssessmentSheet).isMarkedForRefer();
        doReturn(true).when(sectionLevelAssessmentSheet).isMarkedForRefer(); 
        assertThat(policy.isMarkedForRefer(), is(true));
    }

    @Test
    public void testIsMarkedAsDeclined() {
        policy.setAssessmentSheet(null);
        assertThat(policy.isMarkedForDecline(), is(false));
        
        policy.setAssessmentSheet(policyLevelAssessmentSheet);
        doReturn(false).when(policyLevelAssessmentSheet).isMarkedForDecline();
        assertThat(policy.isMarkedForDecline(), is(false));

        doReturn(true).when(policyLevelAssessmentSheet).isMarkedForDecline();
        assertThat(policy.isMarkedForDecline(), is(true));
        
        doReturn(false).when(policyLevelAssessmentSheet).isMarkedForDecline();
        doReturn(false).when(sectionLevelAssessmentSheet).isMarkedForDecline(); 
        assertThat(policy.isMarkedForDecline(), is(false));

        doReturn(true).when(policyLevelAssessmentSheet).isMarkedForDecline();
        doReturn(false).when(sectionLevelAssessmentSheet).isMarkedForDecline(); 
        assertThat(policy.isMarkedForDecline(), is(true));
        
        doReturn(false).when(policyLevelAssessmentSheet).isMarkedForDecline();
        doReturn(true).when(sectionLevelAssessmentSheet).isMarkedForDecline(); 
        assertThat(policy.isMarkedForDecline(), is(true));
    }
}
