package com.bhava.pm.authservice.respository;

import com.bhava.pm.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

Optional<User> findByEmail(String email);
}
