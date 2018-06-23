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
import com.ail.insurance.claim.Claim;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class})
public class PersistClaimFromPageFlowServiceTest {
    private static long USER_ID = 1234L;
    private static long CLAIM_SYSTEM_ID = 1L;
    private PersistClaimFromPageFlowService sut = new PersistClaimFromPageFlowService();

    @Mock
    private ExecutePageActionArgument args;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Claim claim;
    @Mock
    private PortletRequest portletRequest;
    @Mock
    private Principal principal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = spy(new PersistClaimFromPageFlowService());
        sut.setArgs(args);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);

        doReturn(coreProxy).when(sut).getCoreProxyFromPageFlowContext();
        doReturn("pagename").when(sut).getCurrentPageFromPageFlowContext();
        doNothing().when(sut).setClaimToPageFlowContext(any(Claim.class));

        when(PageFlowContext.getClaim()).thenReturn(claim);
        when(PageFlowContext.getRemoteUser()).thenReturn(USER_ID);
    }

    @Test
    public void shouldDoNothingWhenClaimIsNull() throws BaseException {
        when(PageFlowContext.getClaim()).thenReturn(null);
        sut.invoke();
        verify(coreProxy, never()).create(any(Type.class));
        verify(coreProxy, never()).update(any(Type.class));
    }

    @Test
    public void shouldCreateUnsavedClaim() throws BaseException {
        doReturn(false).when(claim).isPersisted();
        doReturn(claim).when(coreProxy).create(any(Claim.class));
        sut.invoke();
        verify(coreProxy).create(any(Claim.class));
        verify(coreProxy, never()).update(any(Type.class));
    }

    @Test
    public void shouldUpdateSavedClaim() throws BaseException {
        doReturn(claim).when(coreProxy).load(eq(Claim.class), eq(CLAIM_SYSTEM_ID));
        doReturn(claim).when(coreProxy).update(any(Claim.class));
        doReturn(CLAIM_SYSTEM_ID).when(claim).getSystemId();
        doReturn(true).when(claim).isPersisted();
        sut.invoke();
        verify(coreProxy).update(eq(claim));
        verify(coreProxy, never()).create(any(Claim.class));
    }

    @Test
    public void shouldNotSetClaimUserIfNullOnCreation() throws BaseException {
        doReturn(false).when(claim).isPersisted();
        doReturn(claim).when(coreProxy).create(any(Claim.class));
        doReturn(null).when(claim).getOwningUser();
        sut.invoke();
        verify(claim, never()).setOwningUser(eq(1234L));
    }

    @Test
    public void shouldNotSetClaimUserIfNotNullOnCreation() throws BaseException {
        doReturn(false).when(claim).isPersisted();
        doReturn(claim).when(coreProxy).create(any(Claim.class));
        doReturn(USER_ID).when(claim).getOwningUser();
        sut.invoke();
        verify(claim, never()).setOwningUser(anyLong());
    }

    @Test
    public void shouldNotSetClaimUserIfNullOnUpdate() throws BaseException {
        doReturn(claim).when(coreProxy).load(eq(Claim.class), eq(CLAIM_SYSTEM_ID));
        doReturn(claim).when(coreProxy).update(any(Claim.class));
        doReturn(CLAIM_SYSTEM_ID).when(claim).getSystemId();
        doReturn(true).when(claim).isPersisted();
        doReturn(null).when(claim).getOwningUser();
        sut.invoke();
        verify(claim, never()).setOwningUser(eq(1234L));
    }

    @Test
    public void shouldNotSetClaimUserIfNotNullOnUpdate() throws BaseException {
        doReturn(claim).when(coreProxy).load(eq(Claim.class), eq(CLAIM_SYSTEM_ID));
        doReturn(claim).when(coreProxy).update(any(Claim.class));
        doReturn(CLAIM_SYSTEM_ID).when(claim).getSystemId();
        doReturn(true).when(claim).isPersisted();
        doReturn(USER_ID).when(claim).getOwningUser();
        sut.invoke();
        verify(claim, never()).setOwningUser(anyLong());
    }
}
