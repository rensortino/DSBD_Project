package com.dsproject.apigateway;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;




@Component
public class MetricsController {



    public final Timer users_register_timer;
    public final Timer users_get_timer;
    public final Timer videos_post;
    public final Timer videos_post_id;
    public final Timer videos_get;
    public final Timer videos_get_id;
    public final Counter request_counter;
    public final Timer videofiles_get_id;
    public final DistributionSummary requests_size;
    public final DistributionSummary response_size;
    public final Counter error_counter;

    public MetricsController(PrometheusMeterRegistry meterRegistry) {

        request_counter = Counter.builder("request_counter").register(meterRegistry);
        users_register_timer = Timer.builder("users_register_timer").tag("method","POST").tag("URI","http://vms:8080/users/register").register(meterRegistry);
        users_get_timer = Timer.builder("user_get_timer").tag("method","GET").tag("URI","http://vms:8080/users/").register(meterRegistry);
        videos_post = Timer.builder("videos_post").tag("method","POST").tag("URI","http://vms:8080/videos/").register(meterRegistry);
        videos_post_id = Timer.builder("videos_post_id").tag("method","POST").tag("URI","http://vms:8080/videos/{id}").register(meterRegistry);
        videos_get = Timer.builder("videos_get").tag("method","GET").tag("URI","http://vms:8080/videos/").register(meterRegistry);
        videos_get_id = Timer.builder("videos_get_id").tag("method","GET").tag("URI","http://vms:8080/video/{id}").register(meterRegistry);
        videofiles_get_id = Timer.builder("videofiles_get_id").tag("method","GET").tag("URI","http://videofiles/{id}/video.mpd").register(meterRegistry);
        requests_size = DistributionSummary.builder("request.size").register(meterRegistry);
        response_size = DistributionSummary.builder("response.size").register(meterRegistry);
        error_counter = Counter.builder("error_counter").register(meterRegistry);

    }
}


