package com.microservicio.autenticarusuario.dto;

import com.microservicio.autenticarusuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Usuario user; 
}
