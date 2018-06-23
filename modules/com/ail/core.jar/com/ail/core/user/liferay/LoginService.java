/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.ail.core.user.liferay;

import static com.ail.core.encryption.Encryptor.encrypt;
import static com.liferay.portal.kernel.util.CookieKeys.COMPANY_ID;
import static com.liferay.portal.kernel.util.CookieKeys.ID;
import static com.liferay.portal.kernel.util.CookieKeys.LOGIN;
import static com.liferay.portal.kernel.util.CookieKeys.PASSWORD;
import static com.liferay.portal.kernel.util.CookieKeys.REMEMBER_ME;
import static com.liferay.portal.kernel.util.CookieKeys.SCREEN_NAME;
import static com.liferay.portal.kernel.util.CookieKeys.USER_UUID;
import static com.liferay.portal.kernel.util.CookieKeys.addCookie;
import static com.liferay.portal.kernel.util.CookieKeys.getDomain;

import java.security.Key;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.encryption.EncryptorException;
import com.ail.core.user.LoginService.LoginServiceArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AuthenticatedUserUUIDStoreUtil;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 */
@ServiceImplementation
public class LoginService extends Service<LoginServiceArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getUsernameArg() == null || args.getUsernameArg().length() == 0) {
            throw new PreconditionException("args.getUsernameArg() == null || args.getUsernameArg().length() == 0");
        }

        if (args.getPasswordArg() == null || args.getPasswordArg().length() == 0) {
            throw new PreconditionException("args.getPasswordArg() == null || args.getPasswordArg().length() == 0");
        }

        if (CoreContext.getRequestWrapper() == null || CoreContext.getResponseWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequest() == null || CoreContext.getResponse() == null");
        }

        handleLogin();
    }

    void handleLogin() {
        try {
            String username = args.getUsernameArg();
            String password = args.getPasswordArg();

            HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(CoreContext.getRequestWrapper().getPortletRequest());
            HttpServletResponse httpResponse = PortalUtil.getHttpServletResponse(CoreContext.getResponseWrapper().getPortletResponse());

            HttpSession httpSession = httpRequest.getSession();

            Company requestsCompany = PortalUtil.getCompany(httpRequest);
            long requestsCompanyId = requestsCompany.getCompanyId();

            Map<String, String[]> headerMap = buildHeaderMap(httpRequest);
            Map<String, String[]> parameterMap = httpRequest.getParameterMap();
            Map<String, Object> resultsMap = new HashMap<>();

            int authResult = UserLocalServiceUtil.authenticateByEmailAddress(requestsCompanyId, username, password, headerMap, parameterMap, resultsMap);

            long userId = MapUtil.getLong(resultsMap, "userId", -1);

            switch (authResult) {
            case Authenticator.FAILURE:
            case Authenticator.DNE:
                args.setAuthenticationSucceededRet(false);
                break;
            case Authenticator.SKIP_LIFERAY_CHECK:
                args.setAuthenticationSucceededRet(true);
                break;
            case Authenticator.SUCCESS:
                args.setAuthenticationSucceededRet(true);
                initialiseSession(username, password, httpRequest, httpResponse, httpSession, requestsCompanyId, userId);
                break;
            }

       } catch (Exception e) {
            getCore().logError("Error processing authentication forward ", e);
        }
     }

    protected void initialiseSession(String username, String password, HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpSession httpSession, long requestsCompanyId, long userId)
            throws PortalException, SystemException, EncryptorException, Exception {
        // Set cookies
        String domain = getDomain(httpRequest);

        User user = UserLocalServiceUtil.getUserById(userId);

        String userIdString = String.valueOf(userId);

        Key companyKeyObj = PortalUtil.getCompany(httpRequest).getKeyObj();

        String userUUID = userIdString.concat(StringPool.PERIOD).concat(String.valueOf(System.nanoTime()));

        httpSession.setAttribute("j_username", userIdString);
        httpSession.setAttribute("j_password", user.getPassword());
        httpSession.setAttribute("j_remoteuser", userIdString);

        Cookie companyIdCookie = createCookie(COMPANY_ID, String.valueOf(requestsCompanyId), domain);
        Cookie idCookie = createCookie(ID, encrypt(companyKeyObj, userIdString), domain);
        Cookie passwordCookie = createCookie(PASSWORD, encrypt(companyKeyObj, password), domain);
        Cookie rememberMeCookie = createCookie(REMEMBER_ME, Boolean.TRUE.toString(), domain);
        Cookie userUUIDCookie = createCookie(USER_UUID, encrypt(companyKeyObj, userUUID), domain);

        userUUIDCookie.setPath(StringPool.SLASH);

        httpSession.setAttribute("USER_UUID", userUUID);

        int loginMaxAge = 31536000;

        if (args.getRememberMeArg()) {
            companyIdCookie.setMaxAge(loginMaxAge);
            idCookie.setMaxAge(loginMaxAge);
            passwordCookie.setMaxAge(loginMaxAge);
            rememberMeCookie.setMaxAge(loginMaxAge);
            userUUIDCookie.setMaxAge(loginMaxAge);
        } else {
            companyIdCookie.setMaxAge(-1);
            idCookie.setMaxAge(-1);
            passwordCookie.setMaxAge(-1);
            rememberMeCookie.setMaxAge(0);
            userUUIDCookie.setMaxAge(-1);
        }

        Cookie loginCookie = createCookie(LOGIN, username, domain);
        loginCookie.setMaxAge(loginMaxAge);

        Cookie screenNameCookie = createCookie(SCREEN_NAME, encrypt(companyKeyObj, user.getScreenName()), domain);
        screenNameCookie.setMaxAge(loginMaxAge);

        boolean secure = httpRequest.isSecure();

        if (secure && !StringUtil.equalsIgnoreCase(Http.HTTPS, null)) {

            Boolean httpsInitial = (Boolean) httpSession.getAttribute("HTTPS_INITIAL");

            if ((httpsInitial == null) || !httpsInitial.booleanValue()) {
                secure = false;
            }
        }

        addCookie(httpRequest, httpResponse, companyIdCookie, secure);
        addCookie(httpRequest, httpResponse, idCookie, secure);
        addCookie(httpRequest, httpResponse, userUUIDCookie, secure);

        if (args.getRememberMeArg()) {
            addCookie(httpRequest, httpResponse, loginCookie, secure);
            addCookie(httpRequest, httpResponse, passwordCookie, secure);
            addCookie(httpRequest, httpResponse, rememberMeCookie, secure);
            addCookie(httpRequest, httpResponse, screenNameCookie, secure);
        }

        AuthenticatedUserUUIDStoreUtil.register(userUUID);

        // redirect to where we already are to force a page refresh.
        httpResponse.sendRedirect(PortalUtil.getCurrentCompleteURL(httpRequest));
    }

    protected Cookie createCookie(String name, String value, String domain) {
        Cookie cookie=new Cookie(name, value);

        if (Validator.isNotNull(domain)) {
            cookie.setDomain(domain);
        }

        cookie.setPath(StringPool.SLASH);

        return cookie;
    }

    Map<String,String[]> buildHeaderMap(HttpServletRequest httpRequest) {
        Map<String, String[]> headerMap = new HashMap<>();

        Enumeration<String> enu1 = httpRequest.getHeaderNames();

        while (enu1.hasMoreElements()) {
            String name = enu1.nextElement();

            Enumeration<String> enu2 = httpRequest.getHeaders(name);

            List<String> headers = new ArrayList<>();

            while (enu2.hasMoreElements()) {
                String value = enu2.nextElement();

                headers.add(value);
            }

            headerMap.put(name, headers.toArray(new String[headers.size()]));
        }

        return headerMap;
    }
}
