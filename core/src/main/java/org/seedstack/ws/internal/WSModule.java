/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.ws.internal;

import com.google.inject.AbstractModule;
import com.sun.xml.wss.RealmAuthenticationAdapter;
import java.util.Set;
import org.seedstack.ws.HttpBasicAuthenticationHandler;

class WSModule extends AbstractModule {
    private final Set<Class<?>> webServiceAnnotatedClasses;
    private final Set<Class<?>> webServiceClientClasses;
    private final Set<Class<?>> handlerClasses;
    private final Class<? extends RealmAuthenticationAdapter> realmAuthenticationAdapterClass;

    WSModule(Set<Class<?>> webServiceAnnotatedClassAndInterface, Set<Class<?>> webServiceClientClass,
            Set<Class<?>> handlerClasses, Class<? extends RealmAuthenticationAdapter> realmAuthenticationAdapterClass) {
        this.webServiceAnnotatedClasses = webServiceAnnotatedClassAndInterface;
        this.webServiceClientClasses = webServiceClientClass;
        this.handlerClasses = handlerClasses;
        this.realmAuthenticationAdapterClass = realmAuthenticationAdapterClass;
    }

    @Override
    protected void configure() {
        requestStaticInjection(SeedInstanceResolver.class);
        requestStaticInjection(HttpBasicAuthenticationHandler.class);
        requestStaticInjection(SeedRealmAuthenticationAdapterDelegate.class);
        bind(RealmAuthenticationAdapter.class).to(realmAuthenticationAdapterClass);
        webServiceAnnotatedClasses.forEach(this::bind);
        webServiceClientClasses.forEach(this::bind);
        handlerClasses.forEach(this::bind);
    }
}
