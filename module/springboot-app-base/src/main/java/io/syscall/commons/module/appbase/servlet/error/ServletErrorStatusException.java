package io.syscall.commons.module.appbase.servlet.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * 예외 객체를 찾을수 없으나 다른 예외 속성이 있는 경우
 */
public class ServletErrorStatusException extends ResponseStatusException {

    private final String requestUri;

    /**
     * @param message    {@link RequestDispatcher#ERROR_MESSAGE}
     * @param statusCode {@link RequestDispatcher#ERROR_STATUS_CODE}
     * @param requestUri {@link RequestDispatcher#ERROR_REQUEST_URI}
     */
    public ServletErrorStatusException(@Nullable String message, String requestUri, int statusCode) {
        super(statusCode, message, null);
        this.requestUri = requestUri;
    }

    public static ServletErrorStatusException extract(HttpServletRequest request, HttpServletResponse response) {
        String requestUri = request.getRequestURI();
        int statusCode = response.getStatus();
        String message = null;

        if (request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) instanceof String tmp) {
            requestUri = tmp;
        }
        if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) instanceof Integer tmp) {
            statusCode = tmp;
        }
        if (request.getAttribute(RequestDispatcher.ERROR_MESSAGE) instanceof String tmp && !tmp.isEmpty()) {
            message = tmp;
        }

        if (message == null && HttpStatusCode.valueOf(statusCode) instanceof HttpStatus s) {
            message = s.getReasonPhrase();
        }

        return new ServletErrorStatusException(message, requestUri, statusCode);
    }

    public String getRequestUri() {
        return requestUri;
    }
}
