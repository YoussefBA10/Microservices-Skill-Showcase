package com.esprit.mailer.Model;

import com.esprit.mailer.Service.EmailTemplateName;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
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
