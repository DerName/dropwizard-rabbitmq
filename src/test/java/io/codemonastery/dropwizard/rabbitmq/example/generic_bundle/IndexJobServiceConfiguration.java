package io.codemonastery.dropwizard.rabbitmq.example.generic_bundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.codemonastery.dropwizard.rabbitmq.ConnectionConfiguration;
import io.codemonastery.dropwizard.rabbitmq.ConnectionFactory;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class IndexJobServiceConfiguration extends Configuration {

    @Valid
    @NotNull
    private ConnectionFactory rabbitMq = new ConnectionFactory();
    
    @Valid
    @NotNull
    private ConnectionConfiguration blahConsumer = new ConnectionConfiguration();

    @JsonProperty
    public ConnectionFactory getRabbitMq() {
        return rabbitMq;
    }

    @JsonProperty
    public void setRabbitMq(ConnectionFactory rabbitMq) {
        this.rabbitMq = rabbitMq;
    }

    @JsonProperty
    public ConnectionConfiguration getBlahConsumer() {
        return blahConsumer;
    }

    @JsonProperty
    public void setBlahConsumer(ConnectionConfiguration blahConsumer) {
        this.blahConsumer = blahConsumer;
    }
}
