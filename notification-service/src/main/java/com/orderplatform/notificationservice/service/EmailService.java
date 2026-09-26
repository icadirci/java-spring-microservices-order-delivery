package com.orderplatform.notificationservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String email, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@orderplatform.test");
        message.setTo(email);
        message.setSubject("Aramıza hoş geldin!");
        message.setText(
                "Merhaba " + fullName + ", \n\nKaydın başarıyla oluşturuldu."
        );

        mailSender.send(message);
    }
}
