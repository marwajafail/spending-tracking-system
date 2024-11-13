package com.example.demo.repository;

import com.example.demo.model.SpLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<SpLog, Long> {
}