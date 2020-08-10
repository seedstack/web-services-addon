/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.server.Adapter;
import com.sun.xml.ws.api.server.TransportBackChannel;
import com.sun.xml.ws.api.server.WSEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JmsAdapter extends Adapter<JmsAdapter.JMSToolkit> {
    private static Logger LOGGER = LoggerFactory.getLogger(JmsAdapter.class);

    @Inject
    private WSJmsTransportFactory wsJmsTransportFactory;

    JmsAdapter(WSEndpoint endpoint) {
        super(endpoint);
    }

    void handle(Message requestMessage, SoapJmsUri uri) throws IOException, JMSException {
        JmsServerTransport connection = wsJmsTransportFactory.createJmsServerTransport(requestMessage, uri);

        JMSToolkit tk = pool.take();
        try {
            tk.handle(connection);
            connection.flush();
        } finally {
            pool.recycle(tk);
            connection.close();
        }
    }

    @Override
    protected JMSToolkit createToolkit() {
        return new JMSToolkit();
    }

    final class JMSToolkit extends Adapter.Toolkit implements TransportBackChannel {
        private final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
        private JmsServerTransport connection;

        private void handle(JmsServerTransport connection) throws IOException {
            this.connection = connection;

            InputStream in = connection.getInputStream();
            Packet packet = new Packet();
            codec.decode(in, connection.getRequestContentType(), packet);

            packet.invocationProperties.put(BindingProvider.USERNAME_PROPERTY, connection.getRequestUsername());
            packet.invocationProperties.put(BindingProvider.PASSWORD_PROPERTY, connection.getRequestPassword());

            try {
                packet = head.process(packet, connection, this);
            } catch (Exception e) {
                throw new WebServiceException("Error during message processing", e);
            }

            com.sun.xml.ws.api.message.Message packetMessage = packet.getMessage();
            if (packetMessage != null) {
                if (packetMessage.isFault() && !connection.canReply()) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ContentType faultContentType = codec.encode(packet, byteArrayOutputStream);
                    LOGGER.warn(
                            "A SOAP fault has been generated but cannot be returned to client:\n\t{}",
                            byteArrayOutputStream.toString(
                                    getCharsetFromContentType(faultContentType.getContentType())
                            )
                    );
                } else {
                    connection.setMustReply(true);

                    ContentType contentType = codec.getStaticContentType(packet);
                    if (contentType != null) {
                        connection.setResponseContentType(contentType.getContentType());
                        codec.encode(packet, connection.getOutputStream());
                    } else {
                        contentType = codec.encode(packet, connection.getOutputStream());
                        connection.setResponseContentType(contentType.getContentType());
                    }
                }
            }
        }

        @Override
        public void close() {
            connection.close();
        }

        private String getCharsetFromContentType(String contentType) {
            if (contentType == null)
                return null;

            Matcher m = charsetPattern.matcher(contentType);
            if (m.find()) {
                return m.group(1).trim().toUpperCase(Locale.ENGLISH);
            }
            return null;
        }
    }
}
