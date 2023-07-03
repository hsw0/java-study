package io.syscall.hsw.study.apiserver.infra.error;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {}
