/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.fixtures;


import org.seedstack.seed.Bind;
import org.seedstack.wsdl.seed.calculator.ImaginaryNumber;


@Bind
public class Calculator {

    public int add(int one, int two){
        return one+two;
    }

    public ImaginaryNumber addImaginary(ImaginaryNumber one, ImaginaryNumber two){
        ImaginaryNumber imaginaryNumber = new ImaginaryNumber();
        imaginaryNumber.setReal(one.getReal() + two.getReal());
        imaginaryNumber.setImaginary(one.getImaginary() + two.getImaginary());
        return imaginaryNumber;
    }
    public int minus(int one, int two){
        return one-two;
    }
}
