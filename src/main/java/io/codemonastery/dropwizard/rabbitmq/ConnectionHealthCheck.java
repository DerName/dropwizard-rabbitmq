package io.codemonastery.dropwizard.rabbitmq;

import com.codahale.metrics.health.HealthCheck;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Internal use. 
 */
class ConnectionHealthCheck extends HealthCheck {
    
    private final Supplier<Connection> connection;

    public ConnectionHealthCheck(Supplier<Connection> connection) {
        this.connection = connection;
    }

    @Override
    protected Result check() throws Exception {
        final Connection connection = this.connection.get();
        Result result;
        if (connection != null && connection.isOpen()) {
            try{
                Channel channel = connection.createChannel();
                channel.close();
                result = Result.healthy();
            }catch (IOException e){
                result = Result.unhealthy("Connection is open, could not create channel");
            }
        } else {
            result = Result.unhealthy("Not Connected");
        }
        return result;
    }
}

