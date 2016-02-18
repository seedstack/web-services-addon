/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.fixtures;

import org.seedstack.wsdl.seed.calculator.CalculatorWS;
import org.seedstack.seed.security.RequiresRoles;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService(
        endpointInterface = "org.seedstack.wsdl.seed.calculator.CalculatorWS",
        targetNamespace = "http://seedstack.org/wsdl/seed/calculator/",
        serviceName = "CalculatorService",
        portName = "CalculatorSoapPort"
)
public class CalculatorServiceImpl implements CalculatorWS {

    @Inject
    Calculator calculator;

    @Resource
    WebServiceContext wsWebServiceContext;

    @Override
    @RequiresRoles("ADD")
    public int add(@WebParam(name = "numberOne", partName = "numberOne") int numberOne, @WebParam(name = "numbertwo", partName = "numbertwo") int numberTwo) {
        return calculator.add(numberOne, numberTwo);
    }

    @Override
    @RequiresRoles("MINUS")
    public int minus(@WebParam(name = "numberOne", partName = "numberOne") int numberOne, @WebParam(name = "numbertwo", partName = "numbertwo") int numberTwo) {
        return calculator.minus(numberOne, numberTwo);
    }
}
