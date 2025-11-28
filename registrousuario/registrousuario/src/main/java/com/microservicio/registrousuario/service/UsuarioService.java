package com.microservicio.registrousuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.model.Usuario;
import com.microservicio.registrousuario.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import com.microservicio.registrousuario.repository.RolRepository; 

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository; 

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, RolRepository rolRepository) { // 3. INYECTAR REPOSITORIO
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository; 
    }

    public Usuario crearUsuario(Usuario usuario) {
        
       
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Error de configuración: El rol CLIENTE no existe en la base de datos."));
        
       
        usuario.setRol(rolCliente);
      

       
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
   
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElse(null); 
    }
   
    @Transactional
    public Usuario actualizarClavePorEmail(String email, String nuevaClave) {
        return usuarioRepository.findByCorreo(email).map(usuario -> {
           
            usuario.setClave(passwordEncoder.encode(nuevaClave));
            return usuarioRepository.save(usuario);
        }).orElse(null);
    }

    
    public Usuario buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname)
                .orElse(null); 
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNickname(usuarioActualizado.getNickname());
            usuario.setCorreo(usuarioActualizado.getCorreo());
            usuario.setRol(usuarioActualizado.getRol());
            
            if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().trim().isEmpty()) {
                usuario.setClave(passwordEncoder.encode(usuarioActualizado.getClave()));
            }
            
            return usuarioRepository.save(usuario);
        }).orElse(null);
    }
}