# 🎬 CINERAMA - Guía Completa para Desarrolladores Frontend

## 📋 Índice

1. [Resumen de la Aplicación](#-resumen-de-la-aplicación)
2. [Configuración Inicial](#-configuración-inicial)
3. [Flujo del Administrador](#-flujo-del-administrador)
4. [Flujo del Cliente](#-flujo-del-cliente)
5. [Endpoints Detallados](#-endpoints-detallados)
6. [Modelos TypeScript](#-modelos-typescript)
7. [Servicios Angular Sugeridos](#-servicios-angular-sugeridos)

---

## 🎯 Resumen de la Aplicación

### ¿Qué hace Cinerama?

**Cinerama** es un sistema de cine online que permite:

| Rol | Funcionalidades |
|-----|-----------------|
| **ADMIN** | Dashboard con estadísticas, selección de películas desde TMDb, creación de funciones con horarios |
| **CLIENTE** | Ver cartelera, seleccionar película y horario, elegir asientos, comprar boletos (con chocolatería opcional), pagar |

### Flujo General

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ADMINISTRADOR                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   LOGIN ──▶ DASHBOARD ──▶ GESTIÓN PELÍCULAS ──▶ CREAR FUNCIONES            │
│     │          │               │                      │                      │
│     │          │               │                      │                      │
│     ▼          ▼               ▼                      ▼                      │
│   Token    Películas      Ver TMDb API         Asignar película             │
│   Admin    más vistas     (estrenos/populares) + sala + horario             │
│            Productos      Seleccionar 15       + precio + fecha             │
│            más vendidos   películas cartelera                               │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                CLIENTE                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   LOGIN ──▶ CARTELERA ──▶ PELÍCULA ──▶ HORARIOS ──▶ ASIENTOS ──▶ CHECKOUT  │
│     │          │             │            │            │            │        │
│     ▼          ▼             ▼            ▼            ▼            ▼        │
│   Token    Películas     Detalles    Funciones    Seleccionar   Chocolatería│
│   Cliente  activas       + sinopsis  disponibles  asientos      (opcional)  │
│            + próximos                              Reservar      ──▶ Pago    │
│            estrenos                                (15 min)      ──▶ Éxito   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Configuración Inicial

### URL Base
```typescript
const API_URL = 'http://localhost:8080/api';
```

### Headers Requeridos

**Sin autenticación:**
```typescript
headers: {
  'Content-Type': 'application/json'
}
```

**Con autenticación (después del login):**
```typescript
headers: {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${token}`
}
```

### Interceptor JWT (Angular)

```typescript
// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token expirado - redirigir a login
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
        return throwError(() => error);
      })
    );
  }
}
```

---

## 👑 Flujo del Administrador

### PASO 1: Login como Admin

```
POST /api/auth/login
```

**Request:**
```json
{
  "username": "admin",
  "password": "Admin123!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "email": "admin@cinerama.com",
    "roles": ["ROLE_ADMIN"],
    "expiresIn": 28800000,
    "expiresAt": "2025-11-28T00:30:00"
  }
}
```

**Guardar en localStorage:**
```typescript
localStorage.setItem('token', response.data.token);
localStorage.setItem('user', JSON.stringify(response.data));
```

**Duración del token Admin:** 8 horas

---

### PASO 2: Dashboard Admin (Home)

El dashboard muestra estadísticas de la aplicación.

#### 2.1 Películas más vistas/compradas

```
GET /api/boletos
```

**Lógica Frontend:**
1. Obtener todos los boletos
2. Agrupar por `funcion.pelicula.id`
3. Contar cantidad de boletos por película
4. Ordenar de mayor a menor
5. Mostrar top 5 o top 10

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "funcion": {
        "id": 5,
        "pelicula": {
          "id": 1,
          "titulo": "Wicked: Por siempre",
          "posterUrl": "https://image.tmdb.org/..."
        }
      },
      "precio": 12.00,
      "estado": "PAGADO"
    }
  ]
}
```

#### 2.2 Productos más vendidos (Chocolatería)

```
GET /api/detalles-venta-producto
```

**Lógica Frontend:**
1. Obtener todos los detalles de venta
2. Agrupar por `producto.id`
3. Sumar cantidades vendidas por producto
4. Ordenar de mayor a menor
5. Mostrar top 5 productos

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "producto": {
        "id": 1,
        "nombre": "Combo Grande",
        "precio": 25.00,
        "categoria": "COMBO"
      },
      "cantidad": 3,
      "subtotal": 75.00
    }
  ]
}
```

---

### PASO 3: Gestión de Películas (El Plato Fuerte 🍽️)

#### 3.1 Ver películas de ESTRENO desde TMDb

```
GET /api/tmdb/en-cartelera
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 939243,
      "title": "Sonic 3: La Película",
      "original_title": "Sonic the Hedgehog 3",
      "overview": "Sonic, Knuckles y Tails...",
      "poster_path": "/dSNCHEHooLQCjDLJLLnpqRuBQee.jpg",
      "backdrop_path": "/xmcKD5pJmxnwhN4odybvNjRuOBV.jpg",
      "release_date": "2024-12-19",
      "vote_average": 7.8,
      "popularity": 1234.56,
      "genre_ids": [28, 878, 35, 10751]
    },
    // ... más películas
  ]
}
```

#### 3.2 Ver películas POPULARES desde TMDb

```
GET /api/tmdb/populares
```

**Response:** (mismo formato que en-cartelera)

#### 3.3 Ver PRÓXIMOS ESTRENOS desde TMDb

```
GET /api/tmdb/proximamente
```

**Response:** (mismo formato)

---

#### 3.4 Seleccionar película y GUARDAR en base de datos

Cuando el admin selecciona una película de TMDb, debe guardarla en la BD:

```
POST /api/peliculas
```

**Request:**
```json
{
  "titulo": "Sonic 3: La Película",
  "sinopsis": "Sonic, Knuckles y Tails se reúnen para enfrentar...",
  "duracion": 110,
  "genero": "Acción, Ciencia Ficción, Comedia",
  "clasificacion": "PG",
  "fechaEstreno": "2024-12-19",
  "posterUrl": "https://image.tmdb.org/t/p/w500/dSNCHEHooLQCjDLJLLnpqRuBQee.jpg",
  "backdropUrl": "https://image.tmdb.org/t/p/original/xmcKD5pJmxnwhN4odybvNjRuOBV.jpg",
  "tmdbId": 939243,
  "idiomaOriginal": "en",
  "tituloOriginal": "Sonic the Hedgehog 3",
  "popularidad": 1234.56,
  "votoPromedio": 7.8,
  "totalVotos": 500,
  "activa": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Película creada exitosamente",
  "data": {
    "id": 2,
    "titulo": "Sonic 3: La Película",
    // ... todos los campos
  }
}
```

> **NOTA:** El admin puede seleccionar hasta **15 películas** para la cartelera.

---

### PASO 4: Crear Funciones (Horarios)

#### 4.1 Ver salas disponibles

```
GET /api/salas/activas
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nombre": "Sala 1",
      "capacidad": 100,
      "tipo": "2D",
      "activa": true
    },
    {
      "id": 2,
      "nombre": "Sala 2",
      "capacidad": 80,
      "tipo": "3D",
      "activa": true
    }
  ]
}
```

#### 4.2 Crear función con horario

```
POST /api/funciones
```

**Request:**
```json
{
  "peliculaId": 1,
  "salaId": 1,
  "fechaHora": "2025-12-04T15:00:00",
  "precioEntrada": 12.00
}
```

**Response:**
```json
{
  "success": true,
  "message": "Función creada exitosamente",
  "data": {
    "id": 25,
    "pelicula": {
      "id": 1,
      "titulo": "Wicked: Por siempre"
    },
    "sala": {
      "id": 1,
      "nombre": "Sala 1"
    },
    "fechaHora": "2025-12-04T15:00:00",
    "precioEntrada": 12.00,
    "asientosDisponibles": 100,
    "asientosTotales": 100
  }
}
```

#### 4.3 Generar asientos para la función

Después de crear la función, se deben generar los asientos:

```
POST /api/asientos/generar/{funcionId}
```

**Ejemplo:**
```
POST /api/asientos/generar/25
```

**Response:**
```json
{
  "success": true,
  "message": "100 asientos generados para la función 25",
  "data": [
    { "id": 300, "fila": "A", "numero": 21, "estado": "DISPONIBLE" },
    { "id": 301, "fila": "A", "numero": 20, "estado": "DISPONIBLE" },
    // ... más asientos
  ]
}
```

#### 4.4 Crear múltiples horarios para una película

El admin puede crear varias funciones para la misma película en diferentes horarios:

```
POST /api/funciones  → { peliculaId: 1, salaId: 1, fechaHora: "2025-12-04T15:00:00" }
POST /api/funciones  → { peliculaId: 1, salaId: 1, fechaHora: "2025-12-04T18:00:00" }
POST /api/funciones  → { peliculaId: 1, salaId: 1, fechaHora: "2025-12-04T21:00:00" }
POST /api/funciones  → { peliculaId: 1, salaId: 2, fechaHora: "2025-12-04T16:00:00" }
```

---

## 👤 Flujo del Cliente

### PASO 1: Login como Cliente

```
POST /api/auth/login
```

**Request:**
```json
{
  "username": "cliente1",
  "password": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "cliente1",
    "roles": ["ROLE_CLIENTE"],
    "expiresIn": 3600000,
    "clienteId": 1
  }
}
```

**Duración del token Cliente:** 1 hora

---

### PASO 2: Ver Cartelera

#### 2.1 Películas en cartelera (activas con funciones)

```
GET /api/peliculas/activas
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "titulo": "Wicked: Por siempre",
      "sinopsis": "Elphaba, una joven incomprendida...",
      "duracion": 160,
      "genero": "Fantasía, Musical",
      "posterUrl": "https://image.tmdb.org/t/p/w500/...",
      "votoPromedio": 8.2,
      "clasificacion": "PG"
    },
    {
      "id": 2,
      "titulo": "Sonic 3: La Película",
      // ...
    }
  ]
}
```

#### 2.2 Próximos estrenos

```
GET /api/tmdb/proximamente
```

O si ya están en BD:
```
GET /api/peliculas
```
Y filtrar por `fechaEstreno > hoy`

---

### PASO 3: Ver Detalles de Película y Horarios

#### 3.1 Obtener detalles de película

```
GET /api/peliculas/{id}
```

**Ejemplo:**
```
GET /api/peliculas/1
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "titulo": "Wicked: Por siempre",
    "sinopsis": "Elphaba, una joven incomprendida por el color verde de su piel...",
    "duracion": 160,
    "genero": "Fantasía, Musical, Drama",
    "clasificacion": "PG",
    "posterUrl": "https://image.tmdb.org/...",
    "backdropUrl": "https://image.tmdb.org/...",
    "votoPromedio": 8.2,
    "fechaEstreno": "2024-11-20"
  }
}
```

#### 3.2 Ver horarios disponibles (funciones)

```
GET /api/funciones/pelicula/{peliculaId}
```

**Ejemplo:**
```
GET /api/funciones/pelicula/1
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "fechaHora": "2025-12-04T15:00:00",
      "precioEntrada": 12.00,
      "sala": {
        "id": 1,
        "nombre": "Sala 1",
        "tipo": "2D"
      },
      "asientosDisponibles": 98,
      "asientosTotales": 100
    },
    {
      "id": 6,
      "fechaHora": "2025-12-04T18:00:00",
      "precioEntrada": 12.00,
      "sala": {
        "id": 1,
        "nombre": "Sala 1",
        "tipo": "2D"
      },
      "asientosDisponibles": 100,
      "asientosTotales": 100
    }
  ]
}
```

---

### PASO 4: Seleccionar Asientos

#### 4.1 Ver mapa de asientos de la función

```
GET /api/asientos/funcion/{funcionId}
```

**Ejemplo:**
```
GET /api/asientos/funcion/5
```

**Response:**
```json
{
  "success": true,
  "data": [
    { "id": 286, "fila": "A", "numero": 6, "tipo": "NORMAL", "estado": "DISPONIBLE", "precio": 12.00 },
    { "id": 287, "fila": "A", "numero": 5, "tipo": "NORMAL", "estado": "DISPONIBLE", "precio": 12.00 },
    { "id": 288, "fila": "A", "numero": 4, "tipo": "NORMAL", "estado": "DISPONIBLE", "precio": 12.00 },
    { "id": 289, "fila": "A", "numero": 3, "tipo": "NORMAL", "estado": "OCUPADO", "precio": 12.00 },
    { "id": 290, "fila": "A", "numero": 2, "tipo": "NORMAL", "estado": "OCUPADO", "precio": 12.00 },
    // ... más asientos
  ]
}
```

**Estados de asientos:**
| Estado | Significado | Color sugerido |
|--------|-------------|----------------|
| `DISPONIBLE` | Se puede seleccionar | 🟢 Verde |
| `RESERVADO` | Reservado temporalmente por otro usuario | 🟡 Amarillo |
| `OCUPADO` | Ya vendido | 🔴 Rojo |
| `BLOQUEADO` | No disponible (mantenimiento) | ⚫ Gris |

#### 4.2 Reservar asientos seleccionados

Por cada asiento que el usuario seleccione:

```
POST /api/asientos/{asientoId}/reservar
```

**Ejemplo (reservar 2 asientos):**
```
POST /api/asientos/286/reservar
POST /api/asientos/287/reservar
```

**Response:**
```json
{
  "success": true,
  "message": "Asiento A6 reservado exitosamente",
  "data": {
    "id": 286,
    "fila": "A",
    "numero": 6,
    "estado": "RESERVADO",
    "fechaExpiracionReserva": "2025-11-27T17:15:00"
  }
}
```

> ⚠️ **IMPORTANTE:** La reserva dura **15 minutos**. Si no se confirma la compra, los asientos se liberan automáticamente.

---

### PASO 5: Chocolatería (Opcional)

#### 5.1 Ver productos disponibles

```
GET /api/productos
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nombre": "Combo Grande",
      "descripcion": "Popcorn grande + 2 gaseosas",
      "precio": 25.00,
      "categoria": "COMBO",
      "disponible": true,
      "imagenUrl": "/images/combo-grande.jpg"
    },
    {
      "id": 2,
      "nombre": "Popcorn Mediano",
      "descripcion": "Popcorn tamaño mediano",
      "precio": 12.00,
      "categoria": "POPCORN",
      "disponible": true
    },
    {
      "id": 3,
      "nombre": "Gaseosa Grande",
      "descripcion": "500ml",
      "precio": 8.00,
      "categoria": "BEBIDA",
      "disponible": true
    }
  ]
}
```

#### 5.2 Si el usuario NO quiere chocolatería

Simplemente no incluir productos en la confirmación de compra:

```json
{
  "clienteId": 1,
  "funcionId": 5,
  "asientoIds": [286, 287],
  "metodoPago": "YAPE",
  "productos": []  // ← Vacío o no incluirlo
}
```

#### 5.3 Si el usuario SÍ quiere chocolatería

Incluir los productos seleccionados:

```json
{
  "clienteId": 1,
  "funcionId": 5,
  "asientoIds": [286, 287],
  "metodoPago": "YAPE",
  "productos": [
    { "productoId": 1, "cantidad": 1 },
    { "productoId": 3, "cantidad": 2 }
  ]
}
```

---

### PASO 6: Calcular Total (Antes de pagar)

```
POST /api/compras/calcular-total
```

**Request:**
```json
{
  "funcionId": 5,
  "asientoIds": [286, 287],
  "productos": [
    { "productoId": 1, "cantidad": 1 }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalBoletos": 24.00,
    "totalProductos": 25.00,
    "totalGeneral": 49.00,
    "cantidadBoletos": 2,
    "detalleAsientos": [
      { "asientoId": 286, "codigoAsiento": "A6", "precio": 12.00 },
      { "asientoId": 287, "codigoAsiento": "A5", "precio": 12.00 }
    ],
    "detalleProductos": [
      { "productoId": 1, "nombreProducto": "Combo Grande", "cantidad": 1, "precioUnitario": 25.00, "subtotal": 25.00 }
    ]
  }
}
```

---

### PASO 7: Seleccionar Método de Pago y Confirmar

#### Métodos de pago disponibles:
- `YAPE`
- `PLIN`
- `TARJETA`
- `EFECTIVO`

```
POST /api/compras/confirmar
```

**Request:**
```json
{
  "clienteId": 1,
  "funcionId": 5,
  "asientoIds": [286, 287],
  "metodoPago": "YAPE",
  "productos": []
}
```

**Response (¡COMPRA EXITOSA!):**
```json
{
  "success": true,
  "message": "Compra confirmada exitosamente",
  "data": {
    "numeroConfirmacion": "7B2D8FE0",
    "fechaCompra": "2025-11-27T16:39:20",
    "totalPagado": 24.00,
    "clienteId": 1,
    "nombreCliente": "Juan Pérez",
    "boletos": [
      {
        "boletoId": 1,
        "pelicula": "Wicked: Por siempre",
        "sala": "Sala 1",
        "fechaHora": "2025-12-04T15:00:00",
        "asiento": "A6",
        "precio": 12.00
      },
      {
        "boletoId": 2,
        "pelicula": "Wicked: Por siempre",
        "sala": "Sala 1",
        "fechaHora": "2025-12-04T15:00:00",
        "asiento": "A5",
        "precio": 12.00
      }
    ],
    "productos": [],
    "pago": {
      "pagoId": 1,
      "metodoPago": "YAPE",
      "estado": "COMPLETADO",
      "monto": 24.00,
      "fechaPago": "2025-11-27T16:39:20"
    }
  }
}
```

---

### PASO 8: Pantalla de Éxito

Mostrar al usuario:
- ✅ Número de confirmación: `7B2D8FE0`
- 🎬 Película: Wicked: Por siempre
- 📅 Fecha: 4 de Diciembre 2025
- 🕐 Hora: 3:00 PM
- 🏛️ Sala: Sala 1
- 💺 Asientos: A6, A5
- 💰 Total pagado: S/ 24.00
- 💳 Método: YAPE

---

## 📡 Endpoints Detallados

### Autenticación

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Iniciar sesión | ❌ |
| POST | `/api/auth/register` | Registrar cliente | ❌ |
| GET | `/api/auth/validate` | Validar token | ✅ |
| POST | `/api/auth/logout` | Cerrar sesión | ✅ |

### Películas

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/peliculas` | Todas las películas | ❌ |
| GET | `/api/peliculas/activas` | Películas en cartelera | ❌ |
| GET | `/api/peliculas/{id}` | Detalle de película | ❌ |
| POST | `/api/peliculas` | Crear película | 👑 ADMIN |
| PUT | `/api/peliculas/{id}` | Actualizar película | 👑 ADMIN |
| DELETE | `/api/peliculas/{id}` | Eliminar película | 👑 ADMIN |

