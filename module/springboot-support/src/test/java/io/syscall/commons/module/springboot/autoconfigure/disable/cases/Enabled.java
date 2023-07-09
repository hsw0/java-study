package io.syscall.commons.module.springboot.autoconfigure.disable.cases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class Enabled {

    private static final Logger log = LoggerFactory.getLogger(Enabled.class);

    public Enabled() {
        log.info("letmein");
    }
}
