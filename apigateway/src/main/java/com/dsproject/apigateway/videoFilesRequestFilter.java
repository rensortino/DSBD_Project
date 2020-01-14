package com.dsproject.apigateway;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class videoFilesRequestFilter extends
        AbstractGatewayFilterFactory<videoFilesRequestFilter.Config> {


    public videoFilesRequestFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(videoFilesRequestFilter.Config config) {
        return (exchange, chain) -> {
            config.getMetrics().request_counter.increment();
            long start = System.currentTimeMillis();
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        if(exchange.getResponse().getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
                            config.getMetrics().error_counter.increment();
                        }
                        config.getMetrics().videofiles_get_id.record(System.currentTimeMillis() - start,TimeUnit.MILLISECONDS);
                        config.getMetrics().requests_size.record(exchange.getRequest().getHeaders().getContentLength());
                        config.getMetrics().response_size.record(exchange.getResponse().getHeaders().getContentLength());
                    }));
        };
    }




    public static class Config {
        MetricsController metrics;

        public Config(MetricsController metrics) {
            this.metrics = metrics;

        }

        public MetricsController getMetrics() {
            return metrics;
        }
    }
}
