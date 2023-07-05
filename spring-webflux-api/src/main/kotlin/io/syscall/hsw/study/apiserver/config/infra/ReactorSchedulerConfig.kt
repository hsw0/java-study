package io.syscall.hsw.study.apiserver.config.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

@Configuration(proxyBeanMethods = false)
internal class ReactorSchedulerConfig {

    @Bean
    internal fun virtualThreadPerTask(): Scheduler {
        return Schedulers.fromExecutorService(Executors.newVirtualThreadPerTaskExecutor(), ::virtualThreadPerTask.name)
    }
}
