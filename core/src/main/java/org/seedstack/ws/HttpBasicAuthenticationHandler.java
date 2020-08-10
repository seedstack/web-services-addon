/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.seedstack.seed.security.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This handler can process HTTP basic authentication headers and authenticate the subject with Seed security (Shiro).
 * Designed to be used in a standalone environment since you would prefer to handler basic authentication with a Web
 * security filter in a Servlet environment.
 */
public final class HttpBasicAuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpBasicAuthenticationHandler.class);

    @Inject
    private static org.apache.shiro.mgt.SecurityManager securityManager;

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(final SOAPMessageContext messageContext) {
        final Boolean outbound = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (!outbound) {
            Map<String, List<String>> httpHeaders = (Map<String, List<String>>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
            if (httpHeaders != null) {
                List<String> authorizationList = httpHeaders.get("Authorization");
                if (authorizationList != null && !authorizationList.isEmpty()) {
                    String authorization = Base64.decodeToString(authorizationList.get(0).substring(6));
                    String[] credentials = authorization.split(":");
                    if (credentials.length == 2) {
                        Subject subject = new Subject.Builder(securityManager).buildSubject();
                        try {
                            subject.login(new UsernamePasswordToken(credentials[0], credentials[1]));
                        } catch (org.apache.shiro.authc.AuthenticationException e) { // NOSONAR
                            messageContext.put(MessageContext.HTTP_RESPONSE_CODE, 401); // TODO review code
                            return false;
                        } catch (Exception e) {
                            LOGGER.error("Authentication error", e);
                            return false;
                        }

                        ThreadContext.bind(subject);
                        ThreadContext.bind(securityManager);

                        return true;
                    }
                }
            }
            throw new AuthenticationException();
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
        ThreadContext.unbindSubject();
        ThreadContext.unbindSecurityManager();
    }
}
