package com.cyanelix.monitor.schedule;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import com.cyanelix.monitor.service.MonitorService;
import com.cyanelix.monitor.service.NotificationService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MonitorSchedulerTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private MonitorService monitorService;

    @Mock
    private NotificationService notificationService;

    @Test
    public void noEndpointsToMonitor_serviceNotCalled_noNotifications() {
        // Given...
        MonitoredEndpoints monitoredEndpoints = new MonitoredEndpoints();

        // When...
        new MonitorScheduler(monitoredEndpoints, monitorService, notificationService).monitor();

        // Then...
        verify(monitorService, never()).makeRequest(any(), any());
        verify(notificationService, never()).sendNotification();
    }

    @Test
    public void endpointReturnsSuccess_serviceCalledOnce_noNotifications() {
        // Given...
        String url = "http://example.com";

        MonitoredEndpoints monitoredEndpoints = new MonitoredEndpoints();
        monitoredEndpoints.setEndpoints(Collections.singletonList(url));

        given(monitorService.makeRequest(url, HttpMethod.HEAD))
                .willReturn(new MonitoringResult(url, HttpMethod.HEAD, HttpStatus.OK));

        // When...
        new MonitorScheduler(monitoredEndpoints, monitorService, notificationService).monitor();

        // Then...
        verify(monitorService).makeRequest(url, HttpMethod.HEAD);
        verify(notificationService, never()).sendNotification();
    }

    @Test
    public void endpointReturnsError_serviceCalledOnce_oneNotification() {
        // Given...
        String url = "http://example.com/error";

        MonitoredEndpoints monitoredEndpoints = new MonitoredEndpoints();
        monitoredEndpoints.setEndpoints(Collections.singletonList(url));

        given(monitorService.makeRequest(url, HttpMethod.HEAD))
                .willReturn(new MonitoringResult(url, HttpMethod.HEAD, HttpStatus.INTERNAL_SERVER_ERROR));

        // When...
        new MonitorScheduler(monitoredEndpoints, monitorService, notificationService).monitor();

        // Then...
        verify(monitorService).makeRequest(url, HttpMethod.HEAD);
        verify(notificationService).sendNotification();
    }
}
