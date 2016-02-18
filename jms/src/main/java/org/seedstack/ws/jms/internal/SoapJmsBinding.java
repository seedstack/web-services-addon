/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

import com.sun.xml.ws.api.model.soap.SOAPBinding;

class SoapJmsBinding extends SOAPBinding {
    /**
     * A constant representing the identity of the SOAP 1.1 over HTTP binding.
     */
    static final String SOAPJMS_BINDING = "http://www.w3.org/2010/soapjms/";
}
