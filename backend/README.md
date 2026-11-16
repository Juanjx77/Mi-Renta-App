# Mi Renta – Backend

Monolito modular en **Kotlin + Spring Boot 3** con **arquitectura hexagonal**, un módulo por bounded context.
Implementa los contextos **Identidad y Perfil** y **Declaración** sobre **PostgreSQL** (ver el documento de arquitectura, ADR-001, 002, 003, 007, 009 y 010).

## Estructura

```
src/main/kotlin/com/mirenta/
  identidad/
    dominio/        Perfil
    aplicacion/     puertos PerfilRepository y VerificadorIdentidad; casos de uso ConsultarPerfil, GuardarPerfil
    adaptadores/    web (PerfilController, FiltroAutenticacion), persistencia (JPA/PostgreSQL), firebase (verificadores)
  declaracion/
    dominio/        Declaracion (agregado), Entradas, CalculadoraRenta, ReglasTributarias (Strategy), Reglas2025, ReglasFactory
    aplicacion/     puerto DeclaracionRepository; casos de uso Consultar/Guardar/SimularDeclaracion, ConsultarReglas
    adaptadores/    web (DeclaracionController + DTOs), persistencia (JPA/PostgreSQL)
  compartido/       UsuarioActual, manejo de errores, OpenAPI
```

Regla de dependencias: `adaptadores → aplicacion → dominio`. El dominio es Kotlin puro (sin Spring ni JPA):
`CalculadoraRenta` y `Reglas2025` son el mismo código de `app/.../domain/` de la app Android, con las reglas
inyectadas como Strategy en vez de un `object` fijo. Los casos de uso no tienen anotaciones de Spring: se
ensamblan en `ConfiguracionDeclaracion` y `ConfiguracionIdentidad`.

## Recorrido de una petición

`PUT /api/v1/declaraciones/2025`

1. `FiltroAutenticacion` (adaptador primario) toma el token `Bearer` y llama al puerto `VerificadorIdentidad`.
   El adaptador `FirebaseVerificadorIdentidad` valida firma, emisor, audiencia y expiración; deja el uid en la petición.
2. `DeclaracionController` (adaptador primario) valida el JSON y lo traduce a `Entradas` del dominio.
3. `GuardarDeclaracion` (caso de uso) carga la declaración por el puerto `DeclaracionRepository`, compara la versión
   leída por el cliente (concurrencia optimista) y guarda.
4. `PostgresDeclaracionRepository` (adaptador secundario) mapea a `DeclaracionEntity` (JPA, con `@Version`).
5. El caso de uso devuelve la declaración con `Declaracion.calcular()`, que usa la estrategia `ReglasFactory.para(2025)`.
6. Si otro dispositivo guardó antes → `ConflictoVersionException` → `ManejadorErrores` responde **409**.

## API (`/api/v1`, todas requieren `Authorization: Bearer <token>`)

| Método | Ruta | Qué hace |
|---|---|---|
| GET / PUT | `/perfil` | Consultar (404 si no existe) / crear o actualizar el perfil |
| GET | `/declaraciones/{anio}` | Declaración guardada con su cálculo (404 si no existe) |
| PUT | `/declaraciones/{anio}` | Guarda `{version, entradas}` y recalcula; 409 si la versión no coincide |
| POST | `/declaraciones/{anio}/simulacion` | Calcula un escenario sin guardar |
| GET | `/reglas/{anio}` | UVT, topes y tarifa del año (configuración guiada por servidor) |

Errores: `{"codigo": "...", "mensaje": "..."}`. Documentación interactiva: `/swagger-ui.html`.

## Autenticación

| `AUTH_MODO` | Comportamiento |
|---|---|
| `demo` (por defecto) | Acepta como uid un texto plano (`Bearer usuario-demo`, útil en Swagger) o un token de Firebase **sin verificar la firma**. Solo para desarrollo y la demo. |
| `firebase` | Verifica el ID token de Firebase. Requiere `FIREBASE_PROJECT_ID` (está en `google-services.json` → `project_info.project_id`). |

## Correr en local

Opción A, con Docker:

```
cd backend
docker compose up --build
```

Opción B, sin Docker (JDK 21 y un PostgreSQL con base `mirenta`, usuario y clave `mirenta`):

```
cd backend
./gradlew bootRun
```

Luego abrir http://localhost:8080/swagger-ui.html → **Authorize** → escribir `usuario-demo`.
Desde el emulador de Android la URL es `http://10.0.2.2:8080/`.

Pruebas: `./gradlew test` (cálculo tributario, casos de uso con repositorio en memoria y API de punta a punta con H2).

## Desplegar gratis (Render + Neon)

1. **Base de datos en [Neon](https://neon.tech)** (plan gratuito): crear un proyecto y copiar la cadena de conexión,
   del tipo `postgresql://USUARIO:CLAVE@HOST/neondb?sslmode=require`.
2. **API en [Render](https://render.com)** (plan gratuito): New → Web Service → conectar este repo de GitHub.
   - Root Directory: `backend`
   - Runtime: Docker
   - Instance type: Free
   - Variables de entorno:
     - `DATABASE_URL` = `jdbc:postgresql://HOST/neondb?sslmode=require` (la de Neon, cambiando `postgresql://USUARIO:CLAVE@` por `jdbc:postgresql://`)
     - `DATABASE_USER` = USUARIO
     - `DATABASE_PASSWORD` = CLAVE
     - `AUTH_MODO` = `demo` (o `firebase` + `FIREBASE_PROJECT_ID`)
   - Health Check Path: `/actuator/health`
3. La URL queda como `https://<nombre>.onrender.com/swagger-ui.html`.

En el plan gratuito, Render apaga el servicio tras 15 minutos sin tráfico y el primer arranque tarda cerca de un minuto:
**ábranlo unos minutos antes de la sustentación.** Flyway crea las tablas al arrancar.
