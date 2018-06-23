package com.ail.insurance.quotation;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.DisaggregatePolicyAndReferService.DisaggregatePolicyAndReferArgument;
import com.ail.insurance.quotation.DisaggregatePolicyService.DisaggregatePolicyCommand;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DisaggregatePolicyAndReferServiceTest {

    private static final long AGGREGATOR_POLICY_ID = 1234L;

    private static final String DUMMY_PRODUCT_TYPE_ID = "DUMMY_PRODUCT_TYPE_ID";

    DisaggregatePolicyAndReferService sut;

    @Mock
    private DisaggregatePolicyAndReferArgument args;
    @Mock
    private Core core;
    @Mock
    private Policy aggregatorPolicy;
    @Mock
    private AssessmentSheet policyAssessmentSheet;
    @Mock
    private Policy newPolicy;
    @Mock
    DisaggregatePolicyCommand disaggregatePolicyCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new DisaggregatePolicyAndReferService();
        sut.setArgs(args);
        sut.setCore(core);

        doReturn(aggregatorPolicy).when(args).getAggregatedPolicyArg();
        doReturn(AGGREGATOR_POLICY_ID).when(aggregatorPolicy).getSystemId();
        doReturn(true).when(aggregatorPolicy).isAggregator();
        doReturn(DUMMY_PRODUCT_TYPE_ID).when(args).getTargetPolicyTypeIdArg();
        doReturn(policyAssessmentSheet).when(aggregatorPolicy).getAssessmentSheetFor(eq(DUMMY_PRODUCT_TYPE_ID));

        doReturn(disaggregatePolicyCommand).when(core).newCommand(eq(DisaggregatePolicyCommand.class));

        doReturn(newPolicy).when(disaggregatePolicyCommand).getPolicyRet();
        doReturn(newPolicy).when(args).getPolicyRet();
    }

    @Test(expected = PreconditionException.class)
    public void aggregatedPolicyArgCannotBeNull() throws BaseException {
        doReturn(null).when(args).getAggregatedPolicyArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void aggregatedPolicyArgMustBeAnAggregator() throws BaseException {
        doReturn(false).when(aggregatorPolicy).isAggregator();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void targetPolicyTypeIdArgCannotBeNull() throws BaseException {
        doReturn(null).when(args).getTargetPolicyTypeIdArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void targetPolicyTypeIdArgCannotBeEmpty() throws BaseException {
        doReturn("").when(args).getTargetPolicyTypeIdArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void aggregatedPolicyArgMustDefinePolicyTypeId() throws BaseException {
        doReturn(null).when(aggregatorPolicy).getAssessmentSheetFor(eq(DUMMY_PRODUCT_TYPE_ID));
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void aggregatedPolicyMustNotHaveSelectedProduct() throws BaseException {

        Map<String, AssessmentSheet> sheets = Maps.newHashMap();
        sheets.put("sheet", policyAssessmentSheet);

        doReturn(sheets).when(aggregatorPolicy).getAssessmentSheetList();
        doReturn(Lists.newArrayList(new Attribute("selectedSystemPolicyId", "100", "string"))).when(policyAssessmentSheet).getAttribute();

        sut.invoke();
    }
    @Test
    public void happyPath() throws BaseException {

        sut.invoke();

        verify(args, times(8)).getAggregatedPolicyArg();
        verify(args, times(5)).getTargetPolicyTypeIdArg();
        verify(disaggregatePolicyCommand, times(1)).invoke();
        verify(newPolicy, times(1)).setStatus(eq(PolicyStatus.REFERRED));
        verify(core, times(1)).create(eq(newPolicy));
        verify(core, times(1)).update(eq(aggregatorPolicy));
    }


}
