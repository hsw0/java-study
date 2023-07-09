package io.syscall.commons.module.springboot.configdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link org.springframework.boot.context.annotation.ImportCandidates#load ImportCandidates#load} - "META-INF/spring/${name}.imports" 에 사용되는 마커
 */
@Target({})
@Retention(RetentionPolicy.SOURCE)
@interface AutoConfigDataFile {}
