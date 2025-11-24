package com.example.aura.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendEmailWithTemplate(String to, String subject,
                                                            String templateName,
                                                            Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process("email/" + templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@aura.com");

            mailSender.send(message);

            log.info("✅ Email HTML enviado exitosamente a: {} | Plantilla: {}", to, templateName);
            return CompletableFuture.completedFuture(true);

        } catch (MessagingException e) {
            log.error("❌ Error enviando email HTML a {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async("emailExecutor")
    public void sendEmailNotification(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.setFrom("noreply@aura.com");

            mailSender.send(message);
            log.info("📧 Email enviado a: {}", to);

        } catch (MessagingException e) {
            log.error("❌ Error enviando email: {}", e.getMessage());
        }
    }

    @Async("notificationExecutor")
    public CompletableFuture<Boolean> sendPushNotification(Long userId, String title, String message) {
        log.info("📱 Push notification enviada a usuario {}: {}", userId, title);

        try {
            Thread.sleep(500);
            return CompletableFuture.completedFuture(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async("notificationExecutor")
    public void sendSMSNotification(String phoneNumber, String message) {
        log.info("📲 SMS enviado a {}: {}", phoneNumber, message);

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}