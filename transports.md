---
title: "Transports"
parent: "Web-Services"
weight: -1
repo: "https://github.com/seedstack/web-services-addon"
tags:
    - communication
    - web
zones:
    - Addons
menu:
    AddonWebServices:
        weight: 30
---

The WS add-on supports HTTP and JMS transports. Each URL syntax described below.

# Standalone HTTP

In a standalone environment you need to specify the full URL with the binding address and the port:

    http://localhost:4578/ws/hello
    
# Web server HTTP

In a Web server environment you only need to specify the URL pattern:

    /ws/hello

# JMS

JMS URIs are unchanged whatever the environment. They conform to the [SOAP JMS specification](http://www.w3.org/TR/soapjms/). 
There are three lookup variants to retrieve connection factories and destinations:

## JNDI lookup

This variant allows to retrieve the connection factory and the destination from JNDI:

    jms:jndi:lookupNameForDestination?jndiConnectionFactoryName=lookupNameForConnectionFactory
    
This is the minimal required URI where:
     
* `lookupNameForDestination` is the configured JNDI name of the JMS destination listened on.
* `lookupNameForConnectionFactory` is the configured JNDI name of the Connection Factory

```
jndiInitialContextFactory=fully.qualified.classname.of.jndi.initial.context.factory&jndiURL=url://to/jndi/context&jndiConnectionFactoryName=lookupNameForConnectionFactory&replyToName=REPLY.DESTINATION.NAME
```
 
The `replyToName` parameter can be omitted in which case the implementation will create a temporary queue for the response.
 
 
 
## Queue lookup

This variant allows to directly specify a queue name using a connection factory from the one(s) configured via the Seed JMS plugin:

    jms:queue:QUEUE.NAME?connectionFactoryName=nameOfConfiguredConnectionFactory&replyToName=REPLY.QUEUE.NAME

The `replyToName` can be omitted in which case the implementation will create a temporary queue for the response. 

## Topic lookup

This variant allows to directly specify a queue name using a connection factory from the one(s) configured via the Seed JMS plugin:

    jms:topic:TOPIC.NAME?connectionFactoryName=nameOfConfiguredConnectionFactory

The `topicReplyToName` can be omitted in which case the implementation will create a temporary queue for the response (not a temporary topic). 

## Message type

The Web-Services add-on uses binary JMS messages by default as [they have a number of advantages over text messages](https://www.w3.org/TR/soapjms/#textmessage-considerations). 
In case you want to force the use of text messages, you can do so by specifying the `messageType` parameter in the URI, as in this example:

    jms:queue:QUEUE.NAME?connectionFactoryName=nameOfConfiguredConnectionFactory&messageType=text
    
