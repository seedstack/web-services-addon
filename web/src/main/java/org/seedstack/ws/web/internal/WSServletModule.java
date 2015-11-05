/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.web.internal;


import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.sun.xml.ws.transport.http.servlet.WSServlet;

import java.util.List;

class WSServletModule extends ServletModule {
    private final List<String> endpointUrls;

    WSServletModule(List<String> endpointUrls) {
        this.endpointUrls = endpointUrls;
    }

    @Override
    protected void configureServlets() {
        bind(WSServlet.class).in(Scopes.SINGLETON);

        for (String endpointUrl : endpointUrls) {
            serve(endpointUrl).with(WSServlet.class);
        }
    }
}
