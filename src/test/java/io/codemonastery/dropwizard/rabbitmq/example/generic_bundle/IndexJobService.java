package io.codemonastery.dropwizard.rabbitmq.example.generic_bundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class IndexJobService extends Application<IndexJobServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new IndexJobService().run(args);
    }

    @Override
    public String getName() {
        return "blah";
    }

    @Override
    public void initialize(Bootstrap<IndexJobServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new IndexConsumerBundle());
    }

    @Override
    public void run(IndexJobServiceConfiguration configuration, Environment environment) throws Exception {
        
    }
    
}
