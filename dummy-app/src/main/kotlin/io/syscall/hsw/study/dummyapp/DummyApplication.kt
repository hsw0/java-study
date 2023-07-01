package io.syscall.hsw.study.dummyapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
public open class DummyApplication

public fun main(args: Array<String>) {
    runApplication<DummyApplication>(*args)
}
