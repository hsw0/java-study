package io.syscall.hsw.study.sampledomain.persistence.repository

import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.persistence.test.TestApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestApplication::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class PersonRepositoryTest {

    @Autowired
    lateinit var repository: PersonRepository

    @Order(1)
    @Test
    fun `repository is injected`() {
        assertThat(repository).isNotNull()
    }

    @Order(2)
    @Test
    @Transactional("sampleTransactionManager")
    fun `can save and find person entity`() {
        val entity = PersonEntity(name = "John Doe", age = 30)
        val saved = repository.save(entity)

        assertThat(saved.id).isNotNull()

        val found = repository.findById(saved.id!!)
        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo("John Doe")
        assertThat(found.get().age).isEqualTo(30)
    }

    @Order(3)
    @Test
    @Transactional("sampleTransactionManager")
    fun `can update person entity`() {
        val entity = PersonEntity(name = "Jane Doe", age = 25)
        val saved = repository.save(entity)
        val id = saved.id!!

        saved.name = "Jane Smith"
        saved.age = 26
        repository.save(saved)

        val updated = repository.findById(id)
        assertThat(updated).isPresent
        assertThat(updated.get().name).isEqualTo("Jane Smith")
        assertThat(updated.get().age).isEqualTo(26)
    }

    @Order(4)
    @Test
    @Transactional("sampleTransactionManager")
    fun `can delete person entity`() {
        val entity = PersonEntity(name = "Delete Me", age = 99)
        val saved = repository.save(entity)
        val id = saved.id!!

        repository.deleteById(id)
        repository.flush()

        assertThat(repository.findById(id)).isEmpty
    }

    @Order(5)
    @Test
    @Transactional("sampleTransactionManager")
    fun `can count entities`() {
        val initialCount = repository.count()

        repository.save(PersonEntity(name = "Person 1", age = 20))
        repository.save(PersonEntity(name = "Person 2", age = 30))

        assertThat(repository.count()).isEqualTo(initialCount + 2)
    }
}
