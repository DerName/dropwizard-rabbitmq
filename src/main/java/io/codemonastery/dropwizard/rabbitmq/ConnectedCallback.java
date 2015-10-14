package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.Connection;

/**
 * Callback interface.
 */
public interface ConnectedCallback {

    void connected(Connection connection) throws Exception;

}


