package io.syscall.commons.module.persistence.jpa.multidatasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link JpaDataSourceDefinitionBuilder}. */
class JpaDataSourceDefinitionBuilderTest {

    @Test
    void buildsWithRequiredFieldsOnly() {
        var definition = JpaDataSourceDefinitionBuilder.builder()
                .name("test")
                .dataSourceBeanName("testDataSource")
                .build();

        assertThat(definition.name()).isEqualTo("test");
        assertThat(definition.dataSourceBeanName()).isEqualTo("testDataSource");
        assertThat(definition.entityPackages()).isEmpty();
        assertThat(definition.entityClasses()).isEmpty();
        assertThat(definition.jpaProperties()).isEmpty();
    }

    @Test
    void buildsWithAllFields() {
        var definition = JpaDataSourceDefinitionBuilder.builder()
                .name("full")
                .dataSourceBeanName("fullDataSource")
                .entityPackage("com.example.entities")
                .entityPackages("com.example.more", "com.example.extra")
                .entityClass(String.class)
                .entityClasses(Integer.class, Long.class)
                .jpaProperty("key1", "value1")
                .jpaProperties(Map.of("key2", "value2"))
                .build();

        assertThat(definition.name()).isEqualTo("full");
        assertThat(definition.dataSourceBeanName()).isEqualTo("fullDataSource");
        assertThat(definition.entityPackages())
                .containsExactly("com.example.entities", "com.example.more", "com.example.extra");
        assertThat(definition.entityClasses()).containsExactly(String.class, Integer.class, Long.class);
        assertThat(definition.jpaProperties()).containsEntry("key1", "value1").containsEntry("key2", "value2");
    }

    @Test
    void derivedBeanNames() {
        var definition = JpaDataSourceDefinitionBuilder.builder()
                .name("myDs")
                .dataSourceBeanName("myDataSource")
                .build();

        assertThat(definition.jpaPropertiesBeanName()).isEqualTo("myDsJpaProperties");
        assertThat(definition.hibernatePropertiesBeanName()).isEqualTo("myDsHibernateProperties");
        assertThat(definition.hibernateJpaConfigurationBeanName()).isEqualTo("myDsHibernateJpaConfiguration");
        assertThat(definition.entityManagerFactoryBeanName()).isEqualTo("myDsEntityManagerFactory");
        assertThat(definition.transactionManagerBeanName()).isEqualTo("myDsTransactionManager");
        assertThat(definition.persistenceManagedTypesBeanName()).isEqualTo("myDsPersistenceManagedTypes");
        assertThat(definition.entityManagerFactoryBuilderBeanName()).isEqualTo("myDsEntityManagerFactoryBuilder");
        assertThat(definition.jpaVendorAdapterBeanName()).isEqualTo("myDsJpaVendorAdapter");
    }

    @Test
    void failsWithoutName() {
        assertThatThrownBy(() -> JpaDataSourceDefinitionBuilder.builder()
                        .dataSourceBeanName("ds")
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("name");
    }

    @Test
    void failsWithoutDataSourceBeanName() {
        assertThatThrownBy(() ->
                        JpaDataSourceDefinitionBuilder.builder().name("test").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dataSourceBeanName");
    }

    @Test
    void failsWithBlankName() {
        assertThatThrownBy(() -> JpaDataSourceDefinitionBuilder.builder()
                        .name("   ")
                        .dataSourceBeanName("ds")
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void failsWithBlankDataSourceBeanName() {
        assertThatThrownBy(() -> JpaDataSourceDefinitionBuilder.builder()
                        .name("test")
                        .dataSourceBeanName("   ")
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dataSourceBeanName");
    }

    @Test
    void recordDefensiveCopy() {
        var packages = new ArrayList<>(List.of("com.example"));
        var definition = JpaDataSourceDefinitionBuilder.builder()
                .name("test")
                .dataSourceBeanName("ds")
                .entityPackages(packages)
                .build();

        // Modifying original list should not affect the definition
        packages.add("com.modified");

        assertThat(definition.entityPackages()).containsExactly("com.example");
    }
}
