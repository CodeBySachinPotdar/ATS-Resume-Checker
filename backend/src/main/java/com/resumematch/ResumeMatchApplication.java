package com.resumematch;

import com.resumematch.domain.UserRole;
import com.resumematch.entity.User;
import com.resumematch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
public class ResumeMatchApplication {

    private static final Logger log = LoggerFactory.getLogger(ResumeMatchApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ResumeMatchApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultEmail = "demo@resumematch.ai";
            if (!userRepository.existsByEmail(defaultEmail)) {
                User demoUser = new User(
                        defaultEmail,
                        passwordEncoder.encode("password123"),
                        "Demo Candidate",
                        UserRole.ROLE_USER
                );
                userRepository.save(demoUser);
                log.info("Initialized default demo user: email=demo@resumematch.ai / password=password123");
            }
        };
    }
}
