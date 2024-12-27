package com.esprit.mailer.Service;

import com.esprit.mailer.Model.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void sendMailRequest(Mail mail) {
        rabbitTemplate.convertAndSend("mailer-exchange", "mailer-routing-key", mail);
    }
}
