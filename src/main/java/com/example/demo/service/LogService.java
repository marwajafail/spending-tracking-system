package com.example.demo.service;

import com.example.demo.model.SpLog;
import com.example.demo.repository.LogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void createLog(SpLog log) {
        logRepository.save(log);
    }
}
