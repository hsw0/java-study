package io.syscall.commons.module.persistence.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
public class PersistenceSupportModuleAutoConfig {}
