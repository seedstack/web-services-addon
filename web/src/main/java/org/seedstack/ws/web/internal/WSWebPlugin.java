/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.web.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.PluginException;
import io.nuun.kernel.api.plugin.context.Context;
import io.nuun.kernel.api.plugin.context.InitContext;
import org.seedstack.seed.core.SeedRuntime;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.web.spi.FilterDefinition;
import org.seedstack.seed.web.spi.ListenerDefinition;
import org.seedstack.seed.web.spi.ServletDefinition;
import org.seedstack.seed.web.spi.WebProvider;
import org.seedstack.ws.internal.EndpointDefinition;
import org.seedstack.ws.internal.WSPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.xml.ws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This plugin enables Web integration of JAX-WS, disabling the standalone WS plugin in the process.
 */
public class WSWebPlugin extends AbstractSeedPlugin implements WebProvider {
    private static final List<String> SUPPORTED_BINDINGS = ImmutableList.of(SOAPBinding.SOAP11HTTP_BINDING, SOAPBinding.SOAP12HTTP_BINDING, SOAPBinding.SOAP11HTTP_MTOM_BINDING, SOAPBinding.SOAP12HTTP_MTOM_BINDING);
    private static final Logger LOGGER = LoggerFactory.getLogger(WSWebPlugin.class);

    private final Map<String, EndpointDefinition> endpointDefinitions = new HashMap<>();
    private final List<String> endpointUrls = new ArrayList<>();
    private WSPlugin wsPlugin;
    private ServletContext servletContext;
    private ServletAdapterList servletAdapters;
    private WSServletDelegate wsServletDelegate;
    private WSWebModule wsWebModule;

    @Override
    public String name() {
        return "ws-web";
    }

    @Override
    public Collection<Class<?>> dependencies() {
        return Lists.newArrayList(WSPlugin.class);
    }

    @Override
    public void setup(SeedRuntime seedRuntime) {
        servletContext = seedRuntime.contextAs(ServletContext.class);
    }

    @Override
    public InitState initialize(InitContext initContext) {
        wsPlugin = initContext.dependency(WSPlugin.class);

        // Always disable standalone publishing (it will not work with the servlet specific configuration anyway)
        wsPlugin.disableEndpointPublishing();

        if (servletContext != null) {
            for (Map.Entry<String, EndpointDefinition> wsEndpointEntry : wsPlugin.getEndpointDefinitions(SUPPORTED_BINDINGS).entrySet()) {
                String endpointName = wsEndpointEntry.getKey();
                EndpointDefinition endpointDefinition = wsEndpointEntry.getValue();

                String urlString = endpointDefinition.getUrl();
                if (urlString == null || urlString.isEmpty()) {
                    throw new PluginException("url property is mandatory for WS endpoint {}", endpointName);
                }

                endpointUrls.add(urlString);
                endpointDefinitions.put(endpointName, endpointDefinition);
            }

            wsWebModule = new WSWebModule();
        }

        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return wsWebModule;
    }

    @Override
    public void start(Context context) {
        if (servletContext != null) {
            servletAdapters = new ServletAdapterList(servletContext);

            for (Map.Entry<String, EndpointDefinition> endpointDefinitionEntry : endpointDefinitions.entrySet()) {
                LOGGER.info("Exposing WS endpoint {} on {}", endpointDefinitionEntry.getKey(), endpointDefinitionEntry.getValue().getUrl());

                servletAdapters.createAdapter(
                        endpointDefinitionEntry.getKey(),
                        endpointDefinitionEntry.getValue().getUrl(),
                        wsPlugin.createWSEndpoint(endpointDefinitionEntry.getValue(), new SeedServletContainer(servletContext))
                );
            }

            wsServletDelegate = new WSServletDelegate(servletAdapters, servletContext);
            servletContext.setAttribute(WSServlet.JAXWS_RI_RUNTIME_INFO, wsServletDelegate);
        }
    }

    @Override
    public void stop() {
        if (wsServletDelegate != null) {
            wsServletDelegate.destroy();
        }

        if (servletAdapters != null) {
            for (ServletAdapter servletAdapter : servletAdapters) {
                LOGGER.info("Disposing WS endpoint {}", servletAdapter.getName());
                servletAdapter.getEndpoint().dispose();
            }
        }
    }

    @Override
    public List<ServletDefinition> servlets() {
        ServletDefinition wsServlet = new ServletDefinition("ws-metro", WSServlet.class);
        wsServlet.setAsyncSupported(true);
        for (String endpointUrl : endpointUrls) {
            wsServlet.addMappings(endpointUrl);
        }
        return Lists.newArrayList(wsServlet);
    }

    @Override
    public List<FilterDefinition> filters() {
        return null;
    }

    @Override
    public List<ListenerDefinition> listeners() {
        return null;
    }
}