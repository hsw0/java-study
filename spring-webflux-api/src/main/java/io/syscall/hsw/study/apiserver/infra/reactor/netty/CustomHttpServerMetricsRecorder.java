package io.syscall.hsw.study.apiserver.infra.reactor.netty;

import java.net.SocketAddress;
import java.time.Duration;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.netty.channel.ChannelMetricsRecorder;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerMetricsRecorder;

/**
 * reactor-netty 의 {@link HttpServer#metrics(boolean, Supplier) HttpServer.metrics()} 에서 사용할
 * {@link ChannelMetricsRecorder}.
 *
 * <p>Request Path 로 부터 메트릭의 {@code uri} 레이블을 매핑하는 로직을 직접 지정해야 하는데, 그냥 {@code Function.identity()} 를 해버리면
 * 레이블의 카디널리티가 너무 높아져서 문제가 발생한다.</p>
 *
 * <p>{@link RequestMapping} (method, path) 조합으로 으로 등록된 매핑을 분석하여 적절한 패턴을 찾아 이 패턴으로 치환해주는 역할.<p>
 *
 * <p>NOTE: {@code reactor.netty.http.server.} 에서 {@code http.server.}에서 제공하지 않는 URL별 메트릭도 있어서 잘 수집해두면 좋을것 같다.</p>
 *
 * @see <a href="https://projectreactor.io/docs/netty/1.1.8/reference/index.html#_metrics_4">
 * reactor-netty: HTTP Server &gt; 5.11. Metrics</a>
 */
public final class CustomHttpServerMetricsRecorder implements HttpServerMetricsRecorder {

    private final HttpServerMetricsRecorder delegate;

    private final SimpleRequestMappingPatternResolver uriPatternResolver;

    public CustomHttpServerMetricsRecorder(
            HttpServerMetricsRecorder delegate, SimpleRequestMappingPatternResolver uriPatternResolver) {
        this.delegate = delegate;
        this.uriPatternResolver = uriPatternResolver;
    }

    private String resolveRoute(String uri, @Nullable String method) {
        var route = uriPatternResolver.resolve(uri, method);
        if (route != null) {
            return route;
        }
        return "UNKNOWN";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordDataReceivedTime(String uri, String method, Duration time) {
        var route = resolveRoute(uri, method);
        delegate.recordDataReceivedTime(route, method, time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordDataSentTime(String uri, String method, String status, Duration time) {
        var route = resolveRoute(uri, method);
        delegate.recordDataSentTime(route, method, status, time);
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@code reactor.netty.http.server.response.time}. {@code http.server.requests} 와 중복</p>
     */
    @Override
    public void recordResponseTime(String uri, String method, String status, Duration time) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordDataReceived(SocketAddress remoteAddress, String uri, long bytes) {
        var route = resolveRoute(uri, null);
        delegate.recordDataReceived(remoteAddress, route, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordDataSent(SocketAddress remoteAddress, String uri, long bytes) {
        var route = resolveRoute(uri, null);
        delegate.recordDataSent(remoteAddress, route, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementErrorsCount(SocketAddress remoteAddress, String uri) {
        var route = resolveRoute(uri, null);
        delegate.incrementErrorsCount(remoteAddress, route);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordServerConnectionActive(SocketAddress localAddress) {
        delegate.recordServerConnectionActive(localAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordServerConnectionInactive(SocketAddress localAddress) {
        delegate.recordServerConnectionInactive(localAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordStreamOpened(SocketAddress localAddress) {
        delegate.recordStreamOpened(localAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordStreamClosed(SocketAddress localAddress) {
        delegate.recordStreamClosed(localAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordServerConnectionOpened(SocketAddress localAddress) {
        delegate.recordServerConnectionOpened(localAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordServerConnectionClosed(SocketAddress localAddress) {
        delegate.recordServerConnectionClosed(localAddress);
    }

    @Override
    public void recordDataReceived(SocketAddress remoteAddress, long bytes) {
        // noop
    }

    @Override
    public void recordDataSent(SocketAddress remoteAddress, long bytes) {
        // noop
    }

    @Override
    public void incrementErrorsCount(SocketAddress remoteAddress) {
        // noop
    }

    @Override
    public void recordTlsHandshakeTime(SocketAddress remoteAddress, Duration time, String status) {
        // noop
    }

    @Override
    public void recordConnectTime(SocketAddress remoteAddress, Duration time, String status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recordResolveAddressTime(SocketAddress remoteAddress, Duration time, String status) {
        throw new UnsupportedOperationException();
    }
}
