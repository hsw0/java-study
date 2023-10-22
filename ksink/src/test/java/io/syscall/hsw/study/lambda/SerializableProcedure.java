package io.syscall.hsw.study.lambda;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface SerializableProcedure extends Serializable {

    /**
     * @see java.lang.Runnable
     */
    @FunctionalInterface
    interface Proc0 extends SerializableProcedure {

        void invoke() throws Throwable;
    }

    /**
     * @see java.util.function.Consumer
     */
    @FunctionalInterface
    interface Proc1<A0> extends SerializableProcedure {

        void invoke(A0 a0) throws Throwable;
    }

    /**
     * @see java.util.function.BiConsumer
     */
    @FunctionalInterface
    interface Proc2<A0, A1> extends SerializableProcedure {

        void invoke(A0 a0, A1 a1) throws Throwable;
    }

    @FunctionalInterface
    interface Proc3<A0, A1, A2> extends SerializableProcedure {

        void invoke(A0 a0, A1 a1, A2 a2) throws Throwable;
    }

    /**
     * @see java.util.concurrent.Callable
     * @see java.util.function.Supplier
     */
    @FunctionalInterface
    interface Fn0<R> extends SerializableProcedure {

        R invoke() throws Throwable;
    }

    /**
     * @see java.util.function.Function
     */
    @FunctionalInterface
    interface Fn1<R, A0> extends SerializableProcedure {

        R invoke(A0 a0) throws Throwable;
    }

    /**
     * @see java.util.function.BiFunction
     */
    @FunctionalInterface
    interface Fn2<R, A0, A1> extends SerializableProcedure {

        R invoke(A0 a0, A1 a1) throws Throwable;
    }

    @FunctionalInterface
    interface Fn3<R, A0, A1, A2> extends SerializableProcedure {

        R invoke(A0 a0, A1 a1, A2 a2) throws Throwable;
    }
}
