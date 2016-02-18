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

import javax.security.auth.Subject;

/**
 * This {@link com.sun.xml.wss.RealmAuthenticationAdapter} always validates authentication without any check, effectively
 * disabling security.
 *
 * @author adrien.lauer@mpsa.com
 */
public class NoSecurityRealmAuthenticationAdapter extends RealmAuthenticationAdapter {
    @Override
    public boolean authenticate(Subject callerSubject, String username, String password) throws XWSSecurityException {
        return true;
    }
}
