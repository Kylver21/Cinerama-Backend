# 📚 DOCUMENTACIÓN DE API - CINERAMA BACKEND

## 🌐 URL Base
- **Desarrollo**: `http://localhost:8080/api`
- **Producción**: `https://tu-dominio.com/api`

---

## 🔐 AUTENTICACIÓN

### Registro de Usuario
```http
POST /auth/registro
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "nombre": "string",
  "apellido": "string",
  "telefono": "string",
  "documento": "string"
}

Respuesta:
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "nombre": "string"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}

Respuesta:
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "string",
    "email": "string",
    "roles": ["ROLE_CLIENTE"]
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🎬 PELÍCULAS

### Obtener Películas Paginadas (NUEVO ⭐)
```http
GET /peliculas/paginadas?page=0&size=10&sortBy=popularidad
Authorization: Bearer {token}

Respuesta:
{
  "success": true,
  "message": "Películas obtenidas exitosamente",
  "data": {
    "content": [ /* array de películas */ ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Buscar por Género Paginado (NUEVO ⭐)
```http
GET /peliculas/genero/paginado?genero=Action&page=0&size=10
```

### Buscar por Título Paginado (NUEVO ⭐)
```http
GET /peliculas/titulo/paginado?titulo=Spider&page=0&size=10
```

### Obtener Todas las Películas (Sin paginación)
```http
GET /peliculas

Respuesta:
{
  "success": true,
  "message": "Películas obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "tmdbId": 550988,
      "titulo": "Spider-Man: No Way Home",
      "genero": "Action, Adventure, Science Fiction",
      "duracion": 148,
      "clasificacion": "PG-13",
      "sinopsis": "...",
      "posterUrl": "https://image.tmdb.org/t/p/original/...",
      "backdropUrl": "https://image.tmdb.org/t/p/original/...",
      "popularidad": 5234.123,
      "votoPromedio": 8.5,
      "fechaEstreno": "2021-12-15",
      "activa": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00"
}
```

### Obtener Película por ID
```http
GET /peliculas/{id}

Respuesta:
{
  "success": true,
  "message": "Película obtenida exitosamente",
  "data": { /* objeto película */ },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Películas Activas
```http
GET /peliculas/activas
```

### Películas Populares
```http
GET /peliculas/populares
```

### Películas Mejor Valoradas
```http
GET /peliculas/mejor-valoradas
```

### Buscar por Género (sin paginación)
```http
GET /peliculas/genero/{genero}
```

### Buscar por Título (sin paginación)
```http
GET /peliculas/titulo/{titulo}
```

### Crear Película (ADMIN)
```http
POST /peliculas
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "titulo": "string (requerido, 1-255 caracteres)",
  "genero": "string",
  "duracion": "number (1-600 minutos)",
  "clasificacion": "string",
  "sinopsis": "string",
  "posterUrl": "string",
  "backdropUrl": "string",
  "popularidad": "number (>= 0)",
  "votoPromedio": "number (0-10)",
  "fechaEstreno": "date (no futura)",
  "activa": true
}

Respuesta:
{
  "success": true,
  "message": "Película creada exitosamente",
  "data": { /* película creada con ID */ },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Actualizar Película (ADMIN)
```http
PUT /peliculas/{id}
Authorization: Bearer {token_admin}
```

### Eliminar Película (ADMIN)
```http
DELETE /peliculas/{id}
Authorization: Bearer {token_admin}

Respuesta:
{
  "success": true,
  "message": "Película eliminada exitosamente",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Sincronizar con TMDb (ADMIN)
```http
POST /peliculas/sync?paginas=2
Authorization: Bearer {token_admin}

Respuesta:
{
  "success": true,
  "message": "Sincronización completada exitosamente",
  "data": {
    "nuevas": 15,
    "actualizadas": 5,
    "total": 20,
    "errores": 0,
    "mensaje": "Sincronización exitosa"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🎭 SALAS

### Listar Salas
```http
GET /salas
```

### Crear Sala (ADMIN)
```http
POST /salas
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "nombre": "Sala VIP 1",
  "capacidad": 50,
  "tipoSala": "VIP",
  "activa": true
}
```

---

## 🎫 FUNCIONES

### Listar Funciones
```http
GET /funciones
```

### Funciones por Película
```http
GET /funciones/pelicula/{peliculaId}
```

### Funciones por Sala
```http
GET /funciones/sala/{salaId}
```

---

## 🪑 ASIENTOS

### Obtener Asientos de una Función
```http
GET /asientos/funcion/{funcionId}
```

### Reservar Asiento
```http
POST /asientos/reservar/{asientoId}
Authorization: Bearer {token}
```

### Confirmar Reserva
```http
POST /asientos/confirmar/{asientoId}
Authorization: Bearer {token}
```

---

## ❌ MANEJO DE ERRORES

### Respuesta de Error
```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Códigos de Estado HTTP
- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado
- **400 Bad Request**: Datos inválidos, validaciones fallidas
- **401 Unauthorized**: No autenticado
- **403 Forbidden**: Sin permisos
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

### Ejemplo de Error de Validación
```json
{
  "success": false,
  "message": "Error de validación",
  "data": {
    "errors": {
      "titulo": "El título es obligatorio",
      "duracion": "La duración debe ser al menos 1 minuto"
    }
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🔑 HEADERS REQUERIDOS

### Para Endpoints Públicos
```
Content-Type: application/json
```

### Para Endpoints Autenticados
```
Content-Type: application/json
Authorization: Bearer {tu_token_jwt}
```

---

## 📊 PAGINACIÓN

### Parámetros de Query
- `page`: Número de página (0-indexed, default: 0)
- `size`: Tamaño de página (default: 10)
- `sortBy`: Campo de ordenamiento (ej: "popularidad", "votoPromedio", "fechaEstreno")

### Estructura de Respuesta Paginada
```json
{
  "success": true,
  "message": "Datos obtenidos exitosamente",
  "data": {
    "content": [],           // Array de resultados
    "pageNumber": 0,         // Página actual
    "pageSize": 10,          // Tamaño de página
    "totalElements": 50,     // Total de elementos
    "totalPages": 5,         // Total de páginas
    "first": true,           // Es primera página
    "last": false,           // Es última página
    "hasNext": true,         // Hay página siguiente
    "hasPrevious": false     // Hay página anterior
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🛡️ ROLES Y PERMISOS

### ROLE_CLIENTE (Usuario normal)
- Ver películas, salas, funciones
- Comprar boletos
- Reservar asientos
- Ver sus propias compras

### ROLE_ADMIN (Administrador)
- Todas las acciones de CLIENTE
- Crear/editar/eliminar películas
- Gestionar salas y funciones
- Ver todas las transacciones
- Sincronizar con TMDb

---

## 💡 TIPS PARA ANGULAR

### Interceptor HTTP (agregar token automáticamente)
```typescript
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('token');
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

### Servicio de Películas con Paginación
```typescript
obtenerPeliculasPaginadas(page: number = 0, size: number = 10, sortBy: string = 'popularidad') {
  return this.http.get<ApiResponse<PagedResponse<Pelicula>>>(
    `${this.apiUrl}/peliculas/paginadas?page=${page}&size=${size}&sortBy=${sortBy}`
  );
}
```

### Manejo de Respuestas
```typescript
this.peliculaService.obtenerPeliculasPaginadas(0, 10).subscribe({
  next: (response) => {
    if (response.success) {
      this.peliculas = response.data.content;
      this.totalPages = response.data.totalPages;
    }
  },
  error: (error) => {
    console.error('Error:', error.error.message);
  }
});
```
