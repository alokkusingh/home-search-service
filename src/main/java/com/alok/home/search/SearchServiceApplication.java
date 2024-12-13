package com.alok.home.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.alok.home.commons.repository")
@EntityScan("com.alok.home.commons.entity")
@ConfigurationPropertiesScan({
		"com.alok.home.commons.security.properties"
})
@SpringBootApplication(
		scanBasePackages = {
				"com.alok.home.search",
				"com.alok.home.commons.exception",
				"com.alok.home.commons.security"
		}
)
public class SearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchServiceApplication.class, args);
	}

}
