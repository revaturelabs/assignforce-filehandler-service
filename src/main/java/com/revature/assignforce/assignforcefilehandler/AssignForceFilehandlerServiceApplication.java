package com.revature.assignforce.assignforcefilehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class AssignForceFilehandlerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignForceFilehandlerServiceApplication.class, args);
    }
}
