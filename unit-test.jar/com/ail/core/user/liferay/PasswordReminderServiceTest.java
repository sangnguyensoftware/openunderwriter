package com.ail.core.user.liferay;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

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
import com.ail.core.user.PasswordReminderService.PasswordReminderArgument;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, PortalUtil.class, UserLocalServiceUtil.class})
public class PasswordReminderServiceTest {

    private static final String DUMMY_EMAIL_ADDRESS = "dummy@email.address";

    PasswordReminderService sut;
    @Mock
    PasswordReminderArgument args;
    @Mock
    PortletRequest porletRequest;
    @Mock
    HttpServletRequest httpRequest;
    @Mock
    Company company;
    @Mock
    RequestWrapper requestWrapper;

    @Before
    public void init() throws Throwable {
        MockitoAnnotations.initMocks(this);

        mockStatic(CoreContext.class);
        mockStatic(PortalUtil.class);
        mockStatic(UserLocalServiceUtil.class);

        sut=new PasswordReminderService();
        sut.setArgs(args);

        doReturn(DUMMY_EMAIL_ADDRESS).when(args).getUsersEmailAddressArg();

        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(requestWrapper.getServletRequest()).thenReturn(httpRequest);
        when(requestWrapper.getCompany()).thenReturn(company);
    }

    @Test(expected=PreconditionException.class)
    public void usernameArgCannotBeNull() throws BaseException {
        doReturn(null).when(args).getUsersEmailAddressArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void usernameArgCannotBeEmpty() throws BaseException {
        doReturn("").when(args).getUsersEmailAddressArg();
        sut.invoke();
    }

}
