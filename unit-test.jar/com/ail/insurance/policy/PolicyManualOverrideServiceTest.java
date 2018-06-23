package com.ail.insurance.policy;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.AddManualLoadingService.AddManualLoadingCommand;
import com.ail.insurance.quotation.AddPolicyNumberService.AddPolicyNumberCommand;
import com.ail.insurance.quotation.CalculateGrossPremiumService.CalculateGrossPremiumCommand;
import com.ail.insurance.quotation.OverrideBasePremiumService.OverrideBasePremiumCommand;
import com.ail.insurance.quotation.OverrideStatusService.OverrideStatusCommand;
import com.ail.insurance.quotation.PolicyManualOverrideService;
import com.ail.insurance.quotation.PolicyManualOverrideService.PolicyManualOverrideArgument;
import com.google.common.collect.Maps;

public class PolicyManualOverrideServiceTest {

    private PolicyManualOverrideService service;

    private Policy policy;

    @Mock
    PolicyManualOverrideArgument args;

    @Mock
    Core core;

    @Mock
    OverrideStatusCommand overrideStatusCommand;

    @Mock
    AddPolicyNumberCommand  addPolicyNumberCommand;

    @Mock
    CalculateGrossPremiumCommand calculateGrossPremiumCommand;

    @Mock
    OverrideBasePremiumCommand overrideBasePremiumCommand;

    @Mock
    AddManualLoadingCommand addManualLoadingCommand;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        policy = new Policy();
        policy.setStatus(PolicyStatus.REFERRED);

        service=new PolicyManualOverrideService();
        service.setArgs(args);
        service.setCore(core);

