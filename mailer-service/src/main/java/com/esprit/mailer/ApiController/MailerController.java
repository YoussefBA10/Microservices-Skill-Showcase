package com.esprit.mailer.ApiController;


import com.esprit.mailer.Model.Mail;
import com.esprit.mailer.Model.Status;
import com.esprit.mailer.Repository.MailRepo;
import com.esprit.mailer.Repository.UserRepo;
import com.esprit.mailer.Service.EmailService;
import com.esprit.mailer.Service.EmailTemplateName;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("mailer")
@RequiredArgsConstructor
public class MailerController {
    private final MailRepo mailRepo;
    private final UserRepo userRepo;
    private final EmailService emailService;
    @PostMapping("/sendmail")
    public ResponseEntity<?> demo(@RequestBody @Valid Mail request) {
        try {
            request.setStatus(Status.SENT);
            request.setEmailTemplateName(EmailTemplateName.ACTIVATE_ACCOUNT);
            request.setSentDate(LocalDateTime.now());
            emailService.sendMail(request);
            //request.setUser(userRepo.findById(usid).get());
            //mailRepo.save(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("Sent");
    }
    @GetMapping("/test")
    public ResponseEntity<?> defo() {
        return ResponseEntity.ok("OK");
    }
}
