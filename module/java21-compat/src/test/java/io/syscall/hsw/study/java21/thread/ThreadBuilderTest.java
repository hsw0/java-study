package io.syscall.hsw.study.java21.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
@Timeout(5)
class ThreadBuilderTest {

    static final Logger log = LoggerFactory.getLogger(ThreadBuilderTest.class);

    static final int jvmVersion = Runtime.version().feature();

    @BeforeAll
    static void beforeAll() {
        log.info("JVM version:{}", jvmVersion);
    }

    @EnabledForJreRange(max = JRE.JAVA_17)
    @Test
    void noVirtualThreadOnJava17() {
        Assertions.assertThrows(UnsupportedJvmException.class, ThreadBuilder::ofVirtual);
    }

    @EnabledForJreRange(max = JRE.JAVA_17)
    @Test
    void compatibilityPlatformThreadOnJava17() {
        assertDoesNotThrow(() -> {
            var latch = new CountDownLatch(1);
            var thread = ThreadBuilder.ofPlatform().name("compat layer").start(() -> {
                log.info("It's working!");
                latch.countDown();
            });
            thread.join();
            latch.await();
        });
    }

    @EnabledForJreRange(min = JRE.JAVA_21)
    @Test
    void virtualThreadOnJava21() throws Exception {
        var vtClass = Class.forName("java.lang.BaseVirtualThread");
        var virtualThread = ThreadBuilder.ofVirtual().start(() -> {
            log.info("Current thread: {} {}", Thread.currentThread());
            assertThat(Thread.currentThread().getClass()).isInstanceOf(vtClass);
        });
        virtualThread.join();
    }

    @EnabledForJreRange(min = JRE.JAVA_21)
    @Test
    void nativePlatformThreadOnJava21() throws InterruptedException {
        var builder = ThreadBuilder.ofPlatform();
        assertThat(builder).describedAs("ThreadBuilder.ofPlatform").isInstanceOf(UsingNativeImpl.class);
        var factory = builder.factory();
        assertThat(factory.getClass()).describedAs("ThreadBuilder.factory()")
                .matches(clazz -> "java.base".equals(clazz.getModule().getName()));

        var latch = new CountDownLatch(1);
        var thread = factory.newThread(() -> {
            log.info("It's working!");
            latch.countDown();
        });
        thread.start();
        thread.join();
        latch.await();
    }

}
