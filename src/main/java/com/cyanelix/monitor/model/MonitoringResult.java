package com.cyanelix.monitor.model;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class MonitoringResult {
    private final String url;
    private final HttpMethod httpMethod;
    private final HttpStatus httpStatus;

    public MonitoringResult(String url, HttpMethod httpMethod, HttpStatus httpStatus) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public boolean isError() {
        return httpStatus.isError();
    }
}
