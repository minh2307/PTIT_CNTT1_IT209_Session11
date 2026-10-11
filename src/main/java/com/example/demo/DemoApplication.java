package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Trigger push lần 2 để kiểm tra Gradle Dependency Caching trên GitHub Actions CI
    @GetMapping("/")
    public String hello() {
        return "Hello IT209 Session 11 - CI Dependency Caching!";
    }
}
