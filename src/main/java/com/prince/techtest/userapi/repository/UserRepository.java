package com.prince.techtest.userapi.repository;

import com.prince.techtest.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
