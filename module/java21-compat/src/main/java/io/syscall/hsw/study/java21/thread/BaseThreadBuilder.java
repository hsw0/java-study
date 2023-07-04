package io.syscall.hsw.study.java21.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;

/**
 * Copied from java.lang.ThreadBuilders.BaseThreadBuilder
 */
@SuppressWarnings({"JavadocReference"})
public class BaseThreadBuilder {

    private String name;
    private long counter;
    private boolean inheritInheritableThreadLocals;
    private UncaughtExceptionHandler uhe;

    String name() {
        return name;
    }

    long counter() {
        return counter;
    }

    boolean inheritInheritableThreadLocals() {
        return inheritInheritableThreadLocals;
    }

    UncaughtExceptionHandler uncaughtExceptionHandler() {
        return uhe;
    }

    String nextThreadName() {
        if (name != null && counter >= 0) {
            return name + (counter++);
        } else {
            return name;
        }
    }

    void setName(String name) {
        this.name = Objects.requireNonNull(name);
        this.counter = -1;
    }

    void setName(String prefix, long start) {
        Objects.requireNonNull(prefix);
        if (start < 0) {
            throw new IllegalArgumentException("'start' is negative");
        }
        this.name = prefix;
        this.counter = start;
    }

    void setInheritInheritableThreadLocals(boolean inherit) {
        this.inheritInheritableThreadLocals = inherit;
    }

    void setUncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
        this.uhe = Objects.requireNonNull(ueh);
    }
}
