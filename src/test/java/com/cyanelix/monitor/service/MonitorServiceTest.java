package com.cyanelix.monitor.service;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class MonitorServiceTest {
    private RestTemplate restTemplate = new RestTemplate();

    private MonitorService monitorService = new MonitorService(restTemplate);

    @Test
    public void makeOKHeadRequest_getOKStatusCodeWithNullBody() {
        // Given...
        String url = "http://example.com";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl(url);
        check.setMethod(httpMethod);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withSuccess());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(monitoringResult.getBody()).isNull();
    }

    @Test
    public void makeNoContentHeadRequest_getNoContentSuccessCodeAndNullBody() {
        // Given...
        String url = "http://example.com/nocontent";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl(url);
        check.setMethod(httpMethod);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withNoContent());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(monitoringResult.getBody()).isNull();
    }

    @Test
    public void makeSuccessfulGetRequest_getSuccessCodeAndBody() {
        // Given...
        String url = "http://example.com/get";
        HttpMethod httpMethod = HttpMethod.GET;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl(url);
        check.setMethod(httpMethod);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withSuccess().body("Test content"));

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(monitoringResult.getBody()).isEqualTo("Test content");
    }

    @Test
    public void makeClientErrorHeadRequest_getClientErrorExceptionAndEmptyBody() {
        // Given...
        String url = "http://example.com/error";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl(url);
        check.setMethod(httpMethod);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withBadRequest());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(monitoringResult.getBody()).isEmpty();
    }

    @Test
    public void makeServerErrorHeadRequest_getServerErrorStatusCodeAndEmptyBody() {
        // Given...
        String url = "http://example.com/error";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl(url);
        check.setMethod(httpMethod);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withServerError());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(monitoringResult.getBody()).isEmpty();
    }

    @Test
    public void makeServerErrorGetRequest_getServerErrorStatusCodeAndResponseBody() {
        // Given...
        String url = "http://example.com/error";
        HttpMethod httpMethod = HttpMethod.GET;

        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl(url);
        check.setMethod(httpMethod);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withServerError().body("Test content"));

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(monitoringResult.getBody()).isEqualTo("Test content");
    }

    @Test
    public void exceptionThrownDuringRequest_getExceptionMessageWithNoStatusCode() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl("http://localhost:-1");
        check.setMethod(HttpMethod.HEAD);

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(check);

        // Then...
        assertThat(monitoringResult.getBody()).isEqualTo("I/O error on HEAD request for \"http://localhost/-1\": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)");
        assertThat(monitoringResult.getHttpStatus()).isNull();
    }
}
