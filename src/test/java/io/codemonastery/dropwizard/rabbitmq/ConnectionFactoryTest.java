package io.codemonastery.dropwizard.rabbitmq;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.lifecycle.setup.ScheduledExecutorServiceBuilder;
import io.dropwizard.setup.Environment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class ConnectionFactoryTest {

    private ExecutorService deliveryExecutor;
    private ScheduledExecutorService connectExecutor;

    @Mock
    private ScheduledExecutorServiceBuilder scheduledExecutorServiceBuilder;
    
    @Mock
    private LifecycleEnvironment lifecycle;

    @Mock
    private HealthCheckRegistry healthCheck;

    @Mock
    private MetricRegistry metrics;

    @Mock
    private Environment environment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        deliveryExecutor = Executors.newSingleThreadExecutor();
        when(environment.lifecycle()).thenReturn(lifecycle);
        when(environment.healthChecks()).thenReturn(healthCheck);
        when(environment.metrics()).thenReturn(metrics);
        when(lifecycle.scheduledExecutorService(anyString())).thenReturn(scheduledExecutorServiceBuilder);
        when(scheduledExecutorServiceBuilder.threads(anyInt())).thenReturn(scheduledExecutorServiceBuilder);
        when(scheduledExecutorServiceBuilder.build()).thenAnswer(invocationOnMock -> {
            if (connectExecutor != null) {
                throw new AssertionError("Should not be called more than once per test");
            }
            connectExecutor = Executors.newSingleThreadScheduledExecutor();
            return connectExecutor;
        });
    }

    @After
    public void tearDown() throws Exception {
        deliveryExecutor.shutdownNow();
        deliveryExecutor = null;
        if (connectExecutor != null) {
            connectExecutor.shutdownNow();
            connectExecutor = null;
        }
    }

    @Test
    public void synchronousStartAndDeclareQueue() throws Exception {
        Connection connection = null;
        try {
            connection = new ConnectionFactory().build(environment, deliveryExecutor, "ConnectionFactoryTest");
            Channel channel = null;
            try {
                channel = connection.createChannel();
                //noinspection unused
                final AMQP.Queue.DeclareOk declareOk = channel.queueDeclare();
            } finally {
                if (channel != null) {
                    channel.close();
                }
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Test
    public void synchronousStartFailure() throws Exception {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("NOT_A_REAL_NAME");
        connectionFactory.setConnectionTimeout(100);
        Connection connection = null;
        try {
            connection = connectionFactory.build(environment, deliveryExecutor, "ConnectionFactoryTest");
            fail("expected connection failure");
        }catch (UnknownHostException e){
            //expected
        }finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Test
    public void asynchronousStartAndDeclareQueue() throws Exception {
        boolean[] called = {false};
        new ConnectionFactory().buildRetryInitialConnect(environment, deliveryExecutor, "ConnectionFactoryTest", connection -> {
            Channel channel = null;
            try {
                channel = connection.createChannel();
                //noinspection unused
                final AMQP.Queue.DeclareOk declareOk = channel.queueDeclare();
            } finally {
                if (channel != null) {
                    channel.close();
                }
            }
            called[0] = true;
        });
        assertTrue(called[0]);
    }
}
