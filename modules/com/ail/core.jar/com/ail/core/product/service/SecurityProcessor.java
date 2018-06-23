package com.ail.core.product.service;

import static com.liferay.portal.model.CompanyConstants.AUTH_TYPE_EA;
import static com.liferay.portal.model.CompanyConstants.AUTH_TYPE_SN;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class SecurityProcessor {

    public static final long OPENUNDERWRITER_LIFERAY_COMPANY_ID = 10157L;
    private static final String DENY_ALL = "DenyAll";
    private static final String PERMIT_ALL = "PermitAll";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\S+@\\S+\\.\\S+$");

    private ServiceRequestUrl serviceRequestUrl;

    private String authorization;

    private User user;

    public SecurityProcessor(ServiceRequestUrl serviceRequestUrl, String authorization) {
        this.serviceRequestUrl = serviceRequestUrl;
        this.authorization = authorization;
    }

    public Integer preProcessRequest() throws PortalException, SystemException, PreconditionException {

        CoreProxy coreProxy = getCore();

        String permission = coreProxy.getParameterValue("SecuredServices." + serviceRequestUrl.resolveServiceName());

        if (permission == null) {
            coreProxy.logError("Registry does not define permissions for: " + serviceRequestUrl.resolveServiceName() + ". SC_UNAUTHORIZED assumed.");
            return SC_UNAUTHORIZED;
        }

        if (PERMIT_ALL.equals(permission)) {
            return SC_ACCEPTED;
        }

        if (DENY_ALL.equals(permission)) {
            return SC_FORBIDDEN;
        }

        this.user = authenticateUser(authorization);

        if (isAccessibleToUser(permission)) {
            return SC_ACCEPTED;
        }

        return SC_UNAUTHORIZED;
    }

    private boolean isAccessibleToUser(String permission) throws SystemException {
        return isAnyRolePermittedRole(rolesAllowed(permission), rolesForUser());
    }

    private boolean isAnyRolePermittedRole(Set<String> permittedRoles, List<Role> usersRoles) {
        for (String pRole : permittedRoles) {
            for (Role uRole : usersRoles) {
                if (pRole.equals(uRole.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private User authenticateUser(String authorization) throws PortalException, SystemException {

        if (StringUtils.isEmpty(authorization)) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(authorization, ":");
        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();

        String authType = AUTH_TYPE_SN;
        if (EMAIL_PATTERN.matcher(username).find()) {
            authType = AUTH_TYPE_EA;
        }

        Long userID = UserLocalServiceUtil.authenticateForBasic(OPENUNDERWRITER_LIFERAY_COMPANY_ID, authType, username, password);

        if (userID == 0L) {
            CoreContext.getCoreProxy().logWarning("Authentication failed for: " + username);
            return null;
        }

        return UserLocalServiceUtil.getUser((long) userID);
    }

    private List<Role> rolesForUser() throws SystemException {
        List<Role> roles = new ArrayList<>();
        if (this.user != null) {
            roles.addAll(RoleLocalServiceUtil.getUserRoles(this.user.getUserId()));
            if (this.user.getGroupIds().length != 0) {
                roles.addAll(RoleLocalServiceUtil.getUserRelatedRoles(this.user.getUserId(), this.user.getGroupIds()));
            }
        }
        return roles;
    }

    private Set<String> rolesAllowed(String permission) {
        Set<String> rolesSet = new HashSet<>(Arrays.asList(permission.split(",")));
        return rolesSet;
    }

    public Long getRemoteUser() {
        return (this.user != null) ? user.getUserId() : null;
    }

    public User getUser() {
        return user;
    }

    private CoreProxy getCore() throws PreconditionException {
        CoreContext.setCoreProxy(new CoreProxy(serviceRequestUrl.resolveNamespace()));
        return CoreContext.getCoreProxy();
    }
}
