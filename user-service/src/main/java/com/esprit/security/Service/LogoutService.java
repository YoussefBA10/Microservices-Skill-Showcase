package com.esprit.security.Service;

import com.esprit.security.Model.Token;
import com.esprit.security.Repository.TokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepo tokenRepo;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwt;
        String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepo.findByToken(jwt)
                .orElse(null);
        if (storedToken != null){
            storedToken.setRevoked(true);
            tokenRepo.save(storedToken);
        }
    }
}
