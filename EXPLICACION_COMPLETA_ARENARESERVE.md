# EXPLICACION COMPLETA ARENARESERVE

Guia de estudio para sustentar el proyecto **ArenaReserve - Sistema Web para la Gestion de Reservas de Canchas Sinteticas**.

Este documento explica el backend y el frontend capa por capa, con enfoque en arquitectura por capas, Programacion Orientada a Objetos, Java, Spring Boot, Angular, TypeScript, PostgreSQL y JWT.

> Nota de estudio: las clases del modelo `Persona`, `Cliente`, `Empleado`, `Cancha`, `Reserva`, `Pago` y `ServicioAdicional` no se explican desde cero porque ya fueron estudiadas. Se referencian solo cuando son necesarias para entender otras capas.

---

## 1. Arquitectura General Del Proyecto

ArenaReserve usa una arquitectura por capas. Esto significa que el codigo esta separado segun responsabilidades. El objetivo no es solo tener carpetas bonitas; el objetivo es evitar que una clase haga trabajo que no le corresponde.

La idea general del backend es:

```text
Controller -> Service -> Repository -> PostgreSQL
```

Y la idea general del sistema completo es:

```text
Angular -> HTTP/REST -> Spring Controller -> Service -> Repository -> PostgreSQL
PostgreSQL -> Repository -> Service -> DTO Response -> Controller -> Angular
```

Cuando ocurre un error:

```text
Service o Modelo lanza Exception
GlobalExceptionHandler convierte la excepcion en JSON
Angular recibe error.error.message
Angular muestra el mensaje al usuario
```

### Por Que No Solo Hay Controller, DTO, Model, Repository Y Service

El proyecto tiene paquetes adicionales porque el dominio y la seguridad lo necesitan.

### `auth`

Contiene login, registro, generacion de token JWT y filtro de autenticacion. No se mezcla con `controller` general porque autenticacion es una preocupacion transversal y delicada.

### `config`

Contiene configuraciones de Spring Security, CORS, password encoder y datos iniciales. Estas clases no son reglas de negocio, pero hacen que el sistema arranque correctamente.

### `exception`

Contiene excepciones personalizadas y un manejador global. Permite que errores de negocio se respondan de forma uniforme.

### `strategy`

Contiene las estrategias de calculo de precio. Aqui se ve polimorfismo: varias clases implementan la misma interfaz `CalculadoraPrecio`, pero cada una calcula diferente.

### `payment`

Contiene las estrategias de procesamiento de pago. Igual que `strategy`, usa interfaces y polimorfismo.

### `user`

Contiene el usuario que inicia sesion en el sistema. Se separa del modelo de personas porque no todo cliente es usuario y no todo usuario representa un cliente.

### `enums`

Contiene valores cerrados: roles, estados, tipos de cancha, tipos de pago y horarios. Evita usar strings sueltos.

### Flujo De Una Peticion: Crear Reserva

1. En Angular, `ReservaFormComponent` captura datos del formulario.
2. Llama a `ReservaService` de Angular.
3. Angular hace `POST /api/reservas`.
4. Llega a `ReservaController`.
5. El controller llama a `ReservaService` del backend.
6. El service valida cancha activa, horario, solapamiento y calcula precio.
7. El service usa repositories para buscar cliente, cancha, servicios y guardar la reserva.
8. PostgreSQL guarda los datos.
9. El backend devuelve `ReservaResponse`.
10. Angular recarga la lista y muestra mensaje de exito.

Si la cancha esta inactiva:

1. `ReservaService` detecta que `EstadoCancha` no es `ACTIVA`.
2. Lanza `OperacionNoPermitidaException`.
3. `GlobalExceptionHandler` responde JSON con status 400 y mensaje claro.
4. Angular extrae el mensaje con `extractHttpErrorMessage`.
5. El formulario muestra la caja roja con el mensaje.

### Que Puedo Decir En Exposicion

"Mi proyecto usa arquitectura por capas. Los controladores solo reciben peticiones y devuelven respuestas. Los servicios contienen reglas de negocio. Los repositorios hablan con PostgreSQL. Los DTOs evitan exponer entidades directamente. Paquetes como `auth`, `config`, `exception`, `strategy` y `payment` existen porque seguridad, errores y comportamientos variables deben estar separados para mantener el sistema limpio y mantenible."

### Conceptos De POO Aplicados

- Abstraccion: interfaces `CalculadoraPrecio` y `MetodoPago`.
- Herencia: `Cliente` y `Empleado` heredan de `Persona`.
- Polimorfismo: calculadoras de precio y metodos de pago.
- Encapsulamiento: los estados cambian mediante metodos de dominio.
- Modularidad: cada paquete tiene responsabilidad concreta.

### Resumen Para Estudiar Rapido

Controller expone API. Service decide reglas. Repository persiste. DTO transporta datos. Exception estandariza errores. Auth protege rutas. Strategy y Payment muestran POO real.

---

## 2. Capa ENUMS

Los enums representan valores cerrados. Se usan cuando una propiedad solo puede tomar algunos valores definidos.

En las entidades se guardan con:

```java
@Enumerated(EnumType.STRING)
```

Eso hace que PostgreSQL guarde `ACTIVA`, `CONFIRMADA`, `ADMIN`, etc., como texto. Es mejor que guardar numeros porque la base queda legible y no se rompe si cambia el orden del enum.

### `Rol.java`

```java
package com.arenareserve.enums;

public enum Rol {
    ADMIN,
    EMPLEADO
}
```

Linea por linea:

- `package com.arenareserve.enums;`: ubica el archivo dentro de la capa de enums.
- `public enum Rol`: declara un tipo especial con valores fijos.
- `ADMIN`: usuario con acceso total.
- `EMPLEADO`: usuario operativo con acceso limitado.

Donde se usa:

- `UsuarioSistema`: atributo `rol`.
- `SecurityConfig`: autorizacion de rutas.
- Angular: `SidebarComponent` muestra o no el menu de usuarios.

Problema que evita:

Evita strings como `"admin"`, `"Administrador"` o `"ADMINN"`.

### `EstadoReserva.java`

```java
public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    FINALIZADA
}
```

Representa el ciclo de vida de una reserva.

- `PENDIENTE`: creada pero no pagada.
- `CONFIRMADA`: pagada.
- `CANCELADA`: anulada.
- `FINALIZADA`: ya terminada.

Se usa en `Reserva`, `PagoService`, `ReservaRepository` y `ReservaService`.

### `EstadoCancha.java`

```java
public enum EstadoCancha {
    ACTIVA,
    INACTIVA,
    MANTENIMIENTO
}
```

Representa si una cancha se puede usar.

- `ACTIVA`: se puede reservar.
- `INACTIVA`: no se puede reservar.
- `MANTENIMIENTO`: no se puede reservar temporalmente.

Se usa en `Cancha`, `CanchaService`, `ReservaService`, `DisponibilidadService` y Angular.

### `TipoCancha.java`

```java
public enum TipoCancha {
    FUTBOL_5,
    FUTBOL_7,
    FUTBOL_11
}
```

Evita que el usuario escriba tipos arbitrarios. Se usa en `Cancha`, `CanchaRequest`, `CanchaResponse` y formularios Angular.

### `TipoPago.java`

```java
public enum TipoPago {
    EFECTIVO,
    TRANSFERENCIA,
    TARJETA
}
```

Se usa en `Pago` y `PagoService`. Permite seleccionar una implementacion de `MetodoPago`.

### `TipoHorario.java`

```java
public enum TipoHorario {
    NORMAL,
    NOCTURNO,
    FIN_SEMANA
}
```

Representa categorias de horario del negocio. Aunque el calculo actual se decide por hora y dia en `ReservaService`, este enum expresa el vocabulario del dominio.

### Que Puedo Decir En Exposicion

"Uso enums porque roles, estados y tipos son conjuntos cerrados. Esto evita errores de escritura y hace que la base de datos guarde valores claros con `EnumType.STRING`."

