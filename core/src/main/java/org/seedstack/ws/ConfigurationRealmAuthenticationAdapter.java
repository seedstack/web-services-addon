/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;

import org.seedstack.seed.Configuration;
import org.seedstack.ws.internal.WSPlugin;
import com.sun.xml.wss.RealmAuthenticationAdapter;
import com.sun.xml.wss.XWSSecurityException;

import javax.security.auth.Subject;

/**
 * This {@link com.sun.xml.wss.RealmAuthenticationAdapter} validates authentication against configured username
 * (org.seedstack.ws.wss.username property) and password (org.seedstack.ws.wss.password property).
 *
 * @author adrien.lauer@mpsa.com
 */
public class ConfigurationRealmAuthenticationAdapter extends RealmAuthenticationAdapter {
    @Configuration(WSPlugin.CONFIGURATION_PREFIX + ".wss.username")
    private String configuredUsername;

    @Configuration(WSPlugin.CONFIGURATION_PREFIX + ".wss.password")
    private String configuredPassword;

    @Override
    public boolean authenticate(Subject callerSubject, String username, String password) throws XWSSecurityException {
        return configuredUsername.equals(username) && configuredPassword.equals(configuredPassword);
    }
}
