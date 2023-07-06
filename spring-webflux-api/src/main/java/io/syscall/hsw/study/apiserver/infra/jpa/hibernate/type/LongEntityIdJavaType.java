package io.syscall.hsw.study.apiserver.infra.jpa.hibernate.type;

import io.syscall.hsw.study.apiserver.example.model.EntityId;
import io.syscall.hsw.study.apiserver.example.model.LongEntityId;
import javax.annotation.Nullable;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.springframework.data.util.KotlinReflectionUtils;


//@JavaTypeRegistration(javaType = PersonId.class, descriptorClass = LongEntityIdJavaType.class)
@SuppressWarnings("java:S119") // Sonar: Type parameter names should comply with a naming convention
public class LongEntityIdJavaType extends AbstractEntityIdJavaType<LongEntityId> {

    @SuppressWarnings("unchecked")
    public static <T extends LongEntityId> LongEntityIdJavaType of(Class<? extends EntityId<?>> clazz) {
        return new LongEntityIdJavaType((Class<T>) clazz);
    }

    public <T extends LongEntityId> LongEntityIdJavaType(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return BigIntJdbcType.INSTANCE;
    }

    @Nullable
    @Override
    public <DB> DB unwrap(@Nullable LongEntityId value, Class<DB> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        return (DB) value.getValue();
    }

    @Nullable
    @Override
    public <DB> LongEntityId wrap(@Nullable DB value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (!KotlinReflectionUtils.isSupportedKotlinClass(getJavaTypeClass())) {
            throw unknownWrap(value.getClass());
        }
        var factory = LongEntityId.factory(getJavaTypeClass());
        return factory.create((Long) value);
    }

}
