package com.utp.cinerama.cinerama.security;

import com.utp.cinerama.cinerama.model.Usuario;
import com.utp.cinerama.cinerama.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.info("Buscando usuario: {}", usernameOrEmail);

        
        Usuario usuario = usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    log.error(" Usuario no encontrado: {}", usernameOrEmail);
                    return new UsernameNotFoundException(
                        "Usuario no encontrado con username o email: " + usernameOrEmail
                    );
                });

        // Verificar si el usuario está activo
        if (!usuario.getActivo()) {
            log.error(" Usuario inactivo: {}", usernameOrEmail);
            throw new UsernameNotFoundException("Usuario inactivo: " + usernameOrEmail);
        }

        log.info(" Usuario encontrado: {} con {} roles", 
            usuario.getUsername(), 
            usuario.getRoles().size()
        );


        
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())    
                .accountExpired(false)
                .accountLocked(!usuario.getActivo())
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioCompleto(String usernameOrEmail) {
        return usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado: " + usernameOrEmail
                ));
    }


    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con id: " + id
                ));

        // Verificar que esté activo
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo con id: " + id);
        }


        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .accountExpired(false)
                .accountLocked(!usuario.getActivo())
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
}
