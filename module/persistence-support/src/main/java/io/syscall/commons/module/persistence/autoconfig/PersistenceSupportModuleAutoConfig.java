package io.syscall.commons.module.persistence.autoconfig;

import io.syscall.commons.module.persistence.jdbc.config.MultiDataSourceBaseConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@Import({MultiDataSourceBaseConfig.class})
public class PersistenceSupportModuleAutoConfig {}
