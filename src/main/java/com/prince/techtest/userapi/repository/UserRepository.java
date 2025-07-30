package com.prince.techtest.userapi.repository;

import com.prince.techtest.userapi.model.User;

// import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDeletedFalse(); // Custom query to find non-deleted users
    Optional<User> findByIdAndDeletedFalse(Long id); // Custom query to find a non-deleted user by ID
    List<User> findByDeletedTrue(); // Custom query to find deleted users
}
