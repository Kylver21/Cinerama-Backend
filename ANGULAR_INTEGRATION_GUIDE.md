# 🎨 GUÍA RÁPIDA ANGULAR - INTEGRACIÓN CON BACKEND CINERAMA

## 🚀 INICIO RÁPIDO

### 1. Crear Interfaces TypeScript

```typescript
// src/app/models/api-response.model.ts
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

// src/app/models/paged-response.model.ts
export interface PagedResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

// src/app/models/pelicula.model.ts
export interface Pelicula {
  id?: number;
  tmdbId?: number;
  titulo: string;
  tituloOriginal?: string;
  idiomaOriginal?: string;
  genero?: string;
  duracion?: number;
  clasificacion?: string;
  sinopsis?: string;
  resumen?: string;
  popularidad?: number;
  posterUrl?: string;
  backdropUrl?: string;
  fechaEstreno?: string;
  votoPromedio?: number;
  totalVotos?: number;
  adult?: boolean;
  activa?: boolean;
}

// src/app/models/usuario.model.ts
export interface Usuario {
  id?: number;
  username: string;
  email: string;
  nombre: string;
  apellido?: string;
  telefono?: string;
  documento?: string;
}

// src/app/models/login.model.ts
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  email: string;
  roles: string[];
}

export interface RegistroRequest {
  username: string;
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono: string;
  documento: string;
}
```

---

## 🔐 2. Servicio de Autenticación

```typescript
// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Cargar usuario del localStorage al iniciar
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  login(credentials: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            // Guardar token y usuario
            localStorage.setItem(this.tokenKey, response.data.token);
            localStorage.setItem('currentUser', JSON.stringify(response.data));
            this.currentUserSubject.next(response.data);
          }
        })
      );
  }

  registro(data: RegistroRequest): Observable<ApiResponse<Usuario>> {
    return this.http.post<ApiResponse<Usuario>>(`${this.apiUrl}/registro`, data);
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
    return user?.roles.includes(role) || false;
  }

  isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }
}
```

---

## 🎬 3. Servicio de Películas

```typescript
// src/app/services/pelicula.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PeliculaService {
  private apiUrl = `${environment.apiUrl}/peliculas`;

  constructor(private http: HttpClient) {}

  // ===== PAGINADOS (RECOMENDADO) =====
  
  obtenerPeliculasPaginadas(
    page: number = 0, 
    size: number = 10, 
    sortBy: string = 'popularidad'
  ): Observable<ApiResponse<PagedResponse<Pelicula>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    
    return this.http.get<ApiResponse<PagedResponse<Pelicula>>>(
      `${this.apiUrl}/paginadas`, 
      { params }
    );
  }

  buscarPorGeneroPaginado(
    genero: string,
    page: number = 0, 
    size: number = 10
  ): Observable<ApiResponse<PagedResponse<Pelicula>>> {
    const params = new HttpParams()
      .set('genero', genero)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApiResponse<PagedResponse<Pelicula>>>(
      `${this.apiUrl}/genero/paginado`, 
      { params }
    );
  }

  buscarPorTituloPaginado(
    titulo: string,
    page: number = 0, 
    size: number = 10
  ): Observable<ApiResponse<PagedResponse<Pelicula>>> {
    const params = new HttpParams()
      .set('titulo', titulo)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApiResponse<PagedResponse<Pelicula>>>(
      `${this.apiUrl}/titulo/paginado`, 
      { params }
    );
  }

  // ===== SIN PAGINACIÓN =====

  obtenerTodasLasPeliculas(): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(this.apiUrl);
  }

  obtenerPeliculaPorId(id: number): Observable<ApiResponse<Pelicula>> {
    return this.http.get<ApiResponse<Pelicula>>(`${this.apiUrl}/${id}`);
  }

  obtenerPeliculasActivas(): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(`${this.apiUrl}/activas`);
  }

  obtenerPeliculasPopulares(): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(`${this.apiUrl}/populares`);
  }

  obtenerMejorValoradas(): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(`${this.apiUrl}/mejor-valoradas`);
  }

  buscarPorGenero(genero: string): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(`${this.apiUrl}/genero/${genero}`);
  }

  buscarPorTitulo(titulo: string): Observable<ApiResponse<Pelicula[]>> {
    return this.http.get<ApiResponse<Pelicula[]>>(`${this.apiUrl}/titulo/${titulo}`);
  }

  // ===== ADMIN =====

  crearPelicula(pelicula: Pelicula): Observable<ApiResponse<Pelicula>> {
    return this.http.post<ApiResponse<Pelicula>>(this.apiUrl, pelicula);
  }

  actualizarPelicula(id: number, pelicula: Pelicula): Observable<ApiResponse<Pelicula>> {
    return this.http.put<ApiResponse<Pelicula>>(`${this.apiUrl}/${id}`, pelicula);
  }

  eliminarPelicula(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/${id}`);
  }

  sincronizarConTMDb(paginas: number = 1): Observable<ApiResponse<any>> {
    const params = new HttpParams().set('paginas', paginas.toString());
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/sync`, null, { params });
  }
}
```

---

## 🛡️ 4. Interceptor HTTP (Agregar Token Automáticamente)

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
    // Obtener token
    const token = this.authService.getToken();

    // Clonar request y agregar token si existe
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // Continuar con la petición y manejar errores
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token inválido o expirado
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
```

### Registrar Interceptor en app.module.ts
```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  // ...
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

---

## 🔒 5. Guard para Rutas Protegidas

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

    // Verificar roles si están especificados
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

### Uso en Rutas
```typescript
// src/app/app-routing.module.ts
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  
  // Rutas protegidas (requiere autenticación)
  { 
    path: 'peliculas', 
    component: PeliculasComponent,
    canActivate: [AuthGuard]
  },
  
  // Rutas solo admin
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] }
  }
];
```

---

## 📄 6. Ejemplo de Componente con Paginación

```typescript
// src/app/components/peliculas/peliculas.component.ts
import { Component, OnInit } from '@angular/core';
import { PeliculaService } from '../../services/pelicula.service';

@Component({
  selector: 'app-peliculas',
  templateUrl: './peliculas.component.html'
})
export class PeliculasComponent implements OnInit {
  peliculas: Pelicula[] = [];
  
  // Paginación
  currentPage = 0;
  pageSize = 12;
  totalPages = 0;
  totalElements = 0;
  
  // Estado
  loading = false;
  error: string | null = null;

  constructor(private peliculaService: PeliculaService) {}

  ngOnInit(): void {
    this.cargarPeliculas();
  }

  cargarPeliculas(page: number = 0): void {
    this.loading = true;
    this.error = null;

    this.peliculaService.obtenerPeliculasPaginadas(page, this.pageSize, 'popularidad')
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.peliculas = response.data.content;
            this.currentPage = response.data.pageNumber;
            this.pageSize = response.data.pageSize;
            this.totalPages = response.data.totalPages;
            this.totalElements = response.data.totalElements;
          }
          this.loading = false;
        },
        error: (error) => {
          this.error = error.error?.message || 'Error al cargar películas';
          this.loading = false;
          console.error('Error:', error);
        }
      });
  }

  cambiarPagina(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.cargarPeliculas(page);
    }
  }

  paginaAnterior(): void {
    if (this.currentPage > 0) {
      this.cambiarPagina(this.currentPage - 1);
    }
  }

  paginaSiguiente(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.cambiarPagina(this.currentPage + 1);
    }
  }
}
```

```html
<!-- peliculas.component.html -->
<div class="container">
  <h2>Películas</h2>

  <!-- Loading -->
  <div *ngIf="loading" class="text-center">
    <p>Cargando películas...</p>
  </div>

  <!-- Error -->
  <div *ngIf="error" class="alert alert-danger">
    {{ error }}
  </div>

  <!-- Grid de películas -->
  <div class="row" *ngIf="!loading && !error">
    <div class="col-md-3" *ngFor="let pelicula of peliculas">
      <div class="card mb-4">
        <img [src]="pelicula.posterUrl" class="card-img-top" [alt]="pelicula.titulo">
        <div class="card-body">
          <h5 class="card-title">{{ pelicula.titulo }}</h5>
          <p class="card-text">{{ pelicula.sinopsis | slice:0:100 }}...</p>
          <p class="text-muted">⭐ {{ pelicula.votoPromedio }}/10</p>
          <a [routerLink]="['/peliculas', pelicula.id]" class="btn btn-primary">Ver Detalles</a>
        </div>
      </div>
    </div>
  </div>

  <!-- Paginación -->
  <nav *ngIf="totalPages > 1">
    <ul class="pagination justify-content-center">
      <li class="page-item" [class.disabled]="currentPage === 0">
        <a class="page-link" (click)="paginaAnterior()">Anterior</a>
      </li>
      
      <li class="page-item" 
          *ngFor="let page of [].constructor(totalPages); let i = index"
          [class.active]="i === currentPage">
        <a class="page-link" (click)="cambiarPagina(i)">{{ i + 1 }}</a>
      </li>
      
      <li class="page-item" [class.disabled]="currentPage === totalPages - 1">
        <a class="page-link" (click)="paginaSiguiente()">Siguiente</a>
      </li>
    </ul>
  </nav>

  <p class="text-center text-muted">
    Mostrando {{ currentPage * pageSize + 1 }} - 
    {{ Math.min((currentPage + 1) * pageSize, totalElements) }} 
    de {{ totalElements }} películas
  </p>
</div>
```

---

## ⚙️ 7. Configuración de Environment

```typescript
// src/environments/environment.ts (Desarrollo)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// src/environments/environment.prod.ts (Producción)
export const environment = {
  production: true,
  apiUrl: 'https://tu-dominio-backend.com/api'
};
```

---

## 🎯 8. Componente de Login

```typescript
// src/app/components/login/login.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  credentials: LoginRequest = {
    username: '',
    password: ''
  };
  
  error: string | null = null;
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.loading = true;
    this.error = null;

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        if (response.success) {
          console.log('Login exitoso:', response.message);
          this.router.navigate(['/peliculas']);
        }
      },
      error: (error) => {
        this.error = error.error?.message || 'Error al iniciar sesión';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
```

---

## 🔥 TIPS IMPORTANTES

### 1. Manejo de Errores Global
```typescript
// Siempre verificar response.success
this.peliculaService.obtenerPeliculasPaginadas(0, 10).subscribe({
  next: (response) => {
    if (response.success) {
      // Usar response.data
      this.peliculas = response.data.content;
    } else {
      // Mostrar response.message
      this.showError(response.message);
    }
  },
  error: (error) => {
    // Error HTTP (401, 404, 500, etc.)
    this.showError(error.error?.message || 'Error desconocido');
  }
});
```

### 2. Validaciones en Formularios
```typescript
// Usar Reactive Forms con validaciones
import { Validators } from '@angular/forms';

peliculaForm = this.fb.group({
  titulo: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
  duracion: ['', [Validators.required, Validators.min(1), Validators.max(600)]],
  votoPromedio: ['', [Validators.min(0), Validators.max(10)]],
  fechaEstreno: ['', [Validators.required]]
});
```

### 3. Optimización con RxJS
```typescript
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

// Búsqueda con delay para evitar muchas peticiones
searchControl.valueChanges.pipe(
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(value => this.peliculaService.buscarPorTituloPaginado(value))
).subscribe(/* ... */);
```

---

## ✅ CHECKLIST DE INTEGRACIÓN

- [ ] Crear interfaces TypeScript
- [ ] Configurar environment.ts
- [ ] Crear AuthService
- [ ] Crear AuthInterceptor
- [ ] Crear AuthGuard
- [ ] Crear servicios para cada entidad (Pelicula, Funcion, etc.)
- [ ] Implementar componente de login
- [ ] Implementar componente de registro
- [ ] Implementar listado con paginación
- [ ] Implementar detalle de película
- [ ] Implementar panel admin (si aplica)
- [ ] Probar todos los endpoints
- [ ] Manejar errores globalmente

---

**¡Todo listo para empezar con Angular! 🎉**
