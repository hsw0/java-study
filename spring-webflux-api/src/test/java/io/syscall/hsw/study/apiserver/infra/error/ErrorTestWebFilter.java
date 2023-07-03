package io.syscall.hsw.study.apiserver.infra.error;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
class ErrorTestWebFilter implements WebFilter {

    static final String HANDLED_EXCEPTION_PATH = "/test/filter-throws/handled";
    static final String UNHANDLED_EXCEPTION_PATH = "/test/filter-throws/unhandled";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var requestPath =
                exchange.getRequest().getPath().pathWithinApplication().value();
        if (HANDLED_EXCEPTION_PATH.equals(requestPath)) {
            return Mono.error(new CatchMeCheckedException());
        } else if (UNHANDLED_EXCEPTION_PATH.equals(requestPath)) {
            throw new CatchMeUnhandledError();
        }
        return chain.filter(exchange);
    }
}
