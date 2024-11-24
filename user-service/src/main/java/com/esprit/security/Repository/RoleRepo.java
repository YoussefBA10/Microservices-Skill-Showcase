package com.esprit.security.Repository;

import com.esprit.security.Model.Role;
import com.esprit.security.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String token);
}
