package io.syscall.commons.module.persistence.jpa.multidatasource

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/** Unit tests for [JpaDataSourceDefinitionBuilder] and DSL. */
class JpaDataSourceDefinitionBuilderTest {

    @Test
    fun `builds with required fields only`() {
        val definition =
            jpaDataSourceDefinition {
                name = "test"
                dataSourceBeanName = "testDataSource"
            }

        definition.name shouldBe "test"
        definition.dataSourceBeanName shouldBe "testDataSource"
        definition.entityPackages.shouldBeEmpty()
        definition.entityClasses.shouldBeEmpty()
        definition.jpaProperties.shouldBeEmpty()
    }

    @Test
    fun `builds with all fields`() {
        val definition =
            jpaDataSourceDefinition {
                name = "full"
                dataSourceBeanName = "fullDataSource"
                entityPackage("com.example.entities")
                entityPackages("com.example.more", "com.example.extra")
                entityClass(String::class.java)
                entityClasses(Int::class.java, Long::class.java)
                jpaProperties["key1"] = "value1"
                jpaProperties["key2"] = "value2"
            }

        definition.name shouldBe "full"
        definition.dataSourceBeanName shouldBe "fullDataSource"
        definition.entityPackages shouldContainExactly
            listOf("com.example.entities", "com.example.more", "com.example.extra")
        definition.entityClasses shouldContainExactly
            listOf(String::class.java, Int::class.java, Long::class.java)
        definition.jpaProperties shouldContainAll mapOf("key1" to "value1", "key2" to "value2")
    }

    @Test
    fun `derived bean names`() {
        val definition =
            jpaDataSourceDefinition {
                name = "myDs"
                dataSourceBeanName = "myDataSource"
            }

        definition.jpaPropertiesBeanName shouldBe "myDsJpaProperties"
        definition.hibernatePropertiesBeanName shouldBe "myDsHibernateProperties"
        definition.hibernateJpaConfigurationBeanName shouldBe "myDsHibernateJpaConfiguration"
        definition.entityManagerFactoryBeanName shouldBe "myDsEntityManagerFactory"
        definition.transactionManagerBeanName shouldBe "myDsTransactionManager"
        definition.persistenceManagedTypesBeanName shouldBe "myDsPersistenceManagedTypes"
        definition.entityManagerFactoryBuilderBeanName shouldBe "myDsEntityManagerFactoryBuilder"
        definition.jpaVendorAdapterBeanName shouldBe "myDsJpaVendorAdapter"
    }

    @Test
    fun `fails without name`() {
        shouldThrow<IllegalStateException> {
            jpaDataSourceDefinition {
                dataSourceBeanName = "ds"
            }
        }.message shouldBe "name is required"
    }

    @Test
    fun `fails without dataSourceBeanName`() {
        shouldThrow<IllegalStateException> {
            jpaDataSourceDefinition {
                name = "test"
            }
        }.message shouldBe "dataSourceBeanName is required"
    }

    @Test
    fun `fails with blank name`() {
        shouldThrow<IllegalArgumentException> {
            jpaDataSourceDefinition {
                name = "   "
                dataSourceBeanName = "ds"
            }
        }.message shouldBe "name must not be blank"
    }

    @Test
    fun `fails with blank dataSourceBeanName`() {
        shouldThrow<IllegalArgumentException> {
            jpaDataSourceDefinition {
                name = "test"
                dataSourceBeanName = "   "
            }
        }.message shouldBe "dataSourceBeanName must not be blank"
    }

    @Test
    fun `defensive copy for entityPackages`() {
        val packages = mutableListOf("com.example")
        val definition =
            JpaDataSourceDefinitionBuilder()
                .name("test")
                .dataSourceBeanName("ds")
                .entityPackages(packages)
                .build()

        // Modifying original list should not affect the definition
        packages.add("com.modified")

        definition.entityPackages shouldContainExactly listOf("com.example")
    }

    @Test
    fun `Java interop builder works`() {
        // Test the static builder() method for Java interop
        val definition =
            JpaDataSourceDefinition
                .builder()
                .name("javaStyle")
                .dataSourceBeanName("javaDataSource")
                .entityPackage("com.example")
                .jpaProperty("key", "value")
                .build()

        definition.name shouldBe "javaStyle"
        definition.dataSourceBeanName shouldBe "javaDataSource"
    }

    @Test
    fun `data class copy works`() {
        val original =
            jpaDataSourceDefinition {
                name = "original"
                dataSourceBeanName = "originalDs"
                jpaProperties["key"] = "value"
            }

        val modified = original.copy(name = "modified")

        modified.name shouldBe "modified"
        modified.dataSourceBeanName shouldBe "originalDs"
        modified.jpaProperties shouldContainAll mapOf("key" to "value")
    }
}
