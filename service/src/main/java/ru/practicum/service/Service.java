package ru.practicum.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum"})
public class Service {
    public static void main(String[] args) {
        SpringApplication.run(Service.class, args);
    }
}
