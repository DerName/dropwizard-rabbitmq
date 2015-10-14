package io.codemonastery.dropwizard.rabbitmq.example.generic_bundle;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.codemonastery.dropwizard.rabbitmq.RabbitMqBundle;

import java.io.IOException;
import java.util.HashMap;

public class IndexConsumerBundle extends RabbitMqBundle<IndexJobServiceConfiguration> {

    public IndexConsumerBundle() {
        super("blah-rabbitmq",
                IndexJobServiceConfiguration::getRabbitMq,
                IndexJobServiceConfiguration::getBlahConsumer);
    }

    @Override
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
        channel.queueDeclare("index", true, false, false, new HashMap<>());
        channel.queueBind("index_job", "job", "index");
    }
}
