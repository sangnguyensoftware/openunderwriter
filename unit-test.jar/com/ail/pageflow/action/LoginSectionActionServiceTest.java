package com.ail.pageflow.action;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Properties;

import javax.portlet.ActionRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.core.user.LoginService.LoginServiceCommand;
import com.ail.pageflow.Answer;
import com.ail.pageflow.LoginSection;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.action.ActionService.ActionArgument;
import com.ail.pageflow.util.Functions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Functions.class, CoreContext.class, PageFlowContext.class })
public class LoginSectionActionServiceTest {

    private static final String SUCCESS_PAGE_ID = "success page id";

    LoginSectionActionService sut;

    @Mock
    ActionArgument actionArgument;
    @Mock
    LoginSection loginSection;
    @Mock
    ActionRequest actionRequest;
    @Mock
    Properties properties;
    @Mock
    LoginServiceCommand loginServiceCommand;
    @Mock
    Core core;
    @Mock
    Type sessionTemp;
    @Mock
    RequestWrapper requestWrapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new LoginSectionActionService();
        sut.setCore(core);

        sut.setArgs(actionArgument);

        mockStatic(Functions.class);
        mockStatic(CoreContext.class);
        mockStatic(PageFlowContext.class);

        when(PageFlowContext.getOperationParameters()).thenReturn(properties);
        when(PageFlowContext.getRequestedOperation()).thenReturn("login-section-login");
        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);

        doReturn(loginSection).when(actionArgument).getPageElementArg();
        doReturn("login-section-login").when(loginSection).getLoginButtonLabel();
    }

    @Test(expected = PreconditionException.class)
    public void testPageElementNullPrecondition() throws Exception {
        doReturn(null).when(actionArgument).getPageElementArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testPageElementNotLoginSectionPrecondition() throws Exception {
        doReturn(mock(Answer.class)).when(actionArgument).getPageElementArg();
        sut.invoke();
    }

    @Test
    public void testLoginButtonInvokesHandler() throws Exception {
        LoginSectionActionService spy = Mockito.spy(sut);
        doNothing().when(spy).handleLogin();
        spy.invoke();
        verify(spy, times(1)).handleLogin();
        verify(spy, never()).handleCreate();
        verify(spy, never()).handleReminder();
    }

    @Test
    public void testCreateInvokesHandler() throws Exception {
        LoginSectionActionService spy = Mockito.spy(sut);
        when(PageFlowContext.getRequestedOperation()).thenReturn("login-section-create");
        doNothing().when(spy).handleLogin();
        spy.invoke();
        verify(spy, never()).handleLogin();
        verify(spy, times(1)).handleCreate();
        verify(spy, never()).handleReminder();
    }

    @Test
    public void testReminderInvokesHandler() throws Exception {
        LoginSectionActionService spy = Mockito.spy(sut);
        when(PageFlowContext.getRequestedOperation()).thenReturn("login-section-reminder");
        doNothing().when(spy).handleReminder();
        spy.invoke();
        verify(spy, never()).handleLogin();
        verify(spy, never()).handleCreate();
        verify(spy, times(1)).handleReminder();
    }

    @Test
    public void testUndefinedOpIsIgnored() throws Exception {
        LoginSectionActionService spy = Mockito.spy(sut);
        when(PageFlowContext.getRequestedOperation()).thenReturn("UNDEFINED");
        sut.invoke();
        verify(spy, never()).handleLogin();
        verify(spy, never()).handleCreate();
        verify(spy, never()).handleReminder();
    }

    @Test
    public void testPageForwardOnSuccessfulAuthenticaton() throws BaseException {
        LoginSectionActionService spy = Mockito.spy(sut);

        doReturn(SUCCESS_PAGE_ID).when(spy).getDestinationOnSuccessPageIdFromPageElement();
        doReturn(loginServiceCommand).when(core).newCommand(eq(LoginServiceCommand.class));
        doReturn(true).when(loginServiceCommand).getAuthenticationSucceededRet();
        doNothing().when(spy).setNextPageNameOnPageFlow(eq(SUCCESS_PAGE_ID));

        spy.invoke();

        verify(spy).setNextPageNameOnPageFlow(eq(SUCCESS_PAGE_ID));
    }

    @Test
    public void testNoPageForwardOnFailedAuthenticaton() throws BaseException {
        LoginSectionActionService spy = Mockito.spy(sut);

        doReturn(SUCCESS_PAGE_ID).when(spy).getDestinationOnSuccessPageIdFromPageElement();
        doReturn(loginServiceCommand).when(core).newCommand(eq(LoginServiceCommand.class));
        doReturn(false).when(loginServiceCommand).getAuthenticationSucceededRet();
        when(CoreContext.getSessionTemp()).thenReturn(sessionTemp);

        spy.invoke();

        verify(spy, never()).setNextPageNameOnPageFlow(anyString());
    }
}