### Conceptos De POO Aplicados

Los enums aplican abstraccion del dominio: convierten conceptos reales en tipos seguros de Java.

### Resumen Rapido

Enums = valores validos controlados. Evitan strings libres. Mejoran seguridad, claridad y persistencia.

---

## 3. Capa STRATEGY

Esta capa aplica el patron Strategy. La idea es tener una interfaz comun para calcular precios, pero varias implementaciones concretas.

### `CalculadoraPrecio.java`

```java
package com.arenareserve.strategy;
import com.arenareserve.model.Reserva;
import java.math.BigDecimal;

public interface CalculadoraPrecio {
    BigDecimal calcular(Reserva reserva);
}
```

Linea por linea:

- `package`: indica que pertenece a la capa de estrategias.
- `import Reserva`: el calculo necesita datos de la reserva.
- `import BigDecimal`: se usa para dinero porque evita errores de precision.
- `public interface CalculadoraPrecio`: define un contrato.
- `BigDecimal calcular(Reserva reserva)`: toda clase que implemente la interfaz debe calcular el precio de una reserva.

Esto es abstraccion porque define que se puede hacer, no como se hace.

### `CalculadoraPrecioNormal.java`

```java
@Component
public class CalculadoraPrecioNormal implements CalculadoraPrecio {
    @Override
    public BigDecimal calcular(Reserva reserva) {
        return reserva.getCancha().getPrecioHora()
                .multiply(BigDecimal.valueOf(reserva.calcularDuracion()));
    }
}
```

- `@Component`: Spring crea una instancia y la puede inyectar.
- `implements CalculadoraPrecio`: cumple el contrato.
- `@Override`: implementa el metodo de la interfaz.
- `getPrecioHora()`: toma el valor por hora de la cancha.
- `reserva.calcularDuracion()`: usa comportamiento del dominio.
- `multiply`: multiplica precio por duracion.

### `CalculadoraPrecioNocturno.java`

```java
return reserva.getCancha().getPrecioHora()
        .multiply(BigDecimal.valueOf(reserva.calcularDuracion()))
        .multiply(new BigDecimal("1.20"));
```

Hace lo mismo que la normal, pero agrega 20% de recargo. `new BigDecimal("1.20")` se escribe con string para precision decimal.

### `CalculadoraPrecioFinSemana.java`

```java
return reserva.getCancha().getPrecioHora()
        .multiply(BigDecimal.valueOf(reserva.calcularDuracion()))
        .multiply(new BigDecimal("1.15"));
```

Aplica recargo de 15%.

### Conexion Con `ReservaService`

`ReservaService` inyecta:

```java
private final CalculadoraPrecioNormal precioNormal;
private final CalculadoraPrecioNocturno precioNocturno;
private final CalculadoraPrecioFinSemana precioFinSemana;
```

Luego selecciona estrategia:

```java
BigDecimal base = esFinSemana(reserva) ? precioFinSemana.calcular(reserva)
        : reserva.getHoraInicio().getHour() >= 18 ? precioNocturno.calcular(reserva) : precioNormal.calcular(reserva);
```

Aunque hay una decision condicional para escoger estrategia, el calculo esta encapsulado en clases separadas.

### Que Puedo Decir En Exposicion

"Use Strategy para no dejar todo el calculo de precios dentro de un metodo gigante. Cada clase sabe calcular un tipo de precio. Esto muestra polimorfismo porque las tres clases responden al mismo metodo `calcular`, pero con resultados distintos."

### Conceptos De POO Aplicados

- Interfaz: `CalculadoraPrecio`.
- Polimorfismo: normal, nocturno y fin de semana.
- Responsabilidad unica: cada calculadora tiene una regla.
- Abierto/cerrado: se puede agregar una nueva calculadora sin modificar las existentes.

### Resumen Rapido

`CalculadoraPrecio` es el contrato. Las clases concretas son estrategias. `ReservaService` las usa para calcular el total.

---

## 4. Capa PAYMENT

Esta capa aplica el mismo enfoque de Strategy, pero para pagos.

### `MetodoPago.java`

```java
package com.arenareserve.payment;
import com.arenareserve.model.Pago;

public interface MetodoPago {
    void procesar(Pago pago);
}
```

- Define un contrato para procesar pagos.
- Recibe un objeto `Pago`.
- No retorna nada; si algo esta mal, lanza excepcion.

### `PagoEfectivo.java`

```java
@Component
public class PagoEfectivo implements MetodoPago {
    @Override
    public void procesar(Pago pago) {
        pago.validarValor();
    }
}
```

El pago en efectivo solo exige valor mayor a cero.

### `PagoTransferencia.java`

```java
pago.validarValor();
if (pago.getReferencia() == null || pago.getReferencia().isBlank()) {
    throw new PagoInvalidoException("La transferencia requiere referencia");
}
```

Primero valida valor. Luego exige referencia. Si no existe, lanza `PagoInvalidoException`.

### `PagoTarjeta.java`

```java
pago.validarValor();
if (pago.getReferencia() == null || pago.getReferencia().length() < 4) {
    throw new PagoInvalidoException("El pago con tarjeta requiere referencia valida");
}
```

Valida valor y referencia minima.

### Conexion Con `PagoService`

`PagoService` inyecta las implementaciones:

```java
private final PagoEfectivo efectivo;
private final PagoTransferencia transferencia;
private final PagoTarjeta tarjeta;
```

Selecciona implementacion:

```java
private MetodoPago metodo(TipoPago tipoPago) {
    return switch (tipoPago) {
        case EFECTIVO -> efectivo;
        case TRANSFERENCIA -> transferencia;
        case TARJETA -> tarjeta;
    };
}
```

Y procesa:

```java
metodo(request.tipoPago()).procesar(pago);
```

Esa linea demuestra polimorfismo: el objeto concreto cambia, pero el metodo invocado es el mismo.

### Que Puedo Decir En Exposicion

"Los pagos tienen reglas distintas. En vez de hacer muchos if mezclados, use una interfaz `MetodoPago` y tres implementaciones. Asi el service trabaja con una abstraccion."

### Conceptos De POO Aplicados

- Abstraccion: `MetodoPago`.
- Polimorfismo: efectivo, transferencia y tarjeta.
- Encapsulamiento: cada clase contiene su propia regla.

### Resumen Rapido

Payment separa comportamientos variables de pago. `PagoService` no conoce detalles internos; solo llama `procesar`.

---

## 5. Repositories

Los repositories son la capa de persistencia. En Spring Data JPA, una interfaz que extiende `JpaRepository` obtiene metodos CRUD automaticamente.

Ejemplos de metodos heredados:

- `findAll()`
- `findById(id)`
- `save(entity)`
- `delete(entity)`
- `count()`
- `existsById(id)`

No escribimos SQL para todo porque Spring Data puede crear consultas a partir del nombre del metodo.

### `ClienteRepository.java`

```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByDocumento(String documento);
}
```

- `JpaRepository<Cliente, Long>`: administra entidades `Cliente` con id `Long`.
- `existsByDocumento`: Spring genera consulta para saber si existe un cliente con ese documento.
- `findByDocumento`: busca un cliente por documento y retorna `Optional`.

Uso:

- `ClienteService.crear`: evita documentos repetidos.
- `ClienteService.actualizar`: evita duplicar documento en otro cliente.

### `CanchaRepository.java`

```java
public interface CanchaRepository extends JpaRepository<Cancha, Long> {
}
```

Aunque esta vacio, hereda CRUD completo.

Uso:

- `CanchaService` lista, crea, actualiza y busca canchas.

### `ReservaRepository.java`

Metodos importantes:

```java
@Query("""
        select count(r) > 0 from Reserva r
        where r.cancha.id = :canchaId and r.fecha = :fecha
        and r.estadoReserva <> com.arenareserve.enums.EstadoReserva.CANCELADA
        and (:inicio < r.horaFin and :fin > r.horaInicio)
        """)
boolean existeSolapamiento(...)
```

Esta consulta detecta si una cancha ya tiene reserva en el mismo rango horario.

La condicion clave es:

```text
inicio nuevo < horaFin existente
y
fin nuevo > horaInicio existente
```

Eso detecta cruces reales de horario.

```java
select distinct r from Reserva r
left join fetch r.serviciosAdicionales
join fetch r.cliente
join fetch r.cancha
```

`findAllConDetalle()` carga reservas con cliente, cancha y servicios. Esto evita `LazyInitializationException` al convertir a `ReservaResponse`.

```java
Optional<Reserva> findByIdConDetalle(Long id)
```

Hace lo mismo, pero para una sola reserva.

### `PagoRepository.java`

```java
List<Pago> findByReservaId(Long reservaId);
```

Busca pagos asociados a una reserva.

```java
@Query("select coalesce(sum(p.valor), 0) from Pago p where p.fechaPago between :inicio and :fin")
BigDecimal ingresosEntre(...)
```

Suma pagos entre dos fechas. `coalesce` evita retornar null cuando no hay pagos.

### `ServicioAdicionalRepository.java`

```java
boolean existsByNombre(String nombre);
```

Se usa en `DataInitializer` para no duplicar servicios iniciales.

### `UsuarioRepository.java`

```java
Optional<UsuarioSistema> findByEmail(String email);
boolean existsByEmail(String email);
```

Se usa en login, registro y validacion de email unico.

### Que Puedo Decir En Exposicion

"Los repositories conectan mi dominio con PostgreSQL usando Spring Data JPA. Heredan CRUD de `JpaRepository` y agregan consultas especificas cuando el negocio lo requiere, como detectar reservas solapadas o sumar ingresos."

### Conceptos De POO Aplicados

Aqui la POO es mas arquitectonica: usamos interfaces para definir contratos de persistencia. Spring crea implementaciones en tiempo de ejecucion.

### Resumen Rapido

Repository = acceso a datos. Hereda CRUD. Consultas personalizadas cuando hay reglas especificas.

---

## 6. DTOs

DTO significa Data Transfer Object. Son objetos para transportar datos entre frontend y backend.

No se recomienda devolver entidades directamente porque:

- Pueden exponer campos sensibles.
- Pueden causar ciclos JSON.
- Pueden disparar carga perezosa.
- Mezclan persistencia con API.
- Dificultan mantenimiento.

### Request vs Response

Request: lo que el frontend envia al backend.

Response: lo que el backend devuelve al frontend.

Ejemplo:

`ClienteRequest` recibe datos para crear/actualizar cliente.

`ClienteResponse` devuelve el cliente con `id`, estado y fecha.

### DTOs De Cliente

`ClienteRequest`:

- `nombre`
- `telefono`
- `email`
- `documento`
- `direccion`

Usa validaciones como `@NotBlank` y `@Email`.

`ClienteResponse`:

- incluye `id`, datos visibles, `fechaRegistro` y `activo`.

### DTOs De Cancha

`CanchaRequest`:

- nombre
- tipoCancha
- capacidad
- precioHora
- estadoCancha

`CanchaResponse` devuelve datos de la cancha ya persistida.

### DTOs De Reserva

`ReservaRequest` recibe:

- `clienteId`
- `canchaId`
- `fecha`
- `horaInicio`
- `horaFin`
- ids de servicios adicionales

`ReservaResponse` devuelve:

- id
- nombre cliente
- nombre cancha
- fecha y horas
- estado
- total
- servicios adicionales

Esto evita devolver entidad `Reserva` directamente.

### DTOs De Pago

`PagoRequest` recibe:

- reservaId
- valor
- tipoPago
- referencia

`PagoResponse` devuelve:

- id
- reservaId
- valor
- tipoPago
- fechaPago
- referencia

### `ErrorResponse`

Representa errores uniformes:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "mensaje claro",
  "path": "/api/..."
}
```

### Auth DTOs

`LoginRequest`:

- email
- password

`RegisterRequest`:

- email
- password
- rol
- empleadoId

`AuthResponse`:

- token
- userId
- email
- rol

### Que Puedo Decir En Exposicion

"Los DTOs separan mi modelo interno de la API publica. Asi controlo que entra y que sale del sistema, aplico validaciones y evito problemas como exponer entidades JPA."

### Conceptos De POO Aplicados

Abstraccion de datos: el frontend no necesita conocer toda la entidad, solo la forma de datos que requiere.

### Resumen Rapido

Request entra. Response sale. ErrorResponse estandariza errores. DTO evita exponer entidades.

---

## 7. Exceptions

Las excepciones personalizadas representan errores del negocio. En lugar de lanzar `RuntimeException` generico, usamos nombres claros.

### Excepciones Simples

Cada excepcion extiende `RuntimeException` y recibe mensaje:

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

Esto se repite en:

- `ReservaSolapadaException`
- `HorarioNoDisponibleException`
- `PagoInvalidoException`
- `OperacionNoPermitidaException`

### Para Que Sirven

- `ResourceNotFoundException`: recurso no encontrado. HTTP 404.
- `ReservaSolapadaException`: horario ocupado. HTTP 409.
- `HorarioNoDisponibleException`: horario/cancha no disponible. HTTP 409.
- `PagoInvalidoException`: pago invalido. HTTP 400.
- `OperacionNoPermitidaException`: regla de negocio violada. HTTP 400.

### `GlobalExceptionHandler.java`

Esta clase tiene `@RestControllerAdvice`. Eso significa que intercepta excepciones lanzadas por controllers/services y construye respuestas HTTP.

Metodos clave:

```java
@ExceptionHandler(ResourceNotFoundException.class)
ResponseEntity<ErrorResponse> notFound(...)
```

Convierte no encontrados en 404.

```java
@ExceptionHandler({ReservaSolapadaException.class, HorarioNoDisponibleException.class})
ResponseEntity<ErrorResponse> conflict(...)
```

Convierte conflictos de reserva/horario en 409.

```java
@ExceptionHandler({PagoInvalidoException.class, OperacionNoPermitidaException.class, IllegalArgumentException.class})
ResponseEntity<ErrorResponse> badRequest(...)
```

Convierte errores de regla o validacion en 400.

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
```

Captura errores de `@Valid`.

```java
private ResponseEntity<ErrorResponse> build(...)
```

Construye siempre el mismo JSON.

### Conexion Con Angular

Angular recibe una respuesta con:

```typescript
error.error.message
```

El helper `extractHttpErrorMessage` extrae ese mensaje y lo muestra en el formulario.

### Que Puedo Decir En Exposicion

"No devuelvo errores improvisados. Las reglas de negocio lanzan excepciones personalizadas y un manejador global las convierte en respuestas JSON uniformes."

### Conceptos De POO Aplicados

Especializacion: cada excepcion representa un tipo concreto de error. Esto hace el codigo mas expresivo.

### Resumen Rapido

Exception = error del negocio. Handler = traductor a JSON HTTP. Angular = muestra mensaje.

---

## 8. Services

Los services son la capa mas importante del backend. Aqui viven las reglas de negocio.

### `ClienteService`

Responsabilidad:

- listar clientes
- obtener cliente
- crear cliente
- actualizar cliente
- eliminar logicamente
- activar/desactivar cliente

Dependencia:

```java
private final ClienteRepository repository;
```

`listar()`:

```java
return repository.findAll().stream().map(this::toResponse).toList();
```

Busca todos los clientes, los transforma a DTO y los devuelve.

`crear()`:

```java
if (repository.existsByDocumento(request.documento())) {
    throw new OperacionNoPermitidaException("El documento del cliente ya existe");
}
```

Valida documento unico.

Luego crea entidad, aplica datos y guarda.

`actualizar()`:

Busca cliente, valida que otro cliente no tenga el mismo documento, aplica datos y guarda.

`eliminar()`:

No borra fisicamente. Llama `cliente.desactivar()`. Esto es borrado logico.

