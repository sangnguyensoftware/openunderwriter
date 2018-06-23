package com.ail.core.product.service;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.google.common.collect.Lists;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreContext.class, UserLocalServiceUtil.class, RoleLocalServiceUtil.class })
public class SecurityProcessorTest {

    private static final String DERIVED_NAMESPACE = "SecuredServices.ServiceName";

    private static final String AUTH = "usr:pw";

    private Long userId = Long.valueOf(1);

    @Mock
    private CoreProxy coreProxy;

    @Mock
    private User user;

    @Mock
    private Role role;

    @Mock
    private ServiceRequestUrl serviceRequestUrl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(UserLocalServiceUtil.class);
        PowerMockito.mockStatic(RoleLocalServiceUtil.class);

        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        when(coreProxy.getParameterValue(eq(DERIVED_NAMESPACE))).thenReturn("Client");
        when(UserLocalServiceUtil.authenticateForBasic(anyLong(), anyString(), anyString(), anyString())).thenReturn(userId);
        when(UserLocalServiceUtil.getUser(eq(Long.valueOf(userId)))).thenReturn(user);
        when(user.getUserId()).thenReturn(userId);
        when(RoleLocalServiceUtil.getUserRoles(eq(userId))).thenReturn(Lists.newArrayList(role));
        when(user.getGroupIds()).thenReturn(new long[0]);
        when(serviceRequestUrl.resolveNamespace()).thenReturn("product.name");
        when(serviceRequestUrl.resolveServiceName()).thenReturn("ServiceName");
    }

    @Test
    public void testPermitAll() throws Exception {
        when(coreProxy.getParameterValue(eq(DERIVED_NAMESPACE))).thenReturn("PermitAll");
        assertEquals((Integer) SC_ACCEPTED, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());
    }

    @Test
    public void testDenyAll() throws Exception {
        when(coreProxy.getParameterValue(eq(DERIVED_NAMESPACE))).thenReturn("DenyAll");
        assertEquals((Integer) SC_FORBIDDEN, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());
    }

    @Test
    public void testNullUserNotAuthenticated() throws Exception {

        assertEquals((Integer) SC_UNAUTHORIZED, new SecurityProcessor(serviceRequestUrl, "").preProcessRequest());
    }

    @Test
    public void testUserAuthenticated() throws Exception {

        when(role.getName()).thenReturn("Client");

        assertEquals((Integer) SC_ACCEPTED, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());

    }

    @Test
    public void testUserNotAuthenticated() throws Exception {

        when(role.getName()).thenReturn("NotClient");

        assertEquals((Integer) SC_UNAUTHORIZED, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());

    }

    @Test
    public void testNoUserFound() throws Exception {

        when(UserLocalServiceUtil.getUser(eq(Long.valueOf(userId)))).thenReturn(null);
        when(role.getName()).thenReturn("Client");

        assertEquals((Integer) SC_UNAUTHORIZED, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());

    }

    @Test
    public void testNoUserIdFound() throws Exception {

        when(UserLocalServiceUtil.authenticateForBasic(anyLong(), anyString(), anyString(), anyString())).thenReturn(0L);
        when(role.getName()).thenReturn("Client");

        assertEquals((Integer) SC_UNAUTHORIZED, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());

    }

    @Test
    public void testNoRoleFound() throws Exception {

        when(role.getName()).thenReturn("");

        assertEquals((Integer) SC_UNAUTHORIZED, new SecurityProcessor(serviceRequestUrl, AUTH).preProcessRequest());

    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionOnBadCreds() throws Exception {

        when(role.getName()).thenReturn("Client");

        assertEquals((Integer) SC_UNAUTHORIZED, new SecurityProcessor(serviceRequestUrl, "user").preProcessRequest());

    }

}
