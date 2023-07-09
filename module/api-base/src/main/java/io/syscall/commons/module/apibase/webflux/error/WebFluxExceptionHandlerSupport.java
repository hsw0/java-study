package io.syscall.commons.module.apibase.webflux.error;

import java.util.Locale;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.resource.ResourceWebHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

public abstract class WebFluxExceptionHandlerSupport implements MessageSourceAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @MonotonicNonNull
    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Late init false positive workaround  for checkerframework
    @SuppressWarnings("return.type.incompatible")
    protected MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * @see org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal
     */
    @SuppressWarnings("JavadocReference")
    protected Mono<ResponseEntity<Object>> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            @Nullable HttpHeaders headers,
            HttpStatusCode status,
            ServerWebExchange exchange) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        if (shouldLog(ex, exchange)) {
            logException(ex, exchange);
        }

        if (body == null && ex instanceof ErrorResponse errorResponse) {
            body = errorResponse.updateAndGetBody(getMessageSource(), getLocale(exchange));
        }

        return createResponseEntity(body, headers, status, exchange);
    }

    /**
     * @see org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler#createResponseEntity
     */
    @SuppressWarnings("JavadocReference")
    protected Mono<ResponseEntity<Object>> createResponseEntity(
            @Nullable Object body,
            @Nullable HttpHeaders headers,
            HttpStatusCode status,
            @SuppressWarnings("unused") ServerWebExchange exchange) {

        return Mono.just(new ResponseEntity<>(body, headers, status));
    }

    protected boolean shouldLog(Exception ex, ServerWebExchange exchange) {
        WebHandler handler = null;
        if (exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE) instanceof WebHandler tmp) {
            handler = tmp;
        }
        if (isStaticResourceNotFound(ex, handler)) {
            return false;
        }
        return true;
    }

    protected void logException(Exception ex, ServerWebExchange exchange) {
        var request = exchange.getRequest();
        var logEvent = log.atError()
                .setCause(ex)
                .setMessage("Exception!")
                .addKeyValue("http.request.method", request.getMethod().name())
                .addKeyValue("url.full", request.getURI());
        if (exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE) instanceof HandlerMethod hm) {
            logEvent = logEvent.addKeyValue("code.namespace", hm.getBeanType().getSimpleName())
                    .addKeyValue("code.function", hm.getMethod().getName());
        }
        logEvent.log();
    }

    protected boolean isStaticResourceNotFound(Exception ex, @Nullable WebHandler handler) {
        return handler instanceof ResourceWebHandler
                && ex instanceof ResponseStatusException rse
                && rse.getStatusCode() == HttpStatus.NOT_FOUND;
    }

    protected Locale getLocale(ServerWebExchange exchange) {
        Locale locale = exchange.getLocaleContext().getLocale();
        return (locale != null ? locale : Locale.getDefault());
    }
}
