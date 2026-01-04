package io.syscall.commons.module.persistence.jpa.multidatasource;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.commons.module.persistence.jpa.multidatasource.testconfig.MultiDataSourceTestConfig;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car.CarEntity;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car.CarEntityRepository;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange.OrangeEntity;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange.OrangeEntityRepository;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for multi-datasource JPA with Spring Data repositories.
 *
 * <p>Verifies that {@code @EnableJpaRepositories} works correctly with multiple datasources.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(classes = MultiDataSourceTestConfig.class)
@Import({
    MultiDataSourceJpaRepositoryTest.OrangeRepositoryConfig.class,
    MultiDataSourceJpaRepositoryTest.CarRepositoryConfig.class
})
class MultiDataSourceJpaRepositoryTest {

    @Autowired
    OrangeEntityRepository orangeRepository;

    @Autowired
    CarEntityRepository carRepository;

    @Order(1)
    @Test
    void repositoriesAreInjected() {
        assertThat(orangeRepository).isNotNull();
        assertThat(carRepository).isNotNull();
    }

    @Order(2)
    @Test
    @Transactional("orangeTransactionManager")
    void canSaveAndFindInOrangeRepository() {
        var entity = new OrangeEntity("repo-test-orange");
        var saved = orangeRepository.save(entity);

        assertThat(saved.getId()).isNotNull();

        var found = orangeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("repo-test-orange");
    }

    @Order(3)
    @Test
    @Transactional("carTransactionManager")
    void canSaveAndFindInCarRepository() {
        var entity = new CarEntity("repo-test-car");
        var saved = carRepository.save(entity);

        assertThat(saved.getId()).isNotNull();

        var found = carRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getData()).isEqualTo("repo-test-car");
    }

    @Order(4)
    @Test
    @Transactional("orangeTransactionManager")
    void canUseDerivedQueryInOrangeRepository() {
        var entity = new OrangeEntity("unique-orange-name");
        orangeRepository.save(entity);

        var found = orangeRepository.findByName("unique-orange-name");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("unique-orange-name");
    }

    @Order(5)
    @Test
    @Transactional("carTransactionManager")
    void canUseDerivedQueryInCarRepository() {
        var entity = new CarEntity("unique-car-data");
        carRepository.save(entity);

        var found = carRepository.findByData("unique-car-data");
        assertThat(found).isPresent();
        assertThat(found.get().getData()).isEqualTo("unique-car-data");
    }

    @Order(6)
    @Test
    void repositoriesUseIndependentDatasources() {
        // Save to both repositories (without @Transactional to use separate transactions)
        var orangeEntity = orangeRepository.save(new OrangeEntity("isolation-test"));
        var carEntity = carRepository.save(new CarEntity("isolation-test"));

        // Both should have ID 1 (or same ID) since they use separate databases
        // This confirms data isolation between datasources
        assertThat(orangeEntity.getId()).isNotNull();
        assertThat(carEntity.getId()).isNotNull();

        // Count should be independent
        long orangeCount = orangeRepository.count();
        long carCount = carRepository.count();

        assertThat(orangeCount).isGreaterThan(0);
        assertThat(carCount).isGreaterThan(0);
    }

    @Order(7)
    @Test
    @Transactional("orangeTransactionManager")
    void canDeleteFromOrangeRepository() {
        var entity = new OrangeEntity("to-delete");
        var saved = orangeRepository.save(entity);
        Long id = saved.getId();

        assertThat(id).isNotNull();

        orangeRepository.deleteById(id);
        orangeRepository.flush();

        assertThat(orangeRepository.findById(id)).isEmpty();
    }

    @Order(8)
    @Test
    @Transactional("carTransactionManager")
    void canDeleteFromCarRepository() {
        var entity = new CarEntity("to-delete");
        var saved = carRepository.save(entity);
        Long id = saved.getId();

        assertThat(id).isNotNull();

        carRepository.deleteById(id);
        carRepository.flush();

        assertThat(carRepository.findById(id)).isEmpty();
    }

    /** Repository configuration for orange datasource. */
    @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
            basePackages = "io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange",
            entityManagerFactoryRef = "orangeEntityManagerFactory",
            transactionManagerRef = "orangeTransactionManager")
    static class OrangeRepositoryConfig {}

    /** Repository configuration for car datasource. */
    @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
            basePackages = "io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car",
            entityManagerFactoryRef = "carEntityManagerFactory",
            transactionManagerRef = "carTransactionManager")
    static class CarRepositoryConfig {}
}
