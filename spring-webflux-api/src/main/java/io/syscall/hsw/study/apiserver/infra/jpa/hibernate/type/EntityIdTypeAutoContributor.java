package io.syscall.hsw.study.apiserver.infra.jpa.hibernate.type;

import io.syscall.hsw.study.apiserver.example.model.EntityId;
import io.syscall.hsw.study.apiserver.example.model.LongEntityId;
import io.syscall.hsw.study.apiserver.example.model.StringEntityId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link EntityId} 계열 타입을 자동으로 등록한다
 */
public class EntityIdTypeAutoContributor implements TypeContributor {

    private static final Logger log = LoggerFactory.getLogger(EntityIdTypeAutoContributor.class);

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        Set<Class<? extends EntityId<?>>> entityIdTypes = Set.of();
        if (typeContributions instanceof MetadataBuilderImplementor metadataBuilderImplementor) {
            entityIdTypes = scanClasses(metadataBuilderImplementor.getBootstrapContext());
        }
        for (var entityIdType : entityIdTypes) {
            AbstractEntityIdJavaType<?> descriptor;
            if (LongEntityId.class.isAssignableFrom(entityIdType)) {
                descriptor = LongEntityIdJavaType.of(entityIdType);
            } else if (StringEntityId.class.isAssignableFrom(entityIdType)) {
                descriptor = StringEntityIdJavaType.of(entityIdType);
            } else {
                log.warn("Skipping unsupported EntityId: {}", entityIdType);
                continue;
            }
            typeContributions.contributeJavaType(descriptor);
        }
    }

    static final Set<String> ELIGIBLE_ANNOTATION_NAMES = Set.of(
            Entity.class.getName(),
            MappedSuperclass.class.getName(),
            Embeddable.class.getName()
    );

    boolean isEligibleType(Class<?> clazz) {
        for (var ann : clazz.getAnnotations()) {
            if (ELIGIBLE_ANNOTATION_NAMES.contains(ann.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }

    private Set<Class<? extends EntityId<?>>> scanClasses(BootstrapContext bootstrapContext) {
        // Hibernate에서 아무 클래스나 줏어먹지 말라고 해서 얘를 만들어둔거 같은데 이렇게까지 해야 되나..?
        var tmpClassLoader = bootstrapContext.getClassLoaderAccess();
        final var entityIdBaseClass = tmpClassLoader.classForName(EntityId.class.getName());

        // final var entityIdBaseClass = EntityId.class; // NOSONAR
        var classLoader = bootstrapContext.getServiceRegistry().getService(ClassLoaderService.class);

        // Spring Data JPA @EntityScan 이나 spring-context-index에 의해 탐지된 엔티티와 기타등등
        // 사용되지 않을 클래스도 들어올수 있지만 뭐....
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

            if (!isEligibleType(clazz)) {
                continue;
            }

            // @Entity
            for (var field : clazz.getDeclaredFields()) {
                if (!entityIdBaseClass.isAssignableFrom(field.getType())) {
                    continue;
                }
                // tmpClassloader -> 실제 classloader의 class로 변환 필요
                Class<? extends EntityId<?>> fieldType = classLoader.classForName(field.getType().getName());
                entityIdTypes.add(fieldType);
            }
        }

        return entityIdTypes;
    }
}
