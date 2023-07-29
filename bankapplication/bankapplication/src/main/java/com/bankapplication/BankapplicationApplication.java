package com.bankapplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import jakarta.persistence.Column;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Secure Banking Application ",
				description = "Backend Rest APIs for ABC Bank",
				version = "1.0",
				contact = @Contact(
						name = "Sumeet Bhangale",
						email = "sumeetbhangale19@gmail.com",
						url = "http://github.com/Sumeet-bhangale"
				),
				license = @License(
						name = "Banking Application",
						url = "http://github.com/Sumeet-bhangale"

				)
		),
		externalDocs = @ExternalDocumentation(
				description = "The Secure Banking Application Documentation",
				url = "http://github.com/Sumeet-bhangale"
		)
)
public class BankapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankapplicationApplication.class, args);
	}

}
