package com.cyanelix.monitor.schedule;

import com.cyanelix.monitor.configuration.DigestConfiguration;
import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import com.cyanelix.monitor.service.MonitorService;
import com.cyanelix.monitor.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MonitorSchedulerTest {
    @Mock
    private DigestConfiguration digestConfiguration;

    @Mock
    private MonitorService monitorService;

    @Mock
    private NotificationService notificationService;

    @Test
    public void noEndpointsToMonitor_serviceNotCalled_noNotifications() {
        // Given...
        MonitoredEndpoints monitoredEndpoints = new MonitoredEndpoints();

        // When...
        new MonitorScheduler(monitoredEndpoints, digestConfiguration, monitorService, notificationService).monitor();

        // Then...
        verify(monitorService, never()).makeRequest(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void endpointReturnsSuccess_serviceCalledOnce_noNotifications() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setExpectedStatus(HttpStatus.OK);

        MonitoredEndpoints monitoredEndpoints = new MonitoredEndpoints();
        monitoredEndpoints.setChecks(Collections.singletonList(check));

        given(monitorService.makeRequest(check))
                .willReturn(new MonitoringResult(check, HttpStatus.OK, ""));

        // When...
        new MonitorScheduler(monitoredEndpoints, digestConfiguration, monitorService, notificationService).monitor();

        // Then...
        verify(monitorService).makeRequest(check);
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void endpointReturnsError_serviceCalledOnce_oneNotification() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();

        MonitoredEndpoints monitoredEndpoints = new MonitoredEndpoints();
        monitoredEndpoints.setChecks(Collections.singletonList(check));

        given(monitorService.makeRequest(check))
                .willReturn(new MonitoringResult(check, HttpStatus.INTERNAL_SERVER_ERROR, ""));

        MonitoringResult expectedResult = new MonitoringResult(check, HttpStatus.INTERNAL_SERVER_ERROR, "");

        // When...
        new MonitorScheduler(monitoredEndpoints, digestConfiguration, monitorService, notificationService).monitor();

        // Then...
        verify(notificationService).sendNotification(expectedResult);
    }

    @Test
    public void sendDigest_resetsCount() {
        // Given...
        String[] recipients = {"digest@example.com"};

        DigestConfiguration digestConfiguration = new DigestConfiguration();
        digestConfiguration.setRecipients(recipients);

        given(monitorService.getChecksCount()).willReturn(3);

        // When...
        new MonitorScheduler(new MonitoredEndpoints(), digestConfiguration, monitorService, notificationService).dailyDigest();

        // Then...
        verify(notificationService).sendDailyDigest(recipients, 3);
        verify(monitorService).resetChecksCount();
    }
}
