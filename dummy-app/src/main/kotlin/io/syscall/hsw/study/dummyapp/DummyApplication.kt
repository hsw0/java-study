package io.syscall.hsw.study.dummyapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DummyApplication

fun main(args: Array<String>) {
    runApplication<DummyApplication>(*args)
}
