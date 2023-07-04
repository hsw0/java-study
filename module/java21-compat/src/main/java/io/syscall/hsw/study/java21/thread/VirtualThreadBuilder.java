package io.syscall.hsw.study.java21.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

final class VirtualThreadBuilder implements ThreadBuilder.OfVirtual {

    @SuppressWarnings("DoNotCallSuggester")
    static VirtualThreadBuilder newInstance() {
        if (Runtime.version().feature() < 21) {
            throw new UnsupportedJvmException("Supported on JVM 21+");
        }
        throw new IllegalStateException("JVM " + Runtime.version() + " Detected, but using fallback class. check if Using Multi-Release jar");
    }

    @Override
    public Thread unstarted(Runnable task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Thread start(Runnable task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThreadFactory factory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThreadBuilder.OfVirtual name(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThreadBuilder.OfVirtual name(String prefix, long start) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThreadBuilder.OfVirtual inheritInheritableThreadLocals(boolean inherit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThreadBuilder.OfVirtual uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
        throw new UnsupportedOperationException();
    }
}
