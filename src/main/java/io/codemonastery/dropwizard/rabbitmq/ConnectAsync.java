package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Internal use.
 */
class ConnectAsync implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectAsync.class);

    private final com.rabbitmq.client.ConnectionFactory connectionFactory;
    private final ExecutorService consumerExecutorService;
    private final String name;
    private final ScheduledExecutorService initialConnectExecutor;
    private final ConnectedCallback callback;

    //output of initial retry loop
    private Connection connection;

    public ConnectAsync(com.rabbitmq.client.ConnectionFactory connectionFactory,
                        ExecutorService consumerExecutorService,
                        String name,
                        ScheduledExecutorService initialConnectExecutor,
                        ConnectedCallback callback) {
        this.connectionFactory = connectionFactory;

        this.consumerExecutorService = consumerExecutorService;
        this.name = name;
        this.initialConnectExecutor = initialConnectExecutor;
        this.callback = callback;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void run() {
        try {
            connection = connectionFactory.newConnection(consumerExecutorService);
            LOG.info("Initial RabbitMQ {}", name);
            callback.connected(connection);
            initialConnectExecutor.shutdown();
        } catch (Exception e) {
            String message = String.format("Could not perform initial connection to RabbitMQ %s, will retry until initially connected", name);
            LOG.error(message, e);
            initialConnectExecutor.schedule(this, connectionFactory.getNetworkRecoveryInterval(), TimeUnit.MILLISECONDS);
        }
    }
}

