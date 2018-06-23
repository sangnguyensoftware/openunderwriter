package com.ail.insurance.quotation;

import static com.ail.insurance.policy.PolicyStatus.APPLICATION;
import static com.ail.insurance.policy.PolicyStatus.CANCELLED_FROM_INCEPTION;
import static com.ail.insurance.policy.PolicyStatus.DECLINED;
import static com.ail.insurance.policy.PolicyStatus.DELETED;
import static com.ail.insurance.policy.PolicyStatus.NOT_TAKEN_UP;
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static com.ail.insurance.policy.PolicyStatus.REFERRED;
import static com.ail.insurance.policy.PolicyStatus.UNUSED;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyCommand;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.AddClaimNumberService.AddClaimNumberArgument;
import com.ail.insurance.quotation.GenerateClaimNumberService.GenerateClaimNumberCommand;

public class AddClaimNumberServiceTest {

    private static final long GENERATED_UNIQUE_ID = 1234L;
    private static final String GENERATED_CLAIM_NUMMBER = "CLAIM_NUMBER";

    private AddClaimNumberService sut;

    @Mock
    private AddClaimNumberArgument args;

    @Mock
    private Claim claim;

    @Mock
    private Policy policy;

    @Mock
    private Core core;

    @Mock
    private GenerateUniqueKeyCommand generateUniqueKeyCommand;

    @Mock
    private GenerateClaimNumberCommand generateClaimNumberCommand;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = spy(new AddClaimNumberService());

        sut.setArgs(args);

        doReturn(claim).when(args).getClaimArgRet();
        when(claim.getClaimNumber()).thenReturn(null, "DUMMY");
        doReturn(policy).when(claim).getPolicy();
        doReturn("DUMMY").when(policy).getProductTypeId();
        doReturn(ON_RISK).when(policy).getStatus();
        doReturn(core).when(sut).getCore();
        doReturn(generateUniqueKeyCommand).when(core).newCommand(eq(GenerateUniqueKeyCommand.class));
        doReturn(generateClaimNumberCommand).when(core).newCommand(eq("GenerateClaimNumber"), eq(GenerateClaimNumberCommand.class));
        doReturn(GENERATED_UNIQUE_ID).when(generateUniqueKeyCommand).getKeyRet();
        doReturn(GENERATED_CLAIM_NUMMBER).when(generateClaimNumberCommand).getClaimNumberRet();
    }

    @Test(expected = PreconditionException.class)
    public void testClaimArgRetCannotBeNullPrecondition() throws BaseException {
        doReturn(null).when(args).getClaimArgRet();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testCalimNumberCannotBeDefinedPrecondition() throws BaseException {
        doReturn("DUMMY").when(claim).getClaimNumber();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testCalimMustHavePolicyPrecondition() throws BaseException {
        doReturn(null).when(claim).getPolicy();
        sut.invoke();
    }

    @Test
    public void testCalimPolicyStatusMustBeValidPrecondition() throws BaseException {
        Arrays.asList(APPLICATION, QUOTATION, CANCELLED_FROM_INCEPTION, DECLINED, DELETED, NOT_TAKEN_UP, REFERRED, UNUSED).
            stream().
            forEach(status -> {
                try {
                    doReturn(status).when(policy).getStatus();
                    sut.invoke();
                    fail("Expected policy status exception not thrown for: " + status);
                } catch (PreconditionException e) {
                    // this is good!
                } catch (Throwable e) {
                    fail("Unexpected exception thrown for: " + status + " exception:" + e);
                }
            });
    }

    @Test(expected = PreconditionException.class)
    public void testThatProductTypeIsDefinedOnPolicy() throws BaseException {
        doReturn(null).when(policy).getProductTypeId();
        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void testThatClaimNumberPostconditionIsChecked() throws BaseException {
        when(claim.getClaimNumber()).thenReturn(null, (String)null);
        sut.invoke();
    }

    @Test
    public void verifyThatClaimNumberGeneratorIsUsed() throws BaseException {
        sut.invoke();
        verify(generateUniqueKeyCommand).setKeyIdArg(eq("ClaimNumber"));
    }

    @Test
    public void verifyGenerateClaimNumberCommandIsInvokedCorrectly() throws BaseException {
        sut.invoke();
        verify(generateClaimNumberCommand).setClaimArg(eq(claim));
        verify(generateClaimNumberCommand).setUniqueNumberArg(eq(GENERATED_UNIQUE_ID));
        verify(claim).setClaimNumber(eq(GENERATED_CLAIM_NUMMBER));
    }
}
