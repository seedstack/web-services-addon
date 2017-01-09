/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.fixtures;

import org.seedstack.seed.security.RequiresRoles;
import org.seedstack.wsdl.seed.calculator.CalculatorPortType;
import org.seedstack.wsdl.seed.calculator.ImaginaryNumber;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService(
        endpointInterface = "org.seedstack.wsdl.seed.calculator.CalculatorPortType",
        targetNamespace = "http://seedstack.org/wsdl/seed/calculator/",
        serviceName = "CalculatorService",
        portName = "CalculatorUsernameTokenPort"
)
public class CalculatorUsernameTokenImpl implements CalculatorPortType {
    @Inject
    private Calculator calculator;
    @Resource
    private WebServiceContext wsWebServiceContext;

    @Override
    @RequiresRoles("ADD")
    public int add(@WebParam(name = "numberOne", partName = "numberOne") int numberOne, @WebParam(name = "numbertwo", partName = "numberTwo") int numberTwo) {
        return calculator.add(numberOne, numberTwo);
    }

    @Override
    @RequiresRoles("ADD")
    public ImaginaryNumber addImaginary(@WebParam(name = "numberOne", partName = "numberOne") ImaginaryNumber numberOne, @WebParam(name = "numbertwo", partName = "numberTwo") ImaginaryNumber numberTwo) {
        return calculator.addImaginary(numberOne, numberTwo);
    }

    @Override
    @RequiresRoles("MINUS")
    public int minus(@WebParam(name = "numberOne", partName = "numberOne") int numberOne, @WebParam(name = "numbertwo", partName = "numberTwo") int numberTwo) {
        return calculator.minus(numberOne, numberTwo);
    }
}
