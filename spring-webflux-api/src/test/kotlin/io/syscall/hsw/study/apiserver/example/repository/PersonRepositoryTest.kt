package io.syscall.hsw.study.apiserver.example.repository

import io.syscall.hsw.study.apiserver.ApiApplication
import io.syscall.hsw.study.apiserver.example.entity.PersonEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

@DataJpaTest
@ContextConfiguration(classes = [ApiApplication::class])
class PersonRepositoryTest {

    @Autowired
    lateinit var repository: PersonRepository

    @Test
    fun testSaveLoad() {
        val entity = PersonEntity(name = "James", age = 50)
        repository.save(entity)
    }
}
