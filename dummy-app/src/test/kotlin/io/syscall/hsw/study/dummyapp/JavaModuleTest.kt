package io.syscall.hsw.study.dummyapp

import io.syscall.hsw.study.dummylib.Dummy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JavaModuleTest {

    @Test
    fun usePublicInterface() {
        val dummy = Dummy.get()
        dummy.hello()
    }

    @Test
    fun usePrivateImplementation() {
        val dummy = Dummy.get()
        println("dummy's class: ${dummy.javaClass}")
        val ctor = dummy.javaClass.getConstructor()
        assertThrows<IllegalAccessException> {
            ctor.newInstance()
        }
    }
}