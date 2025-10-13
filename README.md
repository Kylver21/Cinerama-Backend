# 🎬 Cinerama - Sistema de Gestión de Cine

Un sistema completo de gestión para cines desarrollado con **Spring Boot 3.5.5** y **MySQL**. Cinerama permite administrar clientes, películas, funciones, boletos, productos de concesión y pagos de manera eficiente.

## 🎯 **NUEVA FUNCIONALIDAD: Integración con TMDb API**

Cinerama ahora se integra con **The Movie Database (TMDb)** para sincronizar automáticamente información de películas en cartelera, incluyendo:
- 🎬 Títulos y descripciones en español
- 🖼️ Pósters y imágenes de alta calidad
- ⭐ Valoraciones de usuarios
- 📅 Fechas de estreno
- 🎭 Géneros y clasificaciones

📚 **Documentación de Integración TMDb:**
- 📘 [Guía Completa de Integración](TMDB_INTEGRATION.md)
- 🚀 [Inicio Rápido](INICIO_RAPIDO.md)
- 🔑 [Configurar API Key](CONFIGURAR_API_KEY.md)
- 📊 [Resumen de Implementación](RESUMEN_IMPLEMENTACION.md)

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

## 🔧 Uso

### Ejecutar la Aplicación

```bash
# Desarrollo
mvn spring-boot:run

# Producción (JAR)
mvn clean package
java -jar target/cinerama-0.0.1-SNAPSHOT.jar
```

### Acceder a la API

Base URL: `http://localhost:8080/api`

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

### ✅ Completado
- [x] **Integración con TMDb API** - Sincronización automática de películas
- [x] Sistema CRUD completo para todas las entidades
- [x] Arquitectura en capas con Spring Boot
- [x] Gestión de clientes, boletos y ventas
- [x] Sistema de pagos

### 🚧 En Desarrollo
- [x] Autenticación y autorización con JWT (Spring Security + JWT)
- [ ] Dashboard de administración
- [ ] Reportes avanzados y analytics

### 📋 Próximas Funcionalidades
- [ ] Sincronización automática diaria con TMDb
- [ ] Integración con sistemas de pago externos
- [ ] Notificaciones por email/SMS
- [ ] API para aplicaciones móviles
- [ ] Sistema de reservas online
- [ ] Integración con sistemas de cine (proyectores, etc.)
- [ ] Sistema de recomendaciones basado en TMDb

## 📚 Documentación Adicional

- 📘 [Integración TMDb - Guía Completa](TMDB_INTEGRATION.md)
- 🚀 [Inicio Rápido - TMDb](INICIO_RAPIDO.md)
- 🔑 [Configurar API Key de TMDb](CONFIGURAR_API_KEY.md)
- 📊 [Resumen de Implementación TMDb](RESUMEN_IMPLEMENTACION.md)
- 📮 [Colección de Postman](Cinerama_Postman_Collection.json)

---

*Última actualización: Octubre 2025*
*Versión: 2.1.0 - Seguridad con JWT + TMDb* ✨

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