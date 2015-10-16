package io.codemonastery.dropwizard.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

import javax.validation.constraints.Min;
import java.util.Map;

public class ConnectionConfiguration {

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
    public void setPort(Integer port) {
        this.port = port;
    }

    @JsonProperty
    public Integer getRequestedChannelMax() {
        return requestedChannelMax;
    }

    @JsonProperty
    public void setRequestedChannelMax(Integer requestedChannelMax) {
        this.requestedChannelMax = requestedChannelMax;
    }

    @JsonProperty
    public Integer getRequestedFrameMax() {
        return requestedFrameMax;
    }

    @JsonProperty
    public void setRequestedFrameMax(Integer requestedFrameMax) {
        this.requestedFrameMax = requestedFrameMax;
    }

    @JsonProperty
    public Integer getRequestedHeartbeat() {
        return requestedHeartbeat;
    }

    @JsonProperty
    public void setRequestedHeartbeat(Integer requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    @JsonProperty
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    @JsonProperty
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @JsonProperty
    public Integer getHandshakeTimeout() {
        return handshakeTimeout;
    }

    @JsonProperty
    public void setHandshakeTimeout(Integer handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    @JsonProperty
    public Integer getShutdownTimeout() {
        return shutdownTimeout;
    }

    @JsonProperty
    public void setShutdownTimeout(Integer shutdownTimeout) {
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

    protected com.rabbitmq.client.ConnectionFactory makeConnectionFactory() {
        final com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        //can grab defaults from constants
        final String username = Optional.fromNullable(getUsername()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_USER);
        final String password = Optional.fromNullable(getPassword()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_PASS);
        final String vhost = Optional.fromNullable(getVirtualHost()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_VHOST);
        final String host = Optional.fromNullable(getHost()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_HOST);
        final int amqpPort = Optional.fromNullable(getPort()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_AMQP_PORT);
        final int channelMax = Optional.fromNullable(getRequestedChannelMax()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_CHANNEL_MAX);
        final int frameMax = Optional.fromNullable(getRequestedFrameMax()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_FRAME_MAX);
        final int defaultHeartbeat = Optional.fromNullable(getRequestedHeartbeat()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_HEARTBEAT);
        final int connectionTimeout = Optional.fromNullable(getConnectionTimeout()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT);
        final int handshakeTimeout = Optional.fromNullable(getHandshakeTimeout()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT);
            final int shutdownTimeout = Optional.fromNullable(getShutdownTimeout()).or(com.rabbitmq.client.ConnectionFactory.DEFAULT_SHUTDOWN_TIMEOUT);

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setHost(host);
        connectionFactory.setPort(amqpPort);
        connectionFactory.setRequestedChannelMax(channelMax);
        connectionFactory.setRequestedFrameMax(frameMax);
        connectionFactory.setRequestedHeartbeat(defaultHeartbeat);
        connectionFactory.setConnectionTimeout(connectionTimeout);
        connectionFactory.setHandshakeTimeout(handshakeTimeout);
        connectionFactory.setShutdownTimeout(shutdownTimeout);

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
