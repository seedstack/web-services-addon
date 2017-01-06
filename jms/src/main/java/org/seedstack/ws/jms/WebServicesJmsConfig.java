/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms;

import org.seedstack.coffig.Config;
import org.seedstack.jms.JmsConfig.ConnectionConfig;
import org.seedstack.jms.spi.MessagePoller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Config("webServices")
public class WebServicesJmsConfig {
    private Map<String, JmsEndpointConfig> endpoints = new HashMap<>();
    private JmsConfig jms = new JmsConfig();

    public Map<String, JmsEndpointConfig> getEndpoints() {
        return Collections.unmodifiableMap(endpoints);
    }

    public WebServicesJmsConfig addEndpoint(String name, JmsEndpointConfig endpointConfig) {
        this.endpoints.put(name, endpointConfig);
        return this;
    }

    public JmsConfig jms() {
        return jms;
    }

    public static class JmsEndpointConfig {
        private JmsConfig jms = new JmsConfig();

        public JmsConfig jms() {
            return jms;
        }

        public static class JmsConfig {
            private ConnectionConfig connection = new ConnectionConfig();
            private boolean transactional = true;
            private String selector;
            private Class<? extends MessagePoller> messagePoller;

            public ConnectionConfig getConnection() {
                return connection;
            }

            public JmsConfig setConnection(ConnectionConfig connection) {
                this.connection = connection;
                return this;
            }

            public boolean isTransactional() {
                return transactional;
            }

            public void setTransactional(boolean transactional) {
                this.transactional = transactional;
            }

            public String getSelector() {
                return selector;
            }

            public JmsConfig setSelector(String selector) {
                this.selector = selector;
                return this;
            }

            public Class<? extends MessagePoller> getMessagePoller() {
                return messagePoller;
            }

            public JmsConfig setMessagePoller(Class<? extends MessagePoller> messagePoller) {
                this.messagePoller = messagePoller;
                return this;
            }
        }
    }

    @Config("jms")
    public static class JmsConfig {
        private static final int DEFAULT_TIMEOUT_INTERVAL = 30000;
        private int clientTimeout = DEFAULT_TIMEOUT_INTERVAL;
        private CacheConfig cache = new CacheConfig();

        public int getClientTimeout() {
            return clientTimeout;
        }

        public JmsConfig setClientTimeout(int clientTimeout) {
            this.clientTimeout = clientTimeout;
            return this;
        }

        public CacheConfig cache() {
            return cache;
        }

        @Config("cache")
        public static class CacheConfig {
            private static final int DEFAULT_CACHE_CONCURRENCY = 4;
            private static final int DEFAULT_CACHE_MAX_SIZE = 16;

            private int maxSize = DEFAULT_CACHE_MAX_SIZE;
            private int initialSize = maxSize / 4;
            private int concurrencyLevel = DEFAULT_CACHE_CONCURRENCY;

            public int getInitialSize() {
                return initialSize;
            }

            public CacheConfig setInitialSize(int initialSize) {
                this.initialSize = initialSize;
                return this;
            }

            public int getMaxSize() {
                return maxSize;
            }

            public CacheConfig setMaxSize(int maxSize) {
                this.maxSize = maxSize;
                return this;
            }

            public int getConcurrencyLevel() {
                return concurrencyLevel;
            }

            public CacheConfig setConcurrencyLevel(int concurrencyLevel) {
                this.concurrencyLevel = concurrencyLevel;
                return this;
            }
        }
    }
}