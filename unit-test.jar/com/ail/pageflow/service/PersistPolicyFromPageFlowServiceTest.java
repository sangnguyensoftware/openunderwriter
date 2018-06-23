package com.ail.pageflow.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.security.Principal;

import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class})
public class PersistPolicyFromPageFlowServiceTest {
    private static long USER_ID = 1234L;
    private static long POLICY_SYSTEM_ID = 1L;
    private PersistPolicyFromPageFlowService sut = new PersistPolicyFromPageFlowService();

    @Mock
    private ExecutePageActionArgument args;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Policy policy;
    @Mock
    private PortletRequest portletRequest;
    @Mock
    private Principal principal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = spy(new PersistPolicyFromPageFlowService());
        sut.setArgs(args);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);

        doReturn(coreProxy).when(sut).getCoreProxyFromPageFlowContext();
        doReturn("pagename").when(sut).getCurrentPageFromPageFlowContext();
        doNothing().when(sut).setPolicyToPageFlowContext(any(Policy.class));

        when(PageFlowContext.getPolicy()).thenReturn(policy);
        when(PageFlowContext.getRemoteUser()).thenReturn(USER_ID);
    }

    @Test
    public void shouldDoNothingWhenPolicyIsNull() throws BaseException {
        when(PageFlowContext.getPolicy()).thenReturn(null);
        sut.invoke();
        verify(coreProxy, never()).create(any(Type.class));
        verify(coreProxy, never()).update(any(Type.class));
    }

    @Test
    public void shouldCreateUnsavedPolicy() throws BaseException {
        doReturn(false).when(policy).isPersisted();
        doReturn(policy).when(coreProxy).create(any(Policy.class));
        sut.invoke();
        verify(coreProxy).create(any(Policy.class));
        verify(coreProxy, never()).update(any(Type.class));
    }

    @Test
    public void shouldUpdateSavedPolicy() throws BaseException {
        doReturn(policy).when(coreProxy).load(eq(Policy.class), eq(POLICY_SYSTEM_ID));
        doReturn(policy).when(coreProxy).update(any(Policy.class));
        doReturn(POLICY_SYSTEM_ID).when(policy).getSystemId();
        doReturn(true).when(policy).isPersisted();
        sut.invoke();
        verify(coreProxy).update(eq(policy));
        verify(coreProxy, never()).create(any(Policy.class));
    }

    @Test
    public void shouldNotSetPolicyUserIfNullOnCreation() throws BaseException {
        doReturn(false).when(policy).isPersisted();
        doReturn(policy).when(coreProxy).create(any(Policy.class));
        doReturn(null).when(policy).getOwningUser();
        sut.invoke();
        verify(policy, never()).setOwningUser(eq(1234L));
    }

    @Test
    public void shouldNotSetPolicyUserIfNotNullOnCreation() throws BaseException {
        doReturn(false).when(policy).isPersisted();
        doReturn(policy).when(coreProxy).create(any(Policy.class));
        doReturn(USER_ID).when(policy).getOwningUser();
        sut.invoke();
        verify(policy, never()).setOwningUser(anyLong());
    }

    @Test
    public void shouldNotSetPolicyUserIfNullOnUpdate() throws BaseException {
        doReturn(policy).when(coreProxy).load(eq(Policy.class), eq(POLICY_SYSTEM_ID));
        doReturn(policy).when(coreProxy).update(any(Policy.class));
        doReturn(POLICY_SYSTEM_ID).when(policy).getSystemId();
        doReturn(true).when(policy).isPersisted();
        doReturn(null).when(policy).getOwningUser();
        sut.invoke();
        verify(policy, never()).setOwningUser(eq(1234L));
    }

    @Test
    public void shouldNotSetPolicyUserIfNotNullOnUpdate() throws BaseException {
        doReturn(policy).when(coreProxy).load(eq(Policy.class), eq(POLICY_SYSTEM_ID));
        doReturn(policy).when(coreProxy).update(any(Policy.class));
        doReturn(POLICY_SYSTEM_ID).when(policy).getSystemId();
        doReturn(true).when(policy).isPersisted();
        doReturn(USER_ID).when(policy).getOwningUser();
        sut.invoke();
        verify(policy, never()).setOwningUser(anyLong());
    }
}
