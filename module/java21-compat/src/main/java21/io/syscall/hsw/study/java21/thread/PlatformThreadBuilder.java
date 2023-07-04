package io.syscall.hsw.study.java21.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

final class PlatformThreadBuilder implements ThreadBuilder.OfPlatform, UsingNativeImpl {

    private final Thread.Builder.OfPlatform delegate = Thread.ofPlatform();

    public static PlatformThreadBuilder newInstance() {
        return new PlatformThreadBuilder();
    }

    @Override
    public ThreadBuilder.OfPlatform name(String name) {
        delegate.name(name);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform name(String prefix, long start) {
        delegate.name(prefix, start);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform inheritInheritableThreadLocals(boolean inherit) {
        delegate.inheritInheritableThreadLocals(inherit);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
        delegate.uncaughtExceptionHandler(ueh);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform group(ThreadGroup group) {
        delegate.group(group);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform daemon(boolean on) {
        delegate.daemon(on);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform daemon() {
        delegate.daemon();
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform priority(int priority) {
        delegate.priority(priority);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform stackSize(long stackSize) {
        delegate.stackSize(stackSize);
        return this;
    }

    @Override
    public Thread unstarted(Runnable task) {
        return delegate.unstarted(task);
    }

    @Override public Thread start(Runnable task) {
        return delegate.start(task);
    }

    @Override public ThreadFactory factory() {
        return delegate.factory();
    }
}
