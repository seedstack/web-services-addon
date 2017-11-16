# Version 3.0.2 (2017-11-31)

* [chg] Update to Seed 3.4.0.

# Version 3.0.1 (2017-08-01)

* [chg] Update to Seed 3.3.0.

# Version 3.0.0 (2017-01-05)

* [brk] Update to new configuration system.
* [chg] WS-Security UsernameToken authentication now defaults to Seed security (Shiro) without necessitating a custom validator.

# Version 2.2.1.2 (2016-09-27)

* [fix] Message identifiers were not accessible in one-way message exchanges.

# Version 2.2.1.1 (2016-09-27)

* [chg] Made JMS request message id, reply message id and correlation id accessible through the response context.

# Version 2.2.2 (2016-04-26)

* [chg] Update for SeedStack 16.4.

# Version 2.2.1 (2016-03-09)

* [fix] Fix NPE that occurs during JMS text message reception if the `SOAPJMS_contentType` message property is not set.

# Version 2.2.0 (2016-03-09)

* [new] Add support for sending and receiving JMS text messages (only bytes messages were supported).
* [fix] Add a workaround to make Metro work with Tomcat when a Web application doesn't use a `web.xml` file.

# Version 2.1.1 (2015-11-25)

* [fix] Prevent returning a SOAP fault in failing JMS one-way operations.

# Version 2.1.0 (2015-11-17)

* [chg] Refactored as an add-on and updated to work with Seed 2.1.0+

# Version 2.0.0 (2015-07-30)

* [new] Initial Open-Source release.
