/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.internal;

import org.seedstack.shed.reflect.AnnotationPredicates;

import javax.jws.WebService;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

import static org.seedstack.shed.reflect.ClassPredicates.*;

class WSPredicates {
    static final Predicate<Class<?>> WEB_SERVICE_SPEC = AnnotationPredicates.<Class<?>>elementAnnotatedWith(WebService.class, false)
            .and(classIsInterface().negate())
            .and(classModifierIs(Modifier.ABSTRACT).negate());


    static final Predicate<Class<?>> WEB_SERVICE_CLIENT_SPEC = AnnotationPredicates.<Class<?>>elementAnnotatedWith(WebServiceClient.class, false)
            .and(classIsInterface().negate())
            .and(classModifierIs(Modifier.ABSTRACT).negate());


    static final Predicate<Class<?>> HANDLER_SPEC = classIsAssignableFrom(SOAPHandler.class).or(classIsAssignableFrom(LogicalHandler.class))
            .and(classIsInterface().negate())
            .and(classModifierIs(Modifier.ABSTRACT).negate());
}
