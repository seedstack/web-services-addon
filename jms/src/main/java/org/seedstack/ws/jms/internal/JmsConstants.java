/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

final class JmsConstants {
    private JmsConstants() {
        // hide default constructor
    }

    static final String REQUEST_URI = "SOAPJMS_requestURI";
    static final String BINDING_VERSION = "SOAPJMS_bindingVersion";
    static final String SOAP_ACTION = "SOAPJMS_soapAction";
    static final String TARGET_SERVICE = "SOAPJMS_targetService";
    static final String CONTENT_TYPE_PROPERTY = "SOAPJMS_contentType";
    static final String ENCODING_PROPERTY = "SOAPJMS_contentEncoding";
    static final String IS_FAULT = "SOAPJMS_isFault";

    static final String JMS_REQUEST_HEADERS = "JMS-RQ-Headers";
}
