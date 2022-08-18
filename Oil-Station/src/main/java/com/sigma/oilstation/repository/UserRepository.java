package com.sigma.oilstation.repository;

import com.sigma.oilstation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPasswordAndDeletedIsFalse(String username, String password);

}
