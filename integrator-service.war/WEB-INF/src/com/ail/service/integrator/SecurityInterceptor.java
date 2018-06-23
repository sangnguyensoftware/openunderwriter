package com.ail.service.integrator;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.Base64;

import com.ail.core.CoreContext;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

@Provider
@ServerInterceptor
public class SecurityInterceptor implements PreProcessInterceptor {
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", SC_UNAUTHORIZED, new Headers<Object>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", SC_FORBIDDEN, new Headers<Object>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", SC_INTERNAL_SERVER_ERROR, new Headers<Object>());;

    @Override
    public ServerResponse preProcess(final HttpRequest request, final ResourceMethod resourceMethod) throws Failure, WebApplicationException {
        try {
            Method method = resourceMethod.getMethod();

            User remoteUser = authenticateUser(request);

            CoreContext.setRemoteUser(remoteUser.getUserId());

            if (isAccessibleToAll(method)) {
                return null;
            }

            if (isAccessibleToUser(method, remoteUser)) {
                return null;
            }

            if (isAccessibleToNone(method)) {
                return ACCESS_FORBIDDEN;
            }

            return ACCESS_DENIED;
        } catch (Exception e) {
            return SERVER_ERROR;
        }
    }

    private boolean isAccessibleToNone(Method method) {
        return method.isAnnotationPresent((Class<? extends Annotation>) DenyAll.class);
    }

    private boolean isAccessibleToAll(Method method) {
        return method.isAnnotationPresent((Class<? extends Annotation>) PermitAll.class);
    }

    private boolean isAccessibleToUser(Method method, User user) throws Exception {
        return method.isAnnotationPresent((Class<? extends Annotation>) RolesAllowed.class) && this.isAnyRoleAPermittedRole(this.rolesAllowed(method), rolesForUser(user));
    }

    private boolean isAnyRoleAPermittedRole(Set<String> permittedRoles, List<Role> usersRoles) {
        for (String pRole : permittedRoles) {
            for (Role uRole : usersRoles) {
                if (pRole.equals(uRole.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private User authenticateUser(HttpRequest request) throws Exception {
        HttpHeaders headers = request.getHttpHeaders();
        List<String> authorization = (List<String>) headers.getRequestHeader(AUTHORIZATION_PROPERTY);

        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME, "");
        String usernameAndPassword = new String(Base64.decode(encodedUserPassword));
        StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();

        Long userID = UserLocalServiceUtil.authenticateForBasic(10157L, "emailAddress", username, password);

        if (userID == 0L) {
            return null;
        }

        return UserLocalServiceUtil.getUser((long) userID);
    }

    private static List<Role> rolesForUser(User user) throws Exception {
        List<Role> roles = new ArrayList<Role>();
        if (user != null) {
            roles.addAll(RoleLocalServiceUtil.getUserRoles(user.getUserId()));
            if (user.getGroupIds().length != 0) {
                roles.addAll(RoleLocalServiceUtil.getUserRelatedRoles(user.getUserId(), user.getGroupIds()));
            }
        }
        return roles;
    }

    private Set<String> rolesAllowed(Method method) {
        RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
        Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
        return rolesSet;
    }
}