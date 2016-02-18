/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
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
import io.nuun.kernel.core.AbstractPlugin;
import org.seedstack.seed.web.internal.WebPlugin;
import org.seedstack.ws.internal.EndpointDefinition;
import org.seedstack.ws.internal.WSPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.xml.ws.soap.SOAPBinding;
import java.util.*;

/**
 * This plugin enables Web integration of JAX-WS, disabling the standalone WS plugin in the process.
 *
 * @author emmanuel.vinel@mpsa.com
 */
public class WSWebPlugin extends AbstractPlugin {
    public static final List<String> SUPPORTED_BINDINGS = ImmutableList.of(SOAPBinding.SOAP11HTTP_BINDING, SOAPBinding.SOAP12HTTP_BINDING, SOAPBinding.SOAP11HTTP_MTOM_BINDING, SOAPBinding.SOAP12HTTP_MTOM_BINDING);
    private static final Logger LOGGER = LoggerFactory.getLogger(WSWebPlugin.class);

    private final Map<String, EndpointDefinition> endpointDefinitions = new HashMap<String, EndpointDefinition>();

    private WSPlugin wsPlugin;
    private WebPlugin webPlugin;
    private ServletContext servletContext;
    private ServletAdapterList servletAdapters;
    private WSServletDelegate wsServletDelegate;

    @Override
    public String name() {
        return "ws-web";
    }

    @Override
    public InitState init(InitContext initContext) {
        webPlugin = initContext.dependency(WebPlugin.class);
        wsPlugin = initContext.dependency(WSPlugin.class);

        // Always disable standalone publishing (it will not work with the servlet specific configuration anyway)
        wsPlugin.disableEndpointPublishing();

        if (servletContext == null) {
            LOGGER.info("No servlet context detected, web services servlet integration disabled");
        } else {
            List<String> endpointUrls = new ArrayList<String>();
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

            webPlugin.registerAdditionalModule(new WSServletModule(endpointUrls));
        }

        return InitState.INITIALIZED;
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
    public void provideContainerContext(Object containerContext) {
        if (containerContext != null && ServletContext.class.isAssignableFrom(containerContext.getClass())) {
            this.servletContext = (ServletContext) containerContext;
        }
    }

    @Override
    public Collection<Class<?>> requiredPlugins() {
        return Lists.<Class<?>>newArrayList(WSPlugin.class);
    }

    @Override
    public Collection<Class<?>> dependentPlugins() {
        return Lists.<Class<?>>newArrayList(WebPlugin.class);
    }
}