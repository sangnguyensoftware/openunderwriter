package com.ail.ui.server.common;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.CoreProxy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.search.PolicySearchService.PolicySearchCommand;
import com.ail.ui.shared.model.AdvancedSearchCriteria;
import com.ail.ui.shared.model.NullableDate;

public class CommandArgUtilTest {

    @Mock
    private CoreProxy core;

    @Mock
    private PolicySearchCommand command;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn(command).when(core).newCommand(PolicySearchCommand.class);
    }

    @Test
    public void testPolicySearchCommandWithCriteriaSet() {

        Date createdStart = new Date();
        Date createdEnd = new Date(createdStart.getTime() + 1);
        Date quoteStart = new Date();
        Date quoteEnd = new Date(quoteStart.getTime() + 1);
        Date inceptionStart = new Date();
        Date inceptionEnd = new Date(inceptionStart.getTime() + 1);
        Date expiryStart = new Date(inceptionEnd.getTime() + 1);
        Date expiryEnd = new Date(expiryStart.getTime() + 1);

        List<String> statuses = new ArrayList<>();
        statuses.add("Application");

        AdvancedSearchCriteria criteria = new AdvancedSearchCriteria(
                "QF100",
                "Product",
                statuses,
                new NullableDate(createdStart),
                new NullableDate(createdEnd),
                new NullableDate(quoteStart),
                new NullableDate(quoteEnd),
                new NullableDate(quoteStart),
                new NullableDate(quoteEnd),
                new NullableDate(expiryStart),
                new NullableDate(expiryEnd),
                "CL00123",
                "I Middle Last",
                "6 Street W1",
                "jon@goggle.com",
                new Long(23),
                new Long(24),
                true,
                true,
                "OrderBy",
                "OrderDirection");

        CommandArgUtil.getPolicySearchCommand(criteria, core);

        List<PolicyStatus> policyStatuses = new ArrayList<>();
        policyStatuses.add(PolicyStatus.APPLICATION);

        verify(command).setProductTypeIdArg("Product");
        verify(command).setPolicyStatusArg(policyStatuses);
        verify(command).setCreatedDateMinimumArg(createdStart);
        verify(command).setCreatedDateMaximumArg(any(Date.class));
        verify(command, never()).setCreatedDateMaximumArg(createdEnd);
        verify(command).setQuoteDateMinimumArg(quoteStart);
        verify(command).setQuoteDateMaximumArg(any(Date.class));
        verify(command, never()).setQuoteDateMaximumArg(quoteEnd);
        verify(command).setInceptionDateMinimumArg(inceptionStart);
        verify(command).setInceptionDateMaximumArg(any(Date.class));
        verify(command, never()).setInceptionDateMaximumArg(inceptionEnd);
        verify(command).setExpiryDateMinimumArg(expiryStart);
        verify(command).setExpiryDateMaximumArg(any(Date.class));
        verify(command, never()).setExpiryDateMaximumArg(expiryEnd);
        verify(command).setPartyIdArg("CL00123");
        verify(command).setPartyNameArg("% I%Middle%Last%");
        verify(command).setPartyAddressArg("%6%Street%W1%");
        verify(command).setIncludeTestArg(true);
        verify(command).setOrderByArg("OrderBy");

    }

    @Test
    public void testPolicySearchCommandWithoutCriteriaSet() {

        AdvancedSearchCriteria criteria = new AdvancedSearchCriteria();

        CommandArgUtil.getPolicySearchCommand(criteria, core);

        verify(command).setProductTypeIdArg("");
        verify(command).setPartyNameArg("");
        verify(command).setPartyAddressArg("");
        verify(command).setOrderByArg("");
    }

}
