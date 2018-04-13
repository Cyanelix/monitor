package com.cyanelix.monitor.model;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public final class MonitoringResult {
    private final MonitoredEndpoints.Check check;
    private final HttpStatus httpStatus;
    private final String body;

    public MonitoringResult(MonitoredEndpoints.Check check, HttpStatus httpStatus, String body) {
        this.check = check;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public boolean isError() {
        return httpStatus != check.getExpectedStatus();
    }

    public String getFailureSubject() {
        return String.format("Error detected for %s", check.getUrl());
    }

    public String getFailureMessage() {
        return String.format("%s for a %s request to %s%n%nBody:%n%s",
                getStatusMessage(), check.getMethod(), check.getUrl(), body);
    }

    private String getStatusMessage() {
        if (httpStatus == null) {
            return "No response received";
        }

        return String.format("%s (%s) error detected", httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringResult that = (MonitoringResult) o;
        return Objects.equals(check, that.check) &&
                httpStatus == that.httpStatus &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(check, httpStatus, body);
    }

    public String[] getRecipients() {
        return check.getRecipients();
    }
}
