package io.codemonastery.dropwizard.rabbitmq;


import com.rabbitmq.client.Connection;
import io.dropwizard.lifecycle.Managed;

import java.util.function.Supplier;

public class ManageConnection implements Managed {

    private final Supplier<Connection> connection;

    public ManageConnection(Supplier<Connection> connection) {
        this.connection = connection;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        Connection connection = this.connection.get();
        if(connection != null){
            connection.close();
        }
    }
}

