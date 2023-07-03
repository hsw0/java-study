package io.syscall.hsw.study.apiserver.example.protocol

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

public data class ExampleRes(
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val id: Long,

    val name: String,

    val timestamp: Instant,

    )
