---
title: "Web-Services"
repo: "https://github.com/seedstack/web-services-addon"
author: Adrien LAUER
description: "Provides JAX-WS support through Metro."
tags:
    - communication
    - interfaces
zones:
    - Addons
menu:
    AddonWebServices:
        weight: 10
---

SeedStack Web-Services add-on provides JAX-WS integration through the Metro reference implementation.<!--more-->
<!--more-->

# Dependencies

In a standalone environment (not a Web server), use the following dependency:

{{< dependency g="org.seedstack.addons.ws" a="web-services-core" >}}

In a Web environment, use the following dependency instead:

{{< dependency g="org.seedstack.addons.ws" a="web-services-web" >}}

# WSDL and XSD files

The WSDL and its optional XSD files must be placed under the `META-INF/ws` classpath directory to be properly detected
by the Web-Services add-on. The WSDL below is used as a base for examples of this documentation. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<wsdl:definitions
        xmlns="http://schemas.xmlsoap.org/wsdl/"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
        xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
        targetNamespace="http://myproject.myorganization.org/wsdl/seed/hello/"
        xmlns:tns="http://myproject.myorganization.org/wsdl/seed/hello/"
        name="HelloService">

    <wsdl:message name="sayHello">
        <wsdl:part name="firstName" type="xsd:string"/>
    </wsdl:message>
    <wsdl:message name="sayHelloResponse">
        <wsdl:part name="return" type="xsd:string"/>
    </wsdl:message>

    <wsdl:portType name="Hello">
        <wsdl:operation name="sayHello">
            <wsdl:input message="tns:sayHello"/>
            <wsdl:output message="tns:sayHelloResponse"/>
        </wsdl:operation>
    </wsdl:portType>

    <wsdl:binding name="HelloServicePortBinding" type="tns:Hello">
        <soap:binding style="rpc"   transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="sayHello">
            <soap:operation soapAction=""/>
            <wsdl:input>
                <soap:body
                        namespace="http://myproject.myorganization.org/wsdl/seed/hello/"
                        use="literal"/>
            </wsdl:input>
            <wsdl:output>
                <soap:body
                        namespace="http://myproject.myorganization.org/wsdl/seed/hello/"
                        use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>
    <wsdl:service name="HelloService">

        <wsdl:port name="HelloServicePort" binding="tns:HelloServicePortBinding">
            <wsdl:documentation>Hello World</wsdl:documentation>
            <soap:address location="http://localhost:8080/ws/hello"/>
        </wsdl:port>
    </wsdl:service>
</wsdl:definitions>
```

# WS-import Maven goal
 
WS-import is a tool which generates JAX-WS classes from WSDL such as:

* Service Endpoint Interface (SEI)
* Client Service
* Exception class mapped from wsdl:fault
* JAXB generated value types (mapped java classes from schema types)

You have to generate those artifacts to be able to consume or publish a Web-Service. A typical usage of the plugin can be:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jvnet.jax-ws-commons</groupId>
            <artifactId>jaxws-maven-plugin</artifactId>
            <version>2.3</version>
            <executions>
                <execution>
                    <id>wsimport</id>
                    <goals>
                        <goal>wsimport</goal>
                    </goals>
                    <phase>generate-sources</phase>
                    <configuration>
                        <verbose>true</verbose>
                        <wsdlDirectory>src/main/resources/META-INF/ws</wsdlDirectory>
                        <wsdlLocation>META-INF/ws/Hello.wsdl</wsdlLocation>
                        <wsdlFiles>
                            <wsdlFile>Hello.wsdl</wsdlFile>
                        </wsdlFiles>
                        <!-- The extension flag below is needed to use JMS transport -->
                        <extension>true</extension> 
                        <target>2.1</target>
                        <genJWS>false</genJWS>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

{{% callout info %}}
You can find more information about jaxws-maven-plugin [here](https://jax-ws-commons.java.net/jaxws-maven-plugin/wsimport-mojo.html).
{{% /callout %}}

# Consuming

## JAX-WS classes

To be able to consume a Web-Service, the JAX-WS artifacts must have been generated with the WS-Import Maven goal described
[above](#ws-import-maven-goal). 

## Client code
 
You can find a typical JAX-WS client code below: 

```java
public class SomeClass {
    public void someMethod() {
        // Create the service and retrieve the port 
        Hello helloServicePort = new HelloService().getHelloServicePort();
        
        // Programmatically define the endpoint address
        ((BindingProvider) helloServicePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:" + wsPort + "/ws/hello");
        
        // Optionally specify the username and password
        ((BindingProvider) helloServicePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "user");
        ((BindingProvider) helloServicePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "password");

        // Call the operation
        helloServicePort.sayHello("World");
    }
}
```

{{% callout info %}}
You can find more advanced client JAX-WS examples in the [Web-Services sample](https://github.com/seedstack/web-services-sample).
{{% /callout %}}

# Publishing

## JAX-WS classes

To be able to publish a Web-Service, the JAX-WS artifacts must have been generated with the WS-Import Maven goal described
[above](#ws-import-maven-goal). 

## Implementation class

An implementation of the generated Web-Service interface is required:
   
```java
@WebService(
    endpointInterface = "org.myorganization.myproject.wsdl.seed.hello.HelloService",
    targetNamespace = "http://myproject.myorganization.org/wsdl/seed/hello/",
    serviceName = "HelloService",
    portName = "HelloServicePort"
)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String param) {
        return "Hello " + param;
    }
}
```

## Configuration

To publish the Web-Service you must configure its endpoint:

{{% config p="webServices" %}}
```yaml
webServices:
  # Configured endpoints with the name of the endpoint as key
  endpoints:
    HelloService:
      # Fully qualified name of the Web-Service implementation class
      implementation: (Class<?>)
      # Classpath location of the WSDL file
      wsdl: (String)
      # URL on which the Web-Service is exposed
      url: (String)
      # The location of the WS external metadata if any
      externalMetadata: (String)
      # The name of the WS to use (if not specified, the @WebService serviceName parameter is used)   
      serviceName: (String)
      # The name of the WS port to use (if not specified, the @WebService portName parameter is used)   
      portName: (String)
      # The binding of the WS
      binding: (##SOAP11_HTTP|##SOAP11_HTTP_MTOM|##SOAP12_HTTP|##SOAP12_HTTP_MTOM|##XML_HTTP)
      # If true, MTOM support is enabled for this endpoint
      enableMtom: (boolean)
      # The attachment size threshold from which MTOM is used
      mtomThreshold: (Integer)
      # The data binding mode of this endpoint
      dataBindingMode: (String)
```
{{% /config %}}

{{% callout info %}}
* In a Web environment, the `url` attribute must only contain the suffix of the Web-Service such as `/ws/hello` as the 
hostname and the port are already determined by the environment. 
* In standalone mode, the full URL must be specified, such as `http://myserver.example.org:8080/ws/hello`.
{{% /callout %}}

# JMS transport

This add-on provides an extension to use the JMS transport for Web-Services. To use it, add the following dependency:

{{< dependency g="org.seedstack.addons.ws" a="web-services-jms" >}}

To specify a JMS destination, you must use an URI that conforms to the [SOAP JMS specification](http://www.w3.org/TR/soapjms/)
instead of an HTTP URL. There are three main variants of JMS URI.
  
## JNDI lookup
  
This variant allows to retrieve the connection factory and the destination from JNDI:

```plain
jms:jndi:[jndiDestinationName]?jndiConnectionFactoryName=[jndiConnectionFactoryName]
```
    
Where:
     
* `jndiDestinationName` is the JNDI name of the JMS destination listened on.
* `jndiConnectionFactoryName` is the JNDI name of the Connection Factory.

You can specify additional parameters:

* `jndiInitialContextFactory` is the fully qualified name of the JNDI context class used for the lookup.
* `jndiURL` is the URL to the JNDI context used for the lookup.
* `replyToName` is the JNDI name of the reply destination.

{{% callout tips %}}
The `replyToName` can be omitted in which case the implementation will create a temporary queue for the response. 
{{% /callout %}}

Example:

```plain
jms:jndi:dynamicQueues/TEST.QUEUE?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=vm://localhost?broker.persistent=false
```
  
## Queue lookup

This variant allows to directly specify a queue name using a connection factory configured via the [SeedStack JMS add-on]({{<relref "addons/jms/index.md" >}}):

```plain
jms:queue:[QUEUE.NAME]?connectionFactoryName=[configuredConnectionFactory]&replyToName=[REPLY.QUEUE.NAME]
```

Where:
     
* `QUEUE.NAME` is the name of the queue.
* `configuredConnectionFactory` is the name of the connection factory configured in the JMS add-on.
* `replyToName` is the reply destination name.

{{% callout tips %}}
The `replyToName` can be omitted in which case the implementation will create a temporary queue for the response. 
{{% /callout %}}

## Topic lookup

This variant allows to directly specify a queue name using a connection factory configured via the [SeedStack JMS add-on]({{<relref "addons/jms/index.md" >}}):

```plain
jms:topic:TOPIC.NAME?connectionFactoryName=[configuredConnectionFactory]&replyToName=[REPLY.QUEUE.NAME]
```

Where:
     
* `TOPIC.NAME` is the name of the topic.
* `configuredConnectionFactory` is the name of the connection factory configured in the JMS add-on.
* `replyToName` is the reply destination name.

{{% callout tips %}}
The `replyToName` can be omitted in which case the implementation will create a temporary queue for the response (not a temporary topic). 
{{% /callout %}}


