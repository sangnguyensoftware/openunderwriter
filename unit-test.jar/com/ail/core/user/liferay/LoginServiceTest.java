package com.ail.core.user.liferay;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.ResponseWrapper;
import com.ail.core.user.LoginService.LoginServiceArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class})
public class LoginServiceTest {
    private static final String PASSWORD = "PASSWORD";
    private static final String USERNAME = "USERNAME";

    LoginService sut = new LoginService();

    @Mock
    LoginServiceArgument args;
    @Mock
    PortletRequest portletRequest;
    @Mock
    PortletResponse portletResponse;
    @Mock
    RequestWrapper requestWrapper;
    @Mock
    ResponseWrapper responseWrapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut.setArgs(args);

        mockStatic(CoreContext.class);

        doReturn(USERNAME).when(args).getUsernameArg();
        doReturn(PASSWORD).when(args).getPasswordArg();
        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getResponseWrapper()).thenReturn(responseWrapper);
    }

    @Test
    public void nullOrEmptyUsernameShouldTripPreconditionException() throws BaseException {
        try {
            doReturn("").when(args).getUsernameArg();
            sut.invoke();
            fail("Expected PreconditionException not thrown");
        }
        catch(PreconditionException e) {
            // ignore - this is a good thing
        }

        try {
            doReturn(null).when(args).getUsernameArg();
            sut.invoke();
            fail("Expected PreconditionException not thrown");
        }
        catch(PreconditionException e) {
            // ignore - this is a good thing
        }
    }

    @Test
    public void nullOrEmptyPasswordShouldTripPreconditionException() throws BaseException {
        try {
            doReturn("").when(args).getPasswordArg();
            sut.invoke();
            fail("Expected PreconditionException not thrown");
        }
        catch(PreconditionException e) {
            // ignore - this is a good thing
        }

        try {
            doReturn(null).when(args).getPasswordArg();
            sut.invoke();
            fail("Expected PreconditionException not thrown");
        }
        catch(PreconditionException e) {
            // ignore - this is a good thing
        }
    }

    @Test
    public void ensureThatNullRequestInContextIsTrapped() throws BaseException {
        when(CoreContext.getRequestWrapper()).thenReturn(null);

        try {
            sut.invoke();
            fail("Expected PreconditionException not thrown");
        }
        catch(PreconditionException e) {
            // ignore - this is a good thing
        }
    }

    @Test
    public void ensureThatNullResponseInContextIsTrapped() throws BaseException {
        when(CoreContext.getResponseWrapper()).thenReturn(null);

        try {
            sut.invoke();
            fail("Expected PreconditionException not thrown");
        }
        catch(PreconditionException e) {
            // ignore - this is a good thing
        }
    }
}
