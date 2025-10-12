# 🧪 GUÍA DE TESTING CON POSTMAN - FASE 1: SEGURIDAD

## 📋 PREPARACIÓN

1. **Iniciar aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Verificar inicialización:**
   - Revisar logs: "✅ Datos de seguridad inicializados correctamente"
   - Usuario admin creado: `admin / Admin123!`
   - 3 roles creados: ROLE_ADMIN, ROLE_CLIENTE, ROLE_EMPLEADO
   - 54 permisos creados

---

## 🔐 ENDPOINTS DE AUTENTICACIÓN

### 1️⃣ **REGISTRO DE NUEVO USUARIO**

**POST** `http://localhost:8080/api/auth/register`

**Body (JSON):**
```json
{
  "username": "juanperez",
  "email": "juan.perez@gmail.com",
  "password": "Juan123!",
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "987654321",
  "numeroDocumento": "12345678",
  "tipoDocumento": "DNI"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "mensaje": "Usuario registrado exitosamente",
  "exitoso": true
}
```

**Errores posibles:**

**409 Conflict - Username duplicado:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Usuario Ya Existe",
  "mensaje": "El username 'juanperez' ya está registrado"
}
```

**400 Bad Request - Validación fallida:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validación Fallida",
  "mensaje": "Errores de validación en los datos enviados",
  "errores": {
    "password": "La contraseña debe tener al menos 8 caracteres",
    "email": "Email inválido"
  }
}
```

---

### 2️⃣ **LOGIN**

**POST** `http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
{
  "username": "juanperez",
  "password": "Juan123!"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFucGVyZXoiLCJpYXQiOjE3MDUzMjAwMDAsImV4cCI6MTcwNTQwNjQwMH0...",
  "tipo": "Bearer",
  "username": "juanperez",
  "email": "juan.perez@gmail.com",
  "roles": ["ROLE_CLIENTE"],
  "clienteId": 2,
  "nombreCompleto": "Juan Pérez"
}
```

**❗ IMPORTANTE:** Copiar el `token` para usarlo en requests autenticadas

**Errores posibles:**

**401 Unauthorized - Credenciales incorrectas:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Credenciales Inválidas",
  "mensaje": "Usuario o contraseña incorrectos"
}
```

---

### 3️⃣ **LOGIN COMO ADMIN**

**POST** `http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
{
  "username": "admin",
  "password": "Admin123!"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tipo": "Bearer",
  "username": "admin",
  "email": "admin@cinerama.pe",
  "roles": ["ROLE_ADMIN"],
  "clienteId": 1,
  "nombreCompleto": "Administrador Sistema"
}
```

---

### 4️⃣ **OBTENER INFORMACIÓN DEL USUARIO ACTUAL**

**GET** `http://localhost:8080/api/auth/me`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta esperada (200 OK):**
```json
{
  "id": 2,
  "username": "juanperez",
  "email": "juan.perez@gmail.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "roles": ["ROLE_CLIENTE"],
  "permisos": [
    "PELICULAS_LISTAR",
    "PELICULAS_VER",
    "BOLETOS_CREAR",
    ...
  ],
  "clienteId": 2,
  "activo": true
}
```

**401 Unauthorized - Sin token:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "No Autorizado",
  "mensaje": "Token no proporcionado o inválido"
}
```

---

### 5️⃣ **VALIDAR DISPONIBILIDAD DE USERNAME**

**GET** `http://localhost:8080/api/auth/validar-username/juanperez`

**Respuesta si existe (200 OK):**
```json
{
  "disponible": false,
  "mensaje": "El username 'juanperez' ya está en uso"
}
```

**Respuesta si está disponible (200 OK):**
```json
{
  "disponible": true,
  "mensaje": "El username 'mariafernandez' está disponible"
}
```

---

### 6️⃣ **VALIDAR DISPONIBILIDAD DE EMAIL**

**GET** `http://localhost:8080/api/auth/validar-email/juan.perez@gmail.com`

**Respuesta si existe (200 OK):**
```json
{
  "disponible": false,
  "mensaje": "El email 'juan.perez@gmail.com' ya está en uso"
}
```

