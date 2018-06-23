package com.ail.core.security.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpStatus;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;


public class JwtSecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        String bearerToken = extractBearerToken((HttpServletRequest)servletRequest);

        try {
            Algorithm algorithm = Algorithm.HMAC256(System.getProperty("com.ail.core.security.key"));

            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("OU")
                .build();
            verifier.verify(bearerToken);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (UnsupportedEncodingException exception){
            //UTF-8 encoding not supported
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.SC_FORBIDDEN);
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.SC_FORBIDDEN);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        int index = authorizationHeader.indexOf(" ");
        return authorizationHeader.substring(index + 1, authorizationHeader.length());
    }
}
