package io.syscall.commons.module.appbase.servlet.error;

import static org.springframework.web.servlet.DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * {@link Controller} 바깥에서 예외가 발생하여 {@link ExceptionHandler}로 전달되지 못한 경우 다시 ExceptionHandler로 처리를 유도
 *
 * @see io.syscall.commons.module.appbase.webflux.error.LoopbackErrorWebExceptionHandler
 * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
 */
@Controller
@PermitAll
public class LoopbackErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(LoopbackErrorController.class);

    private static final String ALREADY_EXECUTED_ATTRIBUTE = LoopbackErrorController.class.getName() + ".EXECUTED";
    private static final String ERROR_PATH_PLACEHOLDER = "${server.error.path:${error.path:/error}}";

    private final ErrorAttributes errorAttributes;
    private final HandlerExceptionResolver resolver;
    private final String errorPagePath;

    public LoopbackErrorController(
            ErrorAttributes errorAttributes,
            @Qualifier(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME) HandlerExceptionResolver resolver,
            @Value(ERROR_PATH_PLACEHOLDER) String errorPagePath) {
        this.errorAttributes = errorAttributes;
        this.resolver = resolver;
        this.errorPagePath = errorPagePath;
    }

    @RequestMapping(ERROR_PATH_PLACEHOLDER)
    @Nullable
    // S3516: Refactor this method to not always return the same value.
    @SuppressWarnings("java:S3516")
    public ResponseEntity<Object> error(HttpServletRequest request, HttpServletResponse response) {
        boolean alreadyProcessed = request.getAttribute(ALREADY_EXECUTED_ATTRIBUTE) != null
                || request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE) != null
                || response.isCommitted();

        request.setAttribute(ALREADY_EXECUTED_ATTRIBUTE, true);

        if (alreadyProcessed) {
            return null;
        }

        Throwable t = extractError(request, response);
        if (!(t instanceof Exception ex)) {
            return null;
        }
        if (tryExceptionHandlers(request, response, ex)) {
            return null;
        }

        return handleUncaught(request, response, ex);
    }

    @SuppressWarnings("unused")
    protected ResponseEntity<Object> handleUncaught(
            HttpServletRequest request, HttpServletResponse response, Exception t) {
        log.error("Uncaught exception", t);

        var detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return ResponseEntity.status(detail.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(detail);
    }

    /**
     * {@link HandlerExceptionResolver} 을 사용하여 {@link ExceptionHandler} 들을 수행한다
     *
     * @return 처리 되었는가
     */
    private boolean tryExceptionHandlers(HttpServletRequest request, HttpServletResponse response, Throwable t) {
        if (!(t instanceof Exception ex)) {
            return false;
        }

        return resolver.resolveException(request, response, null, ex) != null;
    }

    private Throwable extractError(HttpServletRequest request, HttpServletResponse response) {
        var ex = errorAttributes.getError(new ServletWebRequest(request));
        if (ex != null) {
            return ex;
        }

        if (errorPagePath.equals(request.getRequestURI()) && request.getDispatcherType() == DispatcherType.REQUEST) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
        }

        return ServletErrorStatusException.extract(request, response);
    }
}
