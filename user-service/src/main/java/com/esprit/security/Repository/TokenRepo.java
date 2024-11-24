package com.esprit.security.Repository;

import com.esprit.security.Model.Token;
import com.esprit.security.Model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TokenRepo extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);
    List<Token> findByUser(User user);

}
