package io.syscall.hsw.study.apiserver.example.controller

import io.syscall.hsw.study.apiserver.example.protocol.ExampleReq
import io.syscall.hsw.study.apiserver.example.protocol.ExampleRes
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
public class ExampleCoroutineController {

    @PostMapping("/example/coroutine")
    public suspend fun sample(
        @RequestBody req: ExampleReq,
    ): ExampleRes {
        return ExampleRes(
            id = 123,
            name = req.name,
            timestamp = Instant.now(),
        )
    }
}
