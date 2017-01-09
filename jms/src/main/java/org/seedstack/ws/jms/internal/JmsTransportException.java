/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

import javax.xml.ws.WebServiceException;

/**
 * Exception for all JMS transport errors.
 */
public class JmsTransportException extends WebServiceException {
    /**
     * Create the exception.
     *
     * @param message the exception message.
     */
    public JmsTransportException(String message) {
        super(message);
    }

    /**
     * Create the exception.
     *
     * @param message the exception message.
     * @param cause   the exception cause.
     */
    public JmsTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
