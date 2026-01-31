package io.syscall.commons.module.jackson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;

public interface ObjectMapperCustomizer {

    @CanIgnoreReturnValue
    <M extends ObjectMapper, B extends MapperBuilder<M, B>> B customize(B builder);
}
