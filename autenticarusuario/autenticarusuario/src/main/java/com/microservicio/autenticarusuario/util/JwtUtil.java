package com.microservicio.autenticarusuario.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.microservicio.autenticarusuario.model.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
   
    
    private final String SECRET_KEY = "EstaEsMiClaveSuperSecretaParaFirmarLosJWTs1234567890";

   
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; 

    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        
       
        claims.put("userId", usuario.getId());
        claims.put("rol", usuario.getRol().getNombre()); 
        
        return createToken(claims, usuario.getNickname());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) 
                .setIssuedAt(new Date(System.currentTimeMillis())) 
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) 
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) 
                .compact();
    }

}

