# 🎬 Integración TMDb API - Cinerama Backend

## 📋 Descripción

Este proyecto ha sido refactorizado para integrar la API externa de **The Movie Database (TMDb)**, permitiendo sincronizar automáticamente información de películas en cartelera desde TMDb hacia la base de datos local de Cinerama.

## 🚀 Cambios Implementados

### 1. **Entidad `Pelicula` Refactorizada**

La entidad ahora incluye campos adicionales para almacenar información de TMDb:

```java
- tmdbId (Long) → ID único de TMDb
- tituloOriginal (String)
- idiomaOriginal (String)
- popularidad (Double)
- posterUrl (String) → URL del póster
- backdropUrl (String) → URL del fondo
- fechaEstreno (LocalDate)
- votoPromedio (Double)
- totalVotos (Integer)
- resumen (String) → Campo "overview" de TMDb
- adult (Boolean)
- activa (Boolean) → Control interno del cine
```

### 2. **DTOs Creados**

- **`TMDbMovieDTO`**: Mapea películas individuales de TMDb
- **`TMDbResponseDTO`**: Mapea la respuesta completa de la API
- **`SyncResponseDTO`**: Respuesta de sincronización con estadísticas

### 3. **Nuevos Servicios**

#### **TMDbService**
- `getNowPlayingMovies(page)` - Obtiene películas en cartelera
- `getPopularMovies(page)` - Obtiene películas populares
- `mapGenreIdsToNames(ids)` - Convierte IDs de géneros a nombres

#### **PeliculaService (actualizado)**
- `sincronizarPeliculasDesdeAPI(paginas)` - Sincroniza películas desde TMDb
- `obtenerPeliculasActivas()` - Películas activas en el cine
- `obtenerPeliculasPorPopularidad()` - Ordenadas por popularidad
- `obtenerPeliculasPorVoto()` - Ordenadas por mejor valoración
- `obtenerPorTmdbId(tmdbId)` - Buscar por ID de TMDb

### 4. **Nuevos Endpoints REST**

```http
# Sincronización
POST /api/peliculas/sync?paginas=2

# Búsquedas mejoradas
GET /api/peliculas/activas
GET /api/peliculas/populares
GET /api/peliculas/mejor-valoradas
GET /api/peliculas/tmdb/{tmdbId}

# Test de conexión
GET /api/peliculas/test-connection
```

## ⚙️ Configuración

### 1. **Obtener API Key de TMDb**

1. Regístrate en: https://www.themoviedb.org/signup
2. Ve a: https://www.themoviedb.org/settings/api
3. Solicita una API Key (es gratis)
4. Copia tu API Key

### 2. **Configurar application.properties**

```properties
# TMDb API Configuration
tmdb.api.key=TU_API_KEY_AQUI
tmdb.api.base-url=https://api.themoviedb.org/3
tmdb.api.language=es-MX
tmdb.api.region=PE
```

### 3. **Ejecutar la Aplicación**

```bash
# Con Maven
./mvnw spring-boot:run

# O con Maven Wrapper en Windows
mvnw.cmd spring-boot:run
```

## 📝 Uso de la API

### **Sincronizar Películas desde TMDb**

```bash
# Sincronizar 1 página (20 películas aprox.)
POST http://localhost:8080/api/peliculas/sync

# Sincronizar múltiples páginas
POST http://localhost:8080/api/peliculas/sync?paginas=3
```

**Respuesta:**
```json
{
  "totalPeliculasAPI": 60,
  "peliculasNuevas": 55,
  "peliculasActualizadas": 5,
  "peliculasOmitidas": 0,
  "mensaje": "✅ Sincronización completada: 55 nuevas, 5 actualizadas, 0 omitidas de 60 totales"
}
```

### **Obtener Películas**

```bash
# Todas las películas
GET http://localhost:8080/api/peliculas

# Solo activas
GET http://localhost:8080/api/peliculas/activas

# Por popularidad
GET http://localhost:8080/api/peliculas/populares

# Mejor valoradas
GET http://localhost:8080/api/peliculas/mejor-valoradas

# Buscar por título
GET http://localhost:8080/api/peliculas/titulo/Oppenheimer

# Buscar por género
GET http://localhost:8080/api/peliculas/genero/Acción
```

### **Obtener Película Específica**

