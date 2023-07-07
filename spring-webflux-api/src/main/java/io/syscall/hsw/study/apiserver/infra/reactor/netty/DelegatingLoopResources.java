package io.syscall.hsw.study.apiserver.infra.reactor.netty;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import java.time.Duration;
import java.util.Objects;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;

@SuppressWarnings({"java:S119" /* Sonar: Type parameter names should comply with a naming convention */})
public abstract class DelegatingLoopResources implements LoopResources {

    protected final LoopResources delegate;

    protected DelegatingLoopResources(LoopResources delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public boolean daemon() {
        return delegate.daemon();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public Mono<Void> disposeLater() {
        return delegate.disposeLater();
    }

    @Override
    public Mono<Void> disposeLater(Duration quietPeriod, Duration timeout) {
        return delegate.disposeLater(quietPeriod, timeout);
    }

    @Override
    public <CHANNEL extends Channel> CHANNEL onChannel(Class<CHANNEL> channelType, EventLoopGroup group) {
        return delegate.onChannel(channelType, group);
    }

    @Override
    public <CHANNEL extends Channel> Class<? extends CHANNEL> onChannelClass(
            Class<CHANNEL> channelType, EventLoopGroup group) {
        return delegate.onChannelClass(channelType, group);
    }

    @Override
    public EventLoopGroup onClient(boolean useNative) {
        return delegate.onClient(useNative);
    }

    @Override
    public EventLoopGroup onServer(boolean useNative) {
        return delegate.onServer(useNative);
    }

    @Override
    public EventLoopGroup onServerSelect(boolean useNative) {
        return delegate.onServerSelect(useNative);
    }

    @Override
    public boolean isDisposed() {
        return delegate.isDisposed();
    }
}
