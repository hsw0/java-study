package io.syscall.hsw.study.apiserver

import jdk.jfr.consumer.RecordingStream
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.aot.AbstractAotProcessor
import org.springframework.context.support.DefaultLifecycleProcessor
import org.springframework.core.metrics.jfr.FlightRecorderApplicationStartup

@SpringBootApplication
public class ApiApplication

public fun main(args: Array<String>) {
    if (System.getProperty(AbstractAotProcessor.AOT_PROCESSING) == null &&
        System.getProperty(DefaultLifecycleProcessor.EXIT_PROPERTY_NAME) == null
    ) {
        val jfrConfig = jdk.jfr.Configuration.getConfiguration("default")
        val jfrRecording = RecordingStream(jfrConfig)
        jfrRecording.startAsync()
    }

    runApplication<ApiApplication>(args = args) {
        applicationStartup = FlightRecorderApplicationStartup()
    }
}
