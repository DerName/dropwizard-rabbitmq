package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.Connection;
import io.dropwizard.setup.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * For documentation about these configurations, see {@link com.rabbitmq.client.ConnectionFactory)}.
 * 
 * Registers a health check, manages the rabbitmq connection. Does not handle metrics.
 * 
 * Note that automaticRecoveryEnabled and topologyRecoveryEnabled are not exposed because they are assumed to be true.
 */
public class ConnectionFactory extends ConnectionConfiguration {
    
    // will cause application to fail to called in run and cannot connect
    public Connection build(final Environment env,
                            final ExecutorService consumerExecutorService,
                            final String name) throws Exception {
        final com.rabbitmq.client.ConnectionFactory connectionFactory = makeConnectionFactory();
        final Connection connection = connectionFactory.newConnection(consumerExecutorService);
        registerWithEnvironment(env, name, () -> connection);
        return connection;
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
        final ConnectAsync connectAsync = new ConnectAsync(connectionFactory, deliveryExecutor, name, initialConnectExecutor, callback);
        registerWithEnvironment(env, name, connectAsync::getConnection);
        connectAsync.run();
    }
    
    private void registerWithEnvironment(final Environment env, final String name, final Supplier<Connection> connection){
        env.healthChecks().register(name, new ConnectionHealthCheck(connection));
        env.lifecycle().manage(new ManageConnection(connection));
    }
}
