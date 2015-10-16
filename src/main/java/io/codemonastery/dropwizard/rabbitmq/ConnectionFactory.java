package io.codemonastery.dropwizard.rabbitmq;

import com.google.common.base.Optional;
import com.rabbitmq.client.Connection;
import io.dropwizard.setup.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * For documentation about these configurations, see {@link com.rabbitmq.client.ConnectionFactory}.
 * 
 * Registers a health check, manages the rabbitmq connection, and adds metrics.
 * 
 * Note that automaticRecoveryEnabled and topologyRecoveryEnabled are not exposed because they are assumed to be true.
 */
public class ConnectionFactory extends ConnectionConfiguration {
    
    private ConnectionMetrics metrics;
    
    public ConnectionFactory metrics(ConnectionMetrics metrics){
        this.metrics = metrics;
        return this;
    }

    /**
     * Synchronously connect to rabbitmq, will cause application to fail if initial connection is unsuccessful.
     * @param env dropwizard environment
     * @param deliveryExecutor executor
     * @param name name of rabbitmq connection
     * @return connection
     * @throws Exception
     */
    public Connection build(final Environment env,
                            final ExecutorService deliveryExecutor,
                            final String name) throws Exception {
        final com.rabbitmq.client.ConnectionFactory connectionFactory = makeConnectionFactory();
        final ConnectionMetrics connectionMetrics = Optional.fromNullable(metrics)
                .or(() -> new DefaultConnectionMetrics(name, env.metrics()));
        final Connection connection = connectionFactory.newConnection(deliveryExecutor);
        registerWithEnvironment(env, name, () -> connection);
        return new WrappedConnectionMetrics(connectionMetrics).wrap(connection);
    }

    /**
     * Asynchronously connect to rabbitmq, and retry until successful
     * @param env dropwizard environment
     * @param deliveryExecutor the executor used by rabbitmq client to deliver messages
     * @param name name of rabbitmq connection
     * @param callback callback when done - which may be after application start
     * @throws Exception
     */
    public void buildRetryInitialConnect(final Environment env,
                                         final ExecutorService deliveryExecutor,
                                         final String name,
                                         final ConnectedCallback callback) throws Exception {
        final com.rabbitmq.client.ConnectionFactory connectionFactory = makeConnectionFactory();
        final ScheduledExecutorService initialConnectExecutor = env.lifecycle()
                .scheduledExecutorService(name + "-initial-connect-thread")
                .threads(1)
                .build();
        
        final ConnectionMetrics connectionMetrics = Optional.fromNullable(metrics)
                .or(() -> new DefaultConnectionMetrics(name, env.metrics()));
        final WrappedConnectionMetrics connectionMetricsWrapper = new WrappedConnectionMetrics(connectionMetrics);
        final ConnectedCallback callbackWithMetrics = connection -> {
            final Connection metricsConnection = connectionMetricsWrapper.wrap(connection);
            callback.connected(metricsConnection);
        };
        final ConnectAsync connectAsync = new ConnectAsync(connectionFactory, deliveryExecutor, name, initialConnectExecutor, callbackWithMetrics);
        registerWithEnvironment(env, name, connectAsync::getConnection);
        connectAsync.run();
    }
    
    private void registerWithEnvironment(final Environment env, final String name, final Supplier<Connection> connection){
        env.healthChecks().register(name, new ConnectionHealthCheck(connection));
        env.lifecycle().manage(new ManageConnection(connection));
    }
}
