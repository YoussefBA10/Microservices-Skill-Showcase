package com.esprit.mailer.Repository;

import com.esprit.mailer.Model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailRepo extends JpaRepository<Mail,Integer> {
    Optional<Mail> findById(Integer id);
}
