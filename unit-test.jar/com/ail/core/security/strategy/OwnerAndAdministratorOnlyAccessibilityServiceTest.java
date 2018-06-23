package com.ail.core.security.strategy;


import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.Owned;
import com.ail.core.PreconditionException;
import com.ail.core.security.ConfirmObjectAccessibilityToUserService.ConfirmObjectAccessibilityToUserArgument;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, UserLocalServiceUtil.class})
public class OwnerAndAdministratorOnlyAccessibilityServiceTest {

    private static final String ADMIN_ROLE_NAME = "ADMIN_ROLE_NAME";

    private static final Long USER_ID = 1234L;

    OwnerAndAdministratorAccessibilityService sut;

    @Mock
    ConfirmObjectAccessibilityToUserArgument args;
    @Mock
    Owned ownedObject;
    @Mock
    Object unownedObject;
    @Mock
    PortletRequest portletRequest;
    @Mock
    User user;
    @Mock
    Role role;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new OwnerAndAdministratorAccessibilityService();
        sut.setArgs(args);

        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(UserLocalServiceUtil.class);

        doReturn(USER_ID).when(ownedObject).getOwningUser();
        doReturn(ownedObject).when(args).getObjectArg();
        doReturn(USER_ID).when(args).getUserIdArg();

        when(UserLocalServiceUtil.getUser(eq(USER_ID))).thenReturn(user);
        doReturn(Arrays.asList(role)).when(user).getRoles();

        doReturn(ADMIN_ROLE_NAME).when(role).getName();
        doReturn(ADMIN_ROLE_NAME).when(args).getOverrideRoleNameArg();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatNullObjectIsTrapped() throws Exception {
        doReturn(null).when(args).getObjectArg();
        sut.invoke();
    }

    @Test
    public void checkThatOwnerHasReadAndWriteAccess() throws Exception {
        doReturn("UNKNOWN_ROLE").when(role).getName();
        sut.invoke();
        verify(args).setReadAccessRet(eq(true));
        verify(args).setWriteAccessRet(eq(true));
    }

    @Test
    public void checkThatAdminRoleHasReadAndWriteAccess() throws Exception {
        doReturn(9999L).when(ownedObject).getOwningUser();
        sut.invoke();
        verify(args).setReadAccessRet(eq(true));
        verify(args).setWriteAccessRet(eq(true));
    }

    @Test
    public void verifyThatNonOwnerNonAdminUserIsDenied() throws Exception {
        doReturn("UNKNOWN_ROLE").when(role).getName();
        doReturn(9999L).when(ownedObject).getOwningUser();
        sut.invoke();
        verify(args, never()).setReadAccessRet(eq(true));
        verify(args, never()).setWriteAccessRet(eq(true));
    }
}
