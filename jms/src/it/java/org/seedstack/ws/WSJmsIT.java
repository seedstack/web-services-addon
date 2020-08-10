/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.ws.jms.internal.JmsConstants;
import org.seedstack.ws.jms.internal.JmsTransportException;
import org.seedstack.wsdl.seed.calculator.CalculatorService;
import org.seedstack.wsdl.seed.calculator.CalculatorWS;

import javax.xml.ws.BindingProvider;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SeedITRunner.class)
public class WSJmsIT {
    @Configuration("sys.seed\\.ws\\.port")
    private int wsPort;

    @Test
    public void http_endpoint_is_working_too() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapHttpPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:" + wsPort + "/ws/calculator");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jndi_queue() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jndi_topic() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicTopics/TEST.TOPIC?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jms_config_queue() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:queue:TEST.QUEUE?connectionName=myConnection");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jms_config_topic() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:topic:TEST.TOPIC?connectionName=myConnection");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void one_way_operation() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false");
        calculatorWS.clear();
    }

    @Test
    public void one_way_operation_with_fault() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false");
        calculatorWS.clearWithFailure();
    }

    @Test
    public void persistent_delivery_mode() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&deliveryMode=PERSISTENT");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void non_persistent_delivery_mode() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&deliveryMode=NON_PERSISTENT");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test(expected = JmsTransportException.class)
    public void wrong_delivery_mode() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&deliveryMode=WRONG");
        calculatorWS.add(1, 1);
        fail("should have failed");
    }

    @Test
    public void jndi_reply_queue() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&replyToName=dynamicQueues/TEST.REPLY.QUEUE");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jndi_reply_topic() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&replyToName=dynamicTopics/TEST.REPLY.TOPIC");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jms_config_reply_queue_with_queue_destination() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:queue:TEST.QUEUE?connectionName=myConnection&replyToName=TEST.REPLY.QUEUE");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jms_config_reply_queue_with_topic_destination() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:topic:TEST.TOPIC?connectionName=myConnection&replyToName=TEST.REPLY.QUEUE");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void jms_config_reply_topic_with_queue_destination() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:queue:TEST.QUEUE?connectionName=myConnection&topicReplyToName=TEST.REPLY.TOPIC");
        calculatorWS.add(1, 1);
    }

    @Test
    public void jms_config_reply_topic_with_topic_destination() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:topic:TEST.TOPIC?connectionName=myConnection&topicReplyToName=TEST.REPLY.TOPIC");
        calculatorWS.add(1, 1);
    }

    @Test
    public void using_text_message() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&deliveryMode=NON_PERSISTENT&messageType=text");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
    }

    @Test
    public void message_identifiers_are_accessible() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&deliveryMode=NON_PERSISTENT&messageType=text");
        assertThat(calculatorWS.add(1, 1)).isEqualTo(2);
        Map<String, Object> responseContext = ((BindingProvider) calculatorWS).getResponseContext();
        assertThat(((String) responseContext.get(JmsConstants.JMS_REQUEST_MESSAGE_ID))).isNotEmpty();
        assertThat(((String) responseContext.get(JmsConstants.JMS_REPLY_MESSAGE_ID))).isNotEmpty();
        assertThat(((String) responseContext.get(JmsConstants.JMS_CORRELATION_ID))).isNotEmpty();
    }

    @Test
    public void message_id_is_accessible_with_one_way() {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapJmsPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false&deliveryMode=NON_PERSISTENT&messageType=text");
        calculatorWS.clear();
        Map<String, Object> responseContext = ((BindingProvider) calculatorWS).getResponseContext();
        assertThat(((String) responseContext.get(JmsConstants.JMS_REQUEST_MESSAGE_ID))).isNotEmpty();
    }
}
