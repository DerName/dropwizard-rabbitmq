package io.codemonastery.dropwizard.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

class ConnectionWithMetrics implements Connection{
    
    private final Connection delegate;
    private final ChannelMetrics channelMetrics;

    ConnectionWithMetrics(Connection delegate, ChannelMetrics channelMetrics) {
        this.delegate = delegate;
        this.channelMetrics = channelMetrics;
    }

    @Override
    public InetAddress getAddress() {
        return delegate.getAddress();
    }

    @Override
    public int getPort() {
        return delegate.getPort();
    }

    @Override
    public int getChannelMax() {
        return delegate.getChannelMax();
    }

    @Override
    public int getFrameMax() {
        return delegate.getFrameMax();
    }

    @Override
    public int getHeartbeat() {
        return delegate.getHeartbeat();
    }

    @Override
    public Map<String, Object> getClientProperties() {
        return delegate.getClientProperties();
    }

    @Override
    public Map<String, Object> getServerProperties() {
        return delegate.getServerProperties();
    }

    @Override
    public Channel createChannel() throws IOException {
        return channelMetrics.wrap(delegate.createChannel());
    }

    @Override
    public Channel createChannel(int channelNumber) throws IOException {
        return channelMetrics.wrap(delegate.createChannel(channelNumber));
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void close(int closeCode, String closeMessage) throws IOException {
        delegate.close(closeCode, closeMessage);
    }

    @Override
    public void close(int timeout) throws IOException {
        delegate.close(timeout);
    }

    @Override
    public void close(int closeCode, String closeMessage, int timeout) throws IOException {
        delegate.close(closeCode, closeMessage, timeout);
    }

    @Override
    public void abort() {
        delegate.abort();
    }

    @Override
    public void abort(int closeCode, String closeMessage) {
        delegate.abort(closeCode, closeMessage);
    }

    @Override
    public void abort(int timeout) {
        delegate.abort(timeout);
    }

    @Override
    public void abort(int closeCode, String closeMessage, int timeout) {
        delegate.abort(closeCode, closeMessage, timeout);
    }

    @Override
    public void addBlockedListener(BlockedListener listener) {
        delegate.addBlockedListener(listener);
    }

    @Override
    public boolean removeBlockedListener(BlockedListener listener) {
        return delegate.removeBlockedListener(listener);
    }

    @Override
    public void clearBlockedListeners() {
        delegate.clearBlockedListeners();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return delegate.getExceptionHandler();
    }

    @Override
    public void addShutdownListener(ShutdownListener listener) {
        delegate.addShutdownListener(listener);
    }

    @Override
    public void removeShutdownListener(ShutdownListener listener) {
        delegate.removeShutdownListener(listener);
    }

    @Override
    public ShutdownSignalException getCloseReason() {
        return delegate.getCloseReason();
    }

    @Override
    public void notifyListeners() {
        delegate.notifyListeners();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }
}
