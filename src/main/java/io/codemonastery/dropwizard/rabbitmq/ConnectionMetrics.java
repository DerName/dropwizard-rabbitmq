package io.codemonastery.dropwizard.rabbitmq;

/**
 * Implement this if you want your own metrics.
 * 
 * Plugs in to {@link io.codemonastery.dropwizard.rabbitmq.ConnectionFactory} 
 */
public interface ConnectionMetrics {
    
    void delivered();

    void acked();

    void nacked();

    void rejected();

    void published();
}
