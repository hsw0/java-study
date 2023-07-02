package io.syscall.hsw.study.apiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
public class ApiApplication

public fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
