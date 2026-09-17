package com.sgd.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleHealthController {

    @GetMapping("/healthz")
    public String health() {
        return "OK";
    }
}
