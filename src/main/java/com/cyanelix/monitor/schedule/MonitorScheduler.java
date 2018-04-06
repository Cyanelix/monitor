package com.cyanelix.monitor.schedule;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import com.cyanelix.monitor.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitorScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorScheduler.class);

    private final MonitoredEndpoints monitoredEndpoints;
    private final MonitorService monitorService;

    @Autowired
    public MonitorScheduler(MonitoredEndpoints monitoredEndpoints, MonitorService monitorService) {
        this.monitoredEndpoints = monitoredEndpoints;
        this.monitorService = monitorService;
    }

    @Scheduled(fixedDelay = 60000)
    public void monitor() {
        monitoredEndpoints.getEndpoints()
                .parallelStream()
                .map(endpoint -> monitorService.makeRequest(endpoint, HttpMethod.HEAD))
                .filter(MonitoringResult::isError)
                .forEach(this::notify);
    }

    private void notify(MonitoringResult monitoringResult) {
        LOG.warn("An HTTP error code has been returned: {}", monitoringResult.getHttpStatus());
    }
}
