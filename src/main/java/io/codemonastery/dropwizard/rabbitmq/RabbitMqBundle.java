package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.Connection;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public abstract class RabbitMqBundle<C extends Configuration> implements ConfiguredBundle<C> {

    private final String name;
    private final Function<C, ConsumerConfiguration> consumerConfiguration;
    private final Function<C, ConnectionFactory> connectionFactory;

    public RabbitMqBundle(String name,
                          Function<C, ConnectionFactory> connectionFactory,
                          Function<C, ConsumerConfiguration> consumerConfiguration) {
        this.name = name;
        this.consumerConfiguration = consumerConfiguration;
        this.connectionFactory = connectionFactory;
    }

    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(C configuration, Environment environment) throws Exception {
        ConsumerConfiguration consumerConfiguration = this.consumerConfiguration.apply(configuration);
        ExecutorService executorService = environment.lifecycle()
                .executorService(name + "-consumer-thread-pool")
                .maxThreads(consumerConfiguration.getNumThreads())
                .build();
        connectionFactory.apply(configuration).buildAsync(environment, executorService, name, this::connected);

    }

    public abstract void connected(Connection connection) throws Exception;

}

