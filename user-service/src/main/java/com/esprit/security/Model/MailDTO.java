package com.esprit.security.Model;

import com.esprit.security.Service.EmailTemplateName;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {
    private Integer id;
    private User user;
    private String to_person;
    private String username;
    private EmailTemplateName emailTemplateName;
    private String confirmationUrl;
    private String activationCode;
    private String subject;
    private LocalDateTime SentDate;
    private Status status;
}
