# 🎬 Cinerama Backend - Sistema de Reservas de Cine

Sistema completo de gestión de cine desarrollado con **Spring Boot 3.5.5** y **Java 21**.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Índice Rápido

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Instalación](#-instalación)
- [API Endpoints](#-api-endpoints)
- [Autenticación JWT](#-autenticación-jwt)
- [Flujo de Compra](#-flujo-de-compra-de-boletos)
- [Integración Frontend](#-integración-con-frontend-angular)

---

## 🎯 Descripción

**Cinerama** es una plataforma que permite:

### Para Clientes:
- 🎥 Ver cartelera de películas activas
- 🎫 Reservar y comprar boletos online
- 💺 Seleccionar asientos en tiempo real (reserva de 15 minutos)
- 🍿 Comprar productos (snacks, bebidas)
- 📱 Recibir confirmación de compra con número único

### Para Administradores:
- 🎬 Gestionar películas (integración con TMDb API)
- 📅 Programar funciones y horarios
- 🏛️ Administrar salas y capacidades
- 📊 Ver estadísticas de ventas y ocupación
- 👥 Gestionar usuarios y clientes

---

## 🛠️ Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.5.5 | Framework backend |
| Spring Security | 6.x | Autenticación/Autorización |
| Spring Data JPA | 3.x | Persistencia de datos |
| MySQL | 8.0+ | Base de datos |
| JWT (jjwt) | 0.11.5 | Tokens de autenticación |
| Lombok | Latest | Reducción de boilerplate |

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Angular)                        │
│                      http://localhost:4200                       │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼ HTTP/REST + JWT
┌─────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT BACKEND                          │
│                      http://localhost:8080                       │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Controllers │──│   Services   │──│ Repositories │          │
│  │   (13 REST)  │  │  (12 Logic)  │  │  (12 JPA)    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Security: JWT + Roles + CORS                 │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    ▼                       ▼
         ┌──────────────────┐    ┌──────────────────┐
         │   MySQL 8.0      │    │    TMDb API      │
         │   dbcinerama     │    │  (Películas)     │
         └──────────────────┘    └──────────────────┘
```

### Capas del Sistema

| Capa | Cantidad | Responsabilidad |
|------|----------|-----------------|
| Controllers | 13 | Endpoints REST, validación de entrada |
| Services | 12 | Lógica de negocio, transacciones |
| Repositories | 12 | Acceso a datos con JPA |
| Models | 12 | Entidades JPA |

---

## 🚀 Instalación

### Requisitos
- Java 21+
- Maven 3.6+
- MySQL 8.0+

### Pasos

```bash
# 1. Clonar repositorio
git clone https://github.com/Kylver21/Cinerama-Backend.git
cd Cinerama-Backend

# 2. Crear base de datos
mysql -u root -p -e "CREATE DATABASE dbcinerama;"

# 3. Configurar credenciales en application.properties

# 4. Ejecutar
./mvnw spring-boot:run
```

Disponible en: `http://localhost:8080`

---

## ⚙️ Configuración

Editar `src/main/resources/application.properties`:

```properties
# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/dbcinerama
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=tu_clave_secreta_segura

# CORS (Frontend Angular)
cors.allowed-origins=http://localhost:4200
```

---

## ☁️ Deploy (Render + Railway + Vercel)

### 1) Backend en Render

Configura estas variables de entorno en Render (Settings → Environment):

- `PORT`: (Render lo define normalmente, no necesitas tocarlo)
- `DATABASE_URL`: tu JDBC URL (ejemplo: `jdbc:mysql://HOST:PORT/DBNAME`)
- `DATABASE_USERNAME`: usuario MySQL
- `DATABASE_PASSWORD`: password MySQL
- `JWT_SECRET`: clave secreta (larga, aleatoria)
- `JWT_EXPIRATION`: (opcional) milisegundos, ej. `86400000`
- `TMDB_API_KEY`: API key de TMDb
- `CORS_ORIGINS`: (opcional) orígenes exactos separados por coma
- `CORS_ORIGIN_PATTERNS`: (recomendado para Vercel) patrones separados por coma
  - Ejemplo seguro y típico: `https://cinerama-frontend.vercel.app,https://*.vercel.app`

Notas:
- Si usas `CORS_ORIGIN_PATTERNS`, puedes dejar `CORS_ORIGINS` tal cual.
- Con credenciales habilitadas, evita usar `*` como origen.

### 2) Base de datos en Railway (MySQL)

En Railway copia los datos de conexión (host, port, database, user, password) y construye:

- `DATABASE_URL` → `jdbc:mysql://<host>:<port>/<database>`
- `DATABASE_USERNAME` → `<user>`
- `DATABASE_PASSWORD` → `<password>`

### 3) Frontend en Vercel (Angular)

En Vercel configura una variable de entorno con la URL pública del backend de Render, por ejemplo:

- `VITE_API_URL` / `API_URL` / `NG_APP_API_URL` (según tu frontend)

Y apúntala a:

- `https://<tu-servicio-backend>.onrender.com`

Importante: tu Angular debe consumir la API usando esa variable (por ejemplo en `environment.prod.ts`).

---

## 📡 API Endpoints

### 🔓 Públicos (Sin Token)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/register` | Registrar cliente |
| GET | `/api/peliculas/activas` | Ver cartelera |
| GET | `/api/funciones/pelicula/{id}` | Funciones por película |
| GET | `/api/asientos/funcion/{id}` | Mapa de asientos |

### 🔐 Cliente (Requiere JWT)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/asientos/{id}/reservar` | Reservar asiento (15 min) |
| POST | `/api/compras/calcular-total` | Calcular total |
| POST | `/api/compras/confirmar` | Confirmar compra |
| GET | `/api/boletos/cliente/{id}` | Mis boletos |

### 👑 Admin (Requiere JWT + ROLE_ADMIN)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/peliculas` | Crear película |
| DELETE | `/api/peliculas/{id}` | Eliminar película |
| POST | `/api/funciones` | Crear función |
| DELETE | `/api/funciones/{id}` | Eliminar función |
| POST | `/api/asientos/generar/{funcionId}` | Generar asientos |

---

## 🔐 Autenticación JWT

### Expiración por Rol

| Rol | Duración |
|-----|----------|
| ADMIN | 8 horas |
| EMPLEADO | 4 horas |
| CLIENTE | 1 hora |

### Flujo

```
1. POST /api/auth/login
   { "username": "cliente1", "password": "123456" }

2. Respuesta:
   {
     "success": true,
     "data": {
       "token": "eyJhbGciOiJIUzI1NiJ9...",
       "roles": ["ROLE_CLIENTE"],
       "expiresIn": 3600000
     }
   }

3. Usar en requests:
   Header: Authorization: Bearer {token}
```

### Formato de Respuestas

**Éxito:**
```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { ... }
}
```

**Error:**
```json
{
  "code": "NOT_FOUND",
  "message": "Recurso no encontrado",
  "status": 404
}
```

---

## 🎫 Flujo de Compra de Boletos

```
LOGIN ──▶ VER CARTELERA ──▶ VER FUNCIONES ──▶ VER ASIENTOS ──▶ RESERVAR ──▶ CONFIRMAR
  │                                                              │            │
  └── Token JWT                                          15 min límite    Boletos +
      (1 hora)                                                            Pago
```

### Ejemplo Confirmar Compra

**Request:**
```json
POST /api/compras/confirmar
{
  "clienteId": 1,
  "funcionId": 5,
  "asientoIds": [286, 287],
  "metodoPago": "YAPE"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "numeroConfirmacion": "7B2D8FE0",
    "totalPagado": 24.00,
    "boletos": [
      { "boletoId": 1, "asiento": "A6", "precio": 12.00 },
      { "boletoId": 2, "asiento": "A5", "precio": 12.00 }
    ],
    "pago": { "estado": "COMPLETADO" }
  }
}
```

---

## 🔗 Integración con Frontend (Angular)

### Interceptor JWT

```typescript
// auth.interceptor.ts
intercept(req: HttpRequest<any>, next: HttpHandler) {
  const token = localStorage.getItem('token');
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next.handle(req);
}
```

### Manejo de Errores

```typescript
catchError((error: HttpErrorResponse) => {
  switch (error.status) {
    case 401: // Token expirado
      this.router.navigate(['/login']);
      break;
    case 403: // Sin permisos
      this.toastr.error('Sin permisos');
      break;
  }
  return throwError(() => error);
});
```

### Estados de Asientos

| Estado | Color | Acción |
|--------|-------|--------|
| DISPONIBLE | 🟢 Verde | Puede seleccionar |
| RESERVADO | 🟡 Amarillo | Temporalmente bloqueado |
| OCUPADO | 🔴 Rojo | Ya vendido |
| BLOQUEADO | ⚫ Gris | No disponible |

---

## 📊 Modelo de Datos

```
Usuario ─── Cliente ─── Boleto ─── Funcion ─── Pelicula
    │           │                     │
   Rol        Pago                  Sala
                                      │
                                   Asiento
```

### Tablas Principales

| Tabla | Descripción |
|-------|-------------|
| usuarios | Autenticación |
| clientes | Datos personales |
| peliculas | Catálogo |
| funciones | Horarios |
| asientos | Por función |
| boletos | Tickets vendidos |
| pagos | Transacciones |

---

## ✅ Estado del Proyecto

| Módulo | Estado |
|--------|--------|
| Autenticación JWT | ✅ Completo |
| Gestión Películas | ✅ Completo |
| Gestión Funciones | ✅ Completo |
| Reserva Asientos | ✅ Completo |
| Compra Boletos | ✅ Completo |
| Venta Productos | ✅ Completo |
| Liberación Automática | ✅ Completo |
| Tests Unitarios | ⚠️ Pendiente |
| Swagger | ⚠️ Pendiente |

---

## 👥 Autor

**Kylver21** - [GitHub](https://github.com/Kylver21)

## 📄 Licencia

MIT License

---

⭐ **¡Dale una estrella si te fue útil!**