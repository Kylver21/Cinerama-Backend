package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Buscar rol por nombre
     */
    Optional<Rol> findByNombre(String nombre);

    /**
     * Verificar si existe un rol
     */
    Boolean existsByNombre(String nombre);

    /**
     * Buscar roles activos
     */
    List<Rol> findByActivoTrue();

    /**
     * Buscar roles con permisos cargados
     */
    @Query("SELECT DISTINCT r FROM Rol r LEFT JOIN FETCH r.permisos")
    List<Rol> findAllWithPermisos();

    /**
     * Buscar rol con permisos por nombre
     */
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.permisos WHERE r.nombre = :nombre")
    Optional<Rol> findByNombreWithPermisos(String nombre);
}
