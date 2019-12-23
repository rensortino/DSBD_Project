package com.dsproject.apigateway;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


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


