package com.esprit.security.Service;

import com.esprit.security.Form.UserLoginForm;
import com.esprit.security.Form.UserRegisterForm;
import com.esprit.security.Model.*;
import com.esprit.security.Repository.RoleRepo;
import com.esprit.security.Repository.TokenRepo;
import com.esprit.security.Security.JwtService;
import com.esprit.security.Repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.esprit.security.Service.EmailTemplateName.ACTIVATE_ACCOUNT;

@Service
@RequiredArgsConstructor
@EnableAsync
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;
    @Value("${application.mailing.frontend.activation_url}")
    private String activationUrl;
    @Value("${application.mailing.backend.mailer}")
    private String mailerapi;
    @Autowired
    private RestTemplate restTemplate;
    public User register(UserRegisterForm form) throws MessagingException {
        var userRole = roleRepo.findByName("USER")
                //todo better exeption handler
                .orElseThrow(() -> new IllegalArgumentException("Role User was not initialized"));
        var user = User.builder()
                .firstname(form.getFirstname())
                .lastname(form.getLastname())
                .email((form.getEmail()))
                .username(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .accoutlocked(false)
                .enabled(false)
                .createdDate(LocalDateTime.now())
                .roles(List.of(userRole))
                .build();
        userRepo.save(user);
        sendValidationEmail(user);
        return user;
    }
    public AuthenticationResponse login(UserLoginForm form){
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        form.getUsername(),
                        form.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        user.setLastTimeLoged(LocalDateTime.now());
        claims.put("fullName", user.getName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepo.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepo.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepo.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepo.save(savedToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        MailDTO mail = MailDTO.builder()
                .activationCode(newToken)
                .user(user)
                .username(user.getUsername())
                .to_person(user.getEmail())
                .username(user.getEmail())
                .emailTemplateName(ACTIVATE_ACCOUNT)
                .confirmationUrl(activationUrl+"?"+newToken)
                .activationCode(newToken)
                .SentDate(LocalDateTime.now())
                .subject("Account activation")
                .build();
        MailerRequest(mail);
       /* emailService.sendMail(
                user.getEmail(),
                user.getUsername(),
                ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );*/
    }
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepo.save(token);

        return generatedToken;
    }

    public String editUser(UserRegisterForm userRegisterForm) {
        String  logged = SecurityContextHolder.getContext().getAuthentication().getName();

        if (logged.equals(userRegisterForm.getEmail()))
            throw new IllegalArgumentException("Can't modify another account");

        var user = userRepo.findUserByEmail(userRegisterForm.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

            if (userRegisterForm.getEmail() != null) {
                user.setEmail(userRegisterForm.getEmail());
            }
            if (userRegisterForm.getUsername() != null) {
                user.setUsername(userRegisterForm.getUsername());
            }
            if (userRegisterForm.getFirstname() != null) {
                user.setFirstname(userRegisterForm.getFirstname());
            }
            if (userRegisterForm.getLastname() != null) {
                user.setLastname(userRegisterForm.getLastname());
            }
            if (userRegisterForm.getPassword() != null) {
                user.setPassword(userRegisterForm.getPassword());
            }
            user.setLastModified(LocalDateTime.now());
            //userRepo.save(user);
            return user.getFullname();
    }

    public String logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    /*    if(auth != null){
           new SecurityContextLogoutHandler().logout(auth.getDetails(),null,null);
        }*/
        SecurityContextHolder.clearContext();
        return null;
    }
    @Async
    public void MailerRequest(MailDTO mail){
        mail.setStatus(Status.SENT);
        mail.setEmailTemplateName(ACTIVATE_ACCOUNT);
        mail.setSentDate(LocalDateTime.now());
        ResponseEntity<String> response = restTemplate.postForEntity(mailerapi, mail, String.class);
        response.getBody();
    }
}
