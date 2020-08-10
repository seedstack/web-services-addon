/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;

import com.sun.xml.wss.RealmAuthenticationAdapter;
import org.seedstack.coffig.Config;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Config("webServices")
public class WebServicesConfig {
    private Map<String, EndpointConfig> endpoints = new HashMap<>();
    @NotNull
    private Class<? extends RealmAuthenticationAdapter> realmAuthenticationAdapter = DefaultRealmAuthenticationAdapter.class;

    public Map<String, EndpointConfig> getEndpoints() {
        return Collections.unmodifiableMap(endpoints);
    }

    public WebServicesConfig addEndpoint(String name, EndpointConfig endpointConfig) {
        this.endpoints.put(name, endpointConfig);
        return this;
    }

    public Class<? extends RealmAuthenticationAdapter> getRealmAuthenticationAdapter() {
        return realmAuthenticationAdapter;
    }

    public WebServicesConfig setRealmAuthenticationAdapter(Class<? extends RealmAuthenticationAdapter> realmAuthenticationAdapter) {
        this.realmAuthenticationAdapter = realmAuthenticationAdapter;
        return this;
    }

    public static class EndpointConfig {
        @NotNull
        private Class<?> implementation;
        private String wsdl;
        private String url;
        private String externalMetadata;
        private String serviceName;
        private String portName;
        private String binding;
        private Boolean enableMtom;
        private Integer mtomThreshold;
        private String dataBindingMode;

        public Class<?> getImplementation() {
            return implementation;
        }

        public EndpointConfig setImplementation(Class<?> implementation) {
            this.implementation = implementation;
            return this;
        }

        public String getWsdl() {
            return wsdl;
        }

        public EndpointConfig setWsdl(String wsdl) {
            this.wsdl = wsdl;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public EndpointConfig setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getExternalMetadata() {
            return externalMetadata;
        }

        public EndpointConfig setExternalMetadata(String externalMetadata) {
            this.externalMetadata = externalMetadata;
            return this;
        }

        public String getServiceName() {
            return serviceName;
        }

        public EndpointConfig setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public String getPortName() {
            return portName;
        }

        public EndpointConfig setPortName(String portName) {
            this.portName = portName;
            return this;
        }

        public String getBinding() {
            return binding;
        }

        public EndpointConfig setBinding(String binding) {
            this.binding = binding;
            return this;
        }

        public Boolean getEnableMtom() {
            return enableMtom;
        }

        public EndpointConfig setEnableMtom(Boolean enableMtom) {
            this.enableMtom = enableMtom;
            return this;
        }

        public Integer getMtomThreshold() {
            return mtomThreshold;
        }

        public EndpointConfig setMtomThreshold(Integer mtomThreshold) {
            this.mtomThreshold = mtomThreshold;
            return this;
        }

        public String getDataBindingMode() {
            return dataBindingMode;
        }

        public EndpointConfig setDataBindingMode(String dataBindingMode) {
            this.dataBindingMode = dataBindingMode;
            return this;
        }
    }
}
