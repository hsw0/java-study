package io.syscall.hsw.study.apiserver.infra.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ErrorTestController {

    static final String SUCCESS_PATH = "/test/really-ok";
    static final String THROW_CHECKED_PATH = "/test/throw-checked";

    @GetMapping(SUCCESS_PATH)
    @ResponseStatus(HttpStatus.ACCEPTED)
    String thisIsFine() {
        return "This is fine.";
    }

    @PostMapping(THROW_CHECKED_PATH)
    String throwsCheckedException() throws Throwable {
        throw new CatchMeCheckedException();
    }
}
