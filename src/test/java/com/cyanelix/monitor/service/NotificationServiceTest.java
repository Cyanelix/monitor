package com.cyanelix.monitor.service;

import com.cyanelix.monitor.configuration.MonitoredEndpoints;
import com.cyanelix.monitor.model.MonitoringResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void monitoringResult_sendNotification_messageSent() {
        // Given...
        MonitoredEndpoints.Check check = new MonitoredEndpoints.Check();
        check.setUrl("http://example.com");
        check.setMethod(HttpMethod.GET);
        check.setRecipients("to@example.com", "totwo@example.com");

        MonitoringResult monitoringResult = new MonitoringResult(check, HttpStatus.BAD_REQUEST, "Test body");

        // When...
        notificationService.sendNotification(monitoringResult);

        // Then...
        ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageArgumentCaptor.capture());

        SimpleMailMessage message = messageArgumentCaptor.getValue();
        assertThat(message.getTo()).containsExactly("to@example.com", "totwo@example.com");
        assertThat(message.getSubject()).isEqualTo("Error detected for http://example.com");
        assertThat(message.getText())
                .isEqualTo("400 (Bad Request) error detected for a GET request to http://example.com\n\nBody:\nTest body");
    }

    @Test
    public void checksCount_sendDailyDigest_messageSent() {
        // Given...
        int checksCount = 3;

        // When...
        notificationService.sendDailyDigest(new String[] {"digest@example.com"}, checksCount);

        // Then...
        ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageArgumentCaptor.capture());

        SimpleMailMessage message = messageArgumentCaptor.getValue();
        assertThat(message.getTo()).containsExactly("digest@example.com");
        assertThat(message.getSubject()).isEqualTo("Monitor Daily Update");
        assertThat(message.getText()).isEqualTo("3 checks performed today");
    }
}
