package io.syscall.hsw.study.java21.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * Copied from jdk-21's java.lang.Thread.Builder
 */
@SuppressWarnings({"CanIgnoreReturnValueSuggester"})
public sealed interface ThreadBuilder permits ThreadBuilder.OfPlatform, ThreadBuilder.OfVirtual {

    static ThreadBuilder.OfPlatform ofPlatform() {
        return PlatformThreadBuilder.newInstance();
    }

    static ThreadBuilder.OfVirtual ofVirtual() {
        return VirtualThreadBuilder.newInstance();
    }

    ThreadBuilder name(String name);

    ThreadBuilder name(String prefix, long start);

    ThreadBuilder inheritInheritableThreadLocals(boolean inherit);

    ThreadBuilder uncaughtExceptionHandler(UncaughtExceptionHandler ueh);

    Thread unstarted(Runnable task);

    Thread start(Runnable task);

    ThreadFactory factory();

    @SuppressWarnings("javadoc")
    sealed interface OfPlatform extends ThreadBuilder permits PlatformThreadBuilder {

        @Override
        OfPlatform name(String name);

        @Override
        OfPlatform name(String prefix, long start);

        @Override
        OfPlatform inheritInheritableThreadLocals(boolean inherit);

        @Override
        OfPlatform uncaughtExceptionHandler(UncaughtExceptionHandler ueh);

        OfPlatform group(ThreadGroup group);

        OfPlatform daemon(boolean on);

        default OfPlatform daemon() {
            return daemon(true);
        }

        OfPlatform priority(int priority);

        OfPlatform stackSize(long stackSize);
    }

    sealed interface OfVirtual extends ThreadBuilder permits VirtualThreadBuilder {

        @Override OfVirtual name(String name);

        @Override OfVirtual name(String prefix, long start);

        @Override OfVirtual inheritInheritableThreadLocals(boolean inherit);

        @Override OfVirtual uncaughtExceptionHandler(UncaughtExceptionHandler ueh);
    }
}
