package com.cyanelix.monitor.service;

import com.cyanelix.monitor.model.MonitoringResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(MonitoringResult result) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(result.getRecipients());
        message.setSubject(result.getFailureSubject());
        message.setText(result.getFailureMessage());

        mailSender.send(message);
    }
}
