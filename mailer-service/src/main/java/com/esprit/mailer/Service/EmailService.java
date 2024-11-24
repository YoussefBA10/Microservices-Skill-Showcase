package com.esprit.mailer.Service;


import com.esprit.mailer.Model.Mail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailerSender;
    private final SpringTemplateEngine templateEngine;


    @Async
    public void sendMail(Mail mail) throws MessagingException {
        String templateName;
        if (mail.getEmailTemplateName() == null){
            templateName = "confirm-email";
        }else{
            templateName = mail.getEmailTemplateName().getName();
        }
        MimeMessage mimeMessage = mailerSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("username",mail.getUsername());
        properties.put("confirmationUrl",mail.getConfirmationUrl());
        properties.put("activation_code",mail.getActivationCode());

        Context context = new Context();
        context.setVariables(properties);
        mimeMessageHelper.setFrom("youssefbenarous@gmail.com");
        mimeMessageHelper.setTo(mail.getTo_person());
        mimeMessageHelper.setSubject(mail.getSubject());
        String tempalte = templateEngine.process(templateName, context);
        mimeMessageHelper.setText(tempalte, true);
        mailerSender.send(mimeMessage);
    }

}
