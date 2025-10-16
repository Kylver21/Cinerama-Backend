# Authentication API Documentation

This document describes the authentication endpoints implemented in the `AuthController`.

## Overview

The AuthController provides comprehensive authentication and user management functionality using JWT (JSON Web Tokens) with Spring Security and BCrypt password hashing.

## Base URL

```
/api/auth
```

## Endpoints

### 1. Register New User

**POST** `/api/auth/register`

Register a new user with their information.

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepass123",
  "nombre": "John",
  "apellido": "Doe",
  "telefono": "123456789",
  "numeroDocumento": "12345678",
  "tipoDocumento": "DNI"
}
```

**Validations:**
- Username: 4-50 characters, alphanumeric with dots, hyphens, underscores
- Email: Valid email format
- Password: Minimum 6 characters
- Phone: 9 digits
- All fields are required

**Response (201 Created):**
```json
{
  "mensaje": "Usuario registrado exitosamente con ID: 1",
  "exitoso": true
}
```

---

### 2. Login

**POST** `/api/auth/login?rememberMe=false`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "securepass123"
}
```

**Validations:**
- Username: Required
- Password: Required

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "username": "johndoe",
  "email": "john@example.com",
  "roles": ["ROLE_CLIENTE"],
  "userId": 1,
  "clienteId": 1,
  "nombreCompleto": "John Doe"
}
```

**Features:**
- BCrypt password verification
- JWT token generation (1 hour expiration)
- Optional "Remember Me" cookie (7 days)
- Returns user information and roles

---

### 3. Get Current User Info

**GET** `/api/auth/me`

Get information about the currently authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "roles": ["ROLE_CLIENTE"],
  "activo": true,
  "clienteId": 1,
  "nombreCompleto": "John Doe",
  "puntosAcumulados": 100,
  "nivelFidelizacion": "Bronze"
}
```

---

### 4. Change Password

**POST** `/api/auth/cambiar-password`

Change the password for the current authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "passwordActual": "currentpassword",
  "passwordNueva": "newpassword123"
}
```

**Validations:**
- Current password: Required
- New password: Required, minimum 6 characters

**Response (200 OK):**
```json
{
  "mensaje": "Contraseña actualizada exitosamente",
  "exitoso": true
}
```

---

### 5. Logout

**POST** `/api/auth/logout`

Logout the current user and invalidate JWT cookie.

**Response (200 OK):**
```json
{
  "mensaje": "Sesión cerrada exitosamente",
  "exitoso": true
}
```

---

### 6. Validate Token

**GET** `/api/auth/validate`

Validate a JWT token.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "valido": true,
  "username": "johndoe",
  "roles": ["ROLE_CLIENTE"],
  "mensaje": "Token válido"
}
```

**Response (401 Unauthorized):**
```json
{
  "valido": false,
  "mensaje": "Token inválido o expirado"
}
```

---

### 7. Refresh Token

**POST** `/api/auth/refresh`

Generate a new JWT token before the current one expires.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "username": "johndoe",
  "email": "john@example.com",
  "roles": ["ROLE_CLIENTE"],
  "userId": 1
}
```

---

### 8. Validate Username Availability

**GET** `/api/auth/validar-username/{username}`

Check if a username is available for registration.

**Response (200 OK):**
```json
{
  "disponible": true,
  "mensaje": "Username disponible"
}
```

---

### 9. Validate Email Availability

**GET** `/api/auth/validar-email/{email}`

Check if an email is available for registration.

**Response (200 OK):**
```json
{
  "disponible": true,
  "mensaje": "Email disponible"
}
```

---

## Security Features

1. **Password Hashing**: All passwords are hashed using BCrypt
2. **JWT Tokens**: Stateless authentication with 1-hour expiration
3. **Role-Based Access**: Users are assigned roles (ROLE_ADMIN, ROLE_CLIENTE, etc.)
4. **Permission-Based Authorization**: Fine-grained permissions per module and action
5. **HTTP-Only Cookies**: Optional secure cookie storage for tokens
6. **Input Validation**: Comprehensive validation on all endpoints

## Error Responses

All endpoints may return the following error responses:

**400 Bad Request:**
```json
{
  "mensaje": "Error de validación",
  "exitoso": false
}
```

**401 Unauthorized:**
```json
{
  "mensaje": "Credenciales inválidas",
  "exitoso": false
}
```

**500 Internal Server Error:**
```json
{
  "mensaje": "Error de autenticación",
  "exitoso": false
}
```

## Authentication Flow

1. User registers via `/register` endpoint
2. User logs in via `/login` endpoint and receives JWT token
3. Client includes token in `Authorization: Bearer <token>` header for protected endpoints
4. Token can be refreshed via `/refresh` before expiration
5. User logs out via `/logout` to clear session

## Notes

- All passwords are stored using BCrypt hashing
- CORS is enabled for all origins (configure for production)
- In production, enable HTTPS and set `cookie.setSecure(true)`
