package io.syscall.hsw.study.sampledomain.business.service

import io.syscall.hsw.study.sampledomain.model.Person
import io.syscall.hsw.study.sampledomain.model.PersonId
import io.syscall.hsw.study.sampledomain.persistence.repository.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class PersonService(
    private val repository: PersonRepository,
) {

    @Transactional(readOnly = true, transactionManager = "sampleTransactionManager")
    public fun get(id: PersonId): Person {
        val entity = repository.getReferenceById(id)
        return Person(id = entity.id!!, name = entity.name, age = entity.age)
    }
}
