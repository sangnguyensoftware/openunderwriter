package com.ail.service.login;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;

@Path("/")
public class LoginService {

    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response login(SecurityRequest request)  {
        try {
            Algorithm algorithm = Algorithm.HMAC256(System.getProperty("com.ail.core.security.key"));

            DateFormat timestamp = new SimpleDateFormat("dd-MM-YYY HH:mm:ss");

            if (request != null) {
                long id = authenticate(request.getUsername(), request.getPassword());
                if ( id > 0 ) {
                    Date expires = new Date();
                    expires.setTime(expires.getTime() + (10 * 60 * 60 * 1000));

                    return Response.ok(JWT.create().withIssuer("OU")
                                               .withExpiresAt(expires)
                                               .withClaim("id", String.valueOf(id))
                                               .withClaim("username", request.getUsername())
                                               .withClaim("timestamp", timestamp.format(new Date()))
                                               .sign(algorithm)).build();
                }
            }
        } catch (IllegalArgumentException e) {
            // Ignore and report unauthorized
        } catch (UnsupportedEncodingException e) {
            // Ignore and report unauthorized
        }
        return Response.status(Status.UNAUTHORIZED).build();
    }

    private long authenticate(final String username, final String password) {
        try {
            return UserLocalServiceUtil.authenticateForBasic(10157L, "emailAddress", username, password);
        } catch (PortalException e) {
            // Ignore and report unauthorized
        } catch (SystemException e) {
            // Ignore and report unauthorized
        }
        return -1;
    }
}