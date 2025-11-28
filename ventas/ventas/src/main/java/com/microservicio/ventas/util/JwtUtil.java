package com.microservicio.ventas.util;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
    
    
    private final String SECRET_KEY = "EstaEsMiClaveSuperSecretaParaFirmarLosJWTs1234567890";
    
   
    public Claims extractAllClaims(String token) {
        
       
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); 
        }
        
       
        if (token == null || token.isBlank()) {
            return null; 
        }

        try {
           
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            
            return null;
        }
    }

   
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) return null;
        
       
        try {
            return Long.valueOf(String.valueOf(claims.get("userId"))); 
        } catch (NumberFormatException | NullPointerException e) {
            
            return null; 
        }
    }

   
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) return null;
        return (String) claims.get("rol");
    }

}