---

### 7️⃣ **CAMBIAR CONTRASEÑA**

**POST** `http://localhost:8080/api/auth/cambiar-password`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Body (JSON):**
```json
{
  "passwordActual": "Juan123!",
  "passwordNueva": "NuevaPassword456!"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "mensaje": "Contraseña actualizada exitosamente",
  "exitoso": true
}
```

**401 Unauthorized - Contraseña actual incorrecta:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Credenciales Inválidas",
  "mensaje": "La contraseña actual es incorrecta"
}
```

---

### 8️⃣ **LOGOUT**

**POST** `http://localhost:8080/api/auth/logout`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta esperada (200 OK):**
```json
{
  "mensaje": "Sesión cerrada exitosamente",
  "exitoso": true
}
```

---

## 🎬 ENDPOINTS PROTEGIDOS (Ejemplos)

### 9️⃣ **LISTAR PELÍCULAS (Público)**

**GET** `http://localhost:8080/api/peliculas`

**Sin token requerido**

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "titulo": "Spider-Man: No Way Home",
    "genero": "Acción",
    "duracion": 148,
    ...
  }
]
```

---

### 🔟 **CREAR PELÍCULA (Solo ADMIN)**

**POST** `http://localhost:8080/api/peliculas`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9... (token de ADMIN)
```

**Body (JSON):**
```json
{
  "titulo": "Avatar 3",
  "genero": "Ciencia Ficción",
  "duracion": 180,
  "sinopsis": "...",
  "clasificacion": "PG-13",
  "idioma": "Inglés"
}
```

**Respuesta con token de ADMIN (201 Created):**
```json
{
  "id": 10,
  "titulo": "Avatar 3",
  ...
}
```

**403 Forbidden - Token de cliente normal:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Acceso Denegado",
  "mensaje": "No tiene permisos para realizar esta acción"
}
```

---

## 📊 CASOS DE PRUEBA

### ✅ **Test Case 1: Flujo completo de registro y compra**

1. Registrar usuario nuevo
2. Login con ese usuario
3. Copiar token JWT
4. Ver películas (GET /api/peliculas)
5. Ver funciones (GET /api/funciones)
6. Reservar asiento (POST /api/asientos/reservar) con token
7. Crear boleto (POST /api/boletos) con token

### ✅ **Test Case 2: Verificar restricciones de roles**

1. Login como cliente
2. Intentar crear película (POST /api/peliculas) ❌ 403 Forbidden
3. Login como admin
4. Crear película (POST /api/peliculas) ✅ 201 Created

### ✅ **Test Case 3: Validación de datos**

1. Registrar con password corta (< 8 chars) ❌ 400 Bad Request
2. Registrar con email inválido ❌ 400 Bad Request
3. Registrar con username duplicado ❌ 409 Conflict

---

## 🛠️ CONFIGURAR POSTMAN

### **Crear Environment:**

1. Crear environment "Cinerama Local"
2. Variables:
   - `baseUrl`: `http://localhost:8080`
   - `token`: (vacía inicialmente)

### **Usar token automáticamente:**

1. En request de login, agregar en **Tests**:
   ```javascript
   pm.environment.set("token", pm.response.json().token);
   ```

2. En requests autenticadas, en **Authorization** → **Bearer Token**:
   ```
   {{token}}
   ```

---

## ✅ CHECKLIST DE VERIFICACIÓN

- [ ] ✅ Registro de usuario funciona
- [ ] ✅ Login devuelve token JWT válido
- [ ] ✅ Token funciona en endpoints autenticados
- [ ] ✅ Admin puede crear películas
- [ ] ✅ Cliente NO puede crear películas (403)
- [ ] ✅ Validación de DTOs funciona (400)
- [ ] ✅ Excepciones devuelven formato consistente
- [ ] ✅ Username/email duplicado devuelve 409
- [ ] ✅ Credenciales incorrectas devuelven 401
- [ ] ✅ Endpoints públicos accesibles sin token

---

**🎉 Si todos los tests pasan, Fase 1: Seguridad está COMPLETA**
