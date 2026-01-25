package io.syscall.commons.module.persistence.jpa.multidatasource;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.AotDetector;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * Post-processor that discovers {@link JpaDataSourceDefinition} beans and registers corresponding
 * JPA infrastructure beans.
 *
 * <p>This processor runs early in the Spring lifecycle to ensure JPA beans are registered before
 * they are needed by other beans.
 */
public class MultiDataSourceJpaBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(MultiDataSourceJpaBeanFactoryPostProcessor.class);

    private @MonotonicNonNull MultiDataSourceJpaConfigSupport configSupport;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        this.configSupport = new MultiDataSourceJpaConfigSupport(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // Skip during AOT runtime - bean definitions are already pre-generated
        if (AotDetector.useGeneratedArtifacts()) {
            log.debug("Running in AOT runtime mode, skipping dynamic JPA bean registration");
            return;
        }

        var definitions = beanFactory.getBeansOfType(JpaDataSourceDefinition.class);
        if (definitions.isEmpty()) {
            log.debug("No JpaDataSourceDefinition beans found");
            return;
        }

        for (var entry : definitions.entrySet()) {
            configSupport.registerJpaBeans(entry.getValue());
        }
    }
}
