package com.ail.pageflow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.insurance.policy.Policy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class})
public class SaveButtonActionTest {
    private static final long DUMMY_USER_ID = 1234L;

    SaveButtonAction sut;

    @Mock
    Policy policy;
    @Mock
    PageFlow pageFlow;
    @Mock
    CoreProxy coreProxy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(PageFlowContext.class);
        mockStatic(CoreContext.class);

        when(PageFlowContext.getPageFlow()).thenReturn(pageFlow);
        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);

        sut = new SaveButtonAction();
    }

    @Test
    public void ownerIsSetIfUnset() throws BaseException {
        doReturn(null).when(policy).getOwningUser();
        when(PageFlowContext.getRequestedOperation()).thenReturn(SaveButtonAction.class.getSimpleName());
        when(CoreContext.getRemoteUser()).thenReturn(DUMMY_USER_ID);


        sut.processActions(policy);

        verify(policy).setOwningUser(eq(DUMMY_USER_ID));
    }

    @Test
    public void ownerIsNotSetIfAlreadySet() throws BaseException {
        doReturn(1L).when(policy).getOwningUser();
        when(PageFlowContext.getRequestedOperation()).thenReturn(SaveButtonAction.class.getSimpleName());
        when(CoreContext.getRemoteUser()).thenReturn(DUMMY_USER_ID);

        sut.processActions(policy);

        verify(policy, never()).setOwningUser(any(Long.class));
    }
}
