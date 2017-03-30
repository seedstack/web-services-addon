---
title: "Consuming"
parent: "Web-Services"
weight: -1
repo: "https://github.com/seedstack/web-services-addon"
tags:
    - communication
zones:
    - Addons
menu:
    AddonWebServices:
        weight: 50
---

The goal of this page is to detail the consumption of an Hello World SOAP based Web Service. Configure the `jaxws-maven-plugin`
and use `wsimport` to generate web service client java from `wsdl`.

You can then use the `wsimport` generated class in your application code:

```java
HelloService helloService = new HelloService();
Hello helloServicePort = helloService.getHelloServicePort();
((BindingProvider) helloServicePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:" + wsPort + "/ws/hello");
helloServicePort.sayHello("World");
```

