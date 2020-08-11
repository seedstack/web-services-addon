/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws;

import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.wsdl.seed.calculator.CalculatorService;
import org.seedstack.wsdl.seed.calculator.CalculatorWS;

import javax.xml.ws.BindingProvider;
import java.net.URL;

import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class WSWebIT  {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @Test(expected = Exception.class) // TODO change for real security exception
    @RunAsClient
    public void without_security(@ArquillianResource URL baseURL) throws Exception {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + "ws/calculator");

        int result = calculatorWS.add(1, 1);
        Assertions.assertThat(result).isEqualTo(2);
    }

    @Test
    @RunAsClient
    public void limited_valid_user_account_calling_allowed_method(@ArquillianResource URL baseURL) throws Exception {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + "ws/calculator");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "limited");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "good");

        int result = calculatorWS.add(1, 1);
        Assertions.assertThat(result).isEqualTo(2);
    }

    @Test(expected = ServerSOAPFaultException.class)
    @RunAsClient
    public void limited_valid_user_account_calling_denied_method(@ArquillianResource URL baseURL) throws Exception {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + "ws/calculator");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "limited");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "good");

        calculatorWS.minus(1, 1);
        fail("should have failed since access is denied");
    }

    @Test
    @RunAsClient
    public void full_valid_user_account_calling_all_methods(@ArquillianResource URL baseURL) throws Exception {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + "ws/calculator");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "full");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "good");

        int result = calculatorWS.add(1, 1);
        Assertions.assertThat(result).isEqualTo(2);

        int result2 = calculatorWS.minus(1, 1);
        Assertions.assertThat(result2).isEqualTo(0);
    }

    @Test(expected = ClientTransportException.class) // TODO review exception
    @RunAsClient
    public void invalid_user_account(@ArquillianResource URL baseURL) throws Exception {
        CalculatorService calculatorService = new CalculatorService();
        CalculatorWS calculatorWS = calculatorService.getCalculatorSoapPort();
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + "ws/calculator");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "full");
        ((BindingProvider) calculatorWS).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "bad");

        int result = calculatorWS.add(1, 1);
    }
}

