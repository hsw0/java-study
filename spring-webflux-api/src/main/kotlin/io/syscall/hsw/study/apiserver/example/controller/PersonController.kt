package io.syscall.hsw.study.apiserver.example.controller

import io.syscall.hsw.study.apiserver.example.model.Person
import io.syscall.hsw.study.apiserver.example.model.PersonId
import io.syscall.hsw.study.apiserver.example.service.PersonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
public class PersonController(private val service: PersonService) {

    @GetMapping("/v1/persons/{id}")
    public fun get(
        @PathVariable id: PersonId,
    ): Person = service.get(id)
}
