package com.utp.cinerama.cinerama.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Permiso para acciones específicas
 * Ejemplos: PELICULAS_CREAR, BOLETOS_LISTAR, USUARIOS_EDITAR
 */
@Entity
@Table(name = "permisos",
    indexes = {
        @Index(name = "idx_modulo", columnList = "modulo")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "roles")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre; // PELICULAS_CREAR, BOLETOS_LISTAR, etc.

    @Column(nullable = false, length = 50)
    private String modulo; // PELICULAS, BOLETOS, USUARIOS, etc.

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relación con Roles (muchos a muchos - lado inverso)
    @ManyToMany(mappedBy = "permisos")
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // ========== Enums para permisos comunes ==========

    /**
     * Enum para módulos del sistema
     */
    public enum Modulo {
        PELICULAS("Películas"),
        SALAS("Salas"),
        FUNCIONES("Funciones"),
        ASIENTOS("Asientos"),
        BOLETOS("Boletos"),
        CLIENTES("Clientes"),
        PRODUCTOS("Productos"),
        VENTAS("Ventas de Productos"),
        PAGOS("Pagos"),
        USUARIOS("Usuarios"),
        ROLES("Roles"),
        PROMOCIONES("Promociones"),
        REPORTES("Reportes");

        private final String descripcion;

        Modulo(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Enum para acciones comunes (CRUD)
     */
    public enum Accion {
        LISTAR("Listar"),
        VER("Ver detalle"),
        CREAR("Crear"),
        EDITAR("Editar"),
        ELIMINAR("Eliminar"),
        GESTIONAR("Gestionar (todos los permisos)");

        private final String descripcion;

        Accion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Generar nombre de permiso estándar
     * Ejemplo: generarNombre(PELICULAS, CREAR) = "PELICULAS_CREAR"
     */
    public static String generarNombre(Modulo modulo, Accion accion) {
        return modulo.name() + "_" + accion.name();
    }
}
