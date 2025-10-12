package com.utp.cinerama.cinerama.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Rol para agrupar permisos
 * Ejemplos: ROLE_ADMIN, ROLE_CLIENTE, ROLE_EMPLEADO
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"usuarios", "permisos"})
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // ROLE_ADMIN, ROLE_CLIENTE, etc.

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relación con Usuarios (muchos a muchos - lado inverso)
    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();

    // Relación con Permisos (muchos a muchos)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_permisos",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    @Builder.Default
    private Set<Permiso> permisos = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // ========== Métodos auxiliares ==========

    /**
     * Agregar un permiso al rol
     */
    public void agregarPermiso(Permiso permiso) {
        this.permisos.add(permiso);
        permiso.getRoles().add(this);
    }

    /**
     * Remover un permiso del rol
     */
    public void removerPermiso(Permiso permiso) {
        this.permisos.remove(permiso);
        permiso.getRoles().remove(this);
    }

    /**
     * Verificar si tiene un permiso específico
     */
    public boolean tienePermiso(String nombrePermiso) {
        return permisos.stream()
            .anyMatch(permiso -> permiso.getNombre().equals(nombrePermiso));
    }
}
