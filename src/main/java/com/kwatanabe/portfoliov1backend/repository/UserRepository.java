package com.kwatanabe.portfoliov1backend.repository;

import com.kwatanabe.portfoliov1backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
}
