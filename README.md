# authorized-rest-template
A simple extension of the Spring [RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) that provides out of the box support for HTTP basic authentication.

Normally, using the standard Spring class, the use of basic HTTP authentication requires some relatively messy Base64 encoding of the username/password and the manual setting of the HTTP Authorization header. The AuthorizedRestTemplate performs this task itself internally - you simply provide the username and password to the class's constructor. 