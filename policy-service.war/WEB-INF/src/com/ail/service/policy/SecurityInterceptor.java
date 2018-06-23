package com.ail.service.policy;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.util.List;

import javax.ws.rs.WebApplicationException;
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
import com.ail.core.CoreProxy;
import com.ail.core.product.service.SecurityProcessor;
import com.ail.core.product.service.ServiceRequestUrl;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;

@Provider
@ServerInterceptor
public class SecurityInterceptor implements PreProcessInterceptor {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", SC_UNAUTHORIZED, new Headers<>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", SC_FORBIDDEN, new Headers<>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", SC_INTERNAL_SERVER_ERROR, new Headers<>());;

    @Override
    public ServerResponse preProcess(final HttpRequest request, final ResourceMethod resourceMethod) throws Failure, WebApplicationException {

        String userPassword = "";

        try {
            List<String> authorization = request.getHttpHeaders().getRequestHeader(AUTHORIZATION_PROPERTY);

            if (authorization != null && !authorization.isEmpty()) {
                String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME, "");
                userPassword = new String(Base64.decode(encodedUserPassword));
            }

            ServiceRequestUrl serviceRequestUrl = new ServiceRequestUrlImpl(request.getPreprocessedPath());

            SecurityProcessor impl = new SecurityProcessor(serviceRequestUrl, userPassword);

            switch (impl.preProcessRequest()) {
            case SC_FORBIDDEN:
                return ACCESS_FORBIDDEN;
            case SC_UNAUTHORIZED:
                return ACCESS_DENIED;
            case SC_ACCEPTED:
                if (impl.getUser() != null) {
                    PermissionThreadLocal.setPermissionChecker(PermissionCheckerFactoryUtil.create(impl.getUser()));
                }
                CoreContext.setRemoteUser(impl.getRemoteUser());
                return null;
            case SC_INTERNAL_SERVER_ERROR:
                return SERVER_ERROR;
            default:
                return ACCESS_DENIED;
            }
        } catch (Exception e) {
            new CoreProxy().logError("Error processing product service request", e);
            return SERVER_ERROR;
        }
    }
}