### TMDb (API Externa)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/tmdb/en-cartelera` | Películas en cines (TMDb) | 👑 ADMIN |
| GET | `/api/tmdb/populares` | Películas populares (TMDb) | 👑 ADMIN |
| GET | `/api/tmdb/proximamente` | Próximos estrenos (TMDb) | 👑 ADMIN |

### Funciones

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/funciones` | Todas las funciones | ❌ |
| GET | `/api/funciones/{id}` | Detalle de función | ❌ |
| GET | `/api/funciones/pelicula/{id}` | Funciones por película | ❌ |
| POST | `/api/funciones` | Crear función | 👑 ADMIN |
| DELETE | `/api/funciones/{id}` | Eliminar función | 👑 ADMIN |

### Asientos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/asientos/funcion/{id}` | Mapa de asientos | ❌ |
| POST | `/api/asientos/{id}/reservar` | Reservar asiento | ✅ |
| POST | `/api/asientos/{id}/liberar` | Liberar asiento | ✅ |
| POST | `/api/asientos/generar/{funcionId}` | Generar asientos | 👑 ADMIN |

### Compras

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/compras/calcular-total` | Calcular total | ✅ |
| POST | `/api/compras/confirmar` | Confirmar compra | ✅ |

### Productos (Chocolatería)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/productos` | Todos los productos | ❌ |
| GET | `/api/productos/{id}` | Detalle producto | ❌ |
| POST | `/api/productos` | Crear producto | 👑 ADMIN |

### Boletos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/boletos/cliente/{id}` | Boletos del cliente | ✅ |
| GET | `/api/boletos` | Todos los boletos | 👑 ADMIN |

---

## 📦 Modelos TypeScript

```typescript
// auth.models.ts
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  email: string;
  roles: string[];
  expiresIn: number;
  expiresAt: string;
  clienteId?: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface ErrorResponse {
  code: string;
  message: string;
  status: number;
  path: string;
  details?: { [key: string]: string };
}

// pelicula.models.ts
export interface Pelicula {
  id: number;
  titulo: string;
  sinopsis: string;
  duracion: number;
  genero: string;
  clasificacion: string;
  fechaEstreno: string;
  posterUrl: string;
  backdropUrl: string;
  votoPromedio: number;
  tmdbId?: number;
  activa: boolean;
}

export interface PeliculaTMDb {
  id: number;
  title: string;
  original_title: string;
  overview: string;
  poster_path: string;
  backdrop_path: string;
  release_date: string;
  vote_average: number;
  popularity: number;
  genre_ids: number[];
}

// funcion.models.ts
export interface Funcion {
  id: number;
  fechaHora: string;
  precioEntrada: number;
  pelicula: Pelicula;
  sala: Sala;
  asientosDisponibles: number;
  asientosTotales: number;
}

export interface CrearFuncionDTO {
  peliculaId: number;
  salaId: number;
  fechaHora: string;
  precioEntrada: number;
}

// sala.models.ts
export interface Sala {
  id: number;
  nombre: string;
  capacidad: number;
  tipo: string;
  activa: boolean;
}

// asiento.models.ts
export interface Asiento {
  id: number;
  fila: string;
  numero: number;
  tipo: 'NORMAL';
  estado: 'DISPONIBLE' | 'RESERVADO' | 'OCUPADO' | 'BLOQUEADO';
  precio: number;
  fechaExpiracionReserva?: string;
}

// producto.models.ts
export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  disponible: boolean;
  imagenUrl?: string;
}

// compra.models.ts
export interface ConfirmarCompraDTO {
  clienteId: number;
  funcionId: number;
  asientoIds: number[];
  metodoPago: 'YAPE' | 'PLIN' | 'TARJETA' | 'EFECTIVO';
  productos?: DetalleProductoDTO[];
}

export interface DetalleProductoDTO {
  productoId: number;
  cantidad: number;
}

export interface ConfirmacionCompra {
  numeroConfirmacion: string;
  fechaCompra: string;
  totalPagado: number;
  clienteId: number;
  nombreCliente: string;
  boletos: BoletoResumen[];
  productos: ProductoResumen[];
  pago: PagoResumen;
}

export interface BoletoResumen {
  boletoId: number;
  pelicula: string;
  sala: string;
  fechaHora: string;
  asiento: string;
  precio: number;
}

export interface ProductoResumen {
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface PagoResumen {
  pagoId: number;
  metodoPago: string;
  estado: string;
  monto: number;
  fechaPago: string;
}

export interface TotalCompra {
  totalBoletos: number;
  totalProductos: number;
  totalGeneral: number;
  cantidadBoletos: number;
  detalleAsientos: DetalleAsiento[];
  detalleProductos: DetalleProducto[];
}

export interface DetalleAsiento {
  asientoId: number;
  codigoAsiento: string;
  precio: number;
}

export interface DetalleProducto {
  productoId: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}
```

