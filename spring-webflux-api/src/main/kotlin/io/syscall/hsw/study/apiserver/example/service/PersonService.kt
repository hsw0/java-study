package io.syscall.hsw.study.apiserver.example.service

import io.syscall.hsw.study.apiserver.example.model.Person
import io.syscall.hsw.study.apiserver.example.model.PersonId
import io.syscall.hsw.study.apiserver.example.repository.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class PersonService(private val repository: PersonRepository) {

    @Transactional(readOnly = true)
    public fun get(id: PersonId): Person {
        val entity = repository.getReferenceById(id)
        return Person(id = entity.id!!, name = entity.name, age = entity.age)
    }
}
