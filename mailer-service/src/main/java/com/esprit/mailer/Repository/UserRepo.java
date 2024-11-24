package com.esprit.mailer.Repository;

import com.esprit.mailer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User> findById(Integer id);
}
