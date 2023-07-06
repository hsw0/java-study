package io.syscall.hsw.study.apiserver.infra.jpa.hibernate.type;

import io.syscall.hsw.study.apiserver.example.model.EntityId;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.service.ServiceRegistry;

/**
 * {@link EntityId} 계열 타입을 자동으로 등록한다
 */
public class EntityIdTypeAutoContributor implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        Set<Class<? extends EntityId<?>>> entityIdTypes = Set.of();
        if (typeContributions instanceof MetadataBuilderImplementor metadataBuilderImplementor) {
            entityIdTypes = scanClasses(metadataBuilderImplementor.getBootstrapContext());
        }
        for (var entityIdType : entityIdTypes) {
            typeContributions.contributeJavaType(LongEntityIdJavaType.of(entityIdType));
        }
    }

    private Set<Class<? extends EntityId<?>>> scanClasses(BootstrapContext bootstrapContext) {
        var tmpClassLoader = bootstrapContext.getClassLoaderAccess();
        var tmpEntityIdClass = tmpClassLoader.classForName(EntityId.class.getName());
        var classLoader = bootstrapContext.getServiceRegistry().getService(ClassLoaderService.class);
        var classNames = bootstrapContext.getScanEnvironment().getExplicitlyListedClassNames();

        Set<Class<? extends EntityId<?>>> entityIdTypes = new HashSet<>();
        for (var name : classNames) {
            Class<?> clazz;
            try {
                clazz = tmpClassLoader.classForName(name);
            } catch (ClassLoadingException e) {
                // ignored. 패키지도 올 수 있음
                continue;
            }

            for (var field : clazz.getDeclaredFields()) {
                if (tmpEntityIdClass.isAssignableFrom(field.getType())) {
                    var fieldType = (Class<? extends EntityId<?>>) classLoader.<EntityId<?>>classForName(
                            field.getType().getName());
                    entityIdTypes.add(fieldType);
                }
            }
        }

        return entityIdTypes;
    }
}
