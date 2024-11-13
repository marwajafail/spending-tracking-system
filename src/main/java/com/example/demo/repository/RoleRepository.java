package com.example.demo.repository;

import com.example.demo.model.SpRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<SpRole, Long> {
    Optional<SpRole> findByName(String name);
}