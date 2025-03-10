package com.aihealth.first.repository;

import com.aihealth.first.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}