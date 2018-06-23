package com.ail.insurance.search.hibernate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.security.FilterListAccessibilityToUserService.FilterListAccessibilityToUserCommand;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.search.PolicySearchService.PolicySearchArgument;
import com.google.common.collect.Lists;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserLocalServiceUtil.class})
public class HibernatePolicySearchServiceTest {

    HibernatePolicySearchService sut;

    @Mock
    PolicySearchArgument args;
    @Mock
    User user;
    @Mock
    Core core;
    @Mock
    Criteria criteria;
    @Mock
    Policy result1;
    @Mock
    Policy result2;
    @Mock
    List<Policy> serviceResult;
    @Mock
    FilterListAccessibilityToUserCommand filterListAccessibilityToUserCommand;

    List<Object> criteriaResult;
    Collection<Policy> filteredList;

    @Before
    public void setup() throws PortalException, SystemException {
        MockitoAnnotations.initMocks(this);

        sut = spy(new HibernatePolicySearchService());
        sut.setArgs(args);
        sut.setCore(core);

        criteriaResult = Arrays.asList((Object)result1, (Object)result2);

        mockStatic(UserLocalServiceUtil.class);

        doReturn(true).when(args).getIncludeSupersededArg();
        doReturn(criteria).when(sut).createCriteria();
        doReturn(criteriaResult).when(criteria).list();
        doReturn(serviceResult).when(args).getPoliciesRet();
        doReturn(filterListAccessibilityToUserCommand).when(core).newCommand(eq("FilterListAccessibilityToUserCommand"),eq(FilterListAccessibilityToUserCommand.class));
        doReturn(filteredList).when(filterListAccessibilityToUserCommand).getListRet();

        when(UserLocalServiceUtil.getUser(anyLong())).thenReturn(user);
    }

    @Test
    public void checkThatConfigurationNamespaceIsSetCorrectly() {
        assertThat(sut.getConfigurationNamespace(), is("AIL.Base.Registry"));
    }

    @Test
    public void checkThatQueryResultIsPassedIntoFilter() throws BaseException {
        sut.invoke();
        verify(filterListAccessibilityToUserCommand).setListArg(eq(criteriaResult));
    }

    @Test
    public void checkThatFilterResultIsPassedBackAsServiceResult() throws BaseException {
        sut.invoke();
        verify(args).setPoliciesRet(eq(filteredList));
    }

    @Test
    public void checkActivePoliciesReturned() {
        Policy policy1 = new Policy();
        Policy policy2 = new Policy();
        Policy policy3 = new Policy();
        Policy policy4 = new Policy();
        Policy policy5 = new Policy();
        Policy policy6 = new Policy();
        Policy policy7 = new Policy();

        policy1.setStatus(PolicyStatus.ON_RISK);
        policy1.setSystemId(1);
        policy1.setPolicyNumber("POL123");
        policy1.setMtaIndex(0L);
        policy1.setRenewalIndex(0L);

        policy2.setStatus(PolicyStatus.ON_RISK);
        policy2.setSystemId(2);
        policy2.setPolicyNumber("POL123");
        policy2.setMtaIndex(1L);
        policy2.setRenewalIndex(0L);

        policy3.setStatus(PolicyStatus.ON_RISK);
        policy3.setSystemId(3);
        policy3.setPolicyNumber("POL123");
        policy3.setMtaIndex(0L);
        policy3.setRenewalIndex(1L);

        policy4.setStatus(PolicyStatus.ON_RISK);
        policy4.setSystemId(4);
        policy4.setPolicyNumber("POL123");
        policy4.setMtaIndex(1L);
        policy4.setRenewalIndex(1L);

        policy5.setStatus(PolicyStatus.APPLICATION);
        policy5.setSystemId(5);

        policy6.setStatus(PolicyStatus.ON_RISK);
        policy6.setPolicyNumber("POL456");
        policy6.setSystemId(6);

        policy7.setStatus(PolicyStatus.DELETED);
        policy7.setSystemId(7);
        policy7.setPolicyNumber("POL123");
        policy7.setMtaIndex(2L);
        policy7.setRenewalIndex(2L);

        List<Policy> policies = Lists.newArrayList(policy2, policy6, policy7, policy1, policy5);
        Collection<Policy> filtered = HibernatePolicySearchService.getActivePolicies(policies);

        assertEquals(3, filtered.size());

        Iterator<Policy> polIt = filtered.iterator();
        assertEquals(2L, polIt.next().getSystemId());
        assertEquals(6L, polIt.next().getSystemId());
        assertEquals(5L, polIt.next().getSystemId());

        policies = Lists.newArrayList(policy3, policy2, policy4, policy7, policy1, policy5);
        filtered = HibernatePolicySearchService.getActivePolicies(policies);

        assertEquals(2, filtered.size());

        polIt = filtered.iterator();
        assertEquals(4L, polIt.next().getSystemId());
        assertEquals(5L, polIt.next().getSystemId());
    }
}
