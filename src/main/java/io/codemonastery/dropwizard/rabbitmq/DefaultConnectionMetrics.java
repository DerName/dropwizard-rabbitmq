package io.codemonastery.dropwizard.rabbitmq;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class DefaultConnectionMetrics implements ConnectionMetrics {

    private final Meter deliveryMeter;
    private final Meter ackMeter;
    private final Meter nackMeter;
    private final Meter publishMeter;
    private final Meter rejectMeter;

    public DefaultConnectionMetrics(String connectionName, MetricRegistry metrics) {
        deliveryMeter = metrics.meter(connectionName + "-delivery");
        ackMeter = metrics.meter(connectionName + "-ack");
        nackMeter = metrics.meter(connectionName + "-nack");
        rejectMeter = metrics.meter(connectionName + "-reject");
        publishMeter = metrics.meter(connectionName + "-publish");
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
