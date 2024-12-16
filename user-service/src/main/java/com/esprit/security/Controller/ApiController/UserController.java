package com.esprit.security.Controller.ApiController;

import com.esprit.security.Form.UserLoginForm;
import com.esprit.security.Form.UserRegisterForm;
import com.esprit.security.Model.*;
import com.esprit.security.Repository.UserRepo;
import com.esprit.security.Service.EmailTemplateName;
import com.esprit.security.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;
    public final UserRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterForm request) throws MessagingException {
        return ResponseEntity.ok(userService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginForm request){
        if (request == null ) return ResponseEntity.badRequest().body("Form null");
        else return ResponseEntity.ok(userService.login(request));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        return ResponseEntity.ok(userService.logout());
    }
    @GetMapping("/activate-account")
    public ResponseEntity<String> confirm(
            @RequestParam String token
    ) throws MessagingException {
        try {
            userService.activateAccount(token);
            return ResponseEntity.ok("Validated");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/editUser")
    public ResponseEntity<?> editUser(
            @RequestBody @Valid UserRegisterForm userRegisterForm
    ) {
        return ResponseEntity.ok(userService.editUser(userRegisterForm));
    }
    @PostMapping("/sendmail")
    public ResponseEntity<?> demo(@RequestBody @Valid MailDTO mail) {
        try {
            mail.setStatus(Status.SENT);
            mail.setEmailTemplateName(EmailTemplateName.ACTIVATE_ACCOUNT);
            mail.setSentDate(LocalDateTime.now());
            userService.MailerRequest(mail);
            return ResponseEntity.ok(Status.SENT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        //return ResponseEntity.ok("ss");
        //return userService.MailerRequest(mail);
    }
    @GetMapping("/test")
    public ResponseEntity<?> ff() {
        return ResponseEntity.ok("OK");
    }
   /* @PostMapping("/deleteUser")
    public ResponseEntity<?> deleteUSer(
            @RequestBody @Valid UserRegisterForm userRegisterForm
    ) {
        return ResponseEntity.ok(userService.deleteUser(userRegisterForm));
    }*/

}
