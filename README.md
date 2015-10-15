Dropwizard RabbitMQ
===================
*Why doesn't this exist already...*

Rabbitmq configuration, metrics, health-checks and lifecycle management integrated with dropwizard, focused on common use cases. Inspired by dropwizard-core and [dropwizard-extra](//github.com/datasift/dropwizard-extra). 

Usage
-----
**Dangerous** Application will fail to start if RabbitMQ initial connection attempt does not succeed.
``` java
final ExecutorService deliveryExecutor = environment.lifecycle()
        .executorService("index-consumer-delivery-thread-pool")
        .maxThreads(configuration.getNumIndexingThreads())
        .build();
final Connection connection = configuration.getRabbitMq()
        .build(environment, deliveryExecutor, "index-consumer");

//give connection to some consumer/publisher
```

Better Usage
------------
This usage will retry the initial connection, calling the callback when it suceeds. The RabbitMQ connection class has reconnect/topology recreate features which are turned on by default in this library, which is why we only need to rety initial connect.
```
final ExecutorService deliveryExecutor = environment.lifecycle()
        .executorService("index-consumer-delivery-thread-pool")
        .maxThreads(configuration.getNumIndexingThreads())
        .build();

//this::connected is a callback
configuration.getRabbitMq()
        .buildRetryInitialConnect(environment, deliveryExecutor, "index-consumer", this::connected);
```
