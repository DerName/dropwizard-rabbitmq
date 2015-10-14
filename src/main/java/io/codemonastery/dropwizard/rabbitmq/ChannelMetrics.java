package io.codemonastery.dropwizard.rabbitmq;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

/**
 * Internal use.
 */
class ChannelMetrics implements MetricsListener {

    private final Meter deliveryMeter;
    private final Meter ackMeter;
    private final Meter nackMeter;
    private final Meter publishMeter;
    private final Meter rejectMeter;

    public ChannelMetrics(String connectionName, MetricRegistry metrics) {
        deliveryMeter = metrics.meter(connectionName + "-delivery");
        ackMeter = metrics.meter(connectionName + "-ack");
        nackMeter = metrics.meter(connectionName + "-nack");
        rejectMeter = metrics.meter(connectionName + "-reject");
        publishMeter = metrics.meter(connectionName + "-publish");
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

    /**
     * Call before delivered.
     */
    @Override
    public void delivered(){
        deliveryMeter.mark();
    }

    /**
     * Call after ack
     */
    @Override
    public void acked(){
        ackMeter.mark();
    }

    @Override
    public void nacked(){
        nackMeter.mark();
    }

    @Override
    public void rejected(){
        rejectMeter.mark();
    }

    @Override
    public void published(){
        publishMeter.mark();
    }
}
