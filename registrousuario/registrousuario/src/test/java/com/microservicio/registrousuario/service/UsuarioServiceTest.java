package com.microservicio.registrousuario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.model.Usuario;
import com.microservicio.registrousuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder; 

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioExistente;
    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(2L, "ADMINISTRADOR");
        usuarioExistente = new Usuario(1L, "adminuser", "clave_antigua_enc", "admin@mail.com", rolAdmin);
    }

   
    @Test
    void testCrearUsuario_EncriptaClaveYGuarda() {
        Usuario usuarioNuevo = new Usuario(null, "newuser", "clave123", "new@mail.com", rolAdmin);
        
        when(passwordEncoder.encode("clave123")).thenReturn("clave_nueva_enc");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente); 

        Usuario resultado = usuarioService.crearUsuario(usuarioNuevo);

        assertNotNull(resultado);
        
        verify(passwordEncoder, times(1)).encode("clave123"); 
        
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); 
    }
    
    
    @Test
    void testListarUsuarios_DevuelveLista() {
        List<Usuario> listaEsperada = Arrays.asList(usuarioExistente, new Usuario());
        when(usuarioRepository.findAll()).thenReturn(listaEsperada);

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    
    @Test
    void testObtenerPorId_Encontrado_DevuelveUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        
        Optional<Usuario> resultado = usuarioService.obtenerPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals("adminuser", resultado.get().getNickname());
        verify(usuarioRepository, times(1)).findById(1L);
    }
    
    
    @Test
    void testActualizarUsuario_ConNuevaClave() {
        Usuario datosActualizados = new Usuario(null, "admin_new", "nueva_clave_123", "admin_new@mail.com", rolAdmin);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode("nueva_clave_123")).thenReturn("clave_hash_nueva");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.actualizarUsuario(1L, datosActualizados);

        assertNotNull(resultado);
        assertEquals("admin_new", resultado.getNickname());
       
        verify(passwordEncoder, times(1)).encode("nueva_clave_123"); 
       
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
    
    
    @Test
    void testActualizarUsuario_SinNuevaClave() {
        
        Usuario datosActualizados = new Usuario(null, "admin_new_2", "", "admin_new_2@mail.com", rolAdmin);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        usuarioService.actualizarUsuario(1L, datosActualizados);

        
        verify(passwordEncoder, times(0)).encode(anyString()); 
       
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
    
    
    @Test
    void testEliminarUsuario_LlamaDelete() {
        usuarioService.eliminarUsuario(1L);
        
        
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

}