        doReturn(calculateGrossPremiumCommand).when(core).newCommand(CalculateGrossPremiumCommand.class);
        doReturn(addPolicyNumberCommand).when(core).newCommand(AddPolicyNumberCommand.class);
        doReturn(overrideStatusCommand).when(core).newCommand(OverrideStatusCommand.class);
        doReturn(overrideBasePremiumCommand).when(core).newCommand(OverrideBasePremiumCommand.class);
        doReturn(addManualLoadingCommand).when(core).newCommand(AddManualLoadingCommand.class);

    }

    @Test(expected=PreconditionException.class)
    public void testNoPolicyPreconditions() throws BaseException {

        doReturn("onrisk").when(args).getOperationArg();

        service.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNoOperationPreconditions() throws BaseException {

        doReturn(policy).when(args).getPolicyArg();

        service.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testBadOperationPreconditions() throws BaseException {

        doReturn(policy).when(args).getPolicyArg();
        doReturn("bad").when(args).getOperationArg();

        service.invoke();
    }

    @Test
    public void testRecalculateOperation() throws BaseException {

        doReturn(policy).when(args).getPolicyArg();
        doReturn("recalculate").when(args).getOperationArg();

        service.invoke();

        verify(calculateGrossPremiumCommand, times(1)).invoke();

        verify(overrideStatusCommand, never()).invoke();
        verify(addPolicyNumberCommand, never()).invoke();
        verify(overrideBasePremiumCommand, never()).invoke();
        verify(addManualLoadingCommand, never()).invoke();

    }

    @Test
    public void testOnriskOperation() throws BaseException {

        doReturn(policy).when(args).getPolicyArg();
        doReturn("onrisk").when(args).getOperationArg();

        service.invoke();

        verify(overrideStatusCommand, times(1)).invoke();
        verify(calculateGrossPremiumCommand, times(1)).invoke();
        verify(addPolicyNumberCommand, times(1)).invoke();

        verify(overrideBasePremiumCommand, never()).invoke();
        verify(addManualLoadingCommand, never()).invoke();

    }


    @Test
    public void testDeclineOperation() throws BaseException {

        doReturn(policy).when(args).getPolicyArg();
        doReturn("decline").when(args).getOperationArg();

        service.invoke();

        verify(overrideStatusCommand, times(1)).invoke();

        verify(addPolicyNumberCommand, never()).invoke();
        verify(calculateGrossPremiumCommand, never()).invoke();
        verify(overrideBasePremiumCommand, never()).invoke();
        verify(addManualLoadingCommand, never()).invoke();

    }

    @Test
    public void testQuoteOperation() throws BaseException {

        doReturn(policy).when(args).getPolicyArg();
        doReturn("quote").when(args).getOperationArg();

        service.invoke();

        verify(overrideStatusCommand, times(1)).invoke();
        verify(calculateGrossPremiumCommand, times(1)).invoke();

        verify(addPolicyNumberCommand, never()).invoke();
        verify(overrideBasePremiumCommand, never()).invoke();
        verify(addManualLoadingCommand, never()).invoke();

    }

    @Test
    public void testBasePremiumOverrides() throws BaseException {

        final long sysId = 101;
        policy.setSystemId(sysId);

        AssessmentSheet sheet = new AssessmentSheet();
        sheet.setLockingActor("User");
        sheet.addFixedSum(PolicyManualOverrideService.BASE_PREMIUM_ID, "base", new CurrencyAmount(1, Currency.GBP));
        sheet.clearLockingActor();

        Map<String, String[]> params = Maps.newHashMap();
        params.put(
                PolicyManualOverrideService.BASE_PREMIUM_OVERRIDE_PARAM_NAME + ":0:" + sysId, new String[]{"2"});

        doReturn(policy).when(args).getPolicyArg();
        doReturn("recalculate").when(args).getOperationArg();
        doReturn(params).when(args).getParameterMapArg();

        service.invoke();

        verify(overrideBasePremiumCommand, times(1)).invoke();
        verify(calculateGrossPremiumCommand, times(1)).invoke();

        verify(overrideStatusCommand, never()).invoke();
        verify(addPolicyNumberCommand, never()).invoke();
        verify(addManualLoadingCommand, never()).invoke();

    }

    @Test
    public void testManualFixedLoadingOverrides() throws BaseException {

        final long sysId = 101;
        policy.setSystemId(sysId);

        AssessmentSheet sheet = new AssessmentSheet();
        sheet.setLockingActor("User");
        sheet.addFixedSum(PolicyManualOverrideService.BASE_PREMIUM_ID, "base", new CurrencyAmount(1, Currency.GBP));
        sheet.clearLockingActor();

        Map<String, String[]> params = Maps.newHashMap();
        params.put(
                PolicyManualOverrideService.MANUAL_FIXED_LOADING_PARAM_NAME + ":0:" + sysId, new String[]{"2"});

        doReturn(policy).when(args).getPolicyArg();
        doReturn("recalculate").when(args).getOperationArg();
        doReturn(params).when(args).getParameterMapArg();

        service.invoke();

        verify(addManualLoadingCommand, times(1)).invoke();
        verify(calculateGrossPremiumCommand, times(1)).invoke();

        verify(overrideStatusCommand, never()).invoke();
        verify(addPolicyNumberCommand, never()).invoke();
        verify(overrideBasePremiumCommand, never()).invoke();

    }

    @Test
    public void testManualPercentageLoadingOverrides() throws BaseException {

        final long sysId = 101;
        policy.setSystemId(sysId);

        AssessmentSheet sheet = new AssessmentSheet();
        sheet.setLockingActor("User");
        sheet.addFixedSum(PolicyManualOverrideService.BASE_PREMIUM_ID, "base", new CurrencyAmount(1, Currency.GBP));
        sheet.clearLockingActor();

        Map<String, String[]> params = Maps.newHashMap();
        params.put(
                PolicyManualOverrideService.MANUAL_PCT_LOADING_PARAM_NAME + ":0:" + sysId, new String[]{"2"});

        doReturn(policy).when(args).getPolicyArg();
        doReturn("recalculate").when(args).getOperationArg();
        doReturn(params).when(args).getParameterMapArg();

        service.invoke();

        verify(addManualLoadingCommand, times(1)).invoke();
        verify(calculateGrossPremiumCommand, times(1)).invoke();

        verify(overrideStatusCommand, never()).invoke();
        verify(addPolicyNumberCommand, never()).invoke();
        verify(overrideBasePremiumCommand, never()).invoke();

    }

    @Test
    public void testNoLoadingForWrongPolicySysId() throws BaseException {

        final long sysId = 101;
        policy.setSystemId(sysId);

        AssessmentSheet sheet = new AssessmentSheet();
        sheet.setLockingActor("User");
        sheet.addFixedSum(PolicyManualOverrideService.BASE_PREMIUM_ID, "base", new CurrencyAmount(1, Currency.GBP));
        sheet.clearLockingActor();

        Map<String, String[]> params = Maps.newHashMap();
        params.put(
                PolicyManualOverrideService.MANUAL_PCT_LOADING_PARAM_NAME + ":0:" + sysId + 1, new String[]{"2"});

        doReturn(policy).when(args).getPolicyArg();
        doReturn("recalculate").when(args).getOperationArg();
        doReturn(params).when(args).getParameterMapArg();

        service.invoke();

        verify(calculateGrossPremiumCommand, times(1)).invoke();

        verify(addManualLoadingCommand, never()).invoke();
        verify(overrideStatusCommand, never()).invoke();
        verify(addPolicyNumberCommand, never()).invoke();
        verify(overrideBasePremiumCommand, never()).invoke();

    }

}
