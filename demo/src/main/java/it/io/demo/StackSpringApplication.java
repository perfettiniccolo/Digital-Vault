package it.io.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 1. Questa riga dice: "Usa la sicurezza chiamata 'bearerAuth' su tutto il sito"
@OpenAPIDefinition(
		info = @Info(title = "Vault API", version = "1.0"),
		security = @SecurityRequirement(name = "bearerAuth")
)
// 2. Questa parte definisce cos'è 'bearerAuth': è un Token JWT
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
public class StackSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(StackSpringApplication.class, args);
	}
}