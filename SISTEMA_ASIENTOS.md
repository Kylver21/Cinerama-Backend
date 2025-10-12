# 🪑 Sistema de Asientos - Cinerama Backend

## 📋 Descripción General

El **Sistema de Asientos** permite gestionar la reserva, confirmación y liberación de asientos para funciones de cine. Implementa:

- ✅ **Reservas temporales** (5 minutos de timeout)
- 🔒 **Bloqueos pesimistas** (evita doble reserva)
- ⏰ **Scheduler automático** (libera asientos expirados)
- 🗺️ **Mapa de asientos en tiempo real**
- 🎭 **Tipos de asientos** (Normal, VIP, Discapacitado, Pareja)
- 📊 **Estadísticas de ocupación**

---

## 🏗️ Arquitectura

### **Modelo: Asiento.java**
```java
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"funcion_id", "fila", "numero"}))
public class Asiento {
    private Long id;
    
    @ManyToOne
    private Funcion funcion;
    
    private String fila;          // A-Z
    private Integer numero;       // 1-N
    private TipoAsiento tipo;     // NORMAL, VIP, DISCAPACITADO, PAREJA
    private EstadoAsiento estado; // DISPONIBLE, SELECCIONADO, OCUPADO, BLOQUEADO
    private Double precio;
    private LocalDateTime fechaCreacion;
    
    public String getCodigoAsiento() { return fila + numero; } // "A1", "B5", etc.
}
```

### **Enums**
```java
enum TipoAsiento {
    NORMAL,         // Asientos estándar (S/. 15)
    VIP,            // Últimas 2 filas (S/. 25)
    DISCAPACITADO,  // Primera fila, columnas 1-2 (S/. 10)
    PAREJA          // Asientos dobles centrales (S/. 18)
}

enum EstadoAsiento {
    DISPONIBLE,    // Nadie lo ha seleccionado
    SELECCIONADO,  // Reservado temporalmente (5 min)
    OCUPADO,       // Confirmado y pagado
    BLOQUEADO      // No disponible para venta
}
```

---

## 🔄 Flujo de Reserva

### **1️⃣ Usuario Selecciona Asiento**
```http
POST /api/asientos/reservar/123
```
- ✅ Verifica que el asiento esté DISPONIBLE
- 🔒 Usa bloqueo pesimista (`@Lock(PESSIMISTIC_WRITE)`)
- ⏰ Marca como SELECCIONADO con timestamp actual
- ⏳ El usuario tiene **5 minutos** para confirmar

### **2️⃣ Usuario Confirma Compra**
```http
POST /api/asientos/confirmar/123
```
- ✅ Verifica que esté SELECCIONADO
- 🕐 Valida que no hayan pasado más de 5 minutos
- 💳 Marca como OCUPADO
- 🎫 Crea el Boleto asociado

### **3️⃣ Usuario Cancela Reserva**
```http
POST /api/asientos/liberar/123
```
- 🔓 Marca como DISPONIBLE nuevamente
- ♻️ Asiento disponible para otros usuarios

### **4️⃣ Timeout Automático** (Scheduler)
```java
@Scheduled(cron = "0 * * * * *") // Cada minuto
public void liberarAsientosExpirados() {
    // Libera SELECCIONADOS con más de 5 minutos
}
```

---

## 📡 Endpoints REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/asientos/funcion/{funcionId}` | Obtiene mapa completo de asientos |
| `POST` | `/api/asientos/reservar/{asientoId}` | Reserva temporal (5 min) |
| `POST` | `/api/asientos/confirmar/{asientoId}` | Confirma reserva → OCUPADO |
| `POST` | `/api/asientos/liberar/{asientoId}` | Libera reserva → DISPONIBLE |
| `POST` | `/api/asientos/generar/{funcionId}` | Genera asientos para función |
| `GET` | `/api/asientos/disponible/{funcionId}/{fila}/{numero}` | Verifica disponibilidad |
| `GET` | `/api/asientos/funcion/{funcionId}/estado/{estado}` | Filtra por estado |
| `GET` | `/api/asientos/funcion/{funcionId}/tipo/{tipo}` | Filtra por tipo (VIP, etc.) |
| `GET` | `/api/asientos/estadisticas/{funcionId}` | Estadísticas de ocupación |

---

## 🎬 Ejemplos de Uso

### **1. Obtener Mapa de Asientos**
```http
GET http://localhost:8080/api/asientos/funcion/1

Response:
[
  {
    "id": 1,
    "fila": "A",
    "numero": 1,
    "tipo": "DISCAPACITADO",
    "estado": "DISPONIBLE",
    "precio": 10.0,
    "codigoAsiento": "A1"
  },
  {
    "id": 2,
    "fila": "A",
    "numero": 2,
    "tipo": "DISCAPACITADO",
    "estado": "DISPONIBLE",
    "precio": 10.0,
    "codigoAsiento": "A2"
  },
  {
    "id": 15,
    "fila": "B",
    "numero": 5,
    "tipo": "NORMAL",
    "estado": "SELECCIONADO",
    "precio": 15.0,
    "codigoAsiento": "B5"
  },
  {
    "id": 50,
    "fila": "E",
    "numero": 10,
    "tipo": "VIP",
    "estado": "OCUPADO",
    "precio": 25.0,
    "codigoAsiento": "E10"
  }
]
```

### **2. Reservar Asiento**
```http
POST http://localhost:8080/api/asientos/reservar/15

Response:
{
  "id": 15,
  "fila": "B",
  "numero": 5,
  "estado": "SELECCIONADO",
  "fechaCreacion": "2025-01-23T10:30:00"
}
```

**⚠️ Error si ya está ocupado:**
```json
{
  "mensaje": "El asiento B5 no está disponible. Estado actual: OCUPADO"
}
```

### **3. Confirmar Reserva**
```http
POST http://localhost:8080/api/asientos/confirmar/15

Response:
{
  "id": 15,
  "estado": "OCUPADO"
}
```

**⚠️ Error si expiró:**
```json
{
  "mensaje": "La reserva expiró. Por favor, vuelva a seleccionar el asiento."
}
```

### **4. Generar Asientos para Función**
```http
POST http://localhost:8080/api/asientos/generar/1

Response:
[
  { "fila": "A", "numero": 1, "tipo": "DISCAPACITADO", "precio": 10.0 },
  { "fila": "A", "numero": 2, "tipo": "DISCAPACITADO", "precio": 10.0 },
  { "fila": "A", "numero": 3, "tipo": "NORMAL", "precio": 15.0 },
  // ... 47 asientos más
  { "fila": "E", "numero": 9, "tipo": "VIP", "precio": 25.0 },
  { "fila": "E", "numero": 10, "tipo": "VIP", "precio": 25.0 }
]
```

### **5. Estadísticas de Ocupación**
```http
GET http://localhost:8080/api/asientos/estadisticas/1

Response:
{
  "total": 50,
  "disponibles": 35,
  "seleccionados": 5,
  "ocupados": 10,
  "bloqueados": 0,
  "porcentajeOcupacion": 20.0
}
```

---

## 🔒 Prevención de Doble Reserva

### **Nivel Base de Datos**
```sql
UNIQUE CONSTRAINT (funcion_id, fila, numero)
```
- No permite dos asientos con misma fila y número en una función

### **Nivel Aplicación**
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Asiento a WHERE a.id = :id")
Optional<Asiento> findByIdWithLock(@Param("id") Long id);
```
- Si dos usuarios intentan reservar el mismo asiento simultáneamente, uno espera a que el otro termine

### **Nivel Transaccional**
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public Asiento reservarAsiento(Long asientoId) { ... }
```
- Máxima protección contra condiciones de carrera

---

## ⏰ Scheduler de Liberación

### **Configuración**
```java
@Scheduled(cron = "0 * * * * *") // Cada minuto
public void liberarAsientosExpirados() {
    LocalDateTime limiteExpiracion = LocalDateTime.now().minusMinutes(5);
    int liberados = asientoRepository.liberarAsientosExpirados(limiteExpiracion);
    log.info("Liberados {} asientos expirados", liberados);
}
```

### **Query de Liberación**
```sql
UPDATE asiento SET estado = 'DISPONIBLE'
WHERE estado = 'SELECCIONADO' 
  AND fecha_creacion < (NOW() - INTERVAL 5 MINUTE)
```

---

## 💰 Lógica de Precios

| Tipo de Asiento | Ubicación | Precio |
|-----------------|-----------|--------|
| 🪑 **NORMAL** | Resto de asientos | S/. 15 |
| ⭐ **VIP** | Últimas 2 filas | S/. 25 |
| ♿ **DISCAPACITADO** | Primera fila (columnas 1-2) | S/. 10 |
| 💑 **PAREJA** | Asientos pares en filas centrales | S/. 18 |

### **Distribución Automática**
Para una sala de **50 asientos** (5 filas x 10 columnas):

```
Fila E: [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP]
Fila D: [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP] [VIP]
Fila C: [NOR] [PAR] [NOR] [PAR] [NOR] [PAR] [NOR] [PAR] [NOR] [PAR]
Fila B: [NOR] [PAR] [NOR] [PAR] [NOR] [PAR] [NOR] [PAR] [NOR] [PAR]
Fila A: [DIS] [DIS] [NOR] [NOR] [NOR] [NOR] [NOR] [NOR] [NOR] [NOR]
```

---

## 🔗 Integración con Boleto

### **Antes (String)**
```java
@Column(nullable = false)
private String asiento; // "A5", "B10", etc.
```

### **Ahora (Relación)**
```java
@OneToOne
@JoinColumn(name = "asiento_id", nullable = false)
private Asiento asiento;
```

### **Al Crear Boleto**
```java
// 1. Reservar asiento
Asiento asiento = asientoService.reservarAsiento(asientoId);

// 2. Usuario completa compra

// 3. Confirmar reserva
asiento = asientoService.confirmarReserva(asientoId);

// 4. Crear boleto
Boleto boleto = Boleto.builder()
    .cliente(cliente)
    .funcion(funcion)
    .asiento(asiento) // ✅ Relación con entidad Asiento
    .precio(asiento.getPrecio())
    .estado(EstadoBoleto.PAGADO)
    .build();
```

---

## 📊 Casos de Uso del Frontend

### **1. Mostrar Mapa de Asientos**
```javascript
const response = await fetch('/api/asientos/funcion/1');
const asientos = await response.json();

// Renderizar mapa visual
asientos.forEach(asiento => {
  const elemento = document.getElementById(`asiento-${asiento.fila}${asiento.numero}`);
  elemento.className = asiento.estado.toLowerCase(); // 'disponible', 'seleccionado', 'ocupado'
});
```

### **2. Reservar al Hacer Click**
```javascript
async function reservarAsiento(asientoId) {
  try {
    const response = await fetch(`/api/asientos/reservar/${asientoId}`, {
      method: 'POST'
    });
    
    if (response.ok) {
      // Iniciar contador de 5 minutos
      iniciarTemporizador(300); // 300 segundos
    } else {
      const error = await response.json();
      alert(error.mensaje); // "El asiento no está disponible"
    }
  } catch (error) {
    console.error('Error al reservar:', error);
  }
}
```

### **3. Confirmar al Pagar**
```javascript
async function confirmarCompra(asientoId) {
  const response = await fetch(`/api/asientos/confirmar/${asientoId}`, {
    method: 'POST'
  });
  
  if (response.ok) {
    // Proceder a crear boleto
    crearBoleto(asientoId);
  } else {
    const error = await response.json();
    if (error.mensaje.includes('expiró')) {
      alert('Tu reserva expiró. Por favor, selecciona el asiento nuevamente.');
      location.reload();
    }
  }
}
```

---

## 🧪 Pruebas Recomendadas

### **Test 1: Reserva Simple**
1. `POST /api/asientos/generar/1` → Generar asientos
2. `GET /api/asientos/funcion/1` → Ver mapa
3. `POST /api/asientos/reservar/15` → Reservar A5
4. `POST /api/asientos/confirmar/15` → Confirmar

### **Test 2: Doble Reserva (Debe Fallar)**
1. Usuario A: `POST /api/asientos/reservar/15` ✅
2. Usuario B: `POST /api/asientos/reservar/15` ❌ "Asiento no disponible"

### **Test 3: Timeout**
1. `POST /api/asientos/reservar/15` → Reservar
2. ⏳ Esperar 6 minutos
3. Verificar que el scheduler lo liberó automáticamente

### **Test 4: Estadísticas**
1. Reservar 10 asientos
2. Confirmar 5
3. `GET /api/asientos/estadisticas/1` → Ver porcentaje de ocupación

---

## 🚀 Próximos Pasos

✅ **Sprint 1 Completado:**
- [x] Modelo Asiento con enums
- [x] Repository con bloqueos pesimistas
- [x] Service con reserva temporal (5 min)
- [x] Controller con endpoints REST
- [x] Scheduler automático
- [x] Integración con Boleto

🔜 **Sprint 2: Sistema de Promociones**
- [ ] Modelo Promocion (código, descuento, vigencia)
- [ ] Validación de códigos promocionales
- [ ] Aplicar descuentos a boletos

🔜 **Sprint 3: Carrito de Compras**
- [ ] Modelos Carrito e ItemCarrito
- [ ] Agregar/eliminar items
- [ ] Checkout con promociones

🔜 **Sprint 4: Mejoras Finales**
- [ ] Refactor de Pago
- [ ] Notificaciones por email
- [ ] Webhook de pagos

---

## 📝 Notas Técnicas

- **Timeout:** Configurable en `AsientoServiceImpl.MINUTOS_EXPIRACION`
- **Scheduler:** Se ejecuta cada minuto (cron: `0 * * * * *`)
- **Transacciones:** Nivel SERIALIZABLE para máxima consistencia
- **Bloqueos:** Pesimistas (`PESSIMISTIC_WRITE`) en reservas
- **Constraint:** Único por (funcion_id, fila, numero)

---

## 🐛 Troubleshooting

**Problema:** "Asiento no disponible" al reservar
- ✅ Verificar que el asiento esté en estado DISPONIBLE
- ✅ Confirmar que no está expirado

**Problema:** Scheduler no libera asientos
- ✅ Verificar que `@EnableScheduling` esté en la configuración
- ✅ Revisar logs para ver si se ejecuta cada minuto

**Problema:** Doble reserva simultánea
- ✅ Asegurar que `@Lock(PESSIMISTIC_WRITE)` esté en el repository
- ✅ Verificar que la transacción sea SERIALIZABLE

---

✅ **Sistema de Asientos Implementado con Éxito**
