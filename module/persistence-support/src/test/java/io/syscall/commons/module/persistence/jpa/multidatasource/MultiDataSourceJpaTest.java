package io.syscall.commons.module.persistence.jpa.multidatasource;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.commons.module.persistence.jpa.multidatasource.testconfig.MultiDataSourceTestConfig;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car.CarEntity;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange.OrangeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Integration tests for multi-datasource JPA configuration. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(classes = MultiDataSourceTestConfig.class)
class MultiDataSourceJpaTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("orangeEntityManagerFactory")
    EntityManagerFactory orangeEmf;

    @Autowired
    @Qualifier("carEntityManagerFactory")
    EntityManagerFactory carEmf;

    @Autowired
    @Qualifier("orangeTransactionManager")
    PlatformTransactionManager orangeTxManager;

    @Autowired
    @Qualifier("carTransactionManager")
    PlatformTransactionManager carTxManager;

    @Order(0)
    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Order(1)
    @Test
    void entityManagerFactoryBeansAreRegistered() {
        assertThat(orangeEmf).isNotNull();
        assertThat(carEmf).isNotNull();
        assertThat(orangeEmf).isNotSameAs(carEmf);
    }

    @Order(2)
    @Test
    void transactionManagerBeansAreRegistered() {
        assertThat(orangeTxManager).isNotNull();
        assertThat(carTxManager).isNotNull();
        assertThat(orangeTxManager).isNotSameAs(carTxManager);
    }

    @Order(3)
    @Test
    void orangeEmfKnowsOnlyOrangeEntities() {
        var metamodel = orangeEmf.getMetamodel();
        var entityNames = metamodel.getEntities().stream()
                .map(e -> e.getJavaType().getSimpleName())
                .toList();

        assertThat(entityNames).contains("OrangeEntity").doesNotContain("CarEntity");
    }

    @Order(4)
    @Test
    void carEmfKnowsOnlyCarEntities() {
        var metamodel = carEmf.getMetamodel();
        var entityNames = metamodel.getEntities().stream()
                .map(e -> e.getJavaType().getSimpleName())
                .toList();

        assertThat(entityNames).contains("CarEntity").doesNotContain("OrangeEntity");
    }

    @Order(5)
    @Test
    void canPersistToOrangeDataSource() {
        var txTemplate = new TransactionTemplate(orangeTxManager);

        Long id = txTemplate.execute(status -> {
            EntityManager em = orangeEmf.createEntityManager();
            em.joinTransaction();

            var entity = new OrangeEntity("test-orange");
            em.persist(entity);
            em.flush();

            return entity.getId();
        });

        assertThat(id).isNotNull();

        // Verify it was persisted
        txTemplate.executeWithoutResult(status -> {
            EntityManager em = orangeEmf.createEntityManager();
            em.joinTransaction();

            var loaded = em.find(OrangeEntity.class, id);
            assertThat(loaded).isNotNull();
            assertThat(loaded.getName()).isEqualTo("test-orange");
        });
    }

    @Order(6)
    @Test
    void canPersistToCarDataSource() {
        var txTemplate = new TransactionTemplate(carTxManager);

        Long id = txTemplate.execute(status -> {
            EntityManager em = carEmf.createEntityManager();
            em.joinTransaction();

            var entity = new CarEntity("test-car");
            em.persist(entity);
            em.flush();

            return entity.getId();
        });

        assertThat(id).isNotNull();

        // Verify it was persisted
        txTemplate.executeWithoutResult(status -> {
            EntityManager em = carEmf.createEntityManager();
            em.joinTransaction();

            var loaded = em.find(CarEntity.class, id);
            assertThat(loaded).isNotNull();
            assertThat(loaded.getData()).isEqualTo("test-car");
        });
    }

    @Order(7)
    @Test
    void dataSourcesAreIsolated() {
        // Create entity in orange
        var orangeTxTemplate = new TransactionTemplate(orangeTxManager);
        Long orangeId = orangeTxTemplate.execute(status -> {
            EntityManager em = orangeEmf.createEntityManager();
            em.joinTransaction();
            var entity = new OrangeEntity("isolation-test");
            em.persist(entity);
            return entity.getId();
        });

        assertThat(orangeId).isNotNull();

        // Create entity in car
        var carTxTemplate = new TransactionTemplate(carTxManager);
        Long carId = carTxTemplate.execute(status -> {
            EntityManager em = carEmf.createEntityManager();
            em.joinTransaction();
            var entity = new CarEntity("isolation-test");
            em.persist(entity);
            return entity.getId();
        });

        assertThat(carId).isNotNull();

        // Both should exist in their respective datasources
        // The IDs being independent confirms different databases
        assertThat(orangeId).isEqualTo(carId); // Both start at 1 in separate DBs
    }

    @Order(8)
    @Test
    void jpaPropertiesBeansAreRegistered() {
        assertThat(context.containsBean("orangeJpaProperties")).isTrue();
        assertThat(context.containsBean("carJpaProperties")).isTrue();
    }

    @Order(9)
    @Test
    void hibernatePropertiesBeansAreRegistered() {
        assertThat(context.containsBean("orangeHibernateProperties")).isTrue();
        assertThat(context.containsBean("carHibernateProperties")).isTrue();
    }

    @Order(10)
    @Test
    void persistenceManagedTypesBeansAreRegistered() {
        assertThat(context.containsBean("orangePersistenceManagedTypes")).isTrue();
        assertThat(context.containsBean("carPersistenceManagedTypes")).isTrue();
    }
}
