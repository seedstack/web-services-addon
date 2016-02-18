/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;


import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 * JAX-WS related utility class.
 *
 * @author emmanuel.vinel@mpsa.com
 * @author adrien.lauer@mpsa.com
 */
public final class WSUtils {
    private static final String SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    private WSUtils() {
    }

    /**
     * This methods creates a username token header from two strings.
     *
     * @param userName the username.
     * @param password the password.
     * @return the header.
     * @throws SOAPException if unable to create the header.
     */
    public static Header createUserNameTokenHeader(String userName, String password) throws SOAPException {
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        QName securityQName = new QName(SECURITY_NAMESPACE, "Security");
        SOAPElement security = soapFactory.createElement(securityQName);
        QName tokenQName = new QName(SECURITY_NAMESPACE, "UsernameToken");
        SOAPElement token = soapFactory.createElement(tokenQName);
        QName userQName = new QName(SECURITY_NAMESPACE, "Username");
        SOAPElement username = soapFactory.createElement(userQName);
        username.addTextNode(userName);
        QName passwordQName = new QName(SECURITY_NAMESPACE, "Password");
        SOAPElement soapElementPassword = soapFactory.createElement(passwordQName);
        soapElementPassword.addTextNode(password);

        token.addChildElement(username);
        token.addChildElement(soapElementPassword);
        security.addChildElement(token);
        return Headers.create(security);
    }
}
