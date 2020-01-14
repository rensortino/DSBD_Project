package com.dsproject.apigateway;

import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class vmsRequestFilter extends
        AbstractGatewayFilterFactory<vmsRequestFilter.Config> {


    public vmsRequestFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            config.getMetrics().request_counter.increment();
            config.getMetrics().requests_size.record(exchange.getRequest().getHeaders().getContentLength());
            long start = System.currentTimeMillis();
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        if(exchange.getResponse().getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
                            config.getMetrics().error_counter.increment();
                        }
                        config.getMetrics().response_size.record(exchange.getResponse().getHeaders().getContentLength());
                        if(exchange.getRequest().getMethod() == HttpMethod.GET){
                            switch (exchange.getRequest().getPath().toString()){
                                case "/users/":
                                    config.getMetrics().users_get_timer.record(System.currentTimeMillis() - start,TimeUnit.MILLISECONDS);
                                    break;
                                case "/videos/":
                                    config.getMetrics().videos_get.record(System.currentTimeMillis() - start,TimeUnit.MILLISECONDS);
                                    break;
                                default:
                                    if(exchange.getRequest().getPath().toString().matches("/videos/\\w{1,}")) {
                                        config.getMetrics().videos_get_id.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                                    }
                                    break;
                            }
                        }
                        if(exchange.getRequest().getMethod() == HttpMethod.POST){
                            switch (exchange.getRequest().getPath().toString()){
                                case "/users/register":
                                    config.getMetrics().users_register_timer.record(System.currentTimeMillis() - start,TimeUnit.MILLISECONDS);
                                    break;
                                case "/videos/":
                                    config.getMetrics().videos_post.record(System.currentTimeMillis() - start,TimeUnit.MILLISECONDS);
                                    break;
                                default:
                                    if(exchange.getRequest().getPath().toString().matches("/videos/\\w{1,}")) {
                                        config.getMetrics().videos_post_id.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                                    }
                                    break;
                            }
                        }
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