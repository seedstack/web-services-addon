/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.internal;

import com.google.inject.Injector;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.server.AbstractMultiInstanceResolver;

import javax.inject.Inject;

class SeedInstanceResolver extends AbstractMultiInstanceResolver<Object> {
    @Inject
    private static Injector injector;

    SeedInstanceResolver(Class<Object> clazz) {
        super(clazz);
    }

    @Override
    public Object resolve(Packet packet) {
        Object instance = injector.getInstance(this.clazz);
        prepare(instance);
        return instance;
    }
}

