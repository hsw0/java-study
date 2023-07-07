package io.syscall.commons.entityid.support.hibernate;

import io.syscall.annotations.Nullable;
import io.syscall.commons.entityid.EntityId;
import java.lang.reflect.Modifier;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public abstract class AbstractEntityIdJavaType<T extends EntityId<?>> extends AbstractClassJavaType<T> {

    protected AbstractEntityIdJavaType(Class<? extends T> clazz) {
        super(clazz, ImmutableMutabilityPlan.instance());
        verifyType(clazz);
    }

    private static <T extends EntityId<?>> void verifyType(Class<? extends T> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || !EntityId.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + ": Concrete EntityId class expected");
        }
    }

    @SuppressWarnings("java:S3038") // Sonar: Abstract methods should not be redundant
    @Override
    public abstract JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators);

    @Override
    public String toString(@Nullable T value) {
        if (value == null) {
            return "null";
        }
        return value.asString();
    }
}
