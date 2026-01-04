package io.syscall.hsw.study.sampledomain.business

import io.syscall.hsw.study.sampledomain.business.service.PersonService
import io.syscall.hsw.study.sampledomain.business.test.TestApplication
import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.persistence.repository.PersonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestApplication::class])
class PersonServiceTest {

    @Autowired
    lateinit var service: PersonService

    @Autowired
    lateinit var repository: PersonRepository

    @Test
    @Transactional("sampleTransactionManager")
    fun `can get person by id`() {
        val entity = PersonEntity(name = "Test Person", age = 25)
        val saved = repository.saveAndFlush(entity)
        val savedPersonId = saved.id!!

        val person = service.get(savedPersonId)

        assertThat(person.id).isEqualTo(savedPersonId)
        assertThat(person.name).isEqualTo("Test Person")
        assertThat(person.age).isEqualTo(25)
    }
}
