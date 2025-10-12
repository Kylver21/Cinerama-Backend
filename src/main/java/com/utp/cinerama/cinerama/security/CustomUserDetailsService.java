package com.utp.cinerama.cinerama.security;

import com.utp.cinerama.cinerama.model.Permiso;
import com.utp.cinerama.cinerama.model.Rol;
import com.utp.cinerama.cinerama.model.Usuario;
import com.utp.cinerama.cinerama.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para cargar detalles del usuario durante la autenticación
 * Implementa UserDetailsService de Spring Security
 * 
 * PATRÓN SIMPLIFICADO: Busca por username o email y retorna User de Spring Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Cargar usuario por username o email
     * Spring Security llama este método automáticamente durante la autenticación
     * 
     * FLUJO:
     * 1. Buscar usuario en BD por username o email
     * 2. Verificar que esté activo
     * 3. Convertir roles y permisos a GrantedAuthority
     * 4. Retornar UserDetails (User de Spring Security)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.info("🔍 Buscando usuario: {}", usernameOrEmail);

        // Buscar por username o email (como en el ejemplo)
        Usuario usuario = usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    log.error("❌ Usuario no encontrado: {}", usernameOrEmail);
                    return new UsernameNotFoundException(
                        "Usuario no encontrado con username o email: " + usernameOrEmail
                    );
                });

        // Verificar si el usuario está activo
        if (!usuario.getActivo()) {
            log.error("❌ Usuario inactivo: {}", usernameOrEmail);
            throw new UsernameNotFoundException("Usuario inactivo: " + usernameOrEmail);
        }

        log.info("✅ Usuario encontrado: {} con {} roles", 
            usuario.getUsername(), 
            usuario.getRoles().size()
        );

        // Convertir roles y permisos a GrantedAuthority
        Collection<GrantedAuthority> authorities = getAuthorities(usuario.getRoles());

        // Retornar User de Spring Security (patrón simplificado)
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword()) // Password ya está hasheado con BCrypt
                .authorities(authorities)        // ROLE_ADMIN, PELICULAS_CREAR, etc.
                .accountExpired(false)
                .accountLocked(!usuario.getActivo())
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    /**
     * Convertir roles y permisos a GrantedAuthority
     * 
     * FORMATO:
     * - Roles: ROLE_ADMIN, ROLE_CLIENTE, ROLE_EMPLEADO
     * - Permisos: PELICULAS_CREAR, BOLETOS_LISTAR, etc.
     * 
     * Ejemplo: Usuario con ROLE_CLIENTE tendrá:
     * [ROLE_CLIENTE, PELICULAS_LISTAR, PELICULAS_VER, BOLETOS_CREAR, ...]
     */
    private Collection<GrantedAuthority> getAuthorities(Set<Rol> roles) {
        return roles.stream()
                .flatMap(rol -> {
                    // Agregar permisos del rol
                    Set<GrantedAuthority> authorities = rol.getPermisos().stream()
                            .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()))
                            .collect(Collectors.toSet());
                    
                    // Agregar el rol con prefijo ROLE_
                    authorities.add(new SimpleGrantedAuthority(rol.getNombre()));
                    
                    return authorities.stream();
                })
                .collect(Collectors.toSet());
    }

    /**
     * Obtener usuario completo (Entity) para uso en servicios
     * NO retorna UserDetails, retorna el objeto Usuario completo
     * 
     * USO: Cuando necesitas acceso a Cliente, ID, etc.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioCompleto(String usernameOrEmail) {
        return usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado: " + usernameOrEmail
                ));
    }

    /**
     * Cargar usuario por ID
     */
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

        Collection<GrantedAuthority> authorities = getAuthorities(usuario.getRoles());

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!usuario.getActivo())
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
}
