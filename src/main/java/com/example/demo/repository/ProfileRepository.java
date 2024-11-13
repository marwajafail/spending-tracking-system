package com.example.demo.repository;

import com.example.demo.model.SpProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<SpProfile, Long> {
}