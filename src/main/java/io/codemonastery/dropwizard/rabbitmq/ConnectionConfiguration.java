package io.codemonastery.dropwizard.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

public class ConnectionConfiguration {

    @Min(1)
    private int numThreads = 1;

    @JsonProperty
    public int getNumThreads() {
        return numThreads;
    }

    @JsonProperty
    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }
}

