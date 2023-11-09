package io.syscall.hsw.study.dummylib.impl

import io.syscall.hsw.study.dummylib.Dummy

internal class DummyImpl : Dummy {
    init {
        System.out.println("Dummy obj init")
    }

    override fun hello() {
        System.out.println("Hello from ${this.javaClass}")
    }
}
