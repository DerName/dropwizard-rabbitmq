package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;

public class ConsumerWithMetrics implements Consumer {
    
    private final Consumer delegate;
    private final WrappedConnectionMetrics connectionMetrics;

    public ConsumerWithMetrics(Consumer delegate, WrappedConnectionMetrics connectionMetrics) {
        this.delegate = delegate;
        this.connectionMetrics = connectionMetrics;
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        delegate.handleConsumeOk(consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        delegate.handleCancelOk(consumerTag);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        delegate.handleCancel(consumerTag);
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        delegate.handleShutdownSignal(consumerTag, sig);
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        delegate.handleRecoverOk(consumerTag);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        connectionMetrics.delivered();
        delegate.handleDelivery(consumerTag, envelope, properties, body);
    }
}
