---
title: "Testing"
parent: "Web-Services"
weight: -1
repo: "https://github.com/seedstack/web-services-addon"
zones:
    - Addons
tags:
    - "web-service"
    - "jax-ws"
    - "metro"
    - "testing"
menu:
    AddonWebServices:
        weight: 60
---

Web Services can be tested in Seed managed integration tests. You can find more about these kind of tests 
[here]({{< ref "docs/seed/manual/testing.md#integration-tests" >}}) and [here]({{< ref "docs/seed/manual/testing.md#web-integration-tests" >}}). 
You'll find a Web black box example below:

```java
public class HelloWSIT extends AbstractSeedWebIT {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    @Test
    @RunAsClient
    public void webservice_is_working_correctly(@ArquillianResource URL baseURL)
    throws Exception {
    
        HelloService helloServiceClient = new HelloService();
        Hello helloServicePort = helloServiceClient.getHelloServicePort();
        ((BindingProvider)helloServicePort).getRequestContext()
            .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + "ws/hello");
        
        String response = helloServicePort.sayHello("World");
        Assertions.assertThat(response).isEqualTo("Hello World");
    }
}
```

You have to specify the endpoint using `@ArquillianResource URL baseURL`, because Arquillian generates
a different base URL for each run. You may also create standalone integration tests (outside a Web environment and as such, 
without Arquillian). 

{{% callout info %}} 
If you need to do manual testing, you can access the WSDL via HTTP at `http://{server}:{port}/ws/hello?wsdl`.
{{% /callout %}}
