package com.cyanelix.monitor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("monitored")
@Validated
public class MonitoredEndpoints {
    @Valid
    private List<Check> checks;

    public MonitoredEndpoints() {
        checks = new ArrayList<>();
    }

    public List<Check> getChecks() {
        return checks;
    }

    public void setChecks(List<Check> checks) {
        this.checks = checks;
    }

    public static final class Check {
        @NotBlank
        private String url;

        @NotNull
        private HttpMethod method;

        @NotNull
        private HttpStatus expectedStatus;

        @NotEmpty
        private String[] recipients;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public HttpStatus getExpectedStatus() {
            return expectedStatus;
        }

        public void setExpectedStatus(HttpStatus expectedStatus) {
            this.expectedStatus = expectedStatus;
        }

        public String[] getRecipients() {
            return recipients;
        }

        public void setRecipients(String... recipients) {
            this.recipients = recipients;
        }
    }
}
