package io.syscall.commons.module.persistence.jpa.multidatasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * Post-processor that discovers {@link JpaDataSourceDefinition} beans and registers corresponding
 * JPA infrastructure beans.
 *
 * <p>This processor runs early in the Spring lifecycle to ensure JPA beans are registered before
 * they are needed by other beans.
 */
public class MultiDataSourceJpaBeanFactoryPostProcessor
        implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    private static final Logger log = LoggerFactory.getLogger(MultiDataSourceJpaBeanFactoryPostProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // At this point, bean definitions are registered but beans aren't instantiated yet.
        // We cannot get actual JpaDataSourceDefinition instances here.
        // Registration will happen in postProcessBeanFactory instead.
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Now we can get actual bean instances
        var definitions = beanFactory.getBeansOfType(JpaDataSourceDefinition.class);

        if (definitions.isEmpty()) {
            log.debug("No JpaDataSourceDefinition beans found");
            return;
        }

        log.info("Found {} JpaDataSourceDefinition bean(s): {}", definitions.size(), definitions.keySet());

        if (beanFactory instanceof BeanDefinitionRegistry registry) {
            for (var entry : definitions.entrySet()) {
                log.debug("Processing JpaDataSourceDefinition: {}", entry.getKey());
                MultiDataSourceJpaConfigSupport.registerJpaBeans(registry, entry.getValue());
            }
        } else {
            throw new IllegalStateException("BeanFactory is not a BeanDefinitionRegistry: "
                    + beanFactory.getClass().getName());
        }
    }

    @Override
    public int getOrder() {
        // Run early to register beans before other post-processors
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
