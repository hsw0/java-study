package io.syscall.hsw.study.apiserver.infra.jpa.hibernate.type;

import io.syscall.hsw.study.apiserver.example.model.EntityId;
import io.syscall.hsw.study.apiserver.example.model.StringEntityId;
import io.syscall.hsw.study.apiserver.example.model.StringEntityId.StringEntityIdFactory;
import javax.annotation.Nullable;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;


@SuppressWarnings("java:S119") // Sonar: Type parameter names should comply with a naming convention
public class StringEntityIdJavaType extends AbstractEntityIdJavaType<StringEntityId> {

    private final StringEntityIdFactory<StringEntityId> factory;

    @SuppressWarnings("unchecked")
    public static <T extends StringEntityId> StringEntityIdJavaType of(Class<? extends EntityId<?>> clazz) {
        return new StringEntityIdJavaType((Class<T>) clazz);
    }

    public <T extends StringEntityId> StringEntityIdJavaType(Class<T> clazz) {
        super(clazz);

        @SuppressWarnings("unchecked")
        var tmp = (StringEntityIdFactory<StringEntityId>) StringEntityId.factory(clazz);
        this.factory = tmp;
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return VarcharJdbcType.INSTANCE;
    }

    @Nullable
    @Override
    public <DB> DB unwrap(@Nullable StringEntityId value, Class<DB> dbType, WrapperOptions options) {
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

    @Nullable
    @Override
    public <DB> StringEntityId wrap(@Nullable DB value, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (value instanceof String s) {
            return factory.create(s);
        }

        throw unknownWrap(value.getClass());
    }

}
