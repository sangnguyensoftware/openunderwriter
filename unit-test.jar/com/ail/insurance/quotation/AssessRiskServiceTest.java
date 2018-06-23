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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.AssessPolicyRiskService.AssessPolicyRiskCommand;
import com.ail.insurance.quotation.AssessRiskService.AssessRiskArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Functions.class)
public class AssessRiskServiceTest {
    private static final String DUMMY_NAMESPACE = "DUMMY_NAMESPACE";
    private static final String DUMMY_PRODUCT_TYPE_ID = "DUMMY_PRODUCT_TYPE_ID";

    private AssessRiskService sut;
    private Core mockCore;
    private AssessRiskArgument mockArgs;
    private Policy mockPolicy;
    private AssessPolicyRiskCommand mockAssessPolicyRiskCommand;
    private AssessmentSheet mockAssessmentSheet;

    @Before
    public void setupSut() {
        mockCore = mock(Core.class);
        mockArgs = mock(AssessRiskArgument.class);
        mockPolicy = mock(Policy.class);
        mockAssessPolicyRiskCommand = mock(AssessPolicyRiskCommand.class);
        mockAssessmentSheet = mock(AssessmentSheet.class);

        sut = spy(new AssessRiskService());
        sut.setCore(mockCore);
        sut.setArgs(mockArgs);

        when(sut.getCore()).thenReturn(mockCore);
        when(mockArgs.getPolicyArgRet()).thenReturn(mockPolicy);
        when(mockArgs.getCallersCore()).thenReturn(mockCore);
        when(mockArgs.getProductTypeIdArg()).thenReturn(DUMMY_PRODUCT_TYPE_ID);
        when(mockPolicy.getStatus()).thenReturn(PolicyStatus.APPLICATION);
        when(mockPolicy.isAggregator()).thenReturn(false);
        when(mockCore.newCommand(eq("AssessPolicyRisk"), eq(AssessPolicyRiskCommand.class)))
                .thenReturn(mockAssessPolicyRiskCommand);
        when(mockAssessPolicyRiskCommand.getAssessmentSheetArgRet()).thenReturn(mockAssessmentSheet);
        
        mockStatic(Functions.class);
        when(Functions.productNameToConfigurationNamespace(eq(DUMMY_PRODUCT_TYPE_ID))).thenReturn(DUMMY_NAMESPACE);
        
    }

    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void testNullPolicy() throws BaseException {
        when(mockArgs.getPolicyArgRet()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void checkThatPolicyProductIDisIgnoredForAggregatorPolicies() throws BaseException {
        when(mockPolicy.isAggregator()).thenReturn(true);
        sut.invoke();
        verify(mockPolicy, never()).getProductTypeId();
    }

    @Test
    public void checkThatPolicyProductIDisUsedForNonAggregatorPolicies() throws BaseException {
        sut.invoke();
        verify(mockPolicy).getProductTypeId();
    }

    @Test(expected=PreconditionException.class) 
    public void testBankProductTypeIdInArgsIsTrappedForAggregatorPolicies() throws BaseException {
        when(mockPolicy.isAggregator()).thenReturn(true);
        when(mockArgs.getProductTypeIdArg()).thenReturn("");
        sut.invoke();
    }
    
    @Test
    public void testBankProductTypeIdInArgsIsIgnoredForNonAggregatorPolicies() throws BaseException {
        when(mockArgs.getProductTypeIdArg()).thenReturn("");
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class) 
    public void testNullProductTypeIdInArgsIsTrappedForAggregatorPolicies() throws BaseException {
        when(mockPolicy.isAggregator()).thenReturn(true);
        when(mockArgs.getProductTypeIdArg()).thenReturn(null);
        sut.invoke();
    }
    
    @Test 
    public void testNullProductTypeIdInArgsIsIgnoredForNonAggregatorPolicies() throws BaseException {
        when(mockArgs.getProductTypeIdArg()).thenReturn(null);
        sut.invoke();
    }
    
    @Test
    public void testApplicationStatusCheck() throws BaseException {
        PolicyStatus[] statuses={PolicyStatus.DECLINED,
                                 PolicyStatus.ON_RISK,
                                 PolicyStatus.QUOTATION,
                                 PolicyStatus.REFERRED,
                                 PolicyStatus.SUBMITTED};
        
        for(PolicyStatus status: statuses) {
            when(mockPolicy.getStatus()).thenReturn(status);
            try {
                sut.invoke();
            }
            catch(PreconditionException e) {
                // this is a good thing
            }
        }

        when(mockPolicy.getStatus()).thenReturn(PolicyStatus.APPLICATION);
        sut.invoke();
    }
    
    @Test
    public void testLockingAndUnlocking() throws BaseException {
        sut.invoke();
        InOrder order=inOrder(mockAssessmentSheet);
        order.verify(mockAssessmentSheet).setLockingActor(eq("AssessRisk"));
        order.verify(mockAssessmentSheet).clearLockingActor();
    }
    
    @Test
    public void testPolicyLevelIsVisited() throws BaseException {
        sut.invoke();
        verify(mockAssessPolicyRiskCommand).setPolicyArg(eq(mockPolicy));
    }
    
    @Test
    public void verifyThatUnnamedAssessmentSheetMethodsAreNotUsed() throws BaseException {
        sut.invoke();
        verify(mockPolicy, never()).getAssessmentSheet();
        verify(mockPolicy, never()).setAssessmentSheet(any(AssessmentSheet.class));
    }
}