`cambiarEstado()`:

Permite activar o desactivar. Si `activo` es true llama `cliente.activar()`, si no llama `desactivar()`.

`toResponse()`:

Convierte entidad a DTO.

Que puedo decir:

"ClienteService centraliza reglas de cliente. El controller no decide si el documento se repite ni como activar/desactivar."

### `CanchaService`

Responsabilidad:

- listar canchas
- crear cancha
- actualizar cancha
- cambiar estado
- buscar cancha por id

Reglas:

- precio mayor a cero se valida desde metodo de dominio `cambiarPrecioHora`.
- estado se cambia con metodos del dominio: `activar`, `desactivar`, `ponerEnMantenimiento`.

`cambiarEstado()`:

```java
if (EstadoCancha.ACTIVA.equals(estado)) cancha.activar();
if (EstadoCancha.INACTIVA.equals(estado)) cancha.desactivar();
if (EstadoCancha.MANTENIMIENTO.equals(estado)) cancha.ponerEnMantenimiento();
```

No se modifica el atributo directamente desde controller.

Que puedo decir:

"CanchaService protege el estado de la cancha usando metodos del dominio. Esto respeta encapsulamiento."

### `ReservaService`

Responsabilidad:

- crear reservas
- listar reservas
- obtener reservas
- cancelar
- finalizar
- calcular total
- validar solapamientos
- mapear a DTO

Dependencias:

```java
private final ReservaRepository repository;
private final ClienteService clienteService;
private final CanchaService canchaService;
private final ServicioAdicionalRepository servicioRepository;
private final CalculadoraPrecioNormal precioNormal;
private final CalculadoraPrecioNocturno precioNocturno;
private final CalculadoraPrecioFinSemana precioFinSemana;
```

Esto muestra colaboracion entre capas y objetos.

`listar()`:

```java
@Transactional(readOnly = true)
public List<ReservaResponse> listar() {
    return repository.findAllConDetalle().stream().map(this::toResponse).toList();
}
```

- `@Transactional(readOnly = true)`: mantiene una sesion de lectura y optimiza.
- `findAllConDetalle()`: usa fetch join para traer cliente, cancha y servicios.
- Evita `LazyInitializationException`.

`crear()`:

1. Busca cancha.
2. Valida que este activa.
3. Crea reserva.
4. Asigna cliente y cancha.
5. Asigna fecha y horas.
6. Valida horario.
7. Valida solapamiento.
8. Carga servicios adicionales.
9. Calcula total.
10. Guarda.
11. Devuelve DTO.

Validacion de cancha:

```java
validarCanchaActiva(cancha);
```

Si esta inactiva o mantenimiento, lanza `OperacionNoPermitidaException`.

Validacion de horario:

```java
reserva.validarHorario();
```

La entidad valida que fecha no sea pasada y hora fin sea mayor.

Solapamiento:

```java
repository.existeSolapamiento(...)
```

Si existe cruce, lanza:

```java
throw new ReservaSolapadaException("La cancha ya tiene una reserva en ese horario");
```

Servicios adicionales:

```java
cargarServicios(request.serviciosAdicionalesIds())
```

Convierte IDs en entidades. Si un servicio no existe, lanza 404.

Calculo total:

```java
BigDecimal base = esFinSemana(reserva) ? precioFinSemana.calcular(reserva)
        : reserva.getHoraInicio().getHour() >= 18 ? precioNocturno.calcular(reserva) : precioNormal.calcular(reserva);
```

Selecciona estrategia de precio.

Extras:

