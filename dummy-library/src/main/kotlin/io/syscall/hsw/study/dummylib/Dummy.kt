package io.syscall.hsw.study.dummylib

import io.syscall.hsw.study.dummylib.impl.DummyImpl

interface Dummy {

    fun hello()

    companion object {
        fun get(): Dummy = DummyImpl()
    }

}
