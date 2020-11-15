package com.hejz.springsecurityjpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.hejz.springsecurityjpa.repository")
public class JpaConfig {
}
