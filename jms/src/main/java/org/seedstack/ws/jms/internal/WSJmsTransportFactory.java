/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;

import javax.jms.Message;
import javax.xml.namespace.QName;
import java.util.Map;

interface WSJmsTransportFactory {
    /**
     * JmsClientTransport factory method for Guice assisted injection.
     *
     * @param packet  the packet.
     * @param headers the headers.
     * @return the instance.
     */
    JmsClientTransport createJmsClientTransport(Packet packet, Map<String, String> headers);

    /**
     * JmsTransportTube factory method for Guice assisted injection.
     *
     * @param codec the codec.
     * @param serviceName the service name.
     * @return the instance.
     */
    JmsTransportTube createJmsTransportTube(Codec codec, QName serviceName);

    /**
     * JmsServerTransport factory method for Guice assisted injection.
     *
     * @param requestMessage the request message.
     * @param uri            the uri.
     * @return the instance.
     */
    JmsServerTransport createJmsServerTransport(Message requestMessage, SoapJmsUri uri);
}
