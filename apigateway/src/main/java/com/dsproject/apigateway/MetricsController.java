package com.dsproject.apigateway;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    @Autowired
    private MeterRegistry registry;


    @GetMapping("/**")
    public void count() {
        registry.counter("Counter").increment();
        System.out.println(registry.counter("Counter").count());
    }


}
