package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

/**
 * Internal use.
 */
class WrappedConnectionMetrics implements ConnectionMetrics {

    private final ConnectionMetrics delegate;

    public WrappedConnectionMetrics(ConnectionMetrics delegate) {

        this.delegate = delegate;
    }

    public Connection wrap(Connection connection) {
        return new ConnectionWithMetrics(connection, this);
    }

    public Channel wrap(Channel channel) {
        return new ChannelWithMetrics(channel, this);
    }

    public Consumer wrap(Consumer callback) {
        return new ConsumerWithMetrics(callback, this);
    }

    @Override
    public void delivered() {
        delegate.delivered();
    }

    @Override
    public void acked() {
        delegate.acked();
    }

    @Override
    public void nacked() {
        delegate.nacked();
    }

    @Override
    public void rejected() {
        delegate.rejected();
    }

    @Override
    public void published() {
        delegate.published();
    }
}
