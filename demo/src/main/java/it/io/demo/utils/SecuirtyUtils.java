package it.io.demo.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecuirtyUtils {
    public static String getCurrentUserId(){
        //1) Recupero dell'autenticazione
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //2) Controllo dell'esistenza di un autenticazione o presenza di un Jwt
        if(authentication == null || ! (authentication.getPrincipal() instanceof Jwt)){
            return null;
        }

        //3) Recupero jwt
        Jwt jwt = (Jwt) authentication.getPrincipal();

        //4) Estrazione sub
        return jwt.getClaimAsString("sub");
    }
}