---

## 🛠️ Servicios Angular Sugeridos

### AuthService

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          if (response.success) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  isAdmin(): boolean {
    const user = this.getUser();
    return user?.roles?.includes('ROLE_ADMIN') || false;
  }

  getUser(): LoginResponse | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
```

### PeliculaService

```typescript
@Injectable({ providedIn: 'root' })
export class PeliculaService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Para clientes - ver cartelera
  getActivas(): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(`${this.apiUrl}/peliculas/activas`);
  }

  getById(id: number): Observable<ApiResponse<Pelicula>> {
    return this.http.get<ApiResponse<Pelicula>>(`${this.apiUrl}/peliculas/${id}`);
  }

  // Para admin - TMDb
  getEnCartelera(): Observable<ApiResponse<PeliculaTMDb[]>> {
    return this.http.get<ApiResponse<PeliculaTMDb[]>>(`${this.apiUrl}/tmdb/en-cartelera`);
  }

  getPopulares(): Observable<ApiResponse<PeliculaTMDb[]>> {
    return this.http.get<ApiResponse<PeliculaTMDb[]>>(`${this.apiUrl}/tmdb/populares`);
  }

  getProximamente(): Observable<ApiResponse<PeliculaTMDb[]>> {
    return this.http.get<ApiResponse<PeliculaTMDb[]>>(`${this.apiUrl}/tmdb/proximamente`);
  }

  // Para admin - guardar película
  crear(pelicula: Partial<Pelicula>): Observable<ApiResponse<Pelicula>> {
    return this.http.post<ApiResponse<Pelicula>>(`${this.apiUrl}/peliculas`, pelicula);
  }
}
```

### FuncionService

```typescript
@Injectable({ providedIn: 'root' })
export class FuncionService {
  private apiUrl = 'http://localhost:8080/api/funciones';

