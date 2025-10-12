package com.utp.cinerama.cinerama.config;

import com.utp.cinerama.cinerama.dto.RegistroDTO;
import com.utp.cinerama.cinerama.model.Permiso;
import com.utp.cinerama.cinerama.model.Rol;
import com.utp.cinerama.cinerama.repository.PermisoRepository;
import com.utp.cinerama.cinerama.repository.RolRepository;
import com.utp.cinerama.cinerama.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Inicializador de datos para roles, permisos y usuario admin
 * Se ejecuta al iniciar la aplicación
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public void run(String... args) {
        // Solo inicializar si no existen datos
        if (rolRepository.count() == 0) {
            log.info("🚀 Inicializando datos de seguridad...");
            
            inicializarPermisos();
            inicializarRoles();
            crearUsuarioAdmin();
            
            log.info("✅ Datos de seguridad inicializados correctamente");
        } else {
            log.info("ℹ️ Los datos de seguridad ya existen, omitiendo inicialización");
        }
    }

    /**
     * Crear todos los permisos del sistema
     */
    private void inicializarPermisos() {
        log.info("📋 Creando permisos...");

        List<Permiso> permisos = new ArrayList<>();

        // ========== PERMISOS DE PELÍCULAS ==========
        permisos.add(crearPermiso("PELICULAS_LISTAR", "PELICULAS", "Listar películas"));
        permisos.add(crearPermiso("PELICULAS_VER", "PELICULAS", "Ver detalle de película"));
        permisos.add(crearPermiso("PELICULAS_CREAR", "PELICULAS", "Crear película"));
        permisos.add(crearPermiso("PELICULAS_EDITAR", "PELICULAS", "Editar película"));
        permisos.add(crearPermiso("PELICULAS_ELIMINAR", "PELICULAS", "Eliminar película"));

        // ========== PERMISOS DE SALAS ==========
        permisos.add(crearPermiso("SALAS_LISTAR", "SALAS", "Listar salas"));
        permisos.add(crearPermiso("SALAS_VER", "SALAS", "Ver detalle de sala"));
        permisos.add(crearPermiso("SALAS_CREAR", "SALAS", "Crear sala"));
        permisos.add(crearPermiso("SALAS_EDITAR", "SALAS", "Editar sala"));
        permisos.add(crearPermiso("SALAS_ELIMINAR", "SALAS", "Eliminar sala"));

        // ========== PERMISOS DE FUNCIONES ==========
        permisos.add(crearPermiso("FUNCIONES_LISTAR", "FUNCIONES", "Listar funciones"));
        permisos.add(crearPermiso("FUNCIONES_VER", "FUNCIONES", "Ver detalle de función"));
        permisos.add(crearPermiso("FUNCIONES_CREAR", "FUNCIONES", "Crear función"));
        permisos.add(crearPermiso("FUNCIONES_EDITAR", "FUNCIONES", "Editar función"));
        permisos.add(crearPermiso("FUNCIONES_ELIMINAR", "FUNCIONES", "Eliminar función"));

        // ========== PERMISOS DE ASIENTOS ==========
        permisos.add(crearPermiso("ASIENTOS_LISTAR", "ASIENTOS", "Listar asientos"));
        permisos.add(crearPermiso("ASIENTOS_VER", "ASIENTOS", "Ver detalle de asiento"));
        permisos.add(crearPermiso("ASIENTOS_RESERVAR", "ASIENTOS", "Reservar asiento"));
        permisos.add(crearPermiso("ASIENTOS_CONFIRMAR", "ASIENTOS", "Confirmar reserva de asiento"));
        permisos.add(crearPermiso("ASIENTOS_LIBERAR", "ASIENTOS", "Liberar asiento"));
        permisos.add(crearPermiso("ASIENTOS_GESTIONAR", "ASIENTOS", "Gestionar asientos (admin)"));

        // ========== PERMISOS DE BOLETOS ==========
        permisos.add(crearPermiso("BOLETOS_LISTAR", "BOLETOS", "Listar boletos"));
        permisos.add(crearPermiso("BOLETOS_VER", "BOLETOS", "Ver detalle de boleto"));
        permisos.add(crearPermiso("BOLETOS_CREAR", "BOLETOS", "Crear boleto"));
        permisos.add(crearPermiso("BOLETOS_CANCELAR", "BOLETOS", "Cancelar boleto"));
        permisos.add(crearPermiso("BOLETOS_GESTIONAR", "BOLETOS", "Gestionar boletos (admin)"));

        // ========== PERMISOS DE CLIENTES ==========
        permisos.add(crearPermiso("CLIENTES_LISTAR", "CLIENTES", "Listar clientes"));
        permisos.add(crearPermiso("CLIENTES_VER", "CLIENTES", "Ver detalle de cliente"));
        permisos.add(crearPermiso("CLIENTES_CREAR", "CLIENTES", "Crear cliente"));
        permisos.add(crearPermiso("CLIENTES_EDITAR", "CLIENTES", "Editar cliente"));
        permisos.add(crearPermiso("CLIENTES_ELIMINAR", "CLIENTES", "Eliminar cliente"));

        // ========== PERMISOS DE PRODUCTOS ==========
        permisos.add(crearPermiso("PRODUCTOS_LISTAR", "PRODUCTOS", "Listar productos"));
        permisos.add(crearPermiso("PRODUCTOS_VER", "PRODUCTOS", "Ver detalle de producto"));
        permisos.add(crearPermiso("PRODUCTOS_CREAR", "PRODUCTOS", "Crear producto"));
        permisos.add(crearPermiso("PRODUCTOS_EDITAR", "PRODUCTOS", "Editar producto"));
        permisos.add(crearPermiso("PRODUCTOS_ELIMINAR", "PRODUCTOS", "Eliminar producto"));

        // ========== PERMISOS DE VENTAS DE PRODUCTOS ==========
        permisos.add(crearPermiso("VENTAS_LISTAR", "VENTAS", "Listar ventas"));
        permisos.add(crearPermiso("VENTAS_VER", "VENTAS", "Ver detalle de venta"));
        permisos.add(crearPermiso("VENTAS_CREAR", "VENTAS", "Crear venta"));
        permisos.add(crearPermiso("VENTAS_GESTIONAR", "VENTAS", "Gestionar ventas (admin)"));

        // ========== PERMISOS DE PAGOS ==========
        permisos.add(crearPermiso("PAGOS_LISTAR", "PAGOS", "Listar pagos"));
        permisos.add(crearPermiso("PAGOS_VER", "PAGOS", "Ver detalle de pago"));
        permisos.add(crearPermiso("PAGOS_CREAR", "PAGOS", "Procesar pago"));
        permisos.add(crearPermiso("PAGOS_GESTIONAR", "PAGOS", "Gestionar pagos (admin)"));

        // ========== PERMISOS DE USUARIOS ==========
        permisos.add(crearPermiso("USUARIOS_LISTAR", "USUARIOS", "Listar usuarios"));
        permisos.add(crearPermiso("USUARIOS_VER", "USUARIOS", "Ver detalle de usuario"));
        permisos.add(crearPermiso("USUARIOS_CREAR", "USUARIOS", "Crear usuario"));
        permisos.add(crearPermiso("USUARIOS_EDITAR", "USUARIOS", "Editar usuario"));
        permisos.add(crearPermiso("USUARIOS_ELIMINAR", "USUARIOS", "Eliminar usuario"));
        permisos.add(crearPermiso("USUARIOS_GESTIONAR_ROLES", "USUARIOS", "Gestionar roles de usuarios"));

        // ========== PERMISOS DE REPORTES ==========
        permisos.add(crearPermiso("REPORTES_VER", "REPORTES", "Ver reportes"));
        permisos.add(crearPermiso("REPORTES_GENERAR", "REPORTES", "Generar reportes"));

        permisoRepository.saveAll(permisos);
        log.info("✅ {} permisos creados", permisos.size());
    }

    /**
     * Crear los roles del sistema
     */
    private void inicializarRoles() {
        log.info("👥 Creando roles...");

        // ========== ROL ADMIN ==========
        Rol rolAdmin = crearRol(
                "ROLE_ADMIN",
                "Administrador del sistema con acceso total"
        );
        // Admin tiene TODOS los permisos
        rolAdmin.setPermisos(new HashSet<>(permisoRepository.findAll()));
        rolRepository.save(rolAdmin);
        log.info("✅ Rol ADMIN creado con {} permisos", rolAdmin.getPermisos().size());

        // ========== ROL CLIENTE ==========
        Rol rolCliente = crearRol(
                "ROLE_CLIENTE",
                "Cliente registrado que puede comprar boletos y productos"
        );
        rolCliente.setPermisos(obtenerPermisosCliente());
        rolRepository.save(rolCliente);
        log.info("✅ Rol CLIENTE creado con {} permisos", rolCliente.getPermisos().size());

        // ========== ROL EMPLEADO ==========
        Rol rolEmpleado = crearRol(
                "ROLE_EMPLEADO",
                "Empleado que gestiona funciones, productos y ventas"
        );
        rolEmpleado.setPermisos(obtenerPermisosEmpleado());
        rolRepository.save(rolEmpleado);
        log.info("✅ Rol EMPLEADO creado con {} permisos", rolEmpleado.getPermisos().size());
    }

    /**
     * Crear usuario administrador por defecto
     */
    private void crearUsuarioAdmin() {
        log.info("👤 Creando usuario administrador...");

        RegistroDTO adminDTO = new RegistroDTO();
        adminDTO.setUsername("admin");
        adminDTO.setEmail("admin@cinerama.pe");
        adminDTO.setPassword("Admin123!");
        adminDTO.setNombre("Administrador");
        adminDTO.setApellido("Sistema");
        adminDTO.setTelefono("999999999");
        adminDTO.setNumeroDocumento("00000000");
        adminDTO.setTipoDocumento("DNI");

        try {
            usuarioService.registrar(adminDTO);
            usuarioService.asignarRol(1L, "ROLE_ADMIN"); // Asignar rol admin
            log.info("✅ Usuario admin creado - Username: admin | Password: Admin123!");
        } catch (Exception e) {
            log.warn("⚠️ No se pudo crear usuario admin: {}", e.getMessage());
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Permiso crearPermiso(String nombre, String modulo, String descripcion) {
        return Permiso.builder()
                .nombre(nombre)
                .modulo(modulo)
                .descripcion(descripcion)
                .activo(true)
                .build();
    }

    private Rol crearRol(String nombre, String descripcion) {
        return Rol.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .activo(true)
                .permisos(new HashSet<>())
                .build();
    }

    /**
     * Obtener permisos para rol CLIENTE
     */
    private Set<Permiso> obtenerPermisosCliente() {
        Set<Permiso> permisos = new HashSet<>();

        // Películas
        agregarPermisoSiExiste(permisos, "PELICULAS_LISTAR");
        agregarPermisoSiExiste(permisos, "PELICULAS_VER");

        // Salas
        agregarPermisoSiExiste(permisos, "SALAS_LISTAR");
        agregarPermisoSiExiste(permisos, "SALAS_VER");

        // Funciones
        agregarPermisoSiExiste(permisos, "FUNCIONES_LISTAR");
        agregarPermisoSiExiste(permisos, "FUNCIONES_VER");

        // Asientos
        agregarPermisoSiExiste(permisos, "ASIENTOS_LISTAR");
        agregarPermisoSiExiste(permisos, "ASIENTOS_VER");
        agregarPermisoSiExiste(permisos, "ASIENTOS_RESERVAR");
        agregarPermisoSiExiste(permisos, "ASIENTOS_CONFIRMAR");
        agregarPermisoSiExiste(permisos, "ASIENTOS_LIBERAR");

        // Boletos
        agregarPermisoSiExiste(permisos, "BOLETOS_LISTAR");
        agregarPermisoSiExiste(permisos, "BOLETOS_VER");
        agregarPermisoSiExiste(permisos, "BOLETOS_CREAR");
        agregarPermisoSiExiste(permisos, "BOLETOS_CANCELAR");

        // Productos
        agregarPermisoSiExiste(permisos, "PRODUCTOS_LISTAR");
        agregarPermisoSiExiste(permisos, "PRODUCTOS_VER");

        // Ventas
        agregarPermisoSiExiste(permisos, "VENTAS_CREAR");

        // Pagos
        agregarPermisoSiExiste(permisos, "PAGOS_CREAR");

        return permisos;
    }

    /**
     * Obtener permisos para rol EMPLEADO
     */
    private Set<Permiso> obtenerPermisosEmpleado() {
        Set<Permiso> permisos = new HashSet<>();

        // Películas (gestión completa)
        agregarPermisoSiExiste(permisos, "PELICULAS_LISTAR");
        agregarPermisoSiExiste(permisos, "PELICULAS_VER");
        agregarPermisoSiExiste(permisos, "PELICULAS_CREAR");
        agregarPermisoSiExiste(permisos, "PELICULAS_EDITAR");

        // Salas (gestión completa)
        agregarPermisoSiExiste(permisos, "SALAS_LISTAR");
        agregarPermisoSiExiste(permisos, "SALAS_VER");
        agregarPermisoSiExiste(permisos, "SALAS_CREAR");
        agregarPermisoSiExiste(permisos, "SALAS_EDITAR");

        // Funciones (gestión completa)
        agregarPermisoSiExiste(permisos, "FUNCIONES_LISTAR");
        agregarPermisoSiExiste(permisos, "FUNCIONES_VER");
        agregarPermisoSiExiste(permisos, "FUNCIONES_CREAR");
        agregarPermisoSiExiste(permisos, "FUNCIONES_EDITAR");

        // Asientos (gestión)
        agregarPermisoSiExiste(permisos, "ASIENTOS_GESTIONAR");

        // Boletos (gestión)
        agregarPermisoSiExiste(permisos, "BOLETOS_GESTIONAR");

        // Clientes (solo ver)
        agregarPermisoSiExiste(permisos, "CLIENTES_LISTAR");
        agregarPermisoSiExiste(permisos, "CLIENTES_VER");

        // Productos (gestión completa)
        agregarPermisoSiExiste(permisos, "PRODUCTOS_LISTAR");
        agregarPermisoSiExiste(permisos, "PRODUCTOS_VER");
        agregarPermisoSiExiste(permisos, "PRODUCTOS_CREAR");
        agregarPermisoSiExiste(permisos, "PRODUCTOS_EDITAR");

        // Ventas (gestión)
        agregarPermisoSiExiste(permisos, "VENTAS_GESTIONAR");

        // Pagos (gestión)
        agregarPermisoSiExiste(permisos, "PAGOS_GESTIONAR");

        // Reportes
        agregarPermisoSiExiste(permisos, "REPORTES_VER");
        agregarPermisoSiExiste(permisos, "REPORTES_GENERAR");

        return permisos;
    }

    private void agregarPermisoSiExiste(Set<Permiso> permisos, String nombrePermiso) {
        permisoRepository.findByNombre(nombrePermiso).ifPresent(permisos::add);
    }
}
