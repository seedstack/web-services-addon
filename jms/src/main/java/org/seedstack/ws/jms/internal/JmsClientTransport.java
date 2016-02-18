/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

import com.google.common.cache.LoadingCache;
import com.google.inject.assistedinject.Assisted;
import com.sun.xml.ws.api.message.Packet;
import org.seedstack.seed.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.ws.WebServiceException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

class JmsClientTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsClientTransport.class);

    public static final int DEFAULT_TIMEOUT_INTERVAL = 30000;
    public static final String MESSAGE_TYPE_BYTES = "bytes";
    public static final String MESSAGE_TYPE_TEXT = "text";

    private final int responseTimeout;
    private final LoadingCache<SoapJmsUri, Connection> connectionCache;
    private final Packet packet;
    private final Map<String, String> requestHeaders;
    private Message replyMessage;

    @Inject
    JmsClientTransport(Application application, LoadingCache<SoapJmsUri, Connection> connectionCache, @Assisted Packet packet, @Assisted Map<String, String> requestHeaders) {
        this.packet = packet;
        this.requestHeaders = requestHeaders;
        this.responseTimeout = application.getConfiguration().getInt("org.seedstack.ws.jms.client-timeout", DEFAULT_TIMEOUT_INTERVAL);
        this.connectionCache = connectionCache;
    }

    byte[] sendMessage(byte[] sndPacket) {
        Session session = null;
        MessageConsumer messageConsumer = null;

        URI uri = packet.endpointAddress.getURI();
        SoapJmsUri destinationAddress = SoapJmsUri.parse(uri);

        Connection connection;
        try {
            connection = connectionCache.get(destinationAddress);
        } catch (ExecutionException e) {
            throw new JmsTransportException("Failed to open JMS transport to " + destinationAddress, e);
        }

        try {
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(SoapJmsUri.getDestination(destinationAddress, session));

                String deliveryMode = destinationAddress.getParameter("deliveryMode");
                if (deliveryMode != null) {
                    if ("PERSISTENT".equals(deliveryMode)) {
                        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                    } else if ("NON_PERSISTENT".equals(deliveryMode)) {
                        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    } else {
                        throw new JmsTransportException("Unknown delivery mode " + deliveryMode);
                    }
                }

                String timeToLive = destinationAddress.getParameter("timeToLive");
                if (timeToLive != null) {
                    try {
                        producer.setTimeToLive(Long.parseLong(timeToLive));
                    } catch (NumberFormatException e) {
                        throw new JmsTransportException("Unable to set message time to live", e);
                    }
                }

                String priority = destinationAddress.getParameter("priority");
                if (priority != null) {
                    try {
                        producer.setPriority(Integer.parseInt(priority));
                    } catch (NumberFormatException e) {
                        throw new JmsTransportException("Unable to set message priority", e);
                    }
                }

                Message message;
                String messageType = destinationAddress.getParameter("messageType");
                if (messageType == null || MESSAGE_TYPE_BYTES.equalsIgnoreCase(messageType)) {
                    message = session.createBytesMessage();
                } else if (MESSAGE_TYPE_TEXT.equalsIgnoreCase(messageType)) {
                    message = session.createTextMessage();
                } else {
                    throw new JmsTransportException(String.format("Unknown message type %s, specify '%s' or '%s'", messageType, MESSAGE_TYPE_BYTES, MESSAGE_TYPE_TEXT));
                }

                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    message.setStringProperty(entry.getKey(), entry.getValue());
                }

                if (packet.expectReply) {
                    String correlationId = UUID.randomUUID().toString();
                    Destination replyDestination = SoapJmsUri.getReplyToDestination(destinationAddress, session);

                    if (replyDestination == null) {
                        replyDestination = session.createTemporaryQueue();
                    }

                    message.setJMSCorrelationID(correlationId);
                    message.setJMSReplyTo(replyDestination);
                    messageConsumer = session.createConsumer(replyDestination, String.format("JMSCorrelationID='%s'", correlationId));
                }

                if (message instanceof BytesMessage) {
                    ((BytesMessage) message).writeBytes(sndPacket);
                } else {
                    // doesn't support binary attachments as it doesn't try to encode them
                    ((TextMessage) message).setText(new String(sndPacket, findCharset(requestHeaders.get(JmsConstants.CONTENT_TYPE_PROPERTY))));
                }

                producer.send(message);
                producer.close();
            } catch (Exception e) {
                throw new JmsTransportException("Error sending JMS message", e);
            }

            if (messageConsumer != null) {
                try {
                    replyMessage = messageConsumer.receive(responseTimeout);

                    if (replyMessage instanceof BytesMessage) {
                        BytesMessage bytesReplyMessage = (BytesMessage) replyMessage;
                        byte[] buffer = new byte[(int) bytesReplyMessage.getBodyLength()];
                        bytesReplyMessage.readBytes(buffer);
                        return buffer;
                    } else if (replyMessage instanceof TextMessage) {
                        // doesn't support encoded text messages (like base64)
                        return ((TextMessage) replyMessage).getText().getBytes(findCharset(getResponseContentType()));
                    } else {
                        throw new JmsTransportException("No suitable reply received");
                    }
                } catch (Exception e) {
                    throw new JmsTransportException("Error receiving JMS message", e);
                }
            }
        } finally {
            try {
                if (messageConsumer != null) {
                    messageConsumer.close();
                }

                if (session != null) {
                    session.close();
                }
            } catch (JMSException e) {
                LOGGER.warn("Unable to close JMS transport after WS send/receive", e);
            }
        }

        return null;
    }

    private String findCharset(String contentType) {
        String charset;
        try {
            charset = new MimeType(contentType).getParameter("charset");
        } catch (MimeTypeParseException e) {
            charset = "UTF-8";
        }
        return charset;
    }

    String getResponseContentType() {
        if (replyMessage != null) {
            try {
                return replyMessage.getStringProperty(JmsConstants.CONTENT_TYPE_PROPERTY);
            } catch (JMSException e) {
                throw new WebServiceException("Error trying to get content type from response", e);
            }
        } else {
            throw new WebServiceException("No response available");
        }
    }
}
