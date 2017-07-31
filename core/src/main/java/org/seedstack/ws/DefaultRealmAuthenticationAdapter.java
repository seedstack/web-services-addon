/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;

import com.sun.xml.wss.RealmAuthenticationAdapter;
import com.sun.xml.wss.XWSSecurityException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.security.auth.Subject;

/**
 * This {@link RealmAuthenticationAdapter} implementation will authenticate the incoming request subject with Seed
 * security (Shiro). This is the default.
 */
public class DefaultRealmAuthenticationAdapter extends RealmAuthenticationAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRealmAuthenticationAdapter.class);
    private final org.apache.shiro.mgt.SecurityManager securityManager;

    @Inject
    public DefaultRealmAuthenticationAdapter(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public boolean authenticate(Subject callerSubject, String username, String password) throws XWSSecurityException {
        org.apache.shiro.subject.Subject subject = new org.apache.shiro.subject.Subject.Builder(securityManager).buildSubject();
        try {
            subject.login(new UsernamePasswordToken(username, password));
            ThreadContext.bind(subject);
            ThreadContext.bind(securityManager);
            return true;
        } catch (org.apache.shiro.authc.AuthenticationException e) {
            LOGGER.debug("Rejected authentication for username {}", username, e);
            return false;
        } catch (Exception e) {
            LOGGER.error("WS endpoint authentication error", e);
            return false;
        }
    }
}
