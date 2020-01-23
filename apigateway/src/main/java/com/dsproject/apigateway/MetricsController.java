package com.dsproject.apigateway;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
public class MetricsController {




    private final Counter request_counter;
    private final DistributionSummary requests_size;
    private final DistributionSummary response_size;
    private final Counter error_counter;
    private   Timer Request_Time;
    private final PrometheusMeterRegistry meterRegistry;

    public MetricsController( PrometheusMeterRegistry meterRegistry) {

        request_counter = Counter.builder("request_counter").tag("URI","all").register(meterRegistry);
        requests_size = DistributionSummary.builder("request.size").register(meterRegistry);
        response_size = DistributionSummary.builder("response.size").register(meterRegistry);
        error_counter = Counter.builder("error_counter").register(meterRegistry);
        this.meterRegistry = meterRegistry;

    }

    public void setRequest_Time(String Uri, String method) {
        Request_Time =Timer.builder("time_request").tag("URI",Uri).tag("method",method).register(meterRegistry);
    }

    public Counter getRequest_counter() {
        return request_counter;
    }

    public DistributionSummary getRequests_size() {
        return requests_size;
    }

    public DistributionSummary getResponse_size() {
        return response_size;
    }

    public Counter getError_counter() {
        return error_counter;
    }

    public Timer getRequest_Time() {
        return Request_Time;
    }
}


