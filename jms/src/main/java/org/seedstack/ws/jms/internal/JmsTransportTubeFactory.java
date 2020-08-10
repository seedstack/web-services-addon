/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.jms.internal;

import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.ws.api.pipe.Tube;

import javax.inject.Inject;

/**
 * Transport tube factory for JMS transport.
 */
public class JmsTransportTubeFactory extends TransportTubeFactory {
    @Inject
    private static WSJmsTransportFactory wsJmsTransportFactory;

    @Override
    public Tube doCreate(ClientTubeAssemblerContext context) {
        if (context.getAddress().getURI().getScheme().equalsIgnoreCase("jms")) {
            return wsJmsTransportFactory.createJmsTransportTube(context.getCodec(), context.getService().getServiceName());
        }

        return null;
    }

}
