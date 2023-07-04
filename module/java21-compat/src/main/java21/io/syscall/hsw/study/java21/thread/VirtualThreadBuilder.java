package io.syscall.hsw.study.java21.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

final class VirtualThreadBuilder implements ThreadBuilder.OfVirtual, UsingNativeImpl {

    private final Thread.Builder.OfVirtual delegate = Thread.ofVirtual();

    static VirtualThreadBuilder newInstance() {
        return new VirtualThreadBuilder();
    }

    @Override
    public ThreadBuilder.OfVirtual name(String name) {
        delegate.name(name);
        return this;
    }

    @Override
    public ThreadBuilder.OfVirtual name(String prefix, long start) {
        delegate.name(prefix, start);
        return this;
    }

    @Override
    public ThreadBuilder.OfVirtual inheritInheritableThreadLocals(boolean inherit) {
        delegate.inheritInheritableThreadLocals(inherit);
        return this;
    }

    @Override
    public ThreadBuilder.OfVirtual uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
        delegate.uncaughtExceptionHandler(ueh);
        return this;
    }

    @Override
    public Thread unstarted(Runnable task) {
        return delegate.unstarted(task);
    }

    @Override
    public Thread start(Runnable task) {
        return delegate.start(task);
    }

    @Override
    public ThreadFactory factory() {
        return delegate.factory();
    }
}
