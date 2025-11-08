# 🎬 Cinerama - Sistema de Reservas de Cine Completo

Sistema robusto de gestión de reservas para cines desarrollado con **Spring Boot 3.5.5**, **Java 23.0.2** y **MySQL 8.0.41**. Cinerama es una plataforma completa que permite a los administradores gestionar carteleras de películas desde TMDb, programar funciones, y a los usuarios finales reservar asientos y comprar productos de confitería de manera segura.

---

## 🎯 Objetivo de la Aplicación

### **Para Administradores:**
1. **Login como ADMIN** → Autenticación segura con JWT
2. **Explorar Catálogo TMDb** → Ver películas en cartelera, próximamente, y populares sin guardarlas
3. **Seleccionar Películas** → Guardar solo las películas deseadas desde TMDb a la base de datos
4. **Crear Funciones** → Asignar película + sala + horario + precio de entrada
5. **Gestionar Salas** → Configurar salas con capacidad y tipos de asientos
6. **Monitorear Ventas** → Ver estadísticas de ocupación y ventas

### **Para Usuarios (Clientes):**
1. **Explorar Cartelera** → Ver películas disponibles con sinopsis, póster, y calificación
2. **Seleccionar Película** → Elegir película y ver horarios disponibles
3. **Elegir Función** → Cada horario corresponde a una sala predeterminada
4. **Seleccionar Asientos** → Mapa visual de asientos con disponibilidad en tiempo real
5. **Agregar Productos (Opcional)** → Cancha, gaseosas, combos, etc.
6. **Calcular Total** → Ver desglose de precios antes de confirmar
7. **Simular Pago** → Confirmación de compra con método de pago
8. **Obtener Confirmación** → Número de confirmación y detalles de la compra
9. **Reserva Automática** → Los asientos quedan bloqueados para otros usuarios por 10 minutos

---

## ✨ Características Principales

### 🎬 **Integración TMDb (The Movie Database)**
- **Proxy de Exploración:** Consulta películas en cartelera, próximamente y populares sin guardarlas
- **Selección Administrativa:** Solo el admin elige qué películas guardar en la BD
- **Información Completa:** Sinopsis, póster, backdrop, géneros, runtime, calificación, fecha de estreno
- **Caché Inteligente:** 10 minutos de TTL para reducir llamadas a la API
- **Endpoints:**
  - `GET /api/tmdb/en-cartelera` → Películas en cines
  - `GET /api/tmdb/proximamente` → Próximos estrenos
  - `GET /api/tmdb/populares` → Películas populares
  - `POST /api/peliculas/agregar-desde-tmdb` → Guardar película seleccionada

### 🎫 **Sistema de Reservas Avanzado**
- **Reservas Temporales:** Bloqueo de asientos por 10 minutos al reservar
- **Liberación Automática:** Scheduler que libera asientos expirados cada minuto
- **Bloqueo Pesimista:** Nivel de aislamiento `SERIALIZABLE` para evitar conflictos
- **Pre-Validaciones:** Verifica disponibilidad, estado de función, compatibilidad sala-asiento

### 🛒 **Proceso de Compra Completo (Orquestador)**
1. **Calcular Total:** `POST /api/compras/calcular-total`
   - Subtotal boletos (precio función × cantidad)
   - Subtotal productos (precio × cantidad)
   - Total general
2. **Confirmar Compra:** `POST /api/compras/confirmar`
   - Creación atómica de boletos + productos + pago
   - Número de confirmación único
   - Respuesta completa con resumen detallado

### 🔒 **Seguridad JWT**
- **Autenticación Stateless:** Tokens JWT con expiración de 24 horas
- **Roles y Permisos:** `ROLE_ADMIN`, `ROLE_CLIENTE`
- **Filtro de Autenticación:** 13 filtros de seguridad configurados
- **Encriptación BCrypt:** Contraseñas hasheadas
- **CORS Configurado:** Orígenes permitidos para frontend

### ⏰ **Validaciones de Negocio**
- **Colisión de Horarios:** Previene funciones solapadas en la misma sala
- **Validación de Precios:** Precio de entrada obligatorio por función
- **Verificación de Capacidad:** Valida que no se excedan asientos disponibles
- **Integridad de Datos:** Validaciones con Jakarta Validation

---

## 📚 **Documentación para Desarrolladores Frontend**

### 🚀 **DOCUMENTO CENTRALIZADO** → [FRONTEND_INTEGRATION_GUIDE.md](FRONTEND_INTEGRATION_GUIDE.md)

