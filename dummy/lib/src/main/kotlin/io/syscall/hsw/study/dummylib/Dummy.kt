package io.syscall.hsw.study.dummylib

import io.syscall.hsw.study.dummylib.impl.DummyImpl

public interface Dummy {
    public fun hello()

    public companion object {
        public fun get(): Dummy = DummyImpl()
    }
}
