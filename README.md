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

- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Uso](#-uso)
- [API Endpoints](#-api-endpoints)
- [Modelo de Datos](#-modelo-de-datos)
- [Contribución](#-contribución)
- [Licencia](#-licencia)

## ✨ Características

### 🎯 Funcionalidades Principales

- **Gestión de Clientes**: Registro, actualización y consulta de clientes
- **Gestión de Películas**: Administración del catálogo de películas
- **Gestión de Salas**: Control de salas con diferentes tipos (Normal, VIP, IMAX, 2D, 3D)
- **Gestión de Funciones**: Programación de horarios de películas
- **Sistema de Boletos**: Reserva, venta y control de asientos
- **Gestión de Productos**: Administración de productos de concesión
- **Sistema de Ventas**: Control de ventas de productos con detalles
- **Sistema de Pagos**: Procesamiento de pagos con múltiples métodos
- **Estadísticas**: Reportes de ventas y ocupación

### 🔧 Características Técnicas

- **API REST** completa con endpoints CRUD
- **Persistencia en Base de Datos** con MySQL
- **Relaciones JPA** entre entidades
- **Validaciones** de datos y reglas de negocio
- **Arquitectura en Capas** (Controller → Service → Repository)
- **Documentación** de API integrada

## 🛠 Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **MySQL 8**
- **Lombok**

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
CREATE DATABASE cineramadb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar Variables de Entorno

Editar el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cineramadb
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
spring.datasource.url=jdbc:mysql://localhost:3306/cineramadb
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

## 📡 API Endpoints

### Clientes
```http
GET    /api/clientes           # Obtener todos los clientes
GET    /api/clientes/{id}      # Obtener cliente por ID
POST   /api/clientes           # Crear nuevo cliente
PUT    /api/clientes/{id}      # Actualizar cliente
DELETE /api/clientes/{id}      # Eliminar cliente
```

### Películas
```http
# CRUD Básico
GET    /api/peliculas                    # Obtener todas las películas
GET    /api/peliculas/{id}               # Obtener película por ID
POST   /api/peliculas                    # Crear nueva película
PUT    /api/peliculas/{id}               # Actualizar película
DELETE /api/peliculas/{id}               # Eliminar película

# Búsquedas
GET    /api/peliculas/genero/{genero}    # Buscar por género
GET    /api/peliculas/titulo/{titulo}    # Buscar por título
GET    /api/peliculas/activas            # Películas activas en cartelera
GET    /api/peliculas/populares          # Ordenadas por popularidad
GET    /api/peliculas/mejor-valoradas    # Mejor valoradas
GET    /api/peliculas/tmdb/{tmdbId}      # Por ID de TMDb

# Integración TMDb (NUEVO) ✨
POST   /api/peliculas/sync               # Sincronizar con TMDb API
GET    /api/peliculas/test-connection    # Probar conexión con TMDb
```

### Salas
```http
GET    /api/salas              # Obtener todas las salas
GET    /api/salas/{id}         # Obtener sala por ID
POST   /api/salas              # Crear nueva sala
PUT    /api/salas/{id}         # Actualizar sala
DELETE /api/salas/{id}         # Eliminar sala
GET    /api/salas/activas      # Obtener salas activas
```

### Funciones
```http
GET    /api/funciones          # Obtener todas las funciones
GET    /api/funciones/{id}     # Obtener función por ID
POST   /api/funciones          # Crear nueva función
PUT    /api/funciones/{id}     # Actualizar función
DELETE /api/funciones/{id}     # Eliminar función
```

### Boletos
```http
GET    /api/boletos                     # Obtener todos los boletos
GET    /api/boletos/{id}                # Obtener boleto por ID
POST   /api/boletos                     # Crear nuevo boleto
PUT    /api/boletos/{id}                # Actualizar boleto
DELETE /api/boletos/{id}                # Eliminar boleto
GET    /api/boletos/cliente/{clienteId} # Boletos por cliente
GET    /api/boletos/funcion/{funcionId} # Boletos por función
```

### Productos
```http
GET    /api/productos          # Obtener todos los productos
GET    /api/productos/{id}     # Obtener producto por ID
POST   /api/productos          # Crear nuevo producto
PUT    /api/productos/{id}     # Actualizar producto
DELETE /api/productos/{id}     # Eliminar producto
```

### Ventas de Productos
```http
GET    /api/ventas-productos              # Obtener todas las ventas
GET    /api/ventas-productos/{id}         # Obtener venta por ID
POST   /api/ventas-productos              # Crear nueva venta
POST   /api/ventas-productos/{id}/completar # Completar venta
```

### Detalles de Venta
```http
GET    /api/detalles-venta-producto                    # Obtener todos los detalles
GET    /api/detalles-venta-producto/{id}               # Obtener detalle por ID
POST   /api/detalles-venta-producto                    # Crear nuevo detalle
PUT    /api/detalles-venta-producto/{id}               # Actualizar detalle
DELETE /api/detalles-venta-producto/{id}               # Eliminar detalle
GET    /api/detalles-venta-producto/venta/{ventaId}    # Detalles por venta
```

### Pagos
```http
GET    /api/pagos              # Obtener todos los pagos
GET    /api/pagos/{id}         # Obtener pago por ID
POST   /api/pagos              # Crear nuevo pago
DELETE /api/pagos/{id}         # Eliminar pago
```

## 📊 Modelo de Datos

### Entidades Principales

- **Cliente**: Información de los clientes del cine
- **Pelicula**: Catálogo de películas disponibles
- **Sala**: Salas del cine con diferentes capacidades y tipos
- **Funcion**: Horarios de proyección de películas
- **Boleto**: Tickets vendidos para las funciones
- **Producto**: Productos de concesión (palomitas, bebidas, etc.)
- **VentaProducto**: Ventas realizadas en concesión
- **DetalleVentaProducto**: Detalles de productos en cada venta
- **Pago**: Información de pagos realizados

### Relaciones

```
Cliente ──┐
          ├── Boleto ──── Funcion ──┬── Pelicula
          │                        └── Sala
          │
          └── VentaProducto ──── DetalleVentaProducto ──── Producto
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
- [ ] Autenticación y autorización con JWT
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
*Versión: 2.0.0 - Con Integración TMDb* ✨