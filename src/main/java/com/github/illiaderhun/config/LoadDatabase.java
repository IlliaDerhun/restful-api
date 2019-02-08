package com.github.illiaderhun.config;

import com.github.illiaderhun.entity.Employee;
import com.github.illiaderhun.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    public CommandLineRunner initDatabase(EmployeeRepository repository) {
        return args -> {
            log.info("Preload" + repository.save(new Employee("Bilbo Baggins", "burglar")));
            log.info("Preload" + repository.save(new Employee("Frodo Baggins", "thief")));
        };
    }

}
