package com.cyanelix.monitor.service;

import com.cyanelix.monitor.model.MonitoringResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class MonitorService {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public MonitorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MonitoringResult makeRequest(String url, HttpMethod httpMethod) {
        LOG.debug("Sending a {} request to {}", httpMethod, url);

        HttpStatus status;
        try {
            status = restTemplate.exchange(url, httpMethod, null, Object.class).getStatusCode();
        } catch (HttpStatusCodeException statusCodeException) {
            status = statusCodeException.getStatusCode();
        }

        return new MonitoringResult(url, httpMethod, status);
    }
}
