package it.io.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Definisce come devon essere filtrate le richieste web
    //Definizione delle cose basilari della sicurezza del progetto: Statelss e Resource server
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //disabilita 403 FORBIDDEN per salvare i dati su mongoBD
                .csrf(AbstractHttpConfigurer::disable)

                //SESSION STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //RESOURCE SERVER
                .oauth2ResourceServer(oauth2->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }



}
