package com.goo.bikerelocationproject.repository;

import com.goo.bikerelocationproject.data.entity.CustomUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<CustomUser, Long> {

  Optional<CustomUser> findByEmail(String email);

  boolean existsByEmail(String email);
}
