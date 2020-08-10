/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.web.internal;

import com.sun.xml.ws.api.ResourceLoader;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.transport.http.servlet.ServletModule;
import org.seedstack.shed.ClassLoaders;

import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class SeedServletContainer extends Container {
    private static final String SECURITY_SERVER_TUBE_CLASS_NAME = "com.sun.xml.wss.jaxws.impl.SecurityServerTube";
    private static final String WSIT_SERVER_AUTH_CONTEXT_CLASS_NAME = "com.sun.xml.wss.provider.wsit.WSITServerAuthContext";
    private static final String GET_REALM_AUTHENTICATION_ADAPTER_METHOD_NAME = "getRealmAuthenticationAdapter";

    private final ServletContext servletContext;

    private final ServletModule module = new SeedServletModule();

    private final ResourceLoader loader = new ResourceLoader() {
        @Override
        public URL getResource(String resource) throws MalformedURLException {
            return servletContext.getResource("/WEB-INF/" + resource);
        }
    };

    SeedServletContainer(ServletContext servletContext) {
        if ("org.apache.catalina.core.StandardContext$NoPluggabilityServletContext".equals(servletContext.getClass().getName())) {
            // This proxy is used for avoiding a reflection bug of Metro when used with Tomcat without web.xml.
            // See: https://java.net/jira/browse/JAX_WS-1175

            this.servletContext = (ServletContext) Proxy.newProxyInstance(
                    ClassLoaders.findMostCompleteClassLoader(SeedServletContainer.class),
                    new Class[]{ServletContext.class},
                    new ServletContextProxy(servletContext)
            );
        } else {
            this.servletContext = servletContext;
        }
    }

    @Override
    public <T> T getSPI(Class<T> spiType) {
        if (spiType == ServletContext.class) {
            if (isCalledForRealmAuthenticationAdapter(findCaller())) {
                // Force the RealmAuthenticationAdapter to be searched from classpath instead of weird webapp location
                return null;
            }

            return spiType.cast(servletContext);
        }
        if (spiType.isAssignableFrom(ServletModule.class)) {
            return spiType.cast(module);
        }
        if (spiType == ResourceLoader.class) {
            return spiType.cast(loader);
        }

        return null;
    }

    private boolean isCalledForRealmAuthenticationAdapter(StackTraceElement stackTrace) {
        return stackTrace != null &&
                (WSIT_SERVER_AUTH_CONTEXT_CLASS_NAME.equals(stackTrace.getClassName()) || SECURITY_SERVER_TUBE_CLASS_NAME.equals(stackTrace.getClassName())) &&
                GET_REALM_AUTHENTICATION_ADAPTER_METHOD_NAME.equals(stackTrace.getMethodName());

    }

    /**
     * Find the caller of a method.
     *
     * @return the found StackTraceElement or null if not found.
     */
    private StackTraceElement findCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!stackTraceElement.getClassName().equals(Thread.class.getCanonicalName()) && !stackTraceElement.getClassName().equals(getClass().getCanonicalName())) {
                return stackTraceElement;
            }
        }

        return null;
    }

    private static class ServletContextProxy implements InvocationHandler {
        private final ServletContext servletContext;

        private ServletContextProxy(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(servletContext, args);
        }
    }

    private static class SeedServletModule extends ServletModule {
        private final List<BoundEndpoint> endpoints = new ArrayList<>();

        @Override
        public List<BoundEndpoint> getBoundEndpoints() {
            return endpoints;
        }

        @Override
        public String getContextPath() {
            // Cannot compute this since we don't know about hostname and port etc
            throw new WebServiceException("Container " + SeedServletContainer.class.getName() + " doesn't support getContextPath()");
        }
    }
}
