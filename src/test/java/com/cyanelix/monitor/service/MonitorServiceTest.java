package com.cyanelix.monitor.service;

import com.cyanelix.monitor.model.MonitoringResult;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class MonitorServiceTest {
    private RestTemplate restTemplate = new RestTemplate();

    private MonitorService monitorService = new MonitorService(restTemplate);

    @Test
    public void makeOKHeadRequest_getOKStatusCode() {
        // Given...
        String url = "http://example.com";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withSuccess());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(url, httpMethod);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void makeNoContentHeadRequest_getNoContentSuccessCode() {
        // Given...
        String url = "http://example.com/nocontent";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withNoContent());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(url, httpMethod);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void makeClientErrorHeadRequest_getClientErrorException() {
        // Given...
        String url = "http://example.com/error";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withBadRequest());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(url, httpMethod);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void makeServerErrorHeadRequest_getServerErrorException() {
        // Given...
        String url = "http://example.com/error";
        HttpMethod httpMethod = HttpMethod.HEAD;

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer
                .expect(requestTo(url))
                .andExpect(method(httpMethod))
                .andRespond(withServerError());

        // When...
        MonitoringResult monitoringResult = monitorService.makeRequest(url, httpMethod);

        // Then...
        assertThat(monitoringResult.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
