package io.syscall.commons.module.appbase.webflux.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.syscall.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@link org.springframework.stereotype.Controller @Controller} 바깥에서 예외가 발생하여
 * {@link org.springframework.web.bind.annotation.ExceptionHandler @ExceptionHandler}로 전달되지 못한 경우 다시 ExceptionHandler로
 * 전달을 유도
 */
public class LoopbackErrorWebExceptionHandler implements ErrorWebExceptionHandler, InitializingBean {

    protected static final Logger log = LoggerFactory.getLogger(LoopbackErrorWebExceptionHandler.class);

    /**
     * @see org.springframework.web.server.adapter.HttpWebHandlerAdapter#DISCONNECTED_CLIENT_EXCEPTIONS
     */
    @SuppressWarnings("JavadocReference")
    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS =
            Set.of("AbortedException", "ClientAbortException", "EOFException", "EofException");

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private final List<HandlerResultHandler> resultHandlers;
    private final ObjectMapper objectMapper;

    @MonotonicNonNull
    private StaticErrorResponse defaultInternalErrorResponse;

    public LoopbackErrorWebExceptionHandler(
            RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            ObjectProvider<HandlerResultHandler> handlerResultHandlers,
            ObjectMapper objectMapper) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.resultHandlers = handlerResultHandlers.orderedStream().toList();
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("MemberName")
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable t) {
        return requestMappingHandlerAdapter
                .handleError(exchange, t)
                // MemberName: error: An unhandled exception was thrown by the Error Prone static analysis plugin.
                .flatMap(hr -> handleResult(exchange, hr)) //
                .onErrorResume(t2 -> handleUncaught(exchange, t2));
    }

    /**
     * Copied from Spring's
     *
     * @see org.springframework.web.reactive.DispatcherHandler#handleResult
     * @see org.springframework.web.reactive.DispatcherHandler#doHandleResult
     */
    @SuppressWarnings("JavadocReference")
    private Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Mono<Void> resultMono = doHandleResult(exchange, result, "Handler " + result.getHandler());
        var eh = result.getExceptionHandler();
        if (eh == null) {
            return resultMono;
        }
        return resultMono.onErrorResume(ex -> eh.handleError(exchange, ex)
                .flatMap(result2 -> doHandleResult(
                        exchange,
                        result2,
                        "Exception handler " + result2.getHandler() + ", error=\"" + ex.getMessage() + "\"")));
    }

    /**
     * Copied from Spring's
     *
     * @see org.springframework.web.reactive.DispatcherHandler#doHandleResult
     */
    @SuppressWarnings("JavadocReference")
    private Mono<Void> doHandleResult(ServerWebExchange exchange, HandlerResult handlerResult, String description) {
        for (var resultHandler : this.resultHandlers) {
            if (resultHandler.supports(handlerResult)) {
                description += " [LoopbackErrorWebExceptionHandler]"; // NOSONAR java:S1643 Use a StringBuilder instead.
                return resultHandler.handleResult(exchange, handlerResult).checkpoint(description);
            }
        }
        return Mono.error(new IllegalStateException("No HandlerResultHandler for " + handlerResult.getReturnValue()));
    }

    @SuppressWarnings("dereference.of.nullable")
    private Mono<Void> handleUncaught(ServerWebExchange exchange, Throwable t) {
        if (exchange.getResponse().isCommitted() || isDisconnectedClientError(t)) {
            return Mono.error(t);
        }

        log.error("Uncaught exception", t);

        return Mono.defer(() -> {
            var responseTemplate = defaultInternalErrorResponse;

            var response = exchange.getResponse();
            response.setRawStatusCode(responseTemplate.statusCode());
            response.getHeaders().setAll(responseTemplate.responseHeaders());
            response.getHeaders().setDate(Instant.now());

            var responseBuffer = DefaultDataBufferFactory.sharedInstance.wrap(responseTemplate.responseBody());
            return response.writeWith(Mono.just(responseBuffer));
        });
    }

    @EnsuresNonNull("this.defaultInternalErrorResponse")
    @Override
    public void afterPropertiesSet() {
        defaultInternalErrorResponse = buildInternalError();
    }

    @VisibleForTesting
    protected StaticErrorResponse buildInternalError() {
        var detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        byte[] responseContent;
        try {
            responseContent = objectMapper.writeValueAsBytes(detail);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to serialize ProblemDetail", e);
        }

        var bb = ByteBuffer.wrap(responseContent).asReadOnlyBuffer();

        return new StaticErrorResponse(detail, headers.toSingleValueMap(), bb);
    }

    record StaticErrorResponse(ProblemDetail detail, Map<String, String> responseHeaders, ByteBuffer responseBody) {

        int statusCode() {
            return detail.getStatus();
        }
    }

    /**
     * @see org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler#isDisconnectedClientError
     */
    @SuppressWarnings("JavadocReference")
    private boolean isDisconnectedClientError(Throwable ex) {
        return DISCONNECTED_CLIENT_EXCEPTIONS.contains(ex.getClass().getSimpleName())
                || isDisconnectedClientErrorMessage(
                        NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
    }

    /**
     * @see org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler#isDisconnectedClientErrorMessage
     */
    @SuppressWarnings("JavadocReference")
    private boolean isDisconnectedClientErrorMessage(@Nullable String message) {
        message = (message != null) ? message.toLowerCase() : "";
        return (message.contains("broken pipe") || message.contains("connection reset by peer"));
    }
}
