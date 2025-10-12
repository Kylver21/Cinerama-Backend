package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    /**
     * Buscar permiso por nombre
     */
    Optional<Permiso> findByNombre(String nombre);

    /**
     * Verificar si existe un permiso
     */
    Boolean existsByNombre(String nombre);

    /**
     * Buscar permisos por módulo
     */
    List<Permiso> findByModulo(String modulo);

    /**
     * Buscar permisos activos
     */
    List<Permiso> findByActivoTrue();

    /**
     * Buscar permisos por módulo ordenados
     */
    List<Permiso> findByModuloOrderByNombreAsc(String modulo);

    /**
     * Obtener todos los módulos únicos
     */
    @Query("SELECT DISTINCT p.modulo FROM Permiso p ORDER BY p.modulo")
    List<String> findDistinctModulos();
}
