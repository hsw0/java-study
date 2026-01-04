package io.syscall.commons.module.persistence.jpa.multidatasource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Registers a {@link MultiDataSourceJpaBeanFactoryPostProcessor} that discovers {@link
 * JpaDataSourceDefinition} beans and registers corresponding JPA infrastructure beans.
 *
 * <p>This registrar is triggered by the {@link EnableMultiDataSourceJpa} annotation.
 */
public class MultiDataSourceJpaRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // Register a BeanFactoryPostProcessor that will:
        // 1. Find all JpaDataSourceDefinition beans
        // 2. Call MultiDataSourceJpaConfigSupport.registerJpaBeans for each

        if (!registry.containsBeanDefinition("multiDataSourceJpaBeanFactoryPostProcessor")) {
            var bd = new GenericBeanDefinition();
            bd.setBeanClass(MultiDataSourceJpaBeanFactoryPostProcessor.class);
            bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            registry.registerBeanDefinition("multiDataSourceJpaBeanFactoryPostProcessor", bd);
        }
    }
}
