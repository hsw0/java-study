package io.syscall.hsw.study.dummyapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
public class DummyApplication

public fun main(args: Array<String>) {
    runApplication<DummyApplication>(args = args)
}
