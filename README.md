### NettoSphere WebSocket Load Test Service
A NettoSphere Server that deploy resources for load test/performance testing

Just do:
```java
% mvn
% cd target
% unzip atmosphere-loadtest-distribution.zip
% chmod a+x bin/nettosphere
% ./nettosphere
```

The point your load driver to

```
/echo/{id}    // echo test
/simple/{id}  // SimpleBroadcaster Test
/default/{id} // DefaultBroadcaster Test