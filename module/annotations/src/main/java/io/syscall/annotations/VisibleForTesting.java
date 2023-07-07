package io.syscall.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Inspired by Guava's {@link com.google.common.annotations.VisibleForTesting}
 */
@SuppressWarnings("JavadocReference")
@Retention(RetentionPolicy.SOURCE)
public @interface VisibleForTesting {}
