/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.internal;

import org.kametic.specifications.Specification;
import org.seedstack.seed.core.internal.utils.SpecificationBuilder;
import org.seedstack.shed.reflect.AnnotationPredicates;

import javax.jws.WebService;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;
import java.lang.reflect.Modifier;

import static org.seedstack.shed.reflect.ClassPredicates.classIsAssignableFrom;
import static org.seedstack.shed.reflect.ClassPredicates.classIsInterface;
import static org.seedstack.shed.reflect.ClassPredicates.classModifierIs;

class WSSpecifications {
    static final Specification<Class<?>> WEB_SERVICE_SPEC = new SpecificationBuilder<>(
            AnnotationPredicates.<Class<?>>elementAnnotatedWith(WebService.class, false)
                    .and(classIsInterface().negate())
                    .and(classModifierIs(Modifier.ABSTRACT).negate()))
            .build();


    static final Specification<Class<?>> WEB_SERVICE_CLIENT_SPEC = new SpecificationBuilder<>(
            AnnotationPredicates.<Class<?>>elementAnnotatedWith(WebServiceClient.class, false)
                    .and(classIsInterface().negate())
                    .and(classModifierIs(Modifier.ABSTRACT).negate()))
            .build();


    static final Specification<Class<?>> HANDLER_SPEC = new SpecificationBuilder<>(
            classIsAssignableFrom(SOAPHandler.class).or(classIsAssignableFrom(LogicalHandler.class))
                    .and(classIsInterface().negate())
                    .and(classModifierIs(Modifier.ABSTRACT).negate()))
            .build();
}
