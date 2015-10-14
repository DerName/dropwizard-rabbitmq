package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.Connection;

public interface ConnectedCallback {

    void connected(Connection connection) throws Exception;

}


