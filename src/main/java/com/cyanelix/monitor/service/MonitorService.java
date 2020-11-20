package com.cyanelix.monitor.service;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MonitorService {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorService.class);

    private final RestTemplate restTemplate;
    private final AtomicInteger checksCount;

    @Autowired
    public MonitorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.checksCount = new AtomicInteger(0);
    }

    public MonitoringResult makeRequest(MonitoredEndpoints.Check check) {
        LOG.debug("Sending a {} request to {}", check.getMethod(), check.getUrl());

        checksCount.incrementAndGet();

        HttpStatus status = null;
        String body;
        try {
            ResponseEntity<String> entity = restTemplate.exchange(check.getUrl(), check.getMethod(), null, String.class);
            status = entity.getStatusCode();
            body = entity.getBody();
        } catch (HttpStatusCodeException statusCodeException) {
            status = statusCodeException.getStatusCode();
            body = statusCodeException.getResponseBodyAsString();
        } catch (Exception ex) {
            body = ex.getMessage();
        }

        return new MonitoringResult(check, status, body);
    }

    public void resetChecksCount() {
        checksCount.lazySet(0);
    }

    public int getChecksCount() {
        return checksCount.get();
    }
}
