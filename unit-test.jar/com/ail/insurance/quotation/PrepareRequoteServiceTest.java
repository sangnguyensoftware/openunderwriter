package com.ail.insurance.quotation;

import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.XMLString;
import com.ail.insurance.policy.AssessmentLine;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.ail.insurance.quotation.PrepareRequoteService.PrepareRequoteArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class PrepareRequoteServiceTest {

    private static final String PRODUCT_NAME = "product name";

    PrepareRequoteService sut;

    @Mock
    private PrepareRequoteArgument args;
    @Mock
    private Policy policy;
    @Mock
    private Policy requote;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Core core;
    @Mock
    private AssessmentSheet policyAassessmentSheet;
    @Mock
    private AssessmentSheet sectionAassessmentSheet;
    @Mock
    private Map<String,AssessmentLine> policyAssessmentSheetLinesMap;
    @Mock
    private Map<String,AssessmentLine> sectionAssessmentSheetLinesMap;
    @Mock
    private Section section;
    @Mock
    private XMLString xmlString;
    @Mock
    private Policy donorPolicy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockStatic(CoreContext.class);

        sut = new PrepareRequoteService();

        sut.setArgs(args);

        doReturn(policy).when(args).getPolicyArg();
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);
        when(CoreContext.getProductName()).thenReturn(PRODUCT_NAME);
        doReturn(PRODUCT_NAME).when(policy).getProductTypeId();
        doReturn(requote).when(coreProxy).newProductType(eq(PRODUCT_NAME), eq("Policy"), eq(Policy.class));
        doReturn(core).when(coreProxy).getCore();
        doReturn(policyAassessmentSheet).when(requote).getAssessmentSheet();
        doReturn(policyAssessmentSheetLinesMap).when(policyAassessmentSheet).getAssessmentLine();
        doReturn(Arrays.asList(section)).when(requote).getSection();
        doReturn(sectionAassessmentSheet).when(section).getAssessmentSheet();
        doReturn(sectionAssessmentSheetLinesMap).when(sectionAassessmentSheet).getAssessmentLine();
        doReturn(xmlString).when(coreProxy).toXML(eq(policy));
        doReturn(donorPolicy).when(coreProxy).fromXML(eq(Policy.class), eq(xmlString));
    }

    @Test(expected=PreconditionException.class)
    public void checkThatNullPolicyArgIsTrapped() throws Exception {
        doReturn(null).when(args).getPolicyArg();

        sut.invoke();
    }

    @Test
    public void checkThatDataIsMergedFromPolicyIntoRequote() throws Exception {
        sut.invoke();
        verify(requote).mergeWithDataFrom(eq(donorPolicy), eq(core));
    }

    @Test
    public void checkThatRequoteStatusIsSetToApplication() throws Exception {
        sut.invoke();
        verify(requote).setStatus(eq(APPLICATION));
    }

    @Test
    public void checkThatRequoteUserSavedFlagIsCleared() throws Exception {
        sut.invoke();
        verify(requote).setUserSaved(eq(false));
    }

    @Test
    public void checkThatRequoteIsMarkedAsNotPersisted() throws Exception {
        sut.invoke();
        verify(requote).markAsNotPersisted();
    }

    @Test
    public void checkThatRequoteAssessmentSheetLinesIsCleared() throws Exception {
        sut.invoke();
        verify(policyAssessmentSheetLinesMap).clear();
        verify(sectionAssessmentSheetLinesMap).clear();
    }

}
