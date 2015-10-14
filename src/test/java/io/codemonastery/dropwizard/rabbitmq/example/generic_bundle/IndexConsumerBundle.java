package io.codemonastery.dropwizard.rabbitmq.example.generic_bundle;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

public class IndexConsumerBundle implements ConfiguredBundle<IndexJobServiceConfiguration> {

    public IndexConsumerBundle() {
       
    }


    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(IndexJobServiceConfiguration configuration, Environment environment) throws Exception {
        final ExecutorService deliveryExecutor = environment.lifecycle()
                .executorService("index-consumer-delivery-thread-pool").maxThreads(10).build();
        configuration.getRabbitMq().buildRetryInitialConnect(environment, deliveryExecutor, "index-consumer", this::connected);
    }

    public void connected(Connection connection) throws Exception {
        final Channel channel = connection.createChannel();
        
        //idempotent setup
        setupIndexJobQueue(channel);
        setupJobStatusExchange(channel);
        
        channel.basicConsume("index_job", new IndexConsumer(channel));
    }

    private void setupJobStatusExchange(Channel channel) throws IOException {
        channel.exchangeDeclare("job_status", "topic", true);
    }

    private void setupIndexJobQueue(Channel channel) throws IOException {
        channel.exchangeDeclare("job", "topic", true);
        channel.queueDeclare("index_job", true, false, false, new HashMap<>());
        channel.queueBind("index_job", "job", "index");
    }
}
