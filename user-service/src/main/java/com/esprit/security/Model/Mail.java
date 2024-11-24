package com.esprit.security.Model;

import com.esprit.security.Service.EmailTemplateName;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
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
