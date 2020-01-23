package com.dsproject.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
public class ConfigFile {



    @Value(value = "${VMS_HOST}")
    private String vms;



    @Bean
    public RouteLocator Gateway(RouteLocatorBuilder builder){

        return builder.routes().route(
                p -> p.path("/vms/**")
                        .filters(f-> f.rewritePath("/vms/(?<service>.*)","/${service}"))
                        .uri(vms)
                        .id("vms")
        ) .route(
                p ->p.path("/videofiles/**")
                        .filters(f ->f.stripPrefix(1))
                        .uri("file:///videofiles")
                        .id("videofiles")
        )

                .build();
    }

    @Bean
    RouterFunction staticResourceLocator(){
        return RouterFunctions.resources("/videofiles/**)", new FileSystemResource("/videofiles"));
    }
}
