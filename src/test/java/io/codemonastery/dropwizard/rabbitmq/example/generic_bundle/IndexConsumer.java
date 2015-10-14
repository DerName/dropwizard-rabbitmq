package io.codemonastery.dropwizard.rabbitmq.example.generic_bundle;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IndexConsumer extends DefaultConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(IndexConsumer.class);
    public static final AMQP.BasicProperties PROPS = new AMQP.BasicProperties();

    public IndexConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        //channels are meant for use within one thread
        //so we can use the consumers channel to publish
        getChannel().basicPublish("job_status", "index", true, PROPS, "STARTED".getBytes());
        try {
            //fake index work
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOG.error("Could not process index job", e);
        }
        getChannel().basicPublish("job_status", "index", true, PROPS, "FINISHED".getBytes());
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
