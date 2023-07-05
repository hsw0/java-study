package io.syscall.hsw.study.apiserver.example.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class DummyController {

    @GetMapping("/test/current-thread")
    internal fun dummy(): String {
        val thread = Thread.currentThread()
        return thread.toString()
    }

}
