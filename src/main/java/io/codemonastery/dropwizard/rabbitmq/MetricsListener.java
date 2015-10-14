package io.codemonastery.dropwizard.rabbitmq;

public interface MetricsListener {
    void delivered();

    void acked();

    void nacked();

    void rejected();

    void published();
}