  constructor(private http: HttpClient) {}

  getByPelicula(peliculaId: number): Observable<ApiResponse<Funcion[]>> {
    return this.http.get<ApiResponse<Funcion[]>>(`${this.apiUrl}/pelicula/${peliculaId}`);
  }

  crear(dto: CrearFuncionDTO): Observable<ApiResponse<Funcion>> {
    return this.http.post<ApiResponse<Funcion>>(this.apiUrl, dto);
  }
}
```

### AsientoService

```typescript
@Injectable({ providedIn: 'root' })
export class AsientoService {
  private apiUrl = 'http://localhost:8080/api/asientos';

  constructor(private http: HttpClient) {}

  getByFuncion(funcionId: number): Observable<ApiResponse<Asiento[]>> {
    return this.http.get<ApiResponse<Asiento[]>>(`${this.apiUrl}/funcion/${funcionId}`);
  }

  reservar(asientoId: number): Observable<ApiResponse<Asiento>> {
    return this.http.post<ApiResponse<Asiento>>(`${this.apiUrl}/${asientoId}/reservar`, {});
  }

  liberar(asientoId: number): Observable<ApiResponse<Asiento>> {
    return this.http.post<ApiResponse<Asiento>>(`${this.apiUrl}/${asientoId}/liberar`, {});
  }