```bash
# Por ID local
GET http://localhost:8080/api/peliculas/1

# Por TMDb ID
GET http://localhost:8080/api/peliculas/tmdb/569094
```

## 🔄 Flujo de Sincronización

1. **Petición**: Se llama al endpoint `/api/peliculas/sync`
2. **Consulta API**: Se obtienen películas de TMDb (now_playing)
3. **Verificación**: Se verifica si la película ya existe por `tmdbId`
4. **Acción**:
   - Si existe → Se actualiza con los nuevos datos
   - Si no existe → Se crea una nueva entrada
5. **Respuesta**: Se devuelve un resumen con estadísticas

## 📊 Estructura de la Base de Datos

La tabla `peliculas` ahora tiene:

```sql
CREATE TABLE peliculas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id BIGINT UNIQUE,           -- ID de TMDb
    titulo VARCHAR(255) NOT NULL,
    titulo_original VARCHAR(255),
    idioma_original VARCHAR(10),
    genero TEXT,
    duracion INT,
    clasificacion VARCHAR(50),
    sinopsis TEXT,
    resumen TEXT,                    -- Overview de TMDb
    popularidad DOUBLE,
    poster_url VARCHAR(500),
    backdrop_url VARCHAR(500),
    fecha_estreno DATE,
    voto_promedio DOUBLE,
    total_votos INT,
    adult BOOLEAN,
    activa BOOLEAN DEFAULT TRUE,
    
    INDEX idx_tmdb_id (tmdb_id)
);
```

## 🎯 Casos de Uso

### **Caso 1: Primera Sincronización**
```bash
POST /api/peliculas/sync?paginas=3
```
- Se descargan ~60 películas de TMDb
- Se guardan en la base de datos
- Todas son marcadas como "nuevas"

### **Caso 2: Sincronización Incremental**
```bash
POST /api/peliculas/sync?paginas=1
```
- Se verifica cada película por `tmdbId`
- Las existentes se actualizan (popularidad, votos, etc.)
- Las nuevas se agregan

### **Caso 3: Gestión Manual**
```bash
# Desactivar una película del cine
PUT /api/peliculas/1
{
  "activa": false
}

# Solo traer activas
GET /api/peliculas/activas
```

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.5**
- **Java 21**
- **MySQL 8**
- **RestTemplate** - Cliente HTTP para TMDb API
- **Jackson** - Serialización/Deserialización JSON
- **Lombok** - Reducción de boilerplate
- **JPA/Hibernate** - ORM

## 📚 Referencias

- **TMDb API Docs**: https://developer.themoviedb.org/docs
- **TMDb Now Playing**: https://developer.themoviedb.org/reference/movie-now-playing-list
- **TMDb Images**: https://developer.themoviedb.org/docs/image-basics

## ⚠️ Notas Importantes

1. **API Key**: La API Key de TMDb es **obligatoria** para que funcione la sincronización
2. **Rate Limiting**: TMDb tiene límites de 50 peticiones por segundo
3. **Páginas**: Se recomienda sincronizar máximo 5 páginas a la vez
4. **Duplicados**: El sistema previene duplicados usando `tmdbId` único
5. **Actualización**: Las películas existentes se actualizan automáticamente

## 🧪 Testing con Postman

### Collection de Pruebas

```json
{
  "info": {
    "name": "Cinerama - TMDb Integration",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Test Conexión TMDb",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/peliculas/test-connection"
      }
    },
    {
      "name": "Sincronizar Películas",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/peliculas/sync?paginas=2"
      }
    },
    {
      "name": "Obtener Películas Populares",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/peliculas/populares"
      }
    }
  ]
}
```

## 🐛 Troubleshooting

### Error: "API Key inválida"
**Solución**: Verifica que tu API Key esté correctamente configurada en `application.properties`

### Error: "Connection timeout"
**Solución**: Verifica tu conexión a internet y que TMDb API esté disponible

### Error: "Duplicate entry for key 'idx_tmdb_id'"
**Solución**: La película ya existe. El sistema debería actualizar automáticamente.

### No se sincronizan películas
**Solución**: 
1. Verifica que la API Key sea válida
2. Revisa los logs en la consola
3. Usa el endpoint `/test-connection`

## 📞 Soporte

Para más información o reportar problemas:
- GitHub: https://github.com/Kylver21/Cinerama-Backend
- Email: soporte@cinerama.pe

---

**Desarrollado con ❤️ por el equipo de Cinerama**
