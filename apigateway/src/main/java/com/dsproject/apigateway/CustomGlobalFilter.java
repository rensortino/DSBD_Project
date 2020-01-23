package com.dsproject.apigateway;


import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CustomGlobalFilter implements GlobalFilter { ;

    final Logger logger =
            LoggerFactory.getLogger(CustomGlobalFilter.class);

    @Autowired
    MetricsController metrics;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        metrics.getRequest_counter().increment();
        metrics.getRequests_size().record(exchange.getRequest().getHeaders().getContentLength());
        metrics.setRequest_Time(exchange.getRequest().getURI().toString(),exchange.getRequest().getMethodValue());
        long start = System.currentTimeMillis();
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    logger.info(exchange.getRequest().getPath().toString());
                    logger.info(exchange.getResponse().toString());

                    if(exchange.getResponse().getStatusCode() != HttpStatus.OK){
                        metrics.getError_counter().increment();
                    }
                    metrics.getResponse_size().record(exchange.getResponse().getHeaders().getContentLength());
                    metrics.getRequest_Time().record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                }));
    }

}
