package ru.eugene.planningpoker.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan("ru.eugene.planningpoker")
@EnableJpaRepositories("ru.eugene.planningpoker")
@EntityScan("ru.eugene.planningpoker")
@EnableTransactionManagement
public class WebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
