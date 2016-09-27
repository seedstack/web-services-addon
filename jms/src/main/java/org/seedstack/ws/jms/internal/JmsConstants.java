/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

public final class JmsConstants {
    private JmsConstants() {
        // hide default constructor
    }

    // JMS headers
    public static final String REQUEST_URI = "SOAPJMS_requestURI";
    public static final String BINDING_VERSION = "SOAPJMS_bindingVersion";
    public static final String SOAP_ACTION = "SOAPJMS_soapAction";
    public static final String TARGET_SERVICE = "SOAPJMS_targetService";
    public static final String CONTENT_TYPE_PROPERTY = "SOAPJMS_contentType";
    public static final String ENCODING_PROPERTY = "SOAPJMS_contentEncoding";
    public static final String IS_FAULT = "SOAPJMS_isFault";

    // Constants used in BindingProvider#getRequestContext() and/or BindingProvider#getResponseContext()
    public static final String JMS_REQUEST_HEADERS = "JMS-RQ-Headers";
    public static final String JMS_REQUEST_MESSAGE_ID = "JMS-RQ-MessageId";
    public static final String JMS_REPLY_MESSAGE_ID = "JMS-RP-MessageId";
    public static final String JMS_CORRELATION_ID = "JMS-CorrelationId";
}
