package io.syscall.hsw.study.java21.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"argument.type.incompatible", "initialization.field.uninitialized"})
final class PlatformThreadBuilder extends BaseThreadBuilder implements ThreadBuilder.OfPlatform {

    private @Nullable ThreadGroup group;
    private boolean daemon;
    private boolean daemonChanged;
    private int priority;
    private long stackSize;

    PlatformThreadBuilder() {
    }

    public static PlatformThreadBuilder newInstance() {
        return new PlatformThreadBuilder();
    }

    static String genThreadName() {
        return "Thread-" + System.nanoTime(); /*Thread.genThreadName();*/
    }

    @Override
    String nextThreadName() {
        String name = super.nextThreadName();
        return (name != null) ? name : genThreadName();
    }

    @Override
    public ThreadBuilder.OfPlatform name(String name) {
        setName(name);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform name(String prefix, long start) {
        setName(prefix, start);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform inheritInheritableThreadLocals(boolean inherit) {
        setInheritInheritableThreadLocals(inherit);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
        setUncaughtExceptionHandler(ueh);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform group(ThreadGroup group) {
        this.group = Objects.requireNonNull(group);
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform daemon(boolean on) {
        daemon = on;
        daemonChanged = true;
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform priority(int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException();
        }
        this.priority = priority;
        return this;
    }

    @Override
    public ThreadBuilder.OfPlatform stackSize(long stackSize) {
        if (stackSize < 0L) {
            throw new IllegalArgumentException();
        }
        this.stackSize = stackSize;
        return this;
    }

    @Override
    public Thread unstarted(Runnable task) {
        Objects.requireNonNull(task);
        String name = nextThreadName();
        //var thread = new Thread(group, name, characteristics(), task, stackSize, null);
        var thread = new Thread(group, task, name, stackSize, inheritInheritableThreadLocals());
        if (daemonChanged) {
            thread.setDaemon(daemon);
        }
        if (priority != 0) {
            thread.setPriority(priority);
        }
        UncaughtExceptionHandler uhe = uncaughtExceptionHandler();
        if (uhe != null) {
            thread.setUncaughtExceptionHandler(uhe);
        }
        return thread;
    }

    @Override
    public Thread start(Runnable task) {
        Thread thread = unstarted(task);
        thread.start();
        return thread;
    }

    @Override
    public ThreadFactory factory() {
        return new PlatformThreadFactory(group, name(), counter(), inheritInheritableThreadLocals(),
                daemonChanged, daemon, priority, stackSize, uncaughtExceptionHandler());
    }

    private abstract static class BaseThreadFactory implements ThreadFactory {

        private static final VarHandle COUNT;

        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                COUNT = l.findVarHandle(BaseThreadFactory.class, "count", long.class);
            } catch (Exception e) {
                throw new InternalError(e);
            }
        }

        private final String name;
        private final boolean inheritInheritableThreadLocals;
        private final UncaughtExceptionHandler uhe;

        private final boolean hasCounter;

        @SuppressWarnings({"unused", "UnusedVariable"})
        private volatile long count; // NOSONAR

        BaseThreadFactory(String name,
                long start,
                boolean inheritInheritableThreadLocals,
                UncaughtExceptionHandler uhe) {
            this.name = name;
            if (name != null && start >= 0) {
                this.hasCounter = true;
                this.count = start;
            } else {
                this.hasCounter = false;
            }
            this.inheritInheritableThreadLocals = inheritInheritableThreadLocals;
            this.uhe = uhe;
        }

        boolean inheritInheritableThreadLocals() {
            return inheritInheritableThreadLocals;
        }

        UncaughtExceptionHandler uncaughtExceptionHandler() {
            return uhe;
        }

        String nextThreadName() {
            if (hasCounter) {
                return name + (long) COUNT.getAndAdd(this, 1);
            } else {
                return name;
            }
        }
    }

    /**
     * ThreadFactory for platform threads.
     */
    private static class PlatformThreadFactory extends BaseThreadFactory {

        private final ThreadGroup group;
        private final boolean daemonChanged;
        private final boolean daemon;
        private final int priority;
        private final long stackSize;

        PlatformThreadFactory(ThreadGroup group,
                String name,
                long start,
                boolean inheritInheritableThreadLocals,
                boolean daemonChanged,
                boolean daemon,
                int priority,
                long stackSize,
                UncaughtExceptionHandler uhe) {
            super(name, start, inheritInheritableThreadLocals, uhe);
            this.group = group;
            this.daemonChanged = daemonChanged;
            this.daemon = daemon;
            this.priority = priority;
            this.stackSize = stackSize;
        }

        @Override
        String nextThreadName() {
            String name = super.nextThreadName();
            return (name != null) ? name : genThreadName();
        }

        @Override
        public Thread newThread(@NonNull Runnable task) {
            Objects.requireNonNull(task);
            String name = nextThreadName();

            //var thread = new Thread(group, name, characteristics(), task, stackSize, null);
            var thread = new Thread(group, task, name, stackSize, inheritInheritableThreadLocals());
            if (daemonChanged) {
                thread.setDaemon(daemon);
            }
            if (priority != 0) {
                thread.setPriority(priority);
            }
            UncaughtExceptionHandler uhe = uncaughtExceptionHandler();
            if (uhe != null) {
                thread.setUncaughtExceptionHandler(uhe);
            }
            return thread;
        }
    }

}
