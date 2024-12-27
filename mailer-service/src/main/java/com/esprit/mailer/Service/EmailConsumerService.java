package com.esprit.mailer.Service;

import com.esprit.mailer.Model.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumerService {
    private final EmailService emailService;

    @RabbitListener(queues = "mailer-queue")
    public void consumeMail(Mail mail) {
        try {
            emailService.sendMail(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
