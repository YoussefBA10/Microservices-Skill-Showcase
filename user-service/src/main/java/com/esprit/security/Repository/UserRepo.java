package com.esprit.security.Repository;

import com.esprit.security.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User>findUserByEmail(String email);
    Optional<User>findUserByUsername(String email);
}