```java
reserva.getServiciosAdicionales().stream()
        .map(ServicioAdicional::getPrecio)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

Suma precios de servicios adicionales.

`cancelar()`:

Busca reserva, llama `reserva.cancelar()`, guarda y devuelve DTO.

`finalizar()`:

Busca reserva, llama `reserva.finalizar()`, guarda y devuelve DTO.

`toResponse()`:

Mapea entidad a DTO, incluyendo nombres de servicios.

Que puedo decir:

"ReservaService es el corazon del negocio. Aqui valido cancha activa, horario, solapamiento, calculo total con estrategias y convierto a DTO para no exponer entidades."

### `PagoService`

Responsabilidad:

- registrar pagos
- listar pagos por reserva
- confirmar reserva automaticamente

`registrar()`:

```java
Reserva reserva = reservaService.find(request.reservaId());
```

Busca reserva.

```java
if (EstadoReserva.CANCELADA.equals(reserva.getEstadoReserva())) {
    throw new OperacionNoPermitidaException("Una reserva cancelada no puede pagarse");
}
```

Regla de negocio.

Crea `Pago`, asigna datos, selecciona metodo:

```java
metodo(request.tipoPago()).procesar(pago);
```

Polimorfismo.

Luego:

```java
reserva.confirmar();
```

La reserva pagada pasa a confirmada.

Finalmente guarda pago.

Que puedo decir:

"PagoService no valida todos los pagos con if internos; delega el comportamiento a `MetodoPago`, aplicando polimorfismo."

### `DisponibilidadService`

Responsabilidad:

- generar franjas de disponibilidad de 08:00 a 22:00.
- hacerlo con recursividad.
- marcar franjas ocupadas.

Constantes:

```java
private static final LocalTime APERTURA = LocalTime.of(8, 0);
private static final LocalTime CIERRE = LocalTime.of(22, 0);
```

`consultar()`:

Busca cancha. Si no esta activa, lanza `HorarioNoDisponibleException`.

Crea lista vacia y llama:

```java
generarRecursivo(canchaId, fecha, APERTURA, CIERRE, franjas);
```

Recursividad:

```java
if (!actual.plusHours(1).isAfter(cierre)) {
```

Caso base: cuando la siguiente franja supera el cierre, no entra al if y termina.

Caso recursivo:

```java
LocalTime fin = actual.plusHours(1);
boolean disponible = !reservaRepository.existeSolapamiento(...);
franjas.add(new DisponibilidadResponse(actual, fin, disponible));
generarRecursivo(canchaId, fecha, fin, cierre, franjas);
```

Genera una franja y se llama a si mismo con la siguiente hora.

Como defenderlo:

"La recursividad tiene un caso base: detenerse cuando la siguiente hora supera el cierre. Tiene un caso recursivo: crear la franja actual y llamar el metodo con la hora fin como nuevo inicio."

### `DashboardService`

Responsabilidad:

- contar reservas del dia.
- contar pendientes y confirmadas.
- sumar ingresos del dia.
- listar proximas reservas.

Usa `ReservaRepository`, `PagoRepository` y `ReservaService`.

Que puedo decir:

"DashboardService concentra consultas agregadas para el tablero. No mezcla logica visual; solo prepara datos para el frontend."

### `UsuarioService`

Responsabilidad:

- cargar usuario para Spring Security.
- listar usuarios.
- crear usuarios.
- actualizar usuarios.
- activar/desactivar.

Implementa `UserDetailsService`, por eso tiene:

```java
loadUserByUsername(String username)
```

Spring Security llama este metodo durante autenticacion.

Al crear:

```java
usuario.setPassword(passwordEncoder.encode(request.password()));
```

Nunca guarda la password plana.

Que puedo decir:

"UsuarioService conecta mi usuario de base de datos con Spring Security. Ademas aplica reglas como email unico y password cifrada."

### Conceptos De POO En Services

- Encapsulamiento: llaman metodos del dominio, no manipulan todo desde controller.
- Polimorfismo: usan calculadoras y metodos de pago.
- Abstraccion: trabajan con servicios y repositorios como colaboradores.
- Modularidad: cada service tiene responsabilidad.

### Resumen Rapido

Services = reglas de negocio. Es la capa que mas debes defender.

---

## 9. Controllers

Los controllers exponen endpoints REST. No deben contener logica de negocio.

### Anotaciones

- `@RestController`: clase que responde JSON.
- `@RequestMapping`: ruta base.
- `@GetMapping`: consultar.
- `@PostMapping`: crear.
- `@PutMapping`: actualizar completo.
- `@PatchMapping`: cambio parcial.
- `@DeleteMapping`: eliminar o desactivar.
- `@RequestBody`: cuerpo JSON.
- `@PathVariable`: variable en la URL.
- `@RequestParam`: parametro query.
- `@Valid`: activa validaciones del DTO.

### `ClienteController`

Endpoints:

- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `POST /api/clientes`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`
- `PATCH /api/clientes/{id}/estado?activo=true`

Cada metodo llama a `ClienteService`.

### `CanchaController`

Endpoints:

- `GET /api/canchas`
- `GET /api/canchas/{id}`
- `POST /api/canchas`
- `PUT /api/canchas/{id}`
- `PATCH /api/canchas/{id}/estado`

Usa `@PreAuthorize("hasRole('ADMIN')")` para crear/editar/cambiar estado.

### `ReservaController`

Endpoints:

- `GET /api/reservas`
- `GET /api/reservas/{id}`
- `POST /api/reservas`
- `PATCH /api/reservas/{id}/cancelar`
- `PATCH /api/reservas/{id}/finalizar`

### `PagoController`

Endpoints:

- `POST /api/pagos`
- `GET /api/pagos/reserva/{reservaId}`

### `DisponibilidadController`

Endpoint:

```text
GET /api/disponibilidad?canchaId=1&fecha=2026-05-26
```

Usa `@RequestParam` para recibir cancha y fecha.

### `DashboardController`

Endpoint:

```text
GET /api/dashboard
```

### `UsuarioController`

Endpoints de administracion de usuarios. Protegidos por `SecurityConfig` para ADMIN.

### `AuthController`

Endpoints:

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`

### Que Puedo Decir En Exposicion

"Mis controllers son delgados. No calculan precios, no validan solapamientos, no cifran passwords. Solo reciben HTTP, delegan al service y devuelven DTOs."

### Conceptos De POO Aplicados

La POO aqui se refleja en separacion de responsabilidades y delegacion.

### Resumen Rapido

Controller = puerta REST. Service = negocio. DTO = datos. No mezclar.

---

## 10. Seguridad JWT

JWT significa JSON Web Token. Sirve para autenticar peticiones sin guardar sesion en el servidor.

### Login Completo

1. Angular envia email y password a `/api/auth/login`.
2. `AuthController` llama a `AuthService`.
3. `AuthService` usa `AuthenticationManager`.
4. Spring usa `UsuarioService.loadUserByUsername`.
5. Compara password con BCrypt.
6. Si es correcto, `JwtService` genera token.
7. Angular guarda token en `localStorage`.
8. En cada peticion, `JwtInterceptor` manda `Authorization: Bearer TOKEN`.
9. `JwtAuthenticationFilter` valida token.
10. Spring permite o bloquea segun rol.

### `UsuarioSistema implements UserDetails`

Esto permite que Spring Security entienda el usuario.

Metodos:

- `getAuthorities()`: devuelve rol como `ROLE_ADMIN` o `ROLE_EMPLEADO`.
- `getUsername()`: devuelve email.
- `isEnabled()`: indica si el usuario esta activo.

### `UsuarioService implements UserDetailsService`

Spring llama:

```java
loadUserByUsername(username)
```

para buscar el usuario por email.

### `JwtService`

Responsabilidad:

- generar token.
- extraer email.
- validar token.
- construir clave secreta.

Token contiene:

- subject: email.
- claim `rol`.
- claim `userId`.
- fecha de emision.
- expiracion.

### `JwtAuthenticationFilter`

Se ejecuta antes del filtro normal de usuario/password.

Lee header:

```text
Authorization: Bearer TOKEN
```

Extrae token, obtiene email, carga usuario y si el token es valido crea autenticacion en `SecurityContextHolder`.

### `SecurityConfig`

Define:

- CSRF desactivado para API REST.
- CORS habilitado.
- sesion stateless.
- rutas publicas.
- rutas de usuarios solo ADMIN.
- filtro JWT antes de `UsernamePasswordAuthenticationFilter`.

### `PasswordConfig`

Define:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

BCrypt cifra passwords con salt. No se guarda texto plano.

Separarlo en `PasswordConfig` ayuda a evitar dependencia circular porque `SecurityConfig` necesita `PasswordEncoder`, y otros servicios tambien.

### Autenticacion vs Autorizacion

Autenticacion: saber quien eres. Login con email/password.

Autorizacion: saber que puedes hacer. Ejemplo: solo ADMIN entra a usuarios.

### Que Puedo Decir En Exposicion

"JWT permite que el backend sea stateless. El login genera un token firmado. Angular lo guarda y lo manda en cada request. Un filtro valida el token y Spring Security decide permisos segun roles."

### Conceptos De POO Aplicados

- Interfaces de Spring: `UserDetails`, `UserDetailsService`.
- Encapsulamiento de seguridad en clases separadas.
- Responsabilidad unica: `JwtService` solo maneja tokens.

### Resumen Rapido

Login -> token. Interceptor manda token. Filtro valida token. SecurityConfig protege rutas.

---

## 11. Configuracion

### `application.properties`

Define:

```properties
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/arenareserve_db}
spring.datasource.username=${DATABASE_USERNAME:postgres}
spring.datasource.password=${DATABASE_PASSWORD:postgres}
```

Usa variables de entorno con valores por defecto.

```properties
spring.jpa.hibernate.ddl-auto=update
```

Hibernate actualiza tablas segun entidades. Util para desarrollo.

```properties
jwt.secret=${JWT_SECRET:...}
jwt.expiration=${JWT_EXPIRATION:86400000}
frontend.url=${FRONTEND_URL:http://localhost:4200}
```

Configura JWT y frontend permitido.

### `DataInitializer`

Implementa `CommandLineRunner`. Se ejecuta al iniciar.

Crea:

- admin inicial si no hay usuarios.
- canchas iniciales si no hay.
- servicios adicionales si no existen.

Admin:

```text
admin@arenareserve.com
Admin12345
```

Password se cifra con BCrypt.

### `CorsConfig`

Permite:

- `http://localhost:4200`
- `FRONTEND_URL`
- dominios `*.vercel.app`

Esto evita que el navegador bloquee peticiones Angular -> Spring.

### Que Puedo Decir En Exposicion

"La configuracion usa variables de entorno para que el sistema funcione local y desplegado. CORS permite el frontend, y DataInitializer garantiza un usuario admin inicial."

### Resumen Rapido

Config = arranque, seguridad, conexion, CORS y datos iniciales.

---

## 12. Frontend Angular: Estructura

El frontend se organiza asi:

```text
core/
  models/
  services/
  guards/
  interceptors/
features/
  auth/
  dashboard/
  clientes/
  canchas/
  reservas/
  pagos/
  usuarios/
shared/
  components/
```

### `core`

Contiene piezas reutilizables globales:

- modelos TypeScript.
- servicios HTTP.
- guards.
- interceptor JWT.

### `features`

Contiene pantallas del negocio:

- login.
- dashboard.
- clientes.
- canchas.
- disponibilidad.
- reservas.
- pagos.
- usuarios.

### `shared`

Componentes reutilizables:

- navbar.
- sidebar.
- confirm dialog.

### Conexion Con Backend

Cada service Angular usa `HttpClient`.

Ejemplo:

```typescript
listar() {
  return this.http.get<Cliente[]>(this.api);
}
```

La URL base viene de:

```typescript
environment.apiUrl
```

### Manejo De Token

`AuthService` guarda token en `localStorage`.

`JwtInterceptor` lee ese token y lo agrega a cada request.

### Manejo De Errores

`http-error.util.ts` intenta leer:

1. `error.error.message`
2. `error.error`
3. `error.message`
4. fallback generico

### Que Puedo Decir En Exposicion

"Angular esta separado por core, features y shared. Core tiene lo global, features las pantallas, shared componentes comunes. La comunicacion con backend se hace por HttpClient y todas las peticiones protegidas llevan JWT."

### Resumen Rapido

Angular no usa datos quemados para el negocio. Consume API REST.

---

## 13. Archivos Frontend Principales

### `app.module.ts`

Declara componentes e importa modulos:

- `BrowserModule`
- `ReactiveFormsModule`
- `HttpClientModule`
- `AppRoutingModule`

Tambien registra el interceptor:

```typescript
provideHttpClient(withInterceptors([jwtInterceptor]))
```

### `app-routing.module.ts`

Define rutas:

- `/login`
- `/dashboard`
- `/clientes`
- `/canchas`
- `/canchas/disponibilidad`
- `/reservas`
- `/pagos`
- `/usuarios`

Rutas protegidas usan `AuthGuard`. Usuarios usa tambien `RoleGuard`.

### `app.component.ts/html/css`

Decide si mostrar login solo o layout completo con sidebar/navbar.

```typescript
isLogin(): boolean {
  return this.router.url.startsWith('/login');
}
```

### `AuthService`

Atributos:

- `tokenKey`
- `roleKey`

Metodos:

- `login()`: llama backend y guarda token.
- `logout()`: borra token y redirige.
- `getToken()`: obtiene token.
- `isAuthenticated()`: verifica si hay token.
- `getRole()`: obtiene rol.

### `JwtInterceptor`

Lee token de localStorage y agrega:

```text
Authorization: Bearer TOKEN
```

Si recibe 401, borra token y redirige a login.

### `AuthGuard`

Permite entrar a una ruta solo si `isAuthenticated()` es true.

### `RoleGuard`

Lee roles permitidos en la ruta y compara con rol guardado.

### `LoginComponent`

Usa Reactive Forms.

Atributos:

- `loading`
- `error`
- `form`

Metodo:

- `submit()`: si el formulario es valido, llama `AuthService.login`.

Si credenciales fallan, muestra error.

### `DashboardComponent`

Inyecta `DashboardService`.

`ngOnInit()` carga datos del dashboard.

Muestra:

- reservas hoy.
- pendientes.
- confirmadas.
- ingresos del dia.
- proximas reservas.

### `ClienteListComponent`

Muestra tabla de clientes.

Atributos:

- `clientes`
- `seleccionado`

Metodos:

- `load()`: llama API.
- `onSaved()`: limpia seleccionado y recarga.
- `eliminar()`: desactiva cliente.
- `activar()`: reactiva cliente.

Muestra badge activo/inactivo.

### `ClienteFormComponent`

Formulario para crear o actualizar.

Usa `@Input() cliente` y `@Output() saved`.

Si recibe cliente, carga datos con `ngOnChanges`.

`save()` decide si crea o actualiza.

### `CanchaListComponent`

Muestra canchas y badge de estado.

Permite editar y cambiar estado.

### `CanchaFormComponent`

Formulario para crear/editar cancha.

Campos:

- nombre.
- tipo.
- capacidad.
- precio.
- estado.

### `DisponibilidadComponent`

Carga canchas, permite seleccionar fecha y consulta franjas.

Muestra disponible en verde y ocupado/no disponible en rojo.

### `ReservaListComponent`

Lista reservas.

Permite cancelar y finalizar.

Integra `ReservaFormComponent`.

### `ReservaFormComponent`

Formulario de reserva.

Atributos:

- clientes.
- canchas.
- errorMessage.
- successMessage.
- form.

Reglas frontend:

- selector muestra `nombre - estado`.
- si cancha no es ACTIVA, muestra advertencia.
- deshabilita boton.
- si backend responde error, muestra mensaje.
- si crea bien, muestra exito y emite `saved`.

Pero se mantiene validacion en backend. Esto es importante:

"El frontend ayuda a la experiencia de usuario, pero la seguridad real esta en backend."

### `PagoFormComponent`

Formulario para registrar pago.

Campos:

- reservaId.
- valor.
- tipoPago.
- referencia.

Al registrar, backend confirma reserva.

### `UsuarioListComponent`

Solo ADMIN puede llegar a esta ruta.

Lista usuarios y permite activar/desactivar.

### `UsuarioFormComponent`

Permite crear usuario con email, password y rol.

### `NavbarComponent`

Muestra nombre del sistema y boton salir.

`auth.logout()` cierra sesion.

### `SidebarComponent`

Menu lateral.

Muestra opcion Usuarios solo si:

```typescript
auth.getRole() === 'ADMIN'
```

### `ConfirmDialogComponent`

Componente reutilizable con `@Input` y `@Output`.

### Que Puedo Decir En Exposicion

"El frontend esta dividido igual que el backend: servicios para comunicacion, componentes para vista, guards para seguridad, interceptor para token y modelos para tipado."

### Conceptos De POO Aplicados

En Angular tambien hay clases, inyeccion de dependencias y separacion de responsabilidades.

### Resumen Rapido

Component = pantalla. Service = HTTP. Guard = protege ruta. Interceptor = agrega token. Model = tipos.

---

## 14. Frases Clave Para Sustentacion

### Arquitectura

"El sistema sigue arquitectura por capas para separar responsabilidades y facilitar mantenimiento."

### POO

"La POO se evidencia en herencia del modelo, encapsulamiento de estados, abstraccion con interfaces y polimorfismo en calculo de precios y pagos."

### Seguridad

"La autenticacion se hace con JWT. El backend es stateless y cada request protegida lleva el token en el header Authorization."

### Persistencia

"Uso Spring Data JPA para mapear entidades a PostgreSQL. Los repositories heredan CRUD y algunas consultas personalizadas resuelven reglas especificas."

### Excepciones

"Los errores de negocio no se devuelven improvisados. Se lanzan excepciones personalizadas y `GlobalExceptionHandler` las transforma en JSON uniforme."

### Recursividad

"La disponibilidad se genera recursivamente: el caso base es llegar al cierre, y el caso recursivo crea una franja de una hora y llama el metodo con la siguiente hora."

### Frontend

"Angular consume la API REST. No usa datos quemados para el negocio. Guarda JWT, protege rutas y muestra errores del backend al usuario."

---

## 15. Mapa Mental Final

```text
ArenaReserve
|
|-- Backend Spring Boot
|   |-- model: dominio
|   |-- enums: valores controlados
|   |-- dto: datos de entrada/salida
|   |-- repository: PostgreSQL/JPA
|   |-- service: reglas de negocio
|   |-- controller: endpoints REST
|   |-- auth: JWT/login
|   |-- config: seguridad/CORS/datos
|   |-- exception: errores uniformes
|   |-- strategy: polimorfismo de precios
|   |-- payment: polimorfismo de pagos
|
|-- Frontend Angular
|   |-- core: servicios, modelos, guards, interceptor
|   |-- features: pantallas
|   |-- shared: componentes comunes
|
|-- Base de datos
|   |-- PostgreSQL
|   |-- JPA crea/actualiza tablas
```

---

## 16. Si El Profesor Pregunta "Donde Esta La POO Real"

Respuesta recomendada:

"La POO no esta solo en tener clases. Esta en como se modelan responsabilidades. La herencia aparece con `Persona`, `Cliente` y `Empleado`. El encapsulamiento aparece en metodos como `reserva.cancelar()`, `reserva.confirmar()`, `cancha.activar()` y `cliente.activar()`. La abstraccion aparece en interfaces como `CalculadoraPrecio` y `MetodoPago`. El polimorfismo aparece porque varias clases implementan esas interfaces y el service puede tratarlas como el mismo tipo. Ademas, cada capa tiene responsabilidades claras."

---

## 17. Si El Profesor Pregunta Por LazyInitializationException

Respuesta recomendada:

"El problema ocurre cuando una relacion perezosa se intenta leer fuera de la sesion de Hibernate. En reservas, `serviciosAdicionales` podia causar eso al mapear a DTO. Se corrigio usando queries con `left join fetch` en `ReservaRepository` y metodos transaccionales de lectura en `ReservaService`. Asi se cargan cliente, cancha y servicios antes de construir `ReservaResponse`."

---

## 18. Si El Profesor Pregunta Por Canchas Inactivas

Respuesta recomendada:

"El frontend deshabilita el boton para mejorar experiencia, pero la validacion importante esta en backend. `ReservaService.validarCanchaActiva` revisa el `EstadoCancha`. Si no es ACTIVA lanza `OperacionNoPermitidaException` con mensaje claro. Esto evita que alguien salte el frontend y consuma la API directamente."

---

## 19. Si El Profesor Pregunta Por Errores En Angular

Respuesta recomendada:

"El backend responde errores con JSON uniforme. Angular tiene un helper `extractHttpErrorMessage` que lee `error.error.message`. Por eso mensajes como 'La cancha ya tiene una reserva en ese horario' o 'No se puede reservar esta cancha porque esta inactiva' se muestran en pantalla."

---

## 20. Checklist De Defensa

- Puedo explicar flujo login JWT.
- Puedo explicar flujo crear reserva.
- Puedo explicar solapamiento.
- Puedo explicar recursividad.
- Puedo explicar DTOs.
- Puedo explicar repositories.
- Puedo explicar exceptions.
- Puedo explicar guards/interceptor Angular.
- Puedo explicar roles ADMIN/EMPLEADO.
- Puedo explicar por que no exponer entidades.
- Puedo explicar polimorfismo en precios y pagos.

---

## 21. Apendice Backend Archivo Por Archivo

Esta seccion sirve para repasar rapido cada archivo cuando el profesor pregunte "muestreme donde esta eso en el codigo".

### `ArenareserveApplication.java`

Archivo de arranque.

```java
@SpringBootApplication
public class ArenareserveApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArenareserveApplication.class, args);
    }
}
```

- `@SpringBootApplication`: activa auto-configuracion, escaneo de componentes y configuracion Spring.
- `main`: punto de entrada Java.
- `SpringApplication.run`: levanta el contenedor Spring, controllers, services, repositories y configuraciones.

Defensa: "Esta clase no tiene negocio; solo arranca la aplicacion y permite que Spring detecte el resto por paquetes."

### Archivos `auth`

#### `LoginRequest`

Record con email y password. Usa validaciones para exigir correo valido y password no vacio.

Defensa: "Representa el cuerpo JSON del login. No uso entidad UsuarioSistema porque el login solo necesita email y password."

#### `RegisterRequest`

Record para crear usuario. Tiene email, password, rol y posible empleado asociado.

Defensa: "Este DTO evita que el frontend mande campos que no debe controlar, como activo o id."

#### `AuthResponse`

Record devuelto por login/registro/me. Contiene token, userId, email y rol.

Defensa: "El frontend necesita token para autenticar y rol para mostrar opciones."

#### `AuthController`

Controlador REST con ruta base `/api/auth`.

- `login`: recibe `LoginRequest`, llama `AuthService.login`.
- `register`: recibe `RegisterRequest`, llama `AuthService.register`.
- `me`: usa `Authentication` de Spring para saber quien esta autenticado.

Defensa: "El controller no valida password ni genera token; delega en AuthService."

#### `AuthService`

Coordina login y registro.

- `AuthenticationManager` verifica credenciales.
- `UsuarioService` busca/crea usuario.
- `JwtService` genera token.

Codigo clave:

```java
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(request.email(), request.password())
);
```

Esta linea no compara manualmente passwords; delega a Spring Security.

Codigo clave:

```java
return new AuthResponse(jwtService.generarToken(usuario), usuario.getId(), usuario.getEmail(), usuario.getRol());
```

Construye respuesta segura.

#### `JwtService`

Responsable del token.

- `generarToken`: crea token firmado.
- `extraerEmail`: lee subject.
- `esValido`: compara email y expiracion.
- `claims`: parsea token.
- `key`: construye clave secreta.

Defensa: "Centralizo JWT para que ningun controller manipule tokens directamente."

#### `JwtAuthenticationFilter`

Filtro que se ejecuta en cada request protegida.

Pasos:

1. Lee header `Authorization`.
2. Si no empieza por `Bearer`, deja pasar.
3. Extrae token.
4. Extrae email.
5. Carga usuario.
6. Valida token.
7. Crea autenticacion en `SecurityContextHolder`.

Defensa: "Este filtro convierte un token valido en un usuario autenticado dentro de Spring Security."

### Archivos `config`

#### `SecurityConfig`

Configura seguridad.

- `csrf.disable`: API REST no usa formularios tradicionales.
- `cors`: permite Angular.
- `SessionCreationPolicy.STATELESS`: no hay sesiones en servidor.
- `requestMatchers`: rutas publicas y protegidas.
- `hasRole("ADMIN")`: autorizacion por rol.
- `addFilterBefore`: pone JWT antes del filtro normal.

Defensa: "Aqui se define que rutas son publicas, cuales requieren token y cuales requieren rol ADMIN."

#### `PasswordConfig`

Declara:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Defensa: "BCrypt protege contrasenas. Separarlo evita dependencia circular y permite inyectarlo en varios servicios."

#### `CorsConfig`

Define origenes permitidos.

- local Angular: `http://localhost:4200`.
- frontend desplegado: `FRONTEND_URL`.
- Vercel: `https://*.vercel.app`.

Defensa: "CORS es una proteccion del navegador. Sin esto Angular no podria consumir el backend desde otro origen."

#### `DataInitializer`

Se ejecuta al iniciar.

- Si no hay usuarios, crea admin.
- Si no hay canchas, crea dos canchas.
- Si no existen servicios, crea Balon, Petos y Arbitraje.

Defensa: "Garantiza que el sistema pueda usarse inmediatamente despues de crear la base de datos."

### Archivos `controller`

Todos siguen el mismo patron:

```java
@RestController
@RequestMapping("/api/...")
@RequiredArgsConstructor
public class XController {
    private final XService service;
}
```

- `@RestController`: devuelve JSON.
- `@RequestMapping`: ruta base.
- `@RequiredArgsConstructor`: Lombok crea constructor para inyeccion.
- `private final Service`: dependencia obligatoria.

#### `ClienteController`

Expone CRUD de clientes y cambio de estado. Usa `@RequestBody` para JSON, `@PathVariable` para id y `@RequestParam` para activo.

#### `CanchaController`

Expone CRUD de canchas y cambio de estado. Crear, actualizar y cambiar estado estan protegidos con `@PreAuthorize("hasRole('ADMIN')")`.

#### `ReservaController`

Expone crear, listar, obtener, cancelar y finalizar reservas.

#### `PagoController`

Expone registrar pago y consultar pagos por reserva.

#### `DisponibilidadController`

Expone consulta por query params:

```text
canchaId
fecha
```

#### `DashboardController`

Devuelve indicadores.

#### `UsuarioController`

Gestiona usuarios. La ruta completa esta protegida en `SecurityConfig`.

### Archivos `repository`

Todos extienden `JpaRepository`.

Patron:

```java
public interface XRepository extends JpaRepository<X, Long>
```

Significa: "Spring Data va a crear implementacion para entidad X con id Long".

Consultas importantes:

- `existsByDocumento`: consulta derivada por nombre.
- `findByEmail`: consulta derivada para seguridad.
- `existeSolapamiento`: JPQL manual por regla compleja.
- `findAllConDetalle`: fetch join para evitar lazy loading.
- `ingresosEntre`: suma valores de pagos.

### Archivos `service`

Patron:

```java
@Service
@RequiredArgsConstructor
public class XService
```

- `@Service`: clase de negocio administrada por Spring.
- `@RequiredArgsConstructor`: inyeccion por constructor.

#### `ClienteService`

Reglas:

- documento unico.
- borrado logico.
- activar/inactivar por metodo de dominio.

#### `CanchaService`

Reglas:

- precio positivo.
- cambio de estado por metodos del dominio.

#### `ReservaService`

Reglas:

- cancha activa.
- horario valido.
- no solapamiento.
- servicios existentes.
- total calculado.
- estados controlados.
- fetch join y transaccion para evitar lazy.

#### `PagoService`

Reglas:

- reserva cancelada no se paga.
- pago debe ser valido.
- metodo de pago polimorfico.
- pago confirma reserva.

#### `DisponibilidadService`

Reglas:

- cancha debe estar activa.
- franjas de una hora.
- recursividad de apertura a cierre.
- consulta solapamiento por franja.

#### `DashboardService`

Reglas:

- obtiene contadores y sumas del dia.
- mapea proximas reservas con `ReservaService`.

#### `UsuarioService`

Reglas:

- email unico.
- password cifrada.
- implementa contrato de Spring Security.

### Archivos `dto`

Patron:

```java
public record NombreDto(...)
```

Un `record` en Java crea automaticamente constructor, getters, `equals`, `hashCode` y `toString`.

DTOs con `Request` reciben datos desde Angular. DTOs con `Response` devuelven datos a Angular.

Validaciones:

- `@NotBlank`: texto obligatorio.
- `@NotNull`: objeto obligatorio.
- `@Email`: formato correo.
- `@Positive`: numero positivo.
- `@FutureOrPresent`: fecha actual o futura.
- `@Size`: longitud minima/maxima.

### Archivos `exception`

Cada excepcion es una clase pequena que extiende `RuntimeException`. Su importancia no es la cantidad de codigo, sino el significado del nombre.

`GlobalExceptionHandler` es el archivo clave porque traduce excepciones a HTTP.

Defensa: "El frontend no necesita interpretar stack traces; recibe JSON limpio."

### Archivos `strategy` y `payment`

Ya explicados en detalle, pero para repasar:

- `strategy`: variacion en calculo de precio.
- `payment`: variacion en validacion/procesamiento de pago.

---

## 22. Apendice Frontend Archivo Por Archivo

### `main.ts`

Arranca Angular:

```typescript
platformBrowserDynamic().bootstrapModule(AppModule)
```

Defensa: "Es el punto de entrada del frontend."

### `index.html`

Contiene:

```html
<app-root></app-root>
```

Angular reemplaza esa etiqueta por `AppComponent`.

### `environment.ts` y `environment.prod.ts`

Guardan `apiUrl`.

Defensa: "No escribo la URL del backend quemada en cada servicio. La centralizo por ambiente."

### `app.module.ts`

Declara componentes, importa modulos y registra interceptor.

Importante:

- `ReactiveFormsModule`: formularios reactivos.
- `HttpClientModule`: peticiones HTTP.
- `AppRoutingModule`: rutas.
- `provideHttpClient(withInterceptors(...))`: interceptor JWT.

### `app-routing.module.ts`

Define navegacion.

Ejemplo:

```typescript
{ path: 'usuarios', component: UsuarioListComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['ADMIN'] } }
```

Significa: solo autenticados y con rol ADMIN.

### `app.component.ts/html/css`

Controla layout global.

Si esta en `/login`, muestra solo login. Si no, muestra sidebar, navbar y contenido.

### Modelos TypeScript

Los archivos en `core/models` son interfaces y tipos.

Ejemplo:

```typescript
export interface Cliente {
  id: number;
  nombre: string;
}
```

Defensa: "TypeScript me permite tipar la respuesta del backend y evitar errores."

### Services Angular

Patron:

```typescript
@Injectable({ providedIn: 'root' })
export class XService {
  private api = `${environment.apiUrl}/...`;
  constructor(private http: HttpClient) {}
}
```

- `@Injectable`: Angular puede inyectarlo.
- `providedIn: 'root'`: instancia global.
- `HttpClient`: cliente HTTP.
- `environment.apiUrl`: ruta base.

#### `AuthService`

Hace login, logout y gestiona token/rol en localStorage.

#### `ClienteService`

Consume `/api/clientes`.

Metodos:

- listar.
- obtener.
- crear.
- actualizar.
- eliminar.
- cambiarEstado.

#### `CanchaService`

Consume `/api/canchas`.

#### `ReservaService`

Consume `/api/reservas`.

#### `PagoService`

Consume `/api/pagos`.

#### `DisponibilidadService`

Consume `/api/disponibilidad`.

#### `DashboardService`

Consume `/api/dashboard`.

#### `UsuarioService`

Consume `/api/usuarios`.

#### `http-error.util.ts`

Funcion reutilizable:

```typescript
extractHttpErrorMessage(error)
```

Busca mensaje en:

1. `error.error.message`
2. texto plano.
3. `error.message`
4. fallback.

Defensa: "Asi todos los componentes pueden mostrar errores claros del backend."

### Guards

#### `AuthGuard`

Si no hay token, redirige a login.

#### `RoleGuard`

Compara rol del usuario con roles permitidos de la ruta.

### Interceptor

`jwt.interceptor.ts`:

- lee token.
- clona request.
- agrega header Authorization.
- si llega 401, borra token y redirige login.

Defensa: "El usuario no tiene que agregar token manualmente; el interceptor lo hace para todas las peticiones."

### Componentes De Login

`LoginComponent`:

- formulario reactivo.
- email/password.
- llama `AuthService.login`.
- muestra error si credenciales fallan.
- redirige a dashboard si todo sale bien.

### Dashboard

`DashboardComponent`:

- llama `DashboardService.obtener`.
- muestra tarjetas y proximas reservas.

### Clientes

`ClienteListComponent`:

- lista clientes.
- muestra badge activo/inactivo.
- edita.
- elimina logicamente.
- reactiva cliente.

`ClienteFormComponent`:

- crea o actualiza.
- usa `@Input` para recibir cliente seleccionado.
- usa `@Output` para avisar que guardo.

### Canchas

`CanchaListComponent`:

- lista canchas.
- muestra badge por estado.
- permite editar/cambiar estado.

`CanchaFormComponent`:

- formulario reactivo.
- crea o actualiza cancha.

`DisponibilidadComponent`:

- selecciona cancha y fecha.
- consulta franjas.
- muestra disponible/no disponible.

### Reservas

`ReservaListComponent`:

- lista reservas.
- cancela.
- finaliza.
- contiene formulario.

`ReservaFormComponent`:

- carga clientes y canchas desde backend.
- selector de cancha muestra nombre y estado.
- si cancha no esta activa, deshabilita boton.
- muestra advertencia.
- al crear, muestra exito.
- si backend responde error, muestra caja roja.

Defensa importante:

"Aunque el frontend bloquea el boton, el backend tambien valida. No confio solo en Angular."

### Pagos

`PagoFormComponent`:

- registra pago por reserva.
- envia valor, tipo y referencia.
- backend confirma la reserva.

### Usuarios

`UsuarioListComponent`:

- lista usuarios.
- cambia estado.

`UsuarioFormComponent`:

- crea usuario con rol.
- solo accesible para ADMIN por guards/backend.

### Shared

`NavbarComponent`:

- boton salir.

`SidebarComponent`:

- menu.
- oculta usuarios si no es ADMIN.

`ConfirmDialogComponent`:

- componente reutilizable con inputs y outputs.

---

## 23. Guia Para Exponer En Orden

Puedes usar este orden en la sustentacion:

1. "Primero explico la arquitectura por capas."
2. "Luego muestro el dominio y los enums."
3. "Despues explico POO: herencia, encapsulamiento, interfaces y polimorfismo."
4. "Luego muestro repositorios y persistencia."
5. "Despues explico services, donde estan las reglas de negocio."
6. "Luego controllers, que exponen REST."
7. "Luego seguridad JWT."
8. "Finalmente Angular: servicios, guards, interceptor y pantallas."

Frase de cierre:

"ArenaReserve no es solo un CRUD. Tiene reglas de negocio reales: estados, reservas no solapadas, canchas activas, pagos con metodos distintos, calculo de precios polimorfico, disponibilidad recursiva, seguridad JWT y frontend conectado a API REST."
