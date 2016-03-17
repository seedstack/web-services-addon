/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.internal;

import com.google.inject.name.Names;
import org.apache.shiro.guice.ShiroModule;

class WSSecurityModule extends ShiroModule {
    private static final String WS_SECURITY_MANAGER_NAME = "wsSecurityManager";

	@Override
	protected void configureShiro() {
		bind(org.apache.shiro.mgt.SecurityManager.class).annotatedWith(Names.named(WS_SECURITY_MANAGER_NAME)).to(org.apache.shiro.mgt.SecurityManager.class);
		expose(org.apache.shiro.mgt.SecurityManager.class).annotatedWith(Names.named(WS_SECURITY_MANAGER_NAME));
	}
}
