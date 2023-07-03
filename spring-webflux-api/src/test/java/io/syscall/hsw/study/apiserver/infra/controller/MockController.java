package io.syscall.hsw.study.apiserver.infra.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/test/echo")
    JsonNode echoJson(@RequestBody JsonNode req) throws Exception {
        return objectMapper.readTree(req.toString());
    }
}
