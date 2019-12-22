package com.dsproject.apigateway;

// import io.prometheus.client.springboot.EnableSpringBootMetricsCollector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ApigatewayApplication {
    //@Value(value = "${VMS_HOST}")
    private String vms = "http://vms:8080";

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayApplication.class, args);
    }

    @Bean
    public RouteLocator  Gateway(RouteLocatorBuilder builder){


        return builder.routes().route(
                p -> p.path("/vms/**")
                        .filters(f-> f.rewritePath("/vms/(?<service>.*)","/${service}"))
                        .uri(vms)
        ).route(
                p ->p.path("/videofiles/**")
                        .uri("file:///videofiles/")
                    )
                .build();
    }


}