  generar(funcionId: number): Observable<ApiResponse<Asiento[]>> {
    return this.http.post<ApiResponse<Asiento[]>>(`${this.apiUrl}/generar/${funcionId}`, {});
  }
}
```

### CompraService

```typescript
@Injectable({ providedIn: 'root' })
export class CompraService {
  private apiUrl = 'http://localhost:8080/api/compras';

  constructor(private http: HttpClient) {}

  calcularTotal(dto: CalcularTotalDTO): Observable<ApiResponse<TotalCompra>> {
    return this.http.post<ApiResponse<TotalCompra>>(`${this.apiUrl}/calcular-total`, dto);
  }

  confirmar(dto: ConfirmarCompraDTO): Observable<ApiResponse<ConfirmacionCompra>> {
    return this.http.post<ApiResponse<ConfirmacionCompra>>(`${this.apiUrl}/confirmar`, dto);
  }
}
```

---

## 🎨 Estructura de Componentes Sugerida

```
src/app/
├── core/
│   ├── interceptors/
│   │   └── auth.interceptor.ts
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   └── admin.guard.ts
│   └── services/
│       ├── auth.service.ts
│       ├── pelicula.service.ts
│       ├── funcion.service.ts
│       ├── asiento.service.ts
│       ├── compra.service.ts
│       └── producto.service.ts
│
├── shared/
│   ├── models/
│   │   ├── auth.models.ts
│   │   ├── pelicula.models.ts
│   │   ├── funcion.models.ts
│   │   ├── asiento.models.ts
│   │   └── compra.models.ts
│   └── components/
│       ├── navbar/
│       └── footer/
│
├── features/
│   ├── auth/
│   │   ├── login/
│   │   └── register/
│   │
│   ├── admin/
│   │   ├── dashboard/           ← Estadísticas
│   │   ├── peliculas/
│   │   │   ├── lista/           ← Películas en BD
│   │   │   └── tmdb/            ← Seleccionar de TMDb
│   │   └── funciones/
│   │       ├── lista/
│   │       └── crear/           ← Crear función + horario
│   │
│   └── cliente/
│       ├── cartelera/           ← Lista de películas
│       ├── pelicula-detalle/    ← Info + horarios
│       ├── seleccion-asientos/  ← Mapa de sala
│       ├── chocolateria/        ← Productos opcionales
│       ├── checkout/            ← Método de pago
│       └── confirmacion/        ← Compra exitosa
│
└── app-routing.module.ts
```

---

## ✅ Checklist de Implementación

### Admin
- [ ] Login como admin
- [ ] Dashboard con estadísticas
- [ ] Ver películas de TMDb (en-cartelera, populares, próximamente)
- [ ] Seleccionar y guardar películas
- [ ] Crear funciones con horarios
- [ ] Generar asientos para funciones

### Cliente
- [ ] Login como cliente
- [ ] Ver cartelera (películas activas)
- [ ] Ver próximos estrenos
- [ ] Ver detalle de película
- [ ] Ver horarios disponibles (funciones)
- [ ] Seleccionar asientos en el mapa
- [ ] Reservar asientos (15 min)
- [ ] Ver chocolatería (opcional)
- [ ] Calcular total
- [ ] Seleccionar método de pago
- [ ] Confirmar compra
- [ ] Ver pantalla de éxito

---

## 🆘 Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| 401 Unauthorized | Token expirado o inválido | Redirigir a login, obtener nuevo token |
| 403 Forbidden | Sin permisos | Verificar rol del usuario |
| 400 Bad Request | Datos inválidos | Revisar formato del request |
| 409 Conflict | Asiento ya reservado | Refrescar mapa de asientos |
| 500 Internal Error | Error del servidor | Revisar consola de Spring Boot |

---

## 📞 Contacto

Si tienen dudas sobre algún endpoint o necesitan ayuda con la integración, no duden en consultarme.

**Backend Developer:** Kylver21
**Repositorio:** https://github.com/Kylver21/Cinerama-Backend

---

¡Éxitos con el desarrollo del frontend! 🚀
