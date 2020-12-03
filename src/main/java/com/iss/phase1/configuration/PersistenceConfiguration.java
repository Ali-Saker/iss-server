package com.iss.phase1.configuration;

import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfiguration {

    @Bean
    public SpringPhysicalNamingStrategy springPhysicalNamingStrategy() {
        return new DatabaseNamingStrategy();
    }
}
