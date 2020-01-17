package com.dsproject.apigateway;

// import io.prometheus.client.springboot.EnableSpringBootMetricsCollector;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import org.apache.logging.log4j.util.StackLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ApigatewayApplication {



    public static void main(String[] args) {
        SpringApplication.run(ApigatewayApplication.class, args);
    }
    @Value(value = "${VMS_HOST}")
    private String vms;

    vmsRequestFilter filter_vms = new vmsRequestFilter();
    videoFilesRequestFilter filter_videofiles = new videoFilesRequestFilter();

    @Autowired
    MetricsController metrics;

    @Bean
    public RouteLocator  Gateway(RouteLocatorBuilder builder){


        return builder.routes().route(
                p -> p.path("/vms/**")
                        .filters(f-> f.rewritePath("/vms/(?<service>.*)","/${service}")
                        .filter(filter_vms.apply(new vmsRequestFilter.Config(metrics))))
                        .uri(vms)
        ).route(
                p ->p.path("/videofiles/**")
                        .filters(f ->f.filter(filter_videofiles.apply(new videoFilesRequestFilter.Config(metrics))))
                        .uri("file:///videofiles/")
                    )
                .build();
    }


}
