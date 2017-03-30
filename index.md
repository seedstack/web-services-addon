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

SeedStack Web-Services add-on provides a JAX-WS integration. JAX-WS offers facilities to create and connect to Web-Services.
<!--more-->
To enable Web-Services standalone add-on (without a Web environment) use the following dependency snippet in your module.

# Dependencies

In a standalone environment (not a Web server), use the following dependency:

{{< dependency g="org.seedstack.addons.ws" a="web-services-core" >}}

In a Web environment, use the following dependency snippet instead:

{{< dependency g="org.seedstack.addons.ws" a="web-services-web" >}}

 
