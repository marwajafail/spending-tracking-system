package com.example.demo.repository;

import com.example.demo.model.SpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SpUser, Long> {

    Optional<SpUser> findByEmail(String username);

    List<SpUser> findByStatus(Boolean status);

    //to login, return user object based on email address
    Optional<SpUser> findUserByEmail(String emailAddress);

    @Query("SELECT u FROM SpUser u WHERE u.verificationCode = ?1")
    Optional<SpUser> findByVerificationCode(String code);

    Optional<SpUser> findByResetToken(String resetToken);

}
