package io.syscall.hsw.study.apiserver.example.repository

import io.syscall.hsw.study.apiserver.AbstractApiApplicationTests
import io.syscall.hsw.study.apiserver.example.entity.PersonEntity
import io.syscall.hsw.study.apiserver.example.model.PersonId
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@Transactional
class PersonRepositoryTest : AbstractApiApplicationTests() {

    @Autowired
    lateinit var repository: PersonRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun testSaveLoad() {
        val entity = PersonEntity(name = "James", age = 50)
        repository.saveAndFlush(entity)
        entityManager.clear()

        val loaded = repository.getReferenceById(PersonId.create(entity.id!!.value))
        assertThat(loaded).isEqualTo(entity)
    }
}