Este documento unifica toda la información necesaria para consumir el backend:
- ✅ **Flujo Completo de Usuario:** Desde login hasta confirmación de compra
- ✅ **Flujo de Administrador:** Desde exploración TMDb hasta creación de funciones
- ✅ **Endpoints Detallados:** Con ejemplos de request/response
- ✅ **Modelos TypeScript:** Interfaces listas para copiar
- ✅ **Servicios Angular:** Ejemplos de implementación
- ✅ **Interceptores JWT:** Configuración de autenticación
- ✅ **Guards de Rutas:** Protección de páginas por rol
- ✅ **Manejo de Errores:** Estrategias recomendadas
- ✅ **Paginación:** Implementación completa con componentes
- ✅ **Estado de la Aplicación:** Qué funciona y qué falta

📖 **Documentación Adicional:**
- 📘 [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Referencia completa de endpoints
- 🔐 [ANGULAR_INTEGRATION_GUIDE.md](ANGULAR_INTEGRATION_GUIDE.md) - Integración con Angular
- 🎬 [TMDB_INTEGRATION.md](TMDB_INTEGRATION.md) - Detalles técnicos de TMDb
- 📋 [COMMIT_SUMMARY.md](COMMIT_SUMMARY.md) - Resumen ejecutivo de cambios

---

## 📋 Tabla de Contenidos


## ✨ Características
### 🎯 Funcionalidades Principales
- **Gestión de Funciones**: Programación de horarios de películas
- **Sistema de Boletos**: Reserva, venta y control de asientos
- **Gestión de Productos**: Administración de productos de concesión
- **Sistema de Ventas**: Control de ventas de productos con detalles
- **Sistema de Pagos**: Procesamiento de pagos con múltiples métodos
- **Estadísticas**: Reportes de ventas y ocupación
- **Gestión de Asientos**: Generación, reserva temporal, confirmación y liberación por función
- **Seguridad**: Autenticación y autorización con Spring Security + JWT, roles y permisos

### 🔧 Características Técnicas

- **API REST** completa con endpoints CRUD
- **Persistencia en Base de Datos** con MySQL
- **Relaciones JPA** entre entidades
- **Validaciones** de datos y reglas de negocio
- **Arquitectura en Capas** (Controller → Service → Repository)
- **Documentación** de API integrada

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **MySQL 8**
- **Lombok**
- **Spring Security 6**
- **JSON Web Token (JJWT)**

### Herramientas de Desarrollo
- **Maven** - Gestión de dependencias
- **Git** - Control de versiones
- **IntelliJ IDEA / VS Code** - IDE recomendado

## 🏗 Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services     │───▶│  Repositories   │───▶│    Database     │
│   (REST API)    │    │ (Business Logic)│    │   (Data Access) │    │     (MySQL)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Capas del Sistema

1. **Controller Layer**: Expone endpoints REST y maneja requests HTTP
2. **Service Layer**: Contiene la lógica de negocio y validaciones
3. **Repository Layer**: Maneja el acceso a datos con Spring Data JPA
4. **Model Layer**: Define las entidades JPA y relaciones

## 📋 Requisitos Previos

- **Java 17** o superior
- **Maven 3.6** o superior
- **MySQL 8.0** o superior
- **Git**

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Kylver21/Cinerama-Backend.git
cd Cinerama-Backend
```

### 2. Configurar Base de Datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE dbcinerama CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar Variables de Entorno

Editar el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dbcinerama
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## ⚙️ Configuración

### Configuración de Base de Datos

```properties
# Configuración de DataSource
spring.datasource.url=jdbc:mysql://localhost:3306/dbcinerama
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Pool de Conexiones HikariCP (incluido por defecto)
spring.datasource.hikari.maximum-pool-size=20
```

## � Tecnologías y Versiones

- **Java:** 23.0.2 (compilación con Java 21)
- **Spring Boot:** 3.5.5
- **Spring Data JPA:** 3.5.5
- **Spring Security:** 6.3.1
- **Hibernate:** 6.6.26
- **MySQL:** 8.0.41
- **JWT (JJWT):** io.jsonwebtoken 0.11.5
- **Lombok:** 1.18.36
- **Jackson:** 2.18.0
- **HikariCP:** 6.2.1
- **Maven:** 3.x

---

## 📊 Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                         │
│              13 Controllers REST (@RestController)              │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │ AuthController | PeliculaController | TMDbController    │  │
│   │ FuncionController | CompraController | AsientoController │  │
│   │ BoletoController | PagoController | ProductoController  │  │
│   │ ClienteController | SalaController | VentaProductoController │
│   └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     CAPA DE SEGURIDAD                           │
│         JwtRequestFilter → SecurityFilterChain (13 filtros)     │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA DE NEGOCIO                              │
│         13 Services (@Service + @Transactional)                 │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │ PeliculaService | TMDbService (con @Cacheable)          │  │
│   │ FuncionService | CompraService | AsientoService         │  │
│   │ BoletoService | PagoService | ProductoService           │  │
│   │ + AsientoScheduler (cron job cada 1 minuto)             │  │
│   └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   CAPA DE PERSISTENCIA                          │
│         12 Repositories (Spring Data JPA + JPQL)                │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │ Queries con @Query | Derived Methods | Pessimistic Lock │  │
│   └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    BASE DE DATOS MySQL 8.0.41                   │
│         13 Tablas (12 entidades + 1 tabla intermedia)           │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │ usuarios | roles | usuario_roles | clientes | peliculas │  │
│   │ salas | funciones | asientos | boletos | productos      │  │
│   │ ventas_productos | detalle_venta_producto | pagos       │  │
│   └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   INTEGRACIÓN EXTERNA                           │
│              TMDb API (The Movie Database v3)                   │
│         RestTemplate + @Cacheable (TTL: 10 minutos)             │
└─────────────────────────────────────────────────────────────────┘
```

### **Patrones de Diseño Implementados**
- ✅ **MVC** (Model-View-Controller)
- ✅ **Repository Pattern** (Spring Data JPA)
- ✅ **Service Layer Pattern** (Lógica de negocio aislada)
- ✅ **DTO Pattern** (Transferencia de datos)
- ✅ **Builder Pattern** (con Lombok @Builder)
- ✅ **Orchestrator Pattern** (CompraController coordina múltiples operaciones)
- ✅ **Proxy Pattern** (TMDbController como proxy a API externa)
- ✅ **Scheduler Pattern** (Liberación automática de asientos)

---

## � Seguridad (Spring Security + JWT)

La aplicación implementa seguridad stateless con Spring Security 6 y JSON Web Tokens (JWT).

- Autenticación: username/password (BCrypt) vía `/api/auth/login`.
- Emisión de token: JWT firmado (HS256), expiración 1 hora.
- Validación: `JwtRequestFilter` lee `Authorization: Bearer <token>` o cookie `jwt`, valida expiración y carga el usuario desde BD.
- Autorización: por rutas (permitAll / authenticated / hasRole('ADMIN')) y autoridades calculadas desde roles/permisos en BD.

Endpoints de autenticación:

```http
POST /api/auth/register              # Registro (público)
POST /api/auth/login                 # Login → devuelve JWT
GET  /api/auth/me                    # Info del usuario actual (JWT)
POST /api/auth/cambiar-password      # Cambiar contraseña (JWT)
POST /api/auth/logout                # Logout (borra cookie 'jwt')
GET  /api/auth/validate              # Validar token Bearer
POST /api/auth/refresh               # Refrescar token
GET  /api/auth/validar-username/{u}  # Validar disponibilidad username (público)
GET  /api/auth/validar-email/{e}     # Validar disponibilidad email (público)
```

Usa este header en rutas protegidas:

```
Authorization: Bearer <jwt_token>
```

## �📡 API Endpoints

### Esquema de Acceso por Seguridad

- Público (sin token): auth/register, auth/login, validadores de username/email y GET de catálogos (películas, salas, funciones) y algunos GET de asientos.
- Autenticado (JWT): boletos, reservas de asientos, ventas de productos, pagos, clientes.
- Admin (`ROLE_ADMIN`): CRUD (POST/PUT/DELETE) de películas, salas, funciones, productos; generación de asientos, usuarios.

### Clientes
```http
GET    /api/clientes           # Obtener todos los clientes (JWT)
GET    /api/clientes/{id}      # Obtener cliente por ID (JWT)
POST   /api/clientes           # Crear nuevo cliente (JWT)
PUT    /api/clientes/{id}      # Actualizar cliente (JWT)
DELETE /api/clientes/{id}      # Eliminar cliente (JWT)
```

### Películas
```http
# CRUD Básico
GET    /api/peliculas                    # Obtener todas las películas (público)
GET    /api/peliculas/{id}               # Obtener película por ID (público)
POST   /api/peliculas                    # Crear nueva película (ADMIN)
PUT    /api/peliculas/{id}               # Actualizar película (ADMIN)
DELETE /api/peliculas/{id}               # Eliminar película (ADMIN)

# Búsquedas
GET    /api/peliculas/genero/{genero}    # Buscar por género (público)
GET    /api/peliculas/titulo/{titulo}    # Buscar por título (público)
GET    /api/peliculas/activas            # Películas activas en cartelera (público)
GET    /api/peliculas/populares          # Ordenadas por popularidad (público)
GET    /api/peliculas/mejor-valoradas    # Mejor valoradas (público)
GET    /api/peliculas/tmdb/{tmdbId}      # Por ID de TMDb (público)

# Integración TMDb (NUEVO) ✨
POST   /api/peliculas/sync               # Sincronizar con TMDb API (ADMIN)
GET    /api/peliculas/test-connection    # Probar conexión con TMDb (público)
```

### Salas
```http
GET    /api/salas              # Obtener todas las salas (público)
GET    /api/salas/{id}         # Obtener sala por ID (público)
POST   /api/salas              # Crear nueva sala (ADMIN)
PUT    /api/salas/{id}         # Actualizar sala (ADMIN)
DELETE /api/salas/{id}         # Eliminar sala (ADMIN)
GET    /api/salas/activas      # Obtener salas activas (público)
GET    /api/salas/tipo/{tipo}  # Salas por tipo (público)
```

### Funciones
```http
GET    /api/funciones          # Obtener todas las funciones (público)
GET    /api/funciones/{id}     # Obtener función por ID (público)
POST   /api/funciones          # Crear nueva función (ADMIN)
PUT    /api/funciones/{id}     # Actualizar función (ADMIN)
DELETE /api/funciones/{id}     # Eliminar función (ADMIN)
```

### Boletos
```http
GET    /api/boletos                     # Obtener todos los boletos (JWT)
GET    /api/boletos/{id}                # Obtener boleto por ID (JWT)
POST   /api/boletos                     # Crear nuevo boleto (JWT)
PUT    /api/boletos/{id}                # Actualizar boleto (JWT)
DELETE /api/boletos/{id}                # Eliminar boleto (JWT)
GET    /api/boletos/cliente/{clienteId} # Boletos por cliente (JWT)
GET    /api/boletos/funcion/{funcionId} # Boletos por función (JWT)
GET    /api/boletos/estado/{estado}     # Boletos por estado (JWT)
```

### Productos
```http
GET    /api/productos          # Obtener todos los productos (público)
GET    /api/productos/{id}     # Obtener producto por ID (público)
POST   /api/productos          # Crear nuevo producto (ADMIN)
PUT    /api/productos/{id}     # Actualizar producto (ADMIN)
DELETE /api/productos/{id}     # Eliminar producto (ADMIN)
```

### Ventas de Productos
```http
GET    /api/ventas-productos                 # Obtener todas las ventas (JWT)
GET    /api/ventas-productos/{id}            # Obtener venta por ID (JWT)
POST   /api/ventas-productos                 # Crear nueva venta (JWT)
POST   /api/ventas-productos/{id}/completar  # Completar venta (JWT)
```

### Detalles de Venta
```http
GET    /api/detalles-venta-producto                    # Obtener todos los detalles (JWT)
GET    /api/detalles-venta-producto/{id}               # Obtener detalle por ID (JWT)
POST   /api/detalles-venta-producto                    # Crear nuevo detalle (JWT)
PUT    /api/detalles-venta-producto/{id}               # Actualizar detalle (JWT)
DELETE /api/detalles-venta-producto/{id}               # Eliminar detalle (JWT)
GET    /api/detalles-venta-producto/venta/{ventaId}    # Detalles por venta (JWT)
```

### Pagos
```http
GET    /api/pagos              # Obtener todos los pagos (JWT)
GET    /api/pagos/{id}         # Obtener pago por ID (JWT)
POST   /api/pagos              # Crear nuevo pago (JWT)
DELETE /api/pagos/{id}         # Eliminar pago (JWT)
```

### Asientos
```http
GET    /api/asientos/funcion/{funcionId}                  # Mapa de asientos (público)
GET    /api/asientos/estadisticas/{funcionId}             # Estadísticas de ocupación (público)
GET    /api/asientos/disponible/{funcionId}/{fila}/{numero}  # Verificar disponibilidad (público)
GET    /api/asientos/funcion/{funcionId}/estado/{estado}  # Asientos por estado (público)
GET    /api/asientos/funcion/{funcionId}/tipo/{tipo}      # Asientos por tipo (público)

POST   /api/asientos/reservar/{asientoId}                 # Reservar (JWT)
POST   /api/asientos/confirmar/{asientoId}                # Confirmar (JWT)
POST   /api/asientos/liberar/{asientoId}                  # Liberar (JWT)
POST   /api/asientos/generar/{funcionId}                  # Generar asientos (ADMIN)
```

## 📊 Modelo de Datos

### Entidades Principales

- **Usuario**: Credenciales y estado de cuenta (implementa UserDetails)
- **Rol**: Agrupación de permisos (ej: ROLE_ADMIN, ROLE_CLIENTE)
- **Permiso**: Acciones granulares (ej: PELICULAS_CREAR)
- **Cliente**: Información del cliente final (1:1 con Usuario)
- **Pelicula**: Catálogo de películas disponibles (incluye campos de TMDb)
- **Sala**: Salas del cine con diferentes capacidades y tipos
- **Funcion**: Horarios de proyección de películas (relaciona Película y Sala)
- **Asiento**: Asientos por función con estado y tipo
- **Boleto**: Tickets vendidos para las funciones
- **Producto**: Productos de concesión (palomitas, bebidas, etc.)
- **VentaProducto**: Ventas realizadas en concesión
- **DetalleVentaProducto**: Detalles de productos en cada venta
- **Pago**: Información de pagos realizados

### Relaciones

```
Usuario ────────┬────────── Rol ─────────── Permiso
 (1)             (M:N)       (M:N)
  │1:1
Cliente

Pelicula ──┐
       └── Funcion ──── Sala
           │
           └── Asiento (por función)

Cliente ── Boleto ── Funcion

VentaProducto ── DetalleVentaProducto ── Producto
    │
    └── Pago
```

## 🧪 Testing

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=ClienteServiceTest
```

## 📝 Ejemplos de Uso

### Crear un Cliente

```json
POST /api/clientes
{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@email.com",
    "telefono": "987654321",
    "numeroDocumento": "12345678",
    "tipoDocumento": "DNI"
}
```

### Crear una Venta

```json
POST /api/ventas-productos?clienteId=1&metodoPago=TARJETA
```

### Agregar Producto a Venta

```json
POST /api/detalles-venta-producto
{
    "ventaProducto": {"id": 1},
    "producto": {"id": 1},
    "cantidad": 2
}
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Estándares de Código

- Usar **Lombok** para reducir boilerplate
- Seguir convenciones de **Spring Boot**
- Documentar métodos públicos
- Escribir tests para nuevas funcionalidades

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Autores

- **Kylver21** - *Desarrollo inicial* - [GitHub](https://github.com/Kylver21)

## 📞 Contacto

Si tienes preguntas o sugerencias sobre el proyecto, no dudes en contactarnos:

- GitHub: [@Kylver21](https://github.com/Kylver21)
- Email: [tu-email@dominio.com]

---

⭐ **¡Dale una estrella al proyecto si te ha sido útil!**

---

## 🔄 Roadmap

### ✅ Completado (Versión 2.0.0)
- [x] **Integración con TMDb API** - Arquitectura selectiva (proxy + BD)
- [x] **Sistema de Reservas Avanzado** - Bloqueo temporal + liberación automática
- [x] **Orquestador de Compras** - Proceso completo de checkout
- [x] **Validaciones de Negocio** - Colisiones de horarios, pre-reservas
- [x] **Autenticación JWT** - Spring Security 6.3.1 + tokens de 24h
- [x] **CRUD Completo** - 12 entidades con relaciones JPA
- [x] **Scheduler Automático** - Liberación de asientos cada minuto
- [x] **Caché Inteligente** - TMDb API con TTL de 10 minutos

### 🚧 En Desarrollo (Q1 2026)
- [ ] Tests Unitarios (Target: 60% coverage)
- [ ] Swagger/OpenAPI - Documentación interactiva
- [ ] @ControllerAdvice Global - Manejo unificado de excepciones
- [ ] Paginación completa en todos los endpoints

### 📋 Próximas Funcionalidades (Backlog)
- [ ] Flyway/Liquibase - Migraciones versionadas de BD
- [ ] Sistema de Descuentos y Promociones
- [ ] Notificaciones por Email/SMS
- [ ] Historial de Compras del Cliente
- [ ] Sistema de Reseñas y Calificaciones
- [ ] Dashboard de Analytics para Admin
- [ ] Integración con Pasarelas de Pago Reales
- [ ] API para Aplicaciones Móviles
- [ ] Sincronización Automática Diaria con TMDb
- [ ] Sistema de Recomendaciones basado en ML

---

## 📚 Documentación Completa

### **Para Desarrolladores Frontend:** 🎯
📖 **[FRONTEND_INTEGRATION_GUIDE.md](FRONTEND_INTEGRATION_GUIDE.md)** ← **DOCUMENTO PRINCIPAL**
- Objetivo completo de la aplicación
- Flujos de usuario detallados (Cliente + Admin)
- Estado actual del backend (qué funciona y qué falta)
- Endpoints con ejemplos de request/response
- Modelos TypeScript listos para usar
- Servicios Angular implementados
- Configuración completa de autenticación
- Casos de uso con código

### **Documentación Técnica Adicional:**
- 📘 [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Referencia de endpoints REST
- 🔐 [ANGULAR_INTEGRATION_GUIDE.md](ANGULAR_INTEGRATION_GUIDE.md) - Integración Angular específica
- 🎬 [TMDB_INTEGRATION.md](TMDB_INTEGRATION.md) - Detalles técnicos de TMDb
- � [COMMIT_SUMMARY.md](COMMIT_SUMMARY.md) - Resumen ejecutivo de cambios del proyecto

---

## 🐛 Scripts de Mantenimiento

### **Limpieza de Base de Datos**

Ubicación: Raíz del proyecto

1. **`cleanup-peliculas-huerfanas.sql`**
   - **Propósito:** Eliminar películas que no fueron estrenadas en Noviembre 2025
   - **Uso:** Ejecutar en MySQL después de hacer backup
   - **Criterio:** `DELETE WHERE NOT (YEAR = 2025 AND MONTH = 11)`

2. **`cleanup-tablas-obsoletas.sql`**
   - **Propósito:** Eliminar tablas sin entidades JPA (`permisos`, `rol_permisos`)
   - **Uso:** Descomentar comandos DROP después de verificar datos
   - **Incluye:** Backup instructions, rollback procedure, OPTIMIZE TABLE

⚠️ **IMPORTANTE:** Siempre hacer backup antes de ejecutar scripts de limpieza:
```bash
mysqldump -u root -p dbcinerama > backup_dbcinerama_$(date +%Y%m%d).sql
```

---

---

## 🧭 Guía rápida de pruebas con Postman

### Variables de entorno sugeridas

Entorno Admin (usuario con `ROLE_ADMIN` ya en BD, password encriptada):

| Variable         | Valor de ejemplo           |
|------------------|----------------------------|
| base_url         | http://localhost:8080      |
| admin_username   | admin                      |
| admin_password   | Admin123!                  |
| jwt_token        | (se completa en login)     |
| jwt_username     | (auto, opcional)           |
| jwt_roles        | (auto, opcional)           |

Entorno Cliente (usuario regular ya en BD):

| Variable   | Valor de ejemplo       |
|------------|------------------------|
| base_url   | http://localhost:8080  |
| username   | cliente1               |
| password   | Secret123!             |
| jwt_token  | (se completa en login) |

### Flujo recomendado para exponer JPA + Security + JWT

1) Login

POST {{base_url}}/api/auth/login

Body (JSON):

```json
{ "username": "{{admin_username}}", "password": "{{admin_password}}" }
```

Postman Tests (guardar token y datos):

```js
const data = pm.response.json();
pm.environment.set('jwt_token', data.token);
pm.environment.set('jwt_username', data.username);
pm.environment.set('jwt_roles', JSON.stringify(data.roles || []));
```

2) Consumir endpoints con JWT

Añadir header a las peticiones protegidas:

```
Authorization: Bearer {{jwt_token}}
```

3) Demostración por perfiles

- Admin: `POST /api/peliculas` (crear), `PUT/DELETE /api/peliculas/{id}`
- Cliente: `POST /api/asientos/reservar/{id}`, `POST /api/ventas-productos`, `POST /api/pagos`

4) Validación y refresh de token

- `GET {{base_url}}/api/auth/validate`
- `POST {{base_url}}/api/auth/refresh`

5) Logout

- `POST {{base_url}}/api/auth/logout`