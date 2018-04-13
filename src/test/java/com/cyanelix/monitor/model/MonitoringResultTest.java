package com.cyanelix.monitor.model;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitoringResultTest {
    @Test
    public void okStatusExpected_errorStatusReceived_isError() {
        // Given...
        HttpStatus actualStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();

        MonitoringResult monitoringResult = new MonitoringResult(check, actualStatus, "");

        // When...
        boolean error = monitoringResult.isError();

        // Then...
        assertThat(error).isTrue();
    }

    @Test
    public void errorStatusExpected_errorStatusReceived_isNotError() {
        // Given...
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setExpectedStatus(status);

        MonitoringResult monitoringResult = new MonitoringResult(check, status, "");

        // When...
        boolean error = monitoringResult.isError();

        // Then...
        assertThat(error).isFalse();
    }

    @Test
    public void okStatusExpected_nullStatusReceived_isError() {
        // Given...
        HttpStatus expectedStatus = HttpStatus.OK;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setExpectedStatus(expectedStatus);

        MonitoringResult monitoringResult = new MonitoringResult(check, null, "");

        // When...
        boolean error = monitoringResult.isError();

        // Then...
        assertThat(error).isTrue();
    }

    @Test
    public void nullStatusExpected_nullStatusReceived_isNotError() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setExpectedStatus(null);

        MonitoringResult monitoringResult = new MonitoringResult(check, null, "");

        // When...
        boolean error = monitoringResult.isError();

        // Then...
        assertThat(error).isFalse();
    }

    @Test
    public void unmatchingStatusReceived_getFailureMessage_returnsMessage() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl("http://example.com");
        check.setMethod(HttpMethod.GET);

        MonitoringResult monitoringResult = new MonitoringResult(check, HttpStatus.NOT_FOUND, "Test content");

        // When...
        String failureMessage = monitoringResult.getFailureMessage();

        // Then...
        assertThat(failureMessage).isEqualTo("404 (Not Found) error detected for a GET request to http://example.com\n\nBody:\nTest content");
    }

    @Test
    public void nullStatusReceived_getFailureMessage_returnsMessage() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl("http://example.com");
        check.setMethod(HttpMethod.HEAD);

        MonitoringResult monitoringResult = new MonitoringResult(check, null, "");

        // When...
        String failureMessage = monitoringResult.getFailureMessage();

        // Then...
        assertThat(failureMessage).isEqualTo("No response received for a HEAD request to http://example.com\n\nBody:\n");
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(MonitoringResult.class).verify();
    }
}
