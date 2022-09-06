# wf-ejb-multi-server
Example project for testing server-to-server EJB invocations on WildFly and identifying a potential issue.

# The Issue

```
Given two EAR applications, app1 and app2, both running on two WildFly servers, apphost1 and apphost2, 
and non-anonymous authentication being used for EJB invocations:

An EJB call in app1, running on one server, apphost1, that makes a remote EJB call to app2 on another server, apphost2, 
will error when it later attempts to make a different EJB call to app2 on apphost1, with the error saying unable to
connect/authenticate to apphost2 and referencing apphost2 as the destination, despite the fact the erroring call is using 
the local InitialContext, separate from the one used for the apphost2 call, and targeting apphost1.     
```

To demonstrate this issue, at least two WildFly servers are required, and these each should deploy the sample
EAR applications provided--`admin-app.ear` and `customer-app.ear`. At an absolute minimum, `admin-app.ear` needs to
be deployed on both.

Set the `report.provider.url` system property on the server that will be receiving the web calls listed below to the other server 
to the appropriate URL for making remote EJB calls, as this will be used to route select EJB calls to the admin-app on the
other server when set. For example, given apphost1 and apphost2 where apphost1 will receive the test web call, apphost1
would set `report.provider.url` to remote+http://apphost2:8080.

Issuing `GET /customer-web/services/customer/{id}/rating/v1` will do the following 
[note that "1" and "2" are automatically supported {id} values by default]:

1) Call the CustomerService#getCustomerSatisfactionRatingV1 EJB method within customer-app
2) This will call ReportService#submitReport EJB method in the admin-app.
   - If `report.provider.url` is set *this specific call* is meant to be routed to that URL/server and will use a separate 
   InitialContext from the default/local one. For example, the call starting on apphost1 would make this call go to apphost2
3) Eventually this will call ConfigService#getConfigProperty in the admin-app.
   - The expectation is this call will remain local to the admin-app on the same server the initial getCustomerSatisfactionRatingV1 EJB method.
   - However, if `report.provider.url` was set, this will error and the message will reference it attempting to go to 
   the other server, despite the fact the default/local InitialContext is being used.

What appears to be happening is something with [org.jboss.ejb.client.TransactionInterceptor](https://github.com/wildfly/jboss-ejb-client/blob/4.0/javax/src/main/java/org/jboss/ejb/client/TransactionInterceptor.java)
and/or [org.jboss.ejb.client.TransactionPostDiscoveryInterceptor](https://github.com/wildfly/jboss-ejb-client/blob/4.0/javax/src/main/java/org/jboss/ejb/client/TransactionPostDiscoveryInterceptor.java)
is directing the second admin-app call to the last destination associated with an EJB call to that app, even though the 
local client/context is not aware of it. This was discovered by adding `TRACE` level logging for `org.jboss.ejb[.client...]`.

The trace logging for this with commentary is included in the [demo-v1.txt](demo-v1.txt) file.

As a contrast, `GET /customer-web/services/customer/{id}/rating/v2` will do almost the same thing, but makes the 
ConfigService#getConfigProperty call first that stays local, but when it trys to make the ReportService#submitReport call
that does not go to the remote server and instead stays local, which doesn't error like in the other case but differs from
what one likely expects.

The trace logging for this is included in the [demo-v2.txt](demo-v2.txt) file.

This has been observed on WildFly 22.0.1.Final as well as WildFly 26.1.0.Final. The error that occurs with the v1 endpoint
does NOT appear to occur when anonymous authentication is used, at least as tested on 22.0.1.Final at the time of writing 
this; however, the ConfigService#getConfigProperty call that should stay local still goes remote contrary to expectation.

It is also worth noting that while the error mentions attempting to use the JBOSS-LOCAL-USER authentication mechanism,
which was configured as a supported authentication mechanism in the test case, an error would still happen if say that 
were removed and other mechanisms such as DIGEST-MD5 were present, with the difference being the message details. Either way,
this appears to go back to attempting to use a local context with a remote server.


# Motivation

This example was created out of a real world problem I encountered where a monolithic EAR application 
(which should probably be restructured, but that's another matter), represented by admin-app.ear, is deployed in a 
multi-standalone server set up and the desire is select complex calls, as represented by ReportService here, 
are routed to a specific server in an attempt to isolate these performance impact calls from other applications, 
while more trivial calls, represented by ConfigService, remain local to the server they are made from.

Removing the behavior override to redirect specific calls to another server is the solution I am currently recommending,
but I am still interested in determining where the actual issue is in this case.

Given that it is possible to have an EJB call that calls other EJBs within the same application and others, as well as remote
calls to another application in the same call (so long as there isn't another one that should stay local after it), it
raises the question of why this specific set of conditions can be a problem or otherwise behave contrary to what I would 
expect.