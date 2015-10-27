/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.ws.internal;

import com.google.inject.Module;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequestBuilder;
import org.seedstack.seed.security.internal.SecurityProvider;

/**
 * Provides security for WS endpoints.
 *
 * @author yves.dautremay@mpsa.com
 * @author adrien.lauer@mpsa.com
 */
public class WSSecurityProvider implements SecurityProvider {

    @Override
    public void init(InitContext initContext) {
        // nothing to do here
    }

    @Override
    public void provideContainerContext(Object containerContext) {
        // nothing to do here
    }

    @Override
    public void classpathScanRequests(ClasspathScanRequestBuilder classpathScanRequestBuilder) {
        // nothing to do here
    }

    @Override
    public Module provideMainSecurityModule() {
        return null;
    }

    @Override
    public Module provideAdditionalSecurityModule() {
        return new WSSecurityModule();
    }

}