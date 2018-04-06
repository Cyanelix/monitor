package com.cyanelix.monitor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("monitored")
public class MonitoredEndpoints {
    private List<String> endpoints;

    public MonitoredEndpoints() {
        endpoints = new ArrayList<>();
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
}
