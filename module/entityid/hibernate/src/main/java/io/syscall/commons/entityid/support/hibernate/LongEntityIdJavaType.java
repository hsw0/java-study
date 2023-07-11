package io.syscall.commons.entityid.support.hibernate;

import io.syscall.commons.entityid.EntityId;
import io.syscall.commons.entityid.LongEntityId;
import io.syscall.commons.entityid.LongEntityIdFactory;
import io.syscall.commons.entityid.LongEntityIdSupport;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

// Sonar: java:S119: Type parameter names should comply with a naming convention
@SuppressWarnings({"serial", "java:S119"})
public class LongEntityIdJavaType extends AbstractEntityIdJavaType<LongEntityId> {

    private final transient LongEntityIdFactory<LongEntityId> factory;

    @SuppressWarnings("unchecked")
    public static <T extends LongEntityId> LongEntityIdJavaType of(Class<? extends EntityId<?>> clazz) {
        return new LongEntityIdJavaType((Class<T>) clazz);
    }

    public <T extends LongEntityId> LongEntityIdJavaType(Class<T> clazz) {
        super(clazz);

        @SuppressWarnings("unchecked")
        var tmp = (LongEntityIdFactory<LongEntityId>) LongEntityIdSupport.factory(clazz);
        this.factory = tmp;
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return BigIntJdbcType.INSTANCE;
    }

    @SuppressWarnings("override.return.invalid")
    @Override
    public <DB> @Nullable DB unwrap(@Nullable LongEntityId value, Class<DB> dbType, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (Long.class.isAssignableFrom(dbType)) {
            @SuppressWarnings("unchecked")
            var tmp = (DB) value.getValue();
            return tmp;
        } else if (String.class.isAssignableFrom(dbType)) {
            @SuppressWarnings("unchecked")
            var tmp = (DB) Long.toString(value.getValue());
            return tmp;
        }

        throw unknownUnwrap(dbType);
    }

    @SuppressWarnings("override.return.invalid")
    @Override
    public <DB> @Nullable LongEntityId wrap(@Nullable DB value, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (value instanceof Long boxedLong) {
            return factory.create(boxedLong);
        }

        throw unknownWrap(value.getClass());
    }
}
