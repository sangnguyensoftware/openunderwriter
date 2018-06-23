/* Copyright Applied Industrial Logic Limited 2013. All rights Reserved */
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
package com.ail.pageflow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.insurance.policy.Policy;

public class QuotationSummaryTest {

    private QuotationSummary sut;
    @Mock
    private Policy policy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(policy.getProductTypeId()).thenReturn("DUMMY");
        sut=new QuotationSummary();
    }
    
    @Test
    public void testThatLoginSectionIsCreatedWithAnId() {
        sut.setId("DUMMY_ID");
        PageElement loginSection=sut.loginSection(policy);
        assertEquals("DUMMY_ID.loginSection", loginSection.getId());
    }
}
