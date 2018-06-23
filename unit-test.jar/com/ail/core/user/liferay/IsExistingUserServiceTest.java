package com.ail.core.user.liferay;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
import com.ail.core.user.IsExistingUserService.IsExistingUserArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, UserLocalServiceUtil.class})
public class IsExistingUserServiceTest {

    private static final String USERNAME = "username";

    private static final Long COMPANY_ID = 999L;

    private IsExistingUserService sut;

    @Mock
    private IsExistingUserArgument args;
    @Mock
    private RequestWrapper requestWrapper;
    @Mock
    private Company company;
    @Mock
    private User user;

    @Before
    public void setup() throws SystemException, PortalException {
        MockitoAnnotations.initMocks(this);

        mockStatic(CoreContext.class);
        when(CoreContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(requestWrapper.getCompany()).thenReturn(company);
        when(company.getCompanyId()).thenReturn(COMPANY_ID);

        mockStatic(UserLocalServiceUtil.class);
        when(UserLocalServiceUtil.fetchUserByScreenName(eq(COMPANY_ID), eq(USERNAME))).thenReturn(user);

        sut=new IsExistingUserService();
        sut.setArgs(args);
        doReturn(USERNAME).when(args).getUsernameArg();
    }

    @Test(expected=PreconditionException.class)
    public void testNullUsernamePreconditionIsChecked() throws BaseException {
        doReturn(null).when(args).getUsernameArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testZeroLengthUsernamePreconditionIsChecked() throws BaseException  {
        doReturn("").when(args).getUsernameArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNullContextRequestIsChecked() throws BaseException {
        when(CoreContext.getRequestWrapper()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void checkThatExceptionFromLiferayServiceIsPassedOn() throws SystemException, BaseException {
        SystemException systemException=mock(SystemException.class);
        when(UserLocalServiceUtil.fetchUserByScreenName(eq(COMPANY_ID), eq(USERNAME))).thenThrow(systemException);
        sut.invoke();
    }

    @Test
    public void checkFalseIsReturnedWhenUserIsNotFound() throws BaseException, SystemException {
        when(UserLocalServiceUtil.fetchUserByScreenName(eq(COMPANY_ID), eq(USERNAME))).thenReturn(null);
        sut.invoke();
        verify(args, times(1)).setExistingUserRet(eq(false));
    }

    @Test
    public void checkTrueIsReturnedWhenUserIsFound() throws BaseException, SystemException {
        sut.invoke();
        verify(args, times(1)).setExistingUserRet(eq(true));
    }
}
