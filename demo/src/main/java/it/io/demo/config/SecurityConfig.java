package it.io.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Definisce come devon essere filtrate le richieste web
    //Definizione delle cose basilari della sicurezza del progetto: Statelss e Resource server
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //Da continuare
        http
                //disabilita 403 FORBIDDEN per salvare i dati su mongoBD
                .csrf(AbstractHttpConfigurer::disable)

                //CORS (Sintassi corretta per Spring Security 6)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //REQUEST MATCHERS (Unico blocco, ordinato dal più specifico al più generico)
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger pubblico
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2. Il resto delle API rimane protetto
                        .requestMatchers("/api/vault/**").authenticated()

                        // 3. Regola finale (chiude tutto il resto)
                        .anyRequest().authenticated()
                )

                //SESSION STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //RESOURCE SERVER
                .oauth2ResourceServer(oauth2->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }


    //Estrazione dei ruoli dal JWT
    @Bean
    protected JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        //Oggetto che si occupa di trasformare i claims in GrantedAuthority
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthorityPrefix("ROLE_");

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            //Se il JWT non ha ruoli, converte almeno gli scopi
            if (realmAccess == null || realmAccess.isEmpty()) {
                return authoritiesConverter.convert(jwt);
            }

            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }

    // Da continuare
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //Definizione di chi può chiamarci
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:80"));

        //Definizione dei metodi permessi
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //Permettiamo l'invio del token
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        configuration.addAllowedOriginPattern("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}