# 🔐 FASE 1: SEGURIDAD - ARCHIVOS RESTANTES

## ✅ Archivos ya creados:
1. ✅ Usuario.java
2. ✅ Rol.java
3. ✅ Permiso.java
4. ✅ Cliente.java (actualizado)
5. ✅ UsuarioRepository.java
6. ✅ RolRepository.java
7. ✅ PermisoRepository.java
8. ✅ RegistroDTO.java
9. ✅ LoginDTO.java
10. ✅ LoginResponseDTO.java
11. ✅ MensajeDTO.java
12. ✅ pom.xml (actualizado con Spring Security, JWT, Validation)
13. ✅ UsuarioService.java (interfaz)

## 📋 ARCHIVOS PENDIENTES POR CREAR:

### 1. UsuarioServiceImpl.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/service/impl/UsuarioServiceImpl.java`

**Funcionalidad:**
- Implementa UsuarioService
- Registra usuarios con BCrypt
- Vincula Usuario con Cliente
- Asigna rol ROLE_CLIENTE por defecto
- Gestiona cambio de contraseñas
- Actualiza último acceso

### 2. CustomUserDetailsService.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/security/CustomUserDetailsService.java`

**Funcionalidad:**
- Implementa UserDetailsService de Spring Security
- Carga usuario por username para autenticación
- Integra con UsuarioRepository

### 3. JwtTokenProvider.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/security/JwtTokenProvider.java`

**Funcionalidad:**
- Genera tokens JWT
- Valida tokens JWT
- Extrae username de tokens
- Configura expiración (24 horas)

### 4. JwtAuthenticationFilter.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/security/JwtAuthenticationFilter.java`

**Funcionalidad:**
- Filtro que intercepta cada request
- Extrae JWT del header Authorization
- Valida token y autentica usuario
- Establece SecurityContext

### 5. SecurityConfig.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/config/SecurityConfig.java`

**Funcionalidad:**
- Configura Spring Security
- Define rutas públicas vs protegidas
- Configura CORS
- Desactiva CSRF (para APIs REST)
- Configura sesiones STATELESS
- Agrega filtro JWT

### 6. AuthController.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/controller/AuthController.java`

**Endpoints:**
- POST /api/auth/register - Registro
- POST /api/auth/login - Login con JWT
- GET /api/auth/me - Usuario actual
- POST /api/auth/logout - Logout

### 7. DataInitializer.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/config/DataInitializer.java`

**Funcionalidad:**
- Inicializa permisos (54 permisos CRUD)
- Inicializa roles (ADMIN, CLIENTE, EMPLEADO)
- Crea usuario admin por defecto
- Solo ejecuta si no existen datos

### 8. Actualizar application.properties
**Agregar:**
```properties
# JWT Configuration
jwt.secret=TuClaveSecretaSuperSeguraParaJWT2024CineramaBackend
jwt.expiration=86400000

# Spring Security
spring.security.user.name=admin
spring.security.user.password=admin123
```

### 9. Excepción PersonalizadaException.java
**Ubicación:** `src/main/java/com/utp/cinerama/cinerama/exception/`

Crear:
- RecursoNoEncontradoException.java
- CredencialesInvalidasException.java
- UsuarioYaExisteException.java
- GlobalExceptionHandler.java

### 10. Proteger Controllers Existentes

Agregar a controladores:
```java
@PreAuthorize("hasRole('ADMIN')")  // Solo admin
@PreAuthorize("hasRole('CLIENTE')") // Solo clientes
@PreAuthorize("isAuthenticated()")  // Cualquier usuario logueado
```

---

## 📊 TESTING CON POSTMAN

### 1. Registrar Usuario
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "juan.perez",
  "email": "juan@email.com",
  "password": "Password123!",
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "987654321",
  "numeroDocumento": "12345678",
  "tipoDocumento": "DNI"
}
```

**Respuesta Esperada:**
```json
{
  "mensaje": "Usuario registrado exitosamente",
  "exitoso": true
}
```

### 2. Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "juan.perez",
  "password": "Password123!"
}
```

**Respuesta Esperada:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFuLnBlcmV6...",
  "tipo": "Bearer",
  "username": "juan.perez",
  "email": "juan@email.com",
  "roles": ["ROLE_CLIENTE"],
  "clienteId": 1,
  "nombreCompleto": "Juan Pérez"
}
```

### 3. Usar Token en Requests Protegidos
```http
GET http://localhost:8080/api/boletos/cliente/1
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### 4. Login como Admin
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin123!"
}
```

### 5. Endpoint Protegido (Solo Admin)
```http
GET http://localhost:8080/api/usuarios
Authorization: Bearer <token-de-admin>
```

---

## 🔐 RESUMEN DE PERMISOS

### ROLE_ADMIN (54 permisos)
- Todos los módulos: LISTAR, VER, CREAR, EDITAR, ELIMINAR
- Gestión de usuarios y roles

### ROLE_CLIENTE (12 permisos)
- PELICULAS_LISTAR, PELICULAS_VER
- FUNCIONES_LISTAR, FUNCIONES_VER
- ASIENTOS_LISTAR, ASIENTOS_VER
- BOLETOS_LISTAR, BOLETOS_CREAR, BOLETOS_VER
- PRODUCTOS_LISTAR, PRODUCTOS_VER
- VENTAS_CREAR

### ROLE_EMPLEADO (30 permisos)
- Gestión de películas, salas, funciones
- Gestión de productos y ventas
- Ver clientes
- No puede gestionar usuarios

---

## 🎯 PRÓXIMOS PASOS

1. Copia el contenido de este archivo
2. Te proporcionaré el código completo de cada archivo pendiente
3. Prueba el sistema con Postman
4. Verifica que:
   - ✅ Registro funciona
   - ✅ Login genera JWT
   - ✅ Endpoints protegidos requieren token
   - ✅ Admin tiene acceso total
   - ✅ Cliente tiene acceso limitado

---

¿Quieres que empiece a generar los archivos pendientes uno por uno?
O prefieres que los cree todos de una vez en un ZIP conceptual para que los copies?
