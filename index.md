---
title: "Basics"
name: "Web-Services"
repo: "https://github.com/seedstack/web-services-addon"
date: 2016-03-09
author: Emmanuel VINEL
description: "Provides JAX-WS support through Metro."
backend: true
weight: -1
tags:
    - "web-service"
    - "jax-ws"
    - "metro"
    - "endorse"
zones:
    - Addons
menu:
    AddonWebServices:
        weight: 10
---

SeedStack Web-Services add-on provides a JAX-WS integration. JAX-WS offers facilities to create and connect to web services.
To enable Web-Services standalone add-on (without a Web environment) use the following dependency snippet in your module.

{{< dependency g="org.seedstack.addons.ws" a="web-services-core" >}}

In a Web environment you must use the following dependency snippet instead:

{{< dependency g="org.seedstack.addons.ws" a="web-services-web" >}}

# Best practices

* Since classes [generated]({{< ref "maven-tools.md" >}}) from WSDL are both for client and server side, this logic should belong to a shared module. 
This module can then be used as a dependency both on client and server side.
* Keep one source WSDL per Web Service and use `copy-resources` of `maven-resources-plugin` just after generating classes from WSDL. Reasons are:
    * successful code generation means the WSDL is probably valid and generated code match that WSDL
    * a copy of last valid WSDL is copied everywhere it is required (eg. published to `META-INF/ws`)
    
# Endorsement

## JDK 6

JDK 6 contains an older version of the JAX-WS specifications than the one used in WS add-on. 
Therefore the endorsed mechanism has to be used with required version of `webservices-api.jar` available 
[here](http://search.maven.org/remotecontent?filepath=org/glassfish/metro/webservices-api/2.3/webservices-api-2.3.jar).

### Endorse WS API

There are two options:

1. **Java Endorsed Standards Override Mechanism** applies to all projects using the JDK with endorsed library:

    * Copy `webservices-api.jar` into **"JDK6 folder"\jre\lib\endorsed** (create **endorsed** directory if it does not exist).
    * Alternative: set the `java.endorsed.dirs` system property to the directory containing `webservices-api.jar`.
    * Documentation is available [here](http://docs.oracle.com/javase/6/docs/technotes/guides/standards/).

2. IDE classpath override. This method must be applied per-project:

    * Add the `webservices-api.jar` library to the **bootstrap classpath** of your project. This ensures that this library
    overrides the one in the JRE. 

### Endorse the Web server

* To use the WS add-on in a Web application, the server JRE has to endorse the library as well. Add the **webservices-api.jar**
JAR to the server `endorsed` directory or to server buildpath.
* As described in **Java Endorsed Standards Override Mechanism**, you can check endorsed directories through `java.endorsed.dirs` java System property.

## JDK 7

JDK 7 works out of the box as required version of JAX-Ws is already embedded.
