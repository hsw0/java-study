package io.syscall.commons.module.appbase.servlet.error;

import io.syscall.commons.module.appbase.webflux.error.CatchMeCheckedException;
import io.syscall.commons.module.appbase.webflux.error.CatchMeUnhandledError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
class ErrorTestFilter extends OncePerRequestFilter {

    static final String HANDLED_EXCEPTION_PATH = "/test/filter-throws/handled";
    static final String UNHANDLED_EXCEPTION_PATH = "/test/filter-throws/unhandled";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals(HANDLED_EXCEPTION_PATH)) {
            throwSneaky(new CatchMeCheckedException());
            return;
        } else if (request.getRequestURI().equals(UNHANDLED_EXCEPTION_PATH)) {
            throw new CatchMeUnhandledError();
        }
        filterChain.doFilter(request, response);
    }

    /**
     * @see com.fasterxml.jackson.databind.util.ExceptionUtil#throwSneaky
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T> T throwSneaky(Throwable e) {
        _sneaky(e);
        return null; // never gets here, needed for compiler tho
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void _sneaky(Throwable e) throws E {
        throw (E) e;
    }
}
