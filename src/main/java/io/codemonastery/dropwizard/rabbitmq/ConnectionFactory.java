package io.codemonastery.dropwizard.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.rabbitmq.client.Connection;
import io.dropwizard.setup.Environment;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * For documentation about these configurations, see {@link com.rabbitmq.client.ConnectionFactory)}.
 * 
 * Note that automaticRecoveryEnabled and topologyRecoveryEnabled are not exposed because they are assumed to be true.
 */
public class ConnectionFactory {

    private String username;

    private String password;

    private String virtualHost;

    private String host;

    @Min(1)
    private Integer port;

    @Min(0)
    private Integer requestedChannelMax;

    @Min(0)
    private Integer requestedFrameMax;

    @Min(0)
    private Integer requestedHeartbeat;

    @Min(0)
    private Integer connectionTimeout;

    @Min(0)
    private Integer handshakeTimeout;

    @Min(0)
    private Integer shutdownTimeout;

    private Map<String, Object> clientProperties;

    @Min(0)
    private Long networkRecoveryInterval;

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    public String getVirtualHost() {
        return virtualHost;
    }

    @JsonProperty
    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public Integer getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public Integer getRequestedChannelMax() {
        return requestedChannelMax;
    }

    @JsonProperty
    public void setRequestedChannelMax(int requestedChannelMax) {
        this.requestedChannelMax = requestedChannelMax;
    }

    @JsonProperty
    public Integer getRequestedFrameMax() {
        return requestedFrameMax;
    }

    @JsonProperty
    public void setRequestedFrameMax(int requestedFrameMax) {
        this.requestedFrameMax = requestedFrameMax;
    }

    @JsonProperty
    public Integer getRequestedHeartbeat() {
        return requestedHeartbeat;
    }

    @JsonProperty
    public void setRequestedHeartbeat(int requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    @JsonProperty
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    @JsonProperty
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @JsonProperty
    public Integer getHandshakeTimeout() {
        return handshakeTimeout;
    }

    @JsonProperty
    public void setHandshakeTimeout(int handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    @JsonProperty
    public Integer getShutdownTimeout() {
        return shutdownTimeout;
    }

    @JsonProperty
    public void setShutdownTimeout(int shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    @JsonProperty
    public Map<String, Object> getClientProperties() {
        return clientProperties;
    }

    @JsonProperty
    public void setClientProperties(Map<String, Object> clientProperties) {
        this.clientProperties = clientProperties;
    }

    @JsonProperty
    public Long getNetworkRecoveryInterval() {
        return networkRecoveryInterval;
    }

    @JsonProperty
    public void setNetworkRecoveryInterval(Long networkRecoveryInterval) {
        this.networkRecoveryInterval = networkRecoveryInterval;
    }

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

    private com.rabbitmq.client.ConnectionFactory makeConnectionFactory() {
        final com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        //can grab defaults from constants
        connectionFactory.setUsername(Optional.fromNullable(username).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_USER));
        connectionFactory.setPassword(Optional.fromNullable(password).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_PASS));
        connectionFactory.setVirtualHost(Optional.fromNullable(virtualHost).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_VHOST));
        connectionFactory.setHost(Optional.fromNullable(host).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_HOST));
        connectionFactory.setPort(Optional.fromNullable(port).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_AMQP_PORT));
        connectionFactory.setRequestedChannelMax(Optional.fromNullable(requestedChannelMax).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_CHANNEL_MAX));
        connectionFactory.setRequestedFrameMax(Optional.fromNullable(requestedFrameMax).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_FRAME_MAX));
        connectionFactory.setRequestedHeartbeat(Optional.fromNullable(requestedHeartbeat).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_HEARTBEAT));
        connectionFactory.setConnectionTimeout(Optional.fromNullable(connectionTimeout).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT));
        connectionFactory.setConnectionTimeout(Optional.fromNullable(connectionTimeout).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT));
        connectionFactory.setHandshakeTimeout(Optional.fromNullable(handshakeTimeout).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT));
        connectionFactory.setShutdownTimeout(Optional.fromNullable(shutdownTimeout).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_SHUTDOWN_TIMEOUT));

        //only write if not null
        if (clientProperties != null) {
            connectionFactory.getClientProperties().putAll(clientProperties);
        }

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setTopologyRecoveryEnabled(true);
        if (networkRecoveryInterval != null) {
            connectionFactory.setNetworkRecoveryInterval(networkRecoveryInterval);
        }
        return connectionFactory;
    }

}
