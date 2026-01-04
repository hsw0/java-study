package io.syscall.hsw.study.sampledomain.persistence

import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.model.PersonId
import io.syscall.hsw.study.sampledomain.persistence.config.SampleDomainJpaConfiguration
import io.syscall.hsw.study.sampledomain.persistence.test.TestApplication
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.hibernate.Hibernate
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestApplication::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PersonEntity equals/hashCode")
class PersonEntityEqualsHashCodeTest {

    @Autowired
    @Qualifier("sampleEntityManagerFactory")
    lateinit var entityManager: EntityManager

    @Nested
    @DisplayName("equals")
    inner class EqualsTests {

        @Test
        @DisplayName("same reference returns true")
        fun sameReference() {
            val entity = PersonEntity(name = "John", age = 30)
            Assertions.assertThat(entity).isEqualTo(entity)
        }

        @Test
        @DisplayName("null returns false")
        fun nullReturnsFalse() {
            val entity = PersonEntity(name = "John", age = 30)
            Assertions.assertThat(entity.equals(null)).isFalse()
        }

        @Test
        @DisplayName("different type returns false")
        fun differentTypeReturnsFalse() {
            val entity = PersonEntity(name = "John", age = 30)
            Assertions.assertThat(entity.equals("not an entity")).isFalse()
        }

        @Test
        @DisplayName("new entities with null IDs are not equal")
        fun newEntitiesWithNullIdsNotEqual() {
            val entity1 = PersonEntity(name = "John", age = 30)
            val entity2 = PersonEntity(name = "John", age = 30)

            Assertions.assertThat(entity1.id).isNull()
            Assertions.assertThat(entity2.id).isNull()
            Assertions.assertThat(entity1).isNotEqualTo(entity2)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("persisted entity equals itself after save")
        fun persistedEntityEqualsItself() {
            val entity = PersonEntity(name = "John", age = 30)
            entityManager.persist(entity)
            entityManager.flush()

            Assertions.assertThat(entity.id).isNotNull()
            Assertions.assertThat(entity).isEqualTo(entity)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("entities with same ID are equal")
        fun entitiesWithSameIdAreEqual() {
            val entity = PersonEntity(name = "John", age = 30)
            entityManager.persist(entity)
            entityManager.flush()
            val id = entity.id!!

            entityManager.clear()

            val found = entityManager.find(PersonEntity::class.java, id)
            Assertions.assertThat(entity).isEqualTo(found)
            Assertions.assertThat(found).isEqualTo(entity)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("entities with different IDs are not equal")
        fun entitiesWithDifferentIdsNotEqual() {
            val entity1 = PersonEntity(name = "John", age = 30)
            val entity2 = PersonEntity(name = "Jane", age = 25)

            entityManager.persist(entity1)
            entityManager.persist(entity2)
            entityManager.flush()

            Assertions.assertThat(entity1.id).isNotEqualTo(entity2.id)
            Assertions.assertThat(entity1).isNotEqualTo(entity2)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("proxy and real entity with same ID are equal")
        fun proxyAndRealEntityEqual() {
            val entity = PersonEntity(name = "John", age = 30)
            entityManager.persist(entity)
            entityManager.flush()
            val id = entity.id!!

            entityManager.clear()

            // getReference returns a proxy
            val proxy = entityManager.getReference(PersonEntity::class.java, id)
            Assertions.assertThat(Hibernate.isInitialized(proxy)).isFalse()

            // find returns the real entity
            val real = entityManager.find(PersonEntity::class.java, id)

            Assertions.assertThat(proxy).isEqualTo(real)
            Assertions.assertThat(real).isEqualTo(proxy)
        }
    }

    @Nested
    @DisplayName("hashCode")
    inner class HashCodeTests {

        @Test
        @DisplayName("hashCode is consistent for new entity")
        fun hashCodeConsistentForNewEntity() {
            val entity = PersonEntity(name = "John", age = 30)
            val hashCode1 = entity.hashCode()
            val hashCode2 = entity.hashCode()
            Assertions.assertThat(hashCode1).isEqualTo(hashCode2)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("hashCode is consistent before and after persist")
        fun hashCodeConsistentBeforeAndAfterPersist() {
            val entity = PersonEntity(name = "John", age = 30)
            val hashCodeBeforePersist = entity.hashCode()

            entityManager.persist(entity)
            entityManager.flush()

            val hashCodeAfterPersist = entity.hashCode()
            Assertions.assertThat(hashCodeBeforePersist).isEqualTo(hashCodeAfterPersist)
        }

        @Test
        @DisplayName("all PersonEntity instances have same hashCode (class-based)")
        fun allInstancesHaveSameHashCode() {
            val entity1 = PersonEntity(name = "John", age = 30)
            val entity2 = PersonEntity(name = "Jane", age = 25)
            val entity3 = PersonEntity(id = PersonId.Companion.create(100L), name = "Bob", age = 40)

            Assertions.assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode())
            Assertions.assertThat(entity2.hashCode()).isEqualTo(entity3.hashCode())
        }
    }

    @Nested
    @DisplayName("HashSet behavior")
    inner class HashSetTests {

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("entity can be found in HashSet after persist")
        fun entityInHashSetAfterPersist() {
            val entity = PersonEntity(name = "John", age = 30)
            val set = HashSet<PersonEntity>()
            set.add(entity)

            Assertions.assertThat(set).contains(entity)

            entityManager.persist(entity)
            entityManager.flush()

            // hashCode should remain consistent, so entity should still be found
            Assertions.assertThat(set).contains(entity)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("multiple entities in HashSet work correctly")
        fun multipleEntitiesInHashSet() {
            val entity1 = PersonEntity(name = "John", age = 30)
            val entity2 = PersonEntity(name = "Jane", age = 25)

            val set = HashSet<PersonEntity>()
            set.add(entity1)
            set.add(entity2)

            Assertions.assertThat(set).hasSize(2)

            entityManager.persist(entity1)
            entityManager.persist(entity2)
            entityManager.flush()

            Assertions.assertThat(set).hasSize(2)
            Assertions.assertThat(set).contains(entity1)
            Assertions.assertThat(set).contains(entity2)
        }

        @Test
        @Transactional("sampleTransactionManager")
        @DisplayName("re-fetched entity equals original in HashSet")
        fun refetchedEntityEqualsOriginal() {
            val entity = PersonEntity(name = "John", age = 30)
            entityManager.persist(entity)
            entityManager.flush()
            val id = entity.id!!

            val set = HashSet<PersonEntity>()
            set.add(entity)

            entityManager.clear()

            val refetched = entityManager.find(PersonEntity::class.java, id)
            Assertions.assertThat(set).contains(refetched)
        }
    }
}
