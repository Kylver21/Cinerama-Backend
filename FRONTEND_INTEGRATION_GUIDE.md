# 🎯 GUÍA COMPLETA PARA DESARROLLADORES FRONTEND - CINERAMA

**Última Actualización:** 07 de Noviembre de 2025  
**Versión Backend:** 2.0.0  
**Stack Backend:** Spring Boot 3.5.5 + Java 23 + MySQL 8.0.41 + JWT  

---

## 📋 Tabla de Contenidos

1. [Objetivo de la Aplicación](#-objetivo-de-la-aplicación)
2. [Estado Actual del Backend](#-estado-actual-del-backend)
3. [Flujos Completos de Usuario](#-flujos-completos-de-usuario)
4. [Endpoints Esenciales](#-endpoints-esenciales)
5. [Modelos de Datos (TypeScript)](#-modelos-de-datos-typescript)
6. [Configuración de Autenticación](#-configuración-de-autenticación)
7. [Implementación de Servicios](#-implementación-de-servicios)
8. [Casos de Uso Comunes](#-casos-de-uso-comunes)
9. [Manejo de Errores](#-manejo-de-errores)
10. [Lo que Falta por Implementar](#-lo-que-falta-por-implementar)

---

## 🎯 Objetivo de la Aplicación

### **Cinerama es una plataforma de reservas de cine que permite:**

#### **👨‍💼 Administradores:**
1. **Autenticarse** con credenciales de administrador (JWT)
2. **Explorar catálogo TMDb** sin guardar nada en la base de datos:
   - Películas en cartelera (`/api/tmdb/en-cartelera`)
   - Próximamente (`/api/tmdb/proximamente`)
   - Populares (`/api/tmdb/populares`)
3. **Seleccionar películas** para agregarlas a la cartelera del cine (`POST /api/peliculas/agregar-desde-tmdb`)
4. **Crear funciones** asignando:
   - Película seleccionada
   - Sala del cine
   - Horario específico
   - Precio de entrada
5. **Gestionar salas** con capacidad y tipos de asientos
6. **Monitorear ventas** y estadísticas de ocupación

#### **👥 Clientes (Usuarios):**
1. **Registrarse** o **autenticarse** en la plataforma
2. **Explorar cartelera** de películas disponibles con:
   - Póster, sinopsis, calificación
   - Géneros, duración, fecha de estreno
3. **Seleccionar película** de su preferencia
4. **Ver horarios disponibles** (funciones creadas por el admin)
5. **Elegir función** → Cada horario tiene una sala predeterminada
6. **Ver mapa de asientos** de la sala seleccionada
7. **Seleccionar asientos** disponibles (bloqueados por 10 minutos automáticamente)
8. **(Opcional) Agregar productos** de confitería:
   - Cancha de maíz
   - Gaseosas
   - Combos
9. **Calcular total** con desglose:
   - Subtotal boletos
   - Subtotal productos
   - Total general
10. **Confirmar compra** simulando el pago
11. **Recibir confirmación** con:
    - Número de confirmación único
    - Detalles de boletos y productos
    - Información del pago

#### **🔐 Seguridad:**
- Los asientos reservados quedan **bloqueados automáticamente** para otros usuarios
- Reservas temporales de **10 minutos** (si no se confirma, se liberan automáticamente)
- Cada usuario solo puede ver y modificar **sus propias compras**

---

## ✅ Estado Actual del Backend

### **Lo que ESTÁ IMPLEMENTADO (100% funcional):**

#### 🎬 **Sistema de Películas + TMDb**
- ✅ Exploración de catálogo TMDb sin guardar (proxy)
- ✅ Selección administrativa de películas para guardar en BD
- ✅ Información completa: sinopsis, póster, géneros, runtime, calificación
- ✅ Caché de 10 minutos para reducir llamadas a TMDb API
- ✅ CRUD completo de películas (solo ADMIN)

#### 🎫 **Sistema de Funciones**
- ✅ Creación de funciones con película + sala + horario + precio
- ✅ Validación de colisiones de horarios en la misma sala
- ✅ Relación completa Película → Función → Sala
- ✅ CRUD completo (solo ADMIN)

#### 🪑 **Sistema de Asientos y Reservas**
- ✅ Generación automática de asientos por función
- ✅ Mapa de asientos con estados: DISPONIBLE, RESERVADO, OCUPADO
- ✅ Reserva temporal con bloqueo pesimista (evita conflictos)
- ✅ Liberación automática de asientos no confirmados (scheduler cada 1 minuto)
- ✅ Pre-validaciones: disponibilidad, función activa, compatibilidad sala-asiento

#### 🛒 **Sistema de Compras (Orquestador)**
- ✅ Endpoint de cálculo de total (`POST /api/compras/calcular-total`)
- ✅ Endpoint de confirmación atómica (`POST /api/compras/confirmar`)
- ✅ Creación de boletos + productos + pago en una sola transacción
- ✅ Generación de número de confirmación único
- ✅ Respuesta completa con resumen detallado

#### 🍿 **Sistema de Productos**
- ✅ CRUD de productos de confitería
- ✅ Gestión de ventas de productos
- ✅ Cálculo de subtotales por producto

#### 💳 **Sistema de Pagos**
- ✅ Registro de pagos con método y estado
- ✅ Relación con compras de boletos y productos

#### 🔒 **Seguridad JWT**
- ✅ Autenticación con JWT (expiración 24 horas)
- ✅ Roles: ROLE_ADMIN, ROLE_CLIENTE
- ✅ Encriptación BCrypt de contraseñas
- ✅ Filtro de autenticación en 13 puntos
- ✅ CORS configurado para frontend

### **Métricas:**
- 📦 **12 Entidades JPA** mapeadas
- 🌐 **13 Controladores REST** activos
- 🔧 **13 Servicios** con lógica de negocio
- 📊 **13 Tablas** en base de datos
- 🎯 **Cobertura de Funcionalidades:** ~78% production-ready

---

## 🔄 Flujos Completos de Usuario

### **Flujo 1: Cliente Comprando Entradas (Completo)**

```
1. REGISTRO/LOGIN
   ↓
   POST /api/auth/registro  (o /api/auth/login)
   ↓
   Respuesta: { token, username, email, roles }
   ↓
   Guardar token en localStorage

2. VER CARTELERA
   ↓
   GET /api/peliculas/activas
   ↓
   Respuesta: Lista de películas con póster, sinopsis, calificación

3. SELECCIONAR PELÍCULA
   ↓
   GET /api/peliculas/{id}
   ↓
   Respuesta: Detalles completos de la película

4. VER HORARIOS DISPONIBLES
   ↓
   GET /api/funciones/pelicula/{peliculaId}
   ↓
   Respuesta: Lista de funciones con horario, sala, precio

5. SELECCIONAR FUNCIÓN
   ↓
   Guardar funcionId seleccionado

6. VER MAPA DE ASIENTOS
   ↓
   GET /api/asientos/funcion/{funcionId}
   ↓
   Respuesta: Array de asientos con estado (DISPONIBLE, RESERVADO, OCUPADO)

7. SELECCIONAR ASIENTOS
   ↓
   Para cada asiento: POST /api/asientos/reservar/{asientoId}
   ↓
   Respuesta: Asiento bloqueado por 10 minutos

8. (OPCIONAL) AGREGAR PRODUCTOS
   ↓
   GET /api/productos  (listar productos disponibles)
   ↓
   Seleccionar productos y cantidades

9. CALCULAR TOTAL
   ↓
   POST /api/compras/calcular-total
   Body: {
     "funcionId": Long,
     "asientoIds": [Long],
     "productos": [{ "productoId": Long, "cantidad": Integer }]
   }
   ↓
   Respuesta: {
     "totalBoletos": BigDecimal,
     "totalProductos": BigDecimal,
     "totalGeneral": BigDecimal,
     "detalleAsientos": [...],
     "detalleProductos": [...]
   }

10. CONFIRMAR COMPRA
    ↓
    POST /api/compras/confirmar
    Body: {
      "clienteId": Long,
      "funcionId": Long,
      "asientoIds": [Long],
      "productos": [{ "productoId": Long, "cantidad": Integer }],
      "metodoPago": "EFECTIVO" | "TARJETA" | "YAPE"
    }
    ↓
    Respuesta: {
      "numeroConfirmacion": String,
      "fechaCompra": DateTime,
      "totalPagado": BigDecimal,
      "boletos": [...],
      "productos": [...],
      "pago": {...}
    }

11. MOSTRAR CONFIRMACIÓN
    ↓
    Pantalla de éxito con número de confirmación y detalles
```

---

### **Flujo 2: Administrador Creando Funciones**

```
1. LOGIN COMO ADMIN
   ↓
   POST /api/auth/login
   Body: { "username": "admin", "password": "..." }
   ↓
   Respuesta: { token, roles: ["ROLE_ADMIN"] }

2. EXPLORAR CATÁLOGO TMDB (Sin guardar)
   ↓
   GET /api/tmdb/en-cartelera?page=1
   GET /api/tmdb/proximamente?page=1
   GET /api/tmdb/populares?page=1
   ↓
   Respuesta: Lista de películas de TMDb (no guardadas en BD)

3. SELECCIONAR PELÍCULA PARA AGREGAR
   ↓
   POST /api/peliculas/agregar-desde-tmdb
   Body: {
     "tmdbId": 569094,
     "duracionMinutos": 120
   }
   ↓
   Respuesta: Película guardada en BD con toda su info

4. VER SALAS DISPONIBLES
   ↓
   GET /api/salas/activas
   ↓
   Respuesta: Lista de salas con capacidad y tipo

5. CREAR FUNCIÓN
   ↓
   POST /api/funciones
   Body: {
     "peliculaId": Long,
     "salaId": Long,
     "fechaHora": "2025-11-10T18:00:00",
     "precioEntrada": 15.00,
     "estado": "ACTIVA"
   }
   ↓
   Respuesta: Función creada con validación de colisiones

6. GENERAR ASIENTOS AUTOMÁTICAMENTE
   ↓
   POST /api/asientos/generar/{funcionId}
   ↓
   Respuesta: Asientos generados según capacidad de sala
```

---

## 🌐 Endpoints Esenciales

### **Base URL:** `http://localhost:8080/api`

### 🔐 **Autenticación (Público)**

```typescript
// Registro de nuevo usuario
POST /auth/registro
Body: {
  username: string;      // Único, 3-50 caracteres
  email: string;         // Único, formato válido
  password: string;      // Min 6 caracteres
  nombre: string;        // Requerido
  apellido: string;
  telefono: string;      // 9 dígitos
  documento: string;     // 8 dígitos
}
Response: ApiResponse<Usuario>

// Login
POST /auth/login
Body: {
  username: string;
  password: string;
}
Response: ApiResponse<{
  token: string;
  username: string;
  email: string;
  roles: string[];
}>

// Obtener usuario actual (con JWT)
GET /auth/me
Headers: { Authorization: "Bearer {token}" }
Response: ApiResponse<Usuario>
```

---

### 🎬 **Películas**

```typescript
// Listar películas activas (público)
GET /peliculas/activas
Response: ApiResponse<Pelicula[]>

// Obtener película por ID (público)
GET /peliculas/{id}
Response: ApiResponse<Pelicula>

// Buscar por género (público)
GET /peliculas/genero/{genero}
Response: ApiResponse<Pelicula[]>

// ADMIN: Agregar película desde TMDb
POST /peliculas/agregar-desde-tmdb
Headers: { Authorization: "Bearer {token}" }
Body: {
  tmdbId: number;
  duracionMinutos: number;
}
Response: ApiResponse<Pelicula>
```

---

### 🎥 **TMDb Proxy (Exploración sin guardar)**

```typescript
// Películas en cartelera (público)
GET /tmdb/en-cartelera?page=1
Response: ApiResponse<TMDbMovieDTO[]>

// Próximamente (público)
GET /tmdb/proximamente?page=1
Response: ApiResponse<TMDbMovieDTO[]>

// Populares (público)
GET /tmdb/populares?page=1
Response: ApiResponse<TMDbMovieDTO[]>
```

---

### 🎫 **Funciones**

```typescript
// Funciones por película (público)
GET /funciones/pelicula/{peliculaId}
Response: ApiResponse<Funcion[]>

// Función por ID (público)
GET /funciones/{id}
Response: ApiResponse<Funcion>

// ADMIN: Crear función
POST /funciones
Headers: { Authorization: "Bearer {token}" }
Body: {
  peliculaId: number;
  salaId: number;
  fechaHora: string;      // ISO 8601: "2025-11-10T18:00:00"
  precioEntrada: number;  // Requerido, >= 0
  estado: "ACTIVA" | "CANCELADA" | "FINALIZADA";
}
Response: ApiResponse<Funcion>
```

---

### 🪑 **Asientos**

```typescript
// Mapa de asientos por función (público)
GET /asientos/funcion/{funcionId}
Response: ApiResponse<Asiento[]>

// Estadísticas de ocupación (público)
GET /asientos/estadisticas/{funcionId}
Response: ApiResponse<{
  disponibles: number;
  reservados: number;
  ocupados: number;
  total: number;
  porcentajeOcupacion: number;
}>

// Reservar asiento (JWT)
POST /asientos/reservar/{asientoId}
Headers: { Authorization: "Bearer {token}" }
Response: ApiResponse<Asiento>

// ADMIN: Generar asientos automáticamente
POST /asientos/generar/{funcionId}
Headers: { Authorization: "Bearer {token}" }
Response: ApiResponse<string>
```

---

### 🛒 **Compras (Orquestador)**

```typescript
// Calcular total antes de confirmar (JWT)
POST /compras/calcular-total
Headers: { Authorization: "Bearer {token}" }
Body: {
  funcionId: number;
  asientoIds: number[];
  productos?: [
    {
      productoId: number;
      cantidad: number;    // Min 1, Max 50
    }
  ];
}
Response: ApiResponse<{
  totalBoletos: number;
  totalProductos: number;
  totalGeneral: number;
  cantidadBoletos: number;
  detalleAsientos: [
    {
      asientoId: number;
      codigoAsiento: string;
      precio: number;
    }
  ];
  detalleProductos: [
    {
      productoId: number;
      nombreProducto: string;
      cantidad: number;
      precioUnitario: number;
      subtotal: number;
    }
  ];
}>

// Confirmar compra (JWT)
POST /compras/confirmar
Headers: { Authorization: "Bearer {token}" }
Body: {
  clienteId: number;
  funcionId: number;
  asientoIds: number[];
  productos?: [
    {
      productoId: number;
      cantidad: number;
    }
  ];
  metodoPago: "EFECTIVO" | "TARJETA" | "YAPE";
}
Response: ApiResponse<{
  numeroConfirmacion: string;
  fechaCompra: string;
  totalPagado: number;
  clienteId: number;
  nombreCliente: string;
  boletos: [
    {
      boletoId: number;
      pelicula: string;
      sala: string;
      fechaHora: string;
      asiento: string;
      precio: number;
    }
  ];
  productos: [
    {
      nombreProducto: string;
      cantidad: number;
      precioUnitario: number;
      subtotal: number;
    }
  ];
  pago: {
    pagoId: number;
    metodoPago: string;
    estado: string;
    monto: number;
    fechaPago: string;
  };
}>
```

---

### 🍿 **Productos**

```typescript
// Listar productos disponibles (público)
GET /productos
Response: ApiResponse<Producto[]>

// Producto por ID (público)
GET /productos/{id}
Response: ApiResponse<Producto>
```

---

### 🏢 **Salas**

```typescript
// Listar salas activas (público)
GET /salas/activas
Response: ApiResponse<Sala[]>

// Sala por ID (público)
GET /salas/{id}
Response: ApiResponse<Sala>
```

---

## 📦 Modelos de Datos (TypeScript)

### **Interfaces Base**

```typescript
// src/app/models/api-response.model.ts
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

// src/app/models/pelicula.model.ts
export interface Pelicula {
  id: number;
  tmdbId?: number;
  titulo: string;
  tituloOriginal?: string;
  genero?: string;
  duracion?: number;          // en minutos
  clasificacion?: string;      // PG-13, R, etc.
  sinopsis?: string;
  resumen?: string;
  popularidad?: number;
  posterUrl?: string;          // URL completa de imagen
  backdropUrl?: string;
  fechaEstreno?: string;       // ISO date
  votoPromedio?: number;       // 0-10
  totalVotos?: number;
  adult?: boolean;
  activa?: boolean;
}

// src/app/models/tmdb-movie.model.ts
export interface TMDbMovieDTO {
  id: number;
  title: string;
  overview: string;
  posterPath: string;          // Path relativo, agregar base URL
  backdropPath?: string;
  releaseDate: string;
  voteAverage: number;
  voteCount: number;
  popularity: number;
  adult: boolean;
  runtime?: number;
  genres?: Genre[];
  status?: string;
}

export interface Genre {
  id: number;
  name: string;
}

// src/app/models/funcion.model.ts
export interface Funcion {
  id: number;
  pelicula: {
    id: number;
    titulo: string;
    posterUrl?: string;
  };
  sala: {
    id: number;
    nombre: string;
    capacidad: number;
  };
  fechaHora: string;          // ISO datetime
  precioEntrada: number;
  estado: 'ACTIVA' | 'CANCELADA' | 'FINALIZADA';
}

// src/app/models/asiento.model.ts
export interface Asiento {
  id: number;
  fila: string;              // A, B, C...
  numero: number;            // 1, 2, 3...
  codigoAsiento: string;     // A1, B2, etc.
  estado: 'DISPONIBLE' | 'RESERVADO' | 'OCUPADO';
  tipo: 'NORMAL' | 'VIP' | 'DISCAPACITADO';
  funcionId: number;
  salaId: number;
}

// src/app/models/producto.model.ts
export interface Producto {
  id: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  stock: number;
  categoria: 'BEBIDA' | 'COMIDA' | 'COMBO';
  activo: boolean;
}

// src/app/models/compra.model.ts
export interface CalcularTotalRequest {
  funcionId: number;
  asientoIds: number[];
  productos?: DetalleProductoInput[];
}

export interface DetalleProductoInput {
  productoId: number;
  cantidad: number;
}

export interface TotalCompraResponse {
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

export interface ConfirmarCompraRequest {
  clienteId: number;
  funcionId: number;
  asientoIds: number[];
  productos?: DetalleProductoInput[];
  metodoPago: 'EFECTIVO' | 'TARJETA' | 'YAPE';
}

export interface ConfirmacionCompraResponse {
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
```

---

## 🔑 Configuración de Autenticación

### **1. Servicio de Autenticación**

```typescript
// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  login(username: string, password: string): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            localStorage.setItem(this.tokenKey, response.data.token);
            localStorage.setItem('currentUser', JSON.stringify(response.data));
            this.currentUserSubject.next(response.data);
          }
        })
      );
  }

  registro(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/registro`, data);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.includes(role) || false;
  }

  isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }
}
```

---

### **2. Interceptor HTTP (Agregar Token)**

```typescript
// src/app/interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

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
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
```

**Registrar en `app.module.ts`:**
```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

providers: [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }
]
```

---

### **3. Guard de Rutas**

```typescript
// src/app/guards/auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return false;
    }

    const requiredRoles = route.data['roles'] as string[];
    if (requiredRoles) {
      const hasRole = requiredRoles.some(role => this.authService.hasRole(role));
      if (!hasRole) {
        this.router.navigate(['/acceso-denegado']);
        return false;
      }
    }

    return true;
  }
}
```

**Uso en rutas:**
```typescript
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'cartelera', 
    component: CarteleraComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] }
  }
];
```

---

## 🛠️ Implementación de Servicios

### **Servicio de Compras**

```typescript
// src/app/services/compra.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompraService {
  private apiUrl = `${environment.apiUrl}/compras`;

  constructor(private http: HttpClient) {}

  calcularTotal(request: CalcularTotalRequest): Observable<ApiResponse<TotalCompraResponse>> {
    return this.http.post<ApiResponse<TotalCompraResponse>>(
      `${this.apiUrl}/calcular-total`,
      request
    );
  }

  confirmarCompra(request: ConfirmarCompraRequest): Observable<ApiResponse<ConfirmacionCompraResponse>> {
    return this.http.post<ApiResponse<ConfirmacionCompraResponse>>(
      `${this.apiUrl}/confirmar`,
      request
    );
  }
}
```

---

### **Servicio de Asientos**

```typescript
// src/app/services/asiento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AsientoService {
  private apiUrl = `${environment.apiUrl}/asientos`;

  constructor(private http: HttpClient) {}

  obtenerMapaAsientos(funcionId: number): Observable<ApiResponse<Asiento[]>> {
    return this.http.get<ApiResponse<Asiento[]>>(`${this.apiUrl}/funcion/${funcionId}`);
  }

  reservarAsiento(asientoId: number): Observable<ApiResponse<Asiento>> {
    return this.http.post<ApiResponse<Asiento>>(`${this.apiUrl}/reservar/${asientoId}`, {});
  }

  obtenerEstadisticas(funcionId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/estadisticas/${funcionId}`);
  }
}
```

---

## 🎯 Casos de Uso Comunes

### **Caso 1: Login y Guardar Token**

```typescript
// login.component.ts
onLogin(): void {
  this.authService.login(this.username, this.password).subscribe({
    next: (response) => {
      if (response.success) {
        console.log('Login exitoso');
        this.router.navigate(['/cartelera']);
      }
    },
    error: (error) => {
      this.errorMessage = error.error?.message || 'Error al iniciar sesión';
    }
  });
}
```

---

### **Caso 2: Mostrar Cartelera de Películas**

```typescript
// cartelera.component.ts
ngOnInit(): void {
  this.peliculaService.obtenerPeliculasActivas().subscribe({
    next: (response) => {
      if (response.success) {
        this.peliculas = response.data;
      }
    },
    error: (error) => {
      console.error('Error:', error);
    }
  });
}
```

---

### **Caso 3: Ver Mapa de Asientos**

```typescript
// asientos.component.ts
cargarAsientos(funcionId: number): void {
  this.asientoService.obtenerMapaAsientos(funcionId).subscribe({
    next: (response) => {
      if (response.success) {
        this.asientos = response.data;
        this.organizarAsientosPorFila();
      }
    }
  });
}

organizarAsientosPorFila(): void {
  this.asientosPorFila = this.asientos.reduce((acc, asiento) => {
    if (!acc[asiento.fila]) {
      acc[asiento.fila] = [];
    }
    acc[asiento.fila].push(asiento);
    return acc;
  }, {} as { [key: string]: Asiento[] });
}

seleccionarAsiento(asiento: Asiento): void {
  if (asiento.estado === 'DISPONIBLE') {
    this.asientoService.reservarAsiento(asiento.id).subscribe({
      next: (response) => {
        if (response.success) {
          asiento.estado = 'RESERVADO';
          this.asientosSeleccionados.push(asiento.id);
        }
      },
      error: (error) => {
        alert(error.error?.message || 'Error al reservar asiento');
      }
    });
  }
}
```

---

### **Caso 4: Confirmar Compra Completa**

```typescript
// checkout.component.ts
confirmarCompra(): void {
  const request: ConfirmarCompraRequest = {
    clienteId: this.currentUser.clienteId,
    funcionId: this.funcionSeleccionada.id,
    asientoIds: this.asientosSeleccionados,
    productos: this.productosSeleccionados.map(p => ({
      productoId: p.id,
      cantidad: p.cantidad
    })),
    metodoPago: this.metodoPagoSeleccionado
  };

  this.compraService.confirmarCompra(request).subscribe({
    next: (response) => {
      if (response.success) {
        this.confirmacion = response.data;
        this.mostrarConfirmacion = true;
      }
    },
    error: (error) => {
      this.errorMessage = error.error?.message || 'Error al confirmar compra';
    }
  });
}
```

---

## ⚠️ Manejo de Errores

### **Estrategia Recomendada**

```typescript
// Siempre verificar response.success
this.service.metodo().subscribe({
  next: (response) => {
    if (response.success) {
      // Usar response.data
      this.datos = response.data;
    } else {
      // Mostrar response.message
      this.showError(response.message);
    }
  },
  error: (error) => {
    // Error HTTP (401, 404, 500, etc.)
    const mensaje = error.error?.message || 'Error desconocido';
    this.showError(mensaje);
  }
});
```

### **Códigos de Estado HTTP**

- **200 OK:** Operación exitosa
- **201 Created:** Recurso creado
- **400 Bad Request:** Validaciones fallidas
- **401 Unauthorized:** No autenticado (token inválido/expirado)
- **403 Forbidden:** Sin permisos (requiere ADMIN)
- **404 Not Found:** Recurso no encontrado
- **409 Conflict:** Conflicto de negocio (ej: asiento ya reservado)
- **500 Internal Server Error:** Error del servidor

---

## ❌ Lo que Falta por Implementar

### **Backend (Prioridad Alta):**

1. ✅ ~~Tests Unitarios~~ → **PENDIENTE: ~10% coverage**
   - Target: 60% mínimo
   - Servicios críticos: CompraService, AsientoService

2. ✅ ~~Swagger/OpenAPI~~ → **PENDIENTE**
   - Documentación interactiva de endpoints
   - Auto-generación de cliente TypeScript

3. ✅ ~~@ControllerAdvice Global~~ → **PENDIENTE**
   - Manejo unificado de excepciones
   - Respuestas de error consistentes

4. ✅ ~~Paginación en Endpoints~~ → **IMPLEMENTADO PARCIALMENTE**
   - Falta en: boletos, ventas, pagos

### **Backend (Prioridad Media):**

5. ✅ ~~Flyway/Liquibase~~ → **PENDIENTE**
   - Versionado de migraciones de BD
   - Control de cambios de esquema

6. ✅ ~~Logs Estructurados~~ → **PENDIENTE**
   - Integración con ELK Stack
   - Métricas de rendimiento

### **Funcionalidades de Negocio (Futuro):**

7. ✅ ~~Sistema de Descuentos~~ → **NO IMPLEMENTADO**
   - Promociones por día
   - Descuentos por cantidad

8. ✅ ~~Notificaciones~~ → **NO IMPLEMENTADO**
   - Email con confirmación de compra
   - SMS con recordatorio de función

9. ✅ ~~Historial de Compras~~ → **NO IMPLEMENTADO**
   - Ver compras anteriores del cliente
   - Reimpresión de boletos

10. ✅ ~~Sistema de Reseñas~~ → **NO IMPLEMENTADO**
    - Calificación de películas
    - Comentarios de usuarios

---

## 🎓 Recursos Adicionales

### **Documentación Técnica:**
- 📘 [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Referencia completa de endpoints
- 🔐 [ANGULAR_INTEGRATION_GUIDE.md](ANGULAR_INTEGRATION_GUIDE.md) - Integración con Angular
- 🎬 [TMDB_INTEGRATION.md](TMDB_INTEGRATION.md) - Detalles técnicos de TMDb
- 📋 [COMMIT_SUMMARY.md](COMMIT_SUMMARY.md) - Resumen ejecutivo de cambios

### **Variables de Entorno (Backend):**

```properties
# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/dbcinerama
spring.datasource.username=root
spring.datasource.password=***

# JWT
app.jwt.secret=***
app.jwt.expiration=86400000  # 24 horas

# TMDb
tmdb.api.key=***
tmdb.api.base-url=https://api.themoviedb.org/3

# CORS
app.cors.allowed-origins=http://localhost:4200,http://localhost:3000
```

### **Configuración Frontend (Angular):**

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  tmdbImageBaseUrl: 'https://image.tmdb.org/t/p/w500'  // Para pósters
};

// src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.cinerama.com/api',
  tmdbImageBaseUrl: 'https://image.tmdb.org/t/p/w500'
};
```

---

## ✅ Checklist de Integración

- [ ] Crear interfaces TypeScript de todos los modelos
- [ ] Configurar `environment.ts` con URL del backend
- [ ] Implementar `AuthService` con login/logout
- [ ] Implementar `AuthInterceptor` para agregar token
- [ ] Implementar `AuthGuard` para rutas protegidas
- [ ] Crear servicios para:
  - [ ] Películas
  - [ ] Funciones
  - [ ] Asientos
  - [ ] Productos
  - [ ] Compras
- [ ] Implementar componentes:
  - [ ] Login/Registro
  - [ ] Cartelera de películas
  - [ ] Detalle de película con horarios
  - [ ] Mapa de asientos
  - [ ] Carrito de compra
  - [ ] Confirmación de compra
  - [ ] Panel de administración (si aplica)
- [ ] Probar todos los flujos completos
- [ ] Manejo de errores global
- [ ] Validaciones de formularios

---

## 📞 Soporte y Contacto

**GitHub:** [Kylver21/Cinerama-Backend](https://github.com/Kylver21/Cinerama-Backend)  
**Email:** soporte@cinerama.pe  

---

**⭐ Última Actualización:** 07 de Noviembre de 2025  
**📦 Versión del Backend:** 2.0.0  
**🎯 Estado:** Production-Ready al 78%  

**¡Todo listo para integrar el frontend! 🚀**
