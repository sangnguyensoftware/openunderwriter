package com.ail.insurance.quotation;

import static com.ail.insurance.policy.PolicyLinkType.DISAGGREGATED_FROM;
import static com.ail.insurance.policy.PolicyLinkType.DISAGGREGATED_TO;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.XMLString;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyLink;
import com.ail.insurance.policy.Section;
import com.ail.insurance.quotation.AddQuoteNumberService.AddQuoteNumberCommand;
import com.ail.insurance.quotation.DisaggregatePolicyService.DisaggregatePolicyArgument;

public class DisaggregatePolicyServiceTest {

    private static final long POLICY_ID = 123L;

    private static final long AGGREGATOR_POLICY_ID = 1234L;

    private static final String DUMMY_PRODUCT_TYPE_ID = "DUMMY_PRODUCT_TYPE_ID";

    DisaggregatePolicyService sut;

    @Mock
    private DisaggregatePolicyArgument args;
    @Mock
    private Core core;
    @Mock
    private Policy aggregatorPolicy;
    @Mock
    private AssessmentSheet policyAssessmentSheet;
    @Mock
    private AssessmentSheet sectionAssessmentSheet;
    @Mock
    private Policy policy;
    @Mock
    private Section section;
    @Mock
    private Map<String,AssessmentSheet> policyAssessmentSheetList;
    @Mock
    private Map<String,AssessmentSheet> sectionAssessmentSheetList;
    @Mock
    private List<PolicyLink> aggregatorPolicyLink;
    @Mock
    private List<PolicyLink> policyLink;
    @Mock
    private AddQuoteNumberCommand addQuoteNumberCommand;
    @Mock
    private XMLString xmlString;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new DisaggregatePolicyService();
        sut.setArgs(args);
        sut.setCore(core);

        doReturn(aggregatorPolicy).when(args).getAggregatedPolicyArg();
        doReturn(AGGREGATOR_POLICY_ID).when(aggregatorPolicy).getSystemId();
        doReturn(true).when(aggregatorPolicy).isAggregator();
        doReturn(DUMMY_PRODUCT_TYPE_ID).when(args).getTargetPolicyTypeIdArg();
        doReturn(policyAssessmentSheet).when(aggregatorPolicy).getAssessmentSheetFor(eq(DUMMY_PRODUCT_TYPE_ID));
        doReturn(policy).when(core).newProductType(eq(DUMMY_PRODUCT_TYPE_ID), eq("Policy"));
        doReturn(policy).when(args).getPolicyRet();
        doReturn(asList(section)).when(policy).getSection();
        doReturn(policyAssessmentSheetList).when(policy).getAssessmentSheetList();
        doReturn(sectionAssessmentSheetList).when(section).getAssessmentSheetList();
        doReturn(policyAssessmentSheet).when(policy).getAssessmentSheetFor(eq(DUMMY_PRODUCT_TYPE_ID));
        doReturn(sectionAssessmentSheet).when(section).getAssessmentSheetFor(eq(DUMMY_PRODUCT_TYPE_ID));
        doReturn(policyLink).when(policy).getPolicyLink();
        doReturn(aggregatorPolicyLink).when(aggregatorPolicy).getPolicyLink();
        doReturn(POLICY_ID).when(policy).getSystemId();

        doReturn(addQuoteNumberCommand).when(core).newCommand(eq(AddQuoteNumberCommand.class));

        doReturn(xmlString).when(core).toXML(any(Policy.class));
        doReturn(policy).when(core).fromXML(eq(Policy.class), eq(xmlString));
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

    @Test
    public void redundantAssessmentSheetsShouldBeRemoved() throws BaseException {
        sut.invoke();

        verify(policyAssessmentSheetList).clear();
        verify(policyAssessmentSheetList).put(eq(AssessmentSheet.DEFAULT_SHEET_NAME), eq(policyAssessmentSheet));

        verify(sectionAssessmentSheetList).clear();
        verify(sectionAssessmentSheetList).put(eq(AssessmentSheet.DEFAULT_SHEET_NAME), eq(sectionAssessmentSheet));
    }

    @Test
    public void targetPolicyProductTypeIDMustBeUpdated() throws BaseException {
        sut.invoke();
        verify(policy).setProductTypeId(eq(DUMMY_PRODUCT_TYPE_ID));
    }

    @Test
    public void targetPolicyShouldBeMarkedAsNotPersisted() throws BaseException {
        sut.invoke();
        verify(policy).markAsNotPersisted();
    }

    @Test
    public void disaggregatedPoicyMustBeLinkedToAggregatorPolicy() throws BaseException {
        sut.invoke();

        verify(policy).getPolicyLink();
        verify(policyLink).add(eq(new PolicyLink(DISAGGREGATED_FROM, AGGREGATOR_POLICY_ID)));
        verify(aggregatorPolicyLink).add(eq(new PolicyLink(DISAGGREGATED_TO, POLICY_ID)));

    }

    @Test
    public void newQuotationNumberMustBeAppliedToNewQuote() throws BaseException {
        sut.invoke();

        verify(policy).setQuotationNumber(null);
        verify(addQuoteNumberCommand).invoke();
        verify(addQuoteNumberCommand).setPolicyArgRet(eq(policy));
    }

    @Test
    public void happyPath() throws BaseException {
        sut.invoke();

        verify(args, times(7)).getAggregatedPolicyArg();
        verify(aggregatorPolicy).isAggregator();
        verify(args, times(6)).getTargetPolicyTypeIdArg();
        verify(core).newProductType(eq(DUMMY_PRODUCT_TYPE_ID), eq("Policy"));
        verify(aggregatorPolicy).getAssessmentSheetFor(eq(DUMMY_PRODUCT_TYPE_ID));
        verify(policy).mergeWithDataFrom(eq(policy), eq(core));
    }

}
