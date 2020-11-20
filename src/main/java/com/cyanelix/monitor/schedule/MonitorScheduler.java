package com.cyanelix.monitor.schedule;

import com.cyanelix.monitor.configuration.DigestConfiguration;
import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import com.cyanelix.monitor.service.MonitorService;
import com.cyanelix.monitor.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitorScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorScheduler.class);

    private final MonitoredEndpoints monitoredEndpoints;
    private final DigestConfiguration digestConfiguration;
    private final MonitorService monitorService;
    private final NotificationService notificationService;

    @Autowired
    public MonitorScheduler(MonitoredEndpoints monitoredEndpoints, DigestConfiguration digestConfiguration, MonitorService monitorService, NotificationService notificationService) {
        this.monitoredEndpoints = monitoredEndpoints;
        this.digestConfiguration = digestConfiguration;
        this.monitorService = monitorService;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 60000)
    public void monitor() {
        monitoredEndpoints.getChecks()
                .parallelStream()
                .map(monitorService::makeRequest)
                .filter(MonitoringResult::isError)
                .forEach(this::notify);
    }

    @Scheduled(fixedDelay = 86400000)
    public void dailyDigest() {
        int checksCount = monitorService.getChecksCount();
        notificationService.sendDailyDigest(digestConfiguration.getRecipients(), checksCount);
        monitorService.resetChecksCount();
    }

    private void notify(MonitoringResult monitoringResult) {
        LOG.debug("An unexpected HTTP status code has been returned: {}", monitoringResult.getHttpStatus());
        notificationService.sendNotification(monitoringResult);
    }
}
