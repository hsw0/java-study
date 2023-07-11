package io.syscall.commons.module.appbase.webflux.error;

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
        if (requestPath.equals(HANDLED_EXCEPTION_PATH)) {
            return Mono.error(new CatchMeCheckedException());
        } else if (requestPath.equals(UNHANDLED_EXCEPTION_PATH)) {
            throw new CatchMeUnhandledError();
        }
        return chain.filter(exchange);
    }
}
