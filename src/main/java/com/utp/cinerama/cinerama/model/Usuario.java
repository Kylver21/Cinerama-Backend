package com.utp.cinerama.cinerama.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entidad Usuario para autenticación y autorización
 * Implementa UserDetails de Spring Security
 */
@Entity
@Table(name = "usuarios",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password", "roles"})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // Almacenado con BCrypt

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "cuenta_no_expirada")
    @Builder.Default
    private Boolean cuentaNoExpirada = true;

    @Column(name = "cuenta_no_bloqueada")
    @Builder.Default
    private Boolean cuentaNoBloqueada = true;

    @Column(name = "credenciales_no_expiradas")
    @Builder.Default
    private Boolean credencialesNoExpiradas = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    // Relación con Cliente (uno a uno)
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cliente cliente;

    // Relación con Roles (muchos a muchos)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // ========== Implementación de UserDetails ==========

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Agregar roles
        for (Rol rol : roles) {
            authorities.add(new SimpleGrantedAuthority(rol.getNombre()));
            
            // Agregar permisos del rol
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return cuentaNoExpirada;
    }

    @Override
    public boolean isAccountNonLocked() {
        return cuentaNoBloqueada;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credencialesNoExpiradas;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    // ========== Métodos auxiliares ==========

    /**
     * Agregar un rol al usuario
     */
    public void agregarRol(Rol rol) {
        this.roles.add(rol);
        rol.getUsuarios().add(this);
    }

    /**
     * Remover un rol del usuario
     */
    public void removerRol(Rol rol) {
        this.roles.remove(rol);
        rol.getUsuarios().remove(this);
    }

    /**
     * Verificar si tiene un rol específico
     */
    public boolean tieneRol(String nombreRol) {
        return roles.stream()
            .anyMatch(rol -> rol.getNombre().equals(nombreRol));
    }

    /**
     * Verificar si tiene un permiso específico
     */
    public boolean tienePermiso(String nombrePermiso) {
        return roles.stream()
            .flatMap(rol -> rol.getPermisos().stream())
            .anyMatch(permiso -> permiso.getNombre().equals(nombrePermiso));
    }

    /**
     * Obtener lista de nombres de roles
     */
    public Set<String> getNombresRoles() {
        return roles.stream()
            .map(Rol::getNombre)
            .collect(Collectors.toSet());
    }
}
