package com.dsproject.apigateway;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsController {

    private final Counter myCounter;
    private final Timer myTimer;

    public MetricsController(PrometheusMeterRegistry meterRegistry) {
        myCounter = Counter
                .builder("mycustomcounter")
                .description("this is my custom counter")
                .register(meterRegistry);
        myTimer = Timer.builder("MyTimer").register(meterRegistry);
    }

    public void increment(){
        myCounter.increment();

    }




}
