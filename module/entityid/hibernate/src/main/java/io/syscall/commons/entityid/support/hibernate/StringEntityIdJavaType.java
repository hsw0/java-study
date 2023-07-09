package io.syscall.commons.entityid.support.hibernate;

import io.syscall.commons.entityid.EntityId;
import io.syscall.commons.entityid.StringEntityId;
import io.syscall.commons.entityid.StringEntityIdFactory;
import io.syscall.commons.entityid.StringEntityIdSupport;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

@SuppressWarnings("java:S119") // Sonar: Type parameter names should comply with a naming convention
public class StringEntityIdJavaType extends AbstractEntityIdJavaType<StringEntityId> {

    private final transient StringEntityIdFactory<StringEntityId> factory;

    @SuppressWarnings("unchecked")
    public static <T extends StringEntityId> StringEntityIdJavaType of(Class<? extends EntityId<?>> clazz) {
        return new StringEntityIdJavaType((Class<T>) clazz);
    }

    public <T extends StringEntityId> StringEntityIdJavaType(Class<T> clazz) {
        super(clazz);

        @SuppressWarnings("unchecked")
        var tmp = (StringEntityIdFactory<StringEntityId>) StringEntityIdSupport.factory(clazz);
        this.factory = tmp;
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return VarcharJdbcType.INSTANCE;
    }

    @SuppressWarnings("override.return.invalid")
    @Override
    public <DB> @Nullable DB unwrap(@Nullable StringEntityId value, Class<DB> dbType, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (String.class.isAssignableFrom(dbType)) {
            @SuppressWarnings("unchecked")
            var tmp = (DB) value.asString();
            return tmp;
        }

        throw unknownUnwrap(dbType);
    }

    @SuppressWarnings("override.return.invalid")
    @Override
    public <DB> @Nullable StringEntityId wrap(@Nullable DB value, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (value instanceof String s) {
            return factory.create(s);
        }

        throw unknownWrap(value.getClass());
    }
}
