Dropwizard RabbitMQ
===================
*Why doesn't this exist already...*

[![Build Status](https://travis-ci.org/code-monastery/dropwizard-rabbitmq.svg?branch=master)](https://travis-ci.org/code-monastery/dropwizard-rabbitmq)

Rabbitmq configuration, metrics, health-checks and lifecycle management integrated with dropwizard, focused on common use cases. Inspired by dropwizard-core and [dropwizard-extra](//github.com/datasift/dropwizard-extra). 

Simple Usage
-----
Easy to use, but if initial connection fails no retry will be performed.
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
Will retry the initial connection, asynchronously calling the callback when it succeeds. The RabbitMQ connection class has reconnect/topology recreate features which are turned on by default in this library, which is why we only need to retry initial connect.
```
final ExecutorService deliveryExecutor = environment.lifecycle()
    .executorService("index-consumer-delivery-thread-pool")
    .maxThreads(configuration.getNumIndexingThreads())
    .build();

//this::connected is a callback
configuration.getRabbitMq()
    .buildRetryInitialConnect(environment, deliveryExecutor, "index-consumer", this::connected);
```
