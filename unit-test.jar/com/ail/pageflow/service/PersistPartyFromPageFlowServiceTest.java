package com.ail.pageflow.service;

import static org.mockito.Matchers.any;
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
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.party.Party;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class})
public class PersistPartyFromPageFlowServiceTest {
    private static long USER_ID = 1234L;
    private static long PARTY_SYSTEM_ID = 1L;
    private PersistPartyFromPageFlowService sut = new PersistPartyFromPageFlowService();

    @Mock
    private ExecutePageActionArgument args;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Party party;
    @Mock
    private PortletRequest portletRequest;
    @Mock
    private Principal principal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = spy(new PersistPartyFromPageFlowService());
        sut.setArgs(args);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);

        doReturn(coreProxy).when(sut).getCoreProxyFromPageFlowContext();
        doReturn("pagename").when(sut).getCurrentPageFromPageFlowContext();
        doNothing().when(sut).setPartyToPageFlowContext(any(Party.class));

        when(PageFlowContext.getParty()).thenReturn(party);
        when(PageFlowContext.getRemoteUser()).thenReturn(USER_ID);
    }

    @Test
    public void shouldDoNothingWhenPartyIsNull() throws BaseException {
        when(PageFlowContext.getParty()).thenReturn(null);
        sut.invoke();
        verify(coreProxy, never()).create(any(Type.class));
        verify(coreProxy, never()).update(any(Type.class));
    }

    @Test
    public void shouldCreateUnsavedParty() throws BaseException {
        doReturn(false).when(party).isPersisted();
        doReturn(party).when(coreProxy).create(any(Party.class));
        sut.invoke();
        verify(coreProxy).create(any(Party.class));
        verify(coreProxy, never()).update(any(Type.class));
    }

    @Test
    public void shouldUpdateSavedParty() throws BaseException {
        doReturn(party).when(coreProxy).load(eq(Party.class), eq(PARTY_SYSTEM_ID));
        doReturn(party).when(coreProxy).update(any(Party.class));
        doReturn(PARTY_SYSTEM_ID).when(party).getSystemId();
        doReturn(true).when(party).isPersisted();
        sut.invoke();
        verify(coreProxy).update(eq(party));
        verify(coreProxy, never()).create(any(Party.class));
    }
}
