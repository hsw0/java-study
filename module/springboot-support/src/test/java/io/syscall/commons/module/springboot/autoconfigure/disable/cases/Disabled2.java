package io.syscall.commons.module.springboot.autoconfigure.disable.cases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class Disabled2 {

    private static final Logger log = LoggerFactory.getLogger(Disabled2.class);

    public Disabled2() {
        log.error("Shouldn't");
    }
}
