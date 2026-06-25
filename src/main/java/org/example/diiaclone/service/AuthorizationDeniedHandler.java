package org.example.diiaclone.service;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationDeniedHandler
        implements MethodAuthorizationDeniedHandler {

    private static final Logger log =
            LoggerFactory.getLogger(AuthorizationDeniedHandler.class);

    @Override
    public Object handleDeniedInvocation(
            MethodInvocation methodInvocation,
            AuthorizationResult authorizationResult) {

        String method = methodInvocation.getMethod().getName();
        log.warn("Access denied for method={} — returning safe default", method);

        return null;
    }
}

