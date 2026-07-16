# Salud Contigo — Documentación técnica de la implementación

Este documento explica **a fondo** cómo funciona la app: arquitectura, cada pantalla, cómo se guardan los datos (Room), la biometría, la navegación y las decisiones técnicas detrás de cada pieza. Está pensado para poder explicar el proyecto de principio a fin.

---

## 1. Resumen del proyecto

**Salud Contigo** es una app Android (Kotlin + Vistas XML) para que un adulto mayor agende citas médicas en pocos pasos. Tiene 8 pantallas:

1. Ingreso (Login)
2. Inicio (Home)
3. Selecciona Especialidad
4. Fecha y Hora
5. Confirmar Cita
6. Éxito
7. Mis Citas
8. Mi Perfil

Usa **persistencia local real con Room** (no hay backend/servidor: todo vive en la base de datos SQLite del propio teléfono), **autenticación biométrica** (huella/Face ID) y **Navigation Component** para moverse entre pantallas.

---

## 2. Stack tecnológico

| Pieza | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | 2.2.10 |
| UI | Vistas XML + ViewBinding | — |
| Componentes visuales | Material Components (Material 3) | 1.13.0 |
| Build system | Android Gradle Plugin (AGP) | 9.2.1 |
| Persistencia | Room (runtime + ktx + compiler) | 2.8.4 |
| Procesamiento de anotaciones de Room | KSP (Kotlin Symbol Processing) | 2.2.10-2.0.2 |
| Navegación | Navigation Component (fragment-ktx + ui-ktx) | 2.7.7 |
| Biometría | androidx.biometric | 1.1.0 |
| Concurrencia | Kotlin Coroutines + Flow | 1.8.1 |
| ViewModel/Lifecycle | androidx.lifecycle | 2.8.7 |
| Listas | RecyclerView | 1.3.2 |

Todo esto está declarado en `gradle/libs.versions.toml` (el catálogo de versiones) y usado desde `app/build.gradle.kts`.

**Nota importante sobre KSP vs kapt:** normalmente Room usa `kapt` para generar código en tiempo de compilación. Aquí no se pudo usar `kapt` porque AGP 9.2.1 trae un soporte de Kotlin "incorporado" (built-in Kotlin) que es incompatible con el plugin clásico `org.jetbrains.kotlin.android` (que es lo que necesita `kapt`). La solución fue usar **KSP**, que sí es compatible con el Kotlin incorporado de AGP. Esto obligó también a:
- Agregar `android.disallowKotlinSourceSets=false` en `gradle.properties` (KSP agrega código generado de una forma que el Kotlin incorporado de AGP bloquea por defecto).
- Subir Room de 2.6.1 a 2.8.4, porque la versión vieja del compilador de Room tenía un bug con KSP + Kotlin 2.2.10 (error `unexpected jvm signature V`).

---

## 3. Arquitectura general: Single-Activity + Fragments

La app **no** usa "una Activity por pantalla" para todo. Se dividió así:

- **`LoginActivity`** y **`RegistroActivity`**: son Activities independientes, porque son las únicas 2 pantallas que **no** tienen la barra de navegación inferior (bottom nav).
- **`MainActivity`**: es la única Activity para *todo lo demás*. Dentro de ella vive un `NavHostFragment` que va cambiando entre 7 `Fragment`s según la pantalla, y una `BottomNavigationView` fija abajo (Inicio / Citas / Perfil).

¿Por qué así? Porque la barra inferior necesita mantenerse visible y sincronizada mientras cambias de pantalla (Inicio → Especialidad → Fecha/Hora → Confirmar), y eso es exactamente lo que resuelve el patrón "Single-Activity con Navigation Component": una sola Activity, varios Fragments intercambiables, un solo lugar donde vive la barra de navegación.

```
LoginActivity ──(si existe el usuario)──▶ MainActivity
     │                                         │
     └──▶ RegistroActivity (crear cuenta)      ├── NavHostFragment (nav_graph.xml)
                                                │     ├── HomeFragment
                                                │     ├── EspecialidadFragment
                                                │     ├── FechaHoraFragment
                                                │     ├── ConfirmacionFragment
                                                │     ├── ExitoFragment
                                                │     ├── MisCitasFragment
                                                │     └── PerfilFragment
                                                └── BottomNavigationView (Inicio/Citas/Perfil)
```

---

## 4. Estructura de paquetes (Kotlin)

```
com.example.saludcontigo
├── LoginActivity.kt          Pantalla de ingreso
├── RegistroActivity.kt       Pantalla de registro
├── MainActivity.kt           Host de Fragments + bottom nav
├── Sesion.kt                 Puntero a la sesión activa (SharedPreferences)
├── data/
│   ├── local/                 ← Room
│   │   ├── UserEntity.kt
│   │   ├── AppointmentEntity.kt
│   │   ├── EstadoCita.kt
│   │   ├── UserDao.kt
│   │   ├── AppointmentDao.kt
│   │   └── AppDatabase.kt
│   └── repository/            ← capa intermedia entre Room y la UI
│       ├── UserRepository.kt
│       └── AppointmentRepository.kt
├── ui/
│   ├── home/HomeFragment.kt
│   ├── booking/                ← flujo de 3 pasos para agendar
│   │   ├── BookingViewModel.kt
│   │   ├── Especialidades.kt
│   │   ├── EspecialidadFragment.kt
│   │   ├── FechaHoraFragment.kt
│   │   ├── ConfirmacionFragment.kt
│   │   └── ExitoFragment.kt
│   ├── miscitas/
│   │   ├── MisCitasFragment.kt
│   │   └── AppointmentAdapter.kt   (RecyclerView)
│   └── perfil/PerfilFragment.kt
└── util/                      ← utilidades sin estado, sin dependencias de Android UI
    ├── PasswordUtil.kt         Hash + verificacion de contrasenas (PBKDF2 + sal)
    └── BiometricKeyManager.kt  Una clave de Android Keystore por cedula, para huella por usuario
```

**Regla de capas:** los Fragments/Activities **nunca** llaman directo a un DAO de Room. Siempre pasan por un `Repository`. Esto es una buena práctica de arquitectura Android (separa "de dónde vienen los datos" de "cómo se muestran").

---

## 5. Persistencia de datos con Room (a fondo)

### 5.1 ¿Qué es Room y por qué se usa?

Room es la librería oficial de Android para trabajar con SQLite sin escribir SQL a mano. Tú describes tus tablas como clases Kotlin (`@Entity`), tus consultas como interfaces (`@Dao`), y Room genera todo el código de bajo nivel (cursores, `ContentValues`, etc.) en tiempo de compilación. Se eligió porque:
- Persiste los datos aunque se cierre la app (a diferencia de una lista en memoria).
- Permite consultas reactivas con `Flow`, así la UI se actualiza sola cuando cambian los datos.
- Es el estándar de la industria en Android para bases de datos locales.

### 5.2 Las dos tablas

**`UserEntity`** (tabla `usuarios`) — un registro por persona que se registra:

```kotlin
@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey val cedula: String,
    val nombre: String,
    val edad: Int?,
    val passwordHash: String,
    val passwordSalt: String,
    val huellaActiva: Boolean = false
)
```

- `passwordHash` / `passwordSalt`: nunca se guarda la contraseña en texto plano — se guarda un hash con sal (ver sección 6.1, `PasswordUtil`).
- `huellaActiva`: si esta cuenta activó el ingreso con huella desde Mi Perfil (ver sección 6.2). Por defecto `false` — la huella nunca reemplaza la contraseña hasta que la propia cuenta la activa explícitamente.

> El campo `eps` que existía antes se eliminó del proyecto (ya no se pide en Registro ni se muestra en Mi Perfil).

**`AppointmentEntity`** (tabla `citas`) — un registro por cita agendada:

```kotlin
@Entity(tableName = "citas")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userCedula: String,      // a qué usuario pertenece (relación lógica con "usuarios")
    val doctorNombre: String,
    val especialidad: String,
    val fechaMillis: Long,       // fecha en milisegundos, para poder ordenar/comparar
    val fechaTexto: String,      // fecha ya formateada para mostrar ("Martes, 15 de Julio...")
    val hora: String,
    val modalidad: String,
    val duracionMin: Int,
    val estado: String           // "PROXIMA" | "AGENDADA" | "PASADA" (ver EstadoCita)
)
```

`estado` se guarda como texto simple (no como un tipo enum de Room) para no necesitar un `TypeConverter`; el enum `EstadoCita` se usa solo en el código Kotlin y se convierte con `.name` / `valueOf(...)`.

### 5.3 Los DAOs (Data Access Object)

Un DAO es una interfaz donde cada método es una consulta SQL (Room la genera a partir de la anotación):

```kotlin
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM usuarios WHERE cedula = :cedula LIMIT 1")
    suspend fun getByCedula(cedula: String): UserEntity?

    @Query("UPDATE usuarios SET huellaActiva = :activa WHERE cedula = :cedula")
    suspend fun setHuellaActiva(cedula: String, activa: Boolean)
}

@Dao
interface AppointmentDao {
    @Insert
    suspend fun insert(appointment: AppointmentEntity): Long

    @Query("SELECT * FROM citas WHERE userCedula = :cedula ORDER BY fechaMillis ASC")
    fun getAllByUser(cedula: String): Flow<List<AppointmentEntity>>
}
```

Puntos clave:
- `suspend fun` → las operaciones de escritura/lectura puntual se ejecutan como corrutinas (no bloquean el hilo principal).
- `Flow<List<AppointmentEntity>>` → esto **no** es una función `suspend`, es un flujo reactivo: Room mantiene esta consulta "viva" y emite una nueva lista automáticamente cada vez que la tabla `citas` cambia (por ejemplo, al agendar una cita nueva). Por eso Home, Mis Citas y Perfil se actualizan solos sin tener que refrescar nada manualmente.

### 5.4 La base de datos (`AppDatabase`)

```kotlin
@Database(entities = [UserEntity::class, AppointmentEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null
        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "salud_contigo.db")
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instancia = it }
            }
    }
}
```

Es un **singleton**: sin importar cuántas pantallas la usen, solo se abre un archivo `salud_contigo.db` una sola vez en toda la vida de la app (abrir la base de datos repetidas veces es costoso y puede causar bugs).

**`version = 2`**: subió desde `1` al agregar `passwordHash`/`passwordSalt`/`huellaActiva` y quitar `eps` de `UserEntity`. Como el proyecto todavía no tiene usuarios reales en producción, se usa `fallbackToDestructiveMigration(dropAllTables = true)` en vez de escribir una migración manual: si Room detecta un esquema viejo en el dispositivo, simplemente borra las tablas y las vuelve a crear con el esquema nuevo (los datos de prueba que ya estaban instalados se pierden, pero no hace falta escribir un `Migration` a mano para un cambio de esquema en desarrollo).

### 5.5 Los repositorios

Son la "puerta de entrada" que usan las pantallas. Ocultan que por debajo hay un DAO de Room:

```kotlin
class UserRepository(context: Context) {
    private val userDao = AppDatabase.obtener(context).userDao()

    suspend fun registrar(cedula: String, nombre: String, edad: Int?, passwordHash: String, passwordSalt: String) { ... }
    suspend fun buscarPorCedula(cedula: String): UserEntity? = userDao.getByCedula(cedula)

    suspend fun validarCredenciales(cedula: String, password: String): UserEntity? {
        val usuario = userDao.getByCedula(cedula) ?: return null
        return if (PasswordUtil.verificar(password, usuario.passwordSalt, usuario.passwordHash)) usuario else null
    }

    suspend fun activarHuella(cedula: String) = userDao.setHuellaActiva(cedula, true)
    suspend fun desactivarHuella(cedula: String) = userDao.setHuellaActiva(cedula, false)
}

class AppointmentRepository(context: Context) {
    private val appointmentDao = AppDatabase.obtener(context).appointmentDao()
    fun obtenerCitasDe(cedula: String): Flow<List<AppointmentEntity>> = appointmentDao.getAllByUser(cedula)
    suspend fun agendar(...): Long = appointmentDao.insert(AppointmentEntity(..., estado = EstadoCita.PROXIMA.name))
}
```

### 5.6 ¿En qué momento se escribe/lee en Room? (flujo real)

| Acción del usuario | Qué pasa en Room |
|---|---|
| Se registra (`RegistroActivity`) | `UserRepository.registrar(...)` → `INSERT` en `usuarios` (con `passwordHash`/`passwordSalt` ya calculados) |
| Ingresa con cédula + contraseña (`LoginActivity`) | `UserRepository.validarCredenciales(...)` → `SELECT` en `usuarios` + verificación del hash |
| Ingresa con huella | `UserRepository.buscarPorCedula(...)` para revisar `huellaActiva`, y después de que `BiometricPrompt` confirma identidad con la clave de Keystore de esa cuenta |
| Activa/desactiva la huella (Mi Perfil) | `UserRepository.activarHuella(...)` / `desactivarHuella(...)` → `UPDATE huellaActiva` en `usuarios` |
| Abre Inicio, Mis Citas o Perfil | `AppointmentRepository.obtenerCitasDe(cedula)` → `SELECT` reactivo (`Flow`) sobre `citas` |
| Toca "Confirmar Cita" | `AppointmentRepository.agendar(...)` → `INSERT` en `citas` |

Ninguna otra pantalla del flujo de reserva (Especialidad, Fecha/Hora) escribe en Room todavía — esos pasos solo guardan la selección temporal en `BookingViewModel` (ver sección 7). La única escritura real ocurre al confirmar.

---

## 6. Autenticación: contraseña y huella por usuario

### 6.1 Contraseña (`util/PasswordUtil.kt`)

Ya no existe un "ingreso solo con cédula": Login y Registro ahora piden contraseña, y Registro además pide confirmarla. La contraseña **nunca se guarda en texto plano**: se guarda un hash con sal usando `PBKDF2WithHmacSHA1` (API estándar de `javax.crypto`, no se agregó ninguna librería nueva):

```kotlin
object PasswordUtil {
    fun generarSalt(): String                                    // 16 bytes aleatorios (SecureRandom), en Base64
    fun hash(password: String, salt: String): String              // PBKDF2, 10 000 iteraciones, 256 bits, en Base64
    fun verificar(password: String, salt: String, hashGuardado: String): Boolean
}
```

- **Registro**: genera un `salt` nuevo, calcula `hash(password, salt)` y guarda ambos en `UserEntity`.
- **Login**: vuelve a calcular el hash con el `salt` guardado de esa cédula y lo compara contra `passwordHash` (`UserRepository.validarCredenciales`).
- Única regla de validación: mínimo 4 caracteres, sin exigir mayúsculas/números/símbolos — se prioriza la baja fricción para adultos mayores (ver `BRAND.md`).
- El botón de "ojito" (`btnTogglePassword`) alterna `EditText.inputType` entre `TYPE_TEXT_VARIATION_PASSWORD` y `TYPE_TEXT_VARIATION_VISIBLE_PASSWORD`, y cambia el ícono (`ic_eye` / `ic_eye_off`) y el `contentDescription`.

### 6.2 Huella por usuario (`util/BiometricKeyManager.kt`)

Se usa `androidx.biometric` (`BiometricManager` + `BiometricPrompt`), la API recomendada por Google para huella/reconocimiento biométrico, pero ahora **ligada a la cuenta**, no a "la última sesión guardada" como antes.

**El problema que resuelve:** Android no permite que una app distinga *cuál* dedo enrolado tocó el sensor — el sistema operativo solo confirma "hay una huella válida en este equipo". Así que la app no puede preguntarle al sensor "¿es la huella de Juan?". Lo que sí puede hacer (y es lo que se implementó) es exigir que:
1. La cédula escrita en el campo de Login corresponda a una cuenta que existe, **y**
2. Esa cuenta haya activado explícitamente el interruptor "Ingresar con huella" en Mi Perfil.

Si una segunda persona se registra con otra cédula en el mismo teléfono y nunca activa su propio interruptor de huella, no puede entrar tocando el sensor — el botón de huella de Login revisa `usuario.huellaActiva` antes de mostrar el prompt, y si es `false` avisa que esa cuenta no lo tiene activado.

**Cómo se liga técnicamente a la cuenta — una clave de Android Keystore por cédula:**

```kotlin
object BiometricKeyManager {
    fun crearClave(cedula: String)         // AES/GCM en AndroidKeyStore, alias = "huella_<cedula>"
    fun obtenerCipher(cedula: String): Cipher?
    fun eliminarClave(cedula: String)
}
```

La clave se crea con `setUserAuthenticationRequired(true)` (cada operación criptográfica exige biometría fresca) y `setInvalidatedByBiometricEnrollment(true)` (si cambian las huellas registradas en el equipo — se agrega o se borra una — la clave se invalida sola). El `Cipher` de esa clave se envuelve en un `BiometricPrompt.CryptoObject` y se exige `BIOMETRIC_STRONG` (obligatorio en Android para prompts basados en criptografía). Que `onAuthenticationSucceeded` se dispare ya prueba que la huella correcta desbloqueó **esa** clave puntual — no una clave genérica de la app.

**Flujo para activar la huella (Mi Perfil → interruptor "Ingresar con huella"):**
1. Revisa `BiometricManager.canAuthenticate(BIOMETRIC_STRONG)` — si el equipo no tiene sensor, avisa y el interruptor vuelve a apagado.
2. `BiometricKeyManager.crearClave(cedula)` + arma el `Cipher`.
3. Muestra el `BiometricPrompt` para confirmar que la huella real funciona.
4. Si es exitoso → `UserRepository.activarHuella(cedula)` (`huellaActiva = true`). Si falla/cancela, el interruptor se revierte a apagado.
5. Al desactivarlo: `UserRepository.desactivarHuella(cedula)` + `BiometricKeyManager.eliminarClave(cedula)` (se borra la clave del Keystore).

**Flujo para ingresar con huella (`LoginActivity.intentarIngresoBiometrico()`):**
1. Exige que el campo de cédula tenga texto (ya no depende de `Sesion.obtenerCedula`, que solo apuntaba a la última sesión).
2. Busca esa cédula en Room; si no existe o `huellaActiva == false`, avisa que esa cuenta no tiene la huella activada.
3. Arma el `Cipher` con `BiometricKeyManager.obtenerCipher(cedula)`. Si el equipo invalidó la clave (`KeyPermanentlyInvalidatedException`, porque cambiaron las huellas registradas), se desactiva `huellaActiva` automáticamente y se pide reactivarla desde Mi Perfil.
4. Si el `Cipher` es válido, se muestra el `BiometricPrompt`; al autenticar con éxito, `Sesion.iniciarSesion(cedula)` y se entra a `MainActivity`.

Permiso necesario en `AndroidManifest.xml`: `android.permission.USE_BIOMETRIC` (ya estaba declarado).

---

## 7. Navigation Component y el flujo de reserva

### 7.1 El grafo de navegación

`res/navigation/nav_graph.xml` declara los 7 destinos (Home, Especialidad, FechaHora, Confirmacion, Exito, MisCitas, Perfil) dentro de `MainActivity`. La navegación entre ellos se hace así en el código:

```kotlin
findNavController().navigate(R.id.especialidad)
```

No fue necesario declarar `<action>` en el XML: `NavController.navigate(id)` puede ir directo a cualquier destino del grafo por su id, sin necesitar una acción explícita.

### 7.2 `BookingViewModel`: el estado que se comparte entre pasos

Especialidad → Fecha/Hora → Confirmación son 3 pantallas distintas (3 Fragments), pero necesitan compartir la especialidad elegida, la fecha y la hora. Para esto se usa un `ViewModel` **con alcance al grafo de navegación** (`navGraphViewModels`):

```kotlin
private val bookingViewModel: BookingViewModel by navGraphViewModels(R.id.nav_graph)
```

Esto significa: **todas** las pantallas que pidan este ViewModel dentro del mismo `nav_graph` reciben la **misma instancia**, y esa instancia vive mientras el grafo esté activo (en la práctica, mientras la app siga abierta). Así, seleccionar "Cardiología" en el paso 1 se ve reflejado automáticamente en el paso 3 sin pasar datos manualmente entre Fragments (sin `Bundle`, sin `arguments`).

`BookingViewModel` guarda simplemente: `especialidad`, `doctor`, `fechaMillis`, `fechaTexto`, `hora`, `modalidad`, `duracionMin`. Son variables simples (no `LiveData`) porque cada pantalla las lee/escribe en un momento puntual (al tocar un botón), no necesita observarlas en tiempo real.

### 7.3 La barra de navegación inferior

`MainActivity` sincroniza manualmente qué ítem se ve "activo" según en qué pantalla estás, con `addOnDestinationChangedListener`:

- En Especialidad, Fecha/Hora, Confirmación y Mis Citas → se resalta "Citas" (aunque esas 3 primeras no sean el destino `misCitas` en sí, visualmente pertenecen a "agendar una cita").
- En Perfil → se resalta "Perfil".
- En Éxito → la barra se **oculta por completo** (esa pantalla es una confirmación final, no tiene sentido navegar desde ahí salvo con su propio botón "Volver al Inicio").

Esto reproduce exactamente el comportamiento del mockup original de diseño.

---

## 8. Detalle pantalla por pantalla

### 1. Login (`LoginActivity` + `activity_login.xml`)
- Campos: cédula/teléfono y **contraseña** (con botón de ojito para mostrarla/ocultarla).
- **Ingresar** → `UserRepository.validarCredenciales(cedula, password)` → si coincide, `Sesion.iniciarSesion(cedula)` y navega a `MainActivity`; si no, mensaje de error.
- **Huella / Face ID** → ver sección 6.2. Ahora exige que el campo de cédula tenga texto y que esa cuenta tenga `huellaActiva = true`.
- **¿No tienes cuenta? Regístrate** → abre `RegistroActivity` (este enlace no estaba en el mockup original, se agregó porque sin él sería imposible crear el primer usuario).

### 2. Registro (`RegistroActivity` + `activity_registro.xml`)
- Campos: nombre, cédula, edad, **contraseña** y **confirmar contraseña** (cada una con su propio botón de ojito).
- **Registrarme** → valida que nombre, cédula y contraseña no estén vacíos, que la contraseña tenga al menos 4 caracteres y que coincida con la confirmación → genera `salt`/`hash` con `PasswordUtil` → `UserRepository.registrar(...)` (INSERT en Room) → `Sesion.iniciarSesion(...)` → vuelve al Login para que la persona entre con su cédula y contraseña.
- La huella **no** se activa aquí: se activa después, desde el interruptor en Mi Perfil (sección 9), para no alargar el formulario de registro.

### 3. Inicio (`HomeFragment` + `fragment_home.xml`)
- Saludo con el primer nombre del usuario (consulta puntual a Room).
- Tarjeta de "próxima cita": primera cita con `estado == PROXIMA`, leída de forma reactiva (`Flow`).
- Dos tarjetas de acción: **Agendar Cita** (→ `especialidad`) y **Mis Citas** (→ `misCitas`).
- Estadísticas: total de citas y citas de este año (calculadas contando/filtrando la lista que llega del `Flow`).

### 4. Selecciona Especialidad (`EspecialidadFragment` + `fragment_especialidad.xml`)
- 4 tarjetas fijas (Cardiología, Medicina General, Neumología, Neurología), cada una con su médico asignado (definidas en `Especialidades.kt`, una lista fija en el código — no vienen de Room, son datos de catálogo, no datos de usuario).
- Al tocar una tarjeta se resalta y se guarda `especialidad`/`doctor` en `BookingViewModel`.
- **Continuar a Fecha y Hora** → navega a `fechaHora`.

### 5. Fecha y Hora (`FechaHoraFragment` + `fragment_fecha_hora.xml`)
- Calendario generado con `java.util.Calendar` (mes real, no inventado), con flechas para cambiar de mes.
- Franja de horarios fijos (7:00 AM a 4:00 PM) mostrados como `Chip`s de selección única.
- **Continuar a Confirmar** → guarda `fechaMillis`, `fechaTexto` (ya formateada en español) y `hora` en `BookingViewModel` → navega a `confirmacion`.

### 6. Confirmar Cita (`ConfirmacionFragment` + `fragment_confirmacion.xml`)
- Muestra el resumen completo leyendo todo desde `BookingViewModel`.
- **Confirmar Cita** → llama a `AppointmentRepository.agendar(...)` dentro de una corrutina (`lifecycleScope.launch`) → esto es la **única inserción real de una cita en Room** → navega a `exito`.

### 7. Éxito (`ExitoFragment` + `fragment_exito.xml`)
- Ícono de check con una animación de "rebote" (`OvershootInterpolator`).
- Resumen de la cita recién creada.
- **Volver al Inicio** → usa `NavOptions` con `popUpTo(home)` para **limpiar** del back stack todas las pantallas del flujo de reserva (así, si el usuario presiona "atrás" en Inicio, no vuelve a ver Confirmación/Fecha/Especialidad de la cita ya agendada).

### 8. Mis Citas (`MisCitasFragment` + `fragment_mis_citas.xml`)
- Dos pestañas: Próximas (`estado != PASADA`) y Pasadas (`estado == PASADA`), filtradas en memoria sobre la lista que llega del `Flow` de Room.
- Lista con `RecyclerView` + `AppointmentAdapter` (cada fila es un `item_cita.xml`).

### 9. Mi Perfil (`PerfilFragment` + `fragment_perfil.xml`)
- Datos del usuario (`UserEntity`): nombre, cédula, edad.
- Estadísticas (total de citas, citas de este año, próxima cita) calculadas desde el mismo `Flow` de citas.
- **Menú hamburguesa** (`btnMenu`, ícono ☰ junto al título): abre un `PopupMenu` (`res/menu/menu_perfil.xml`) con la opción **Cerrar sesión**, que pide confirmación con un `AlertDialog` y, al confirmar, llama a `Sesion.cerrarSesion(...)` y regresa a `LoginActivity` limpiando el back stack (`FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK`).
- **Interruptor "Ingresar con huella"** (`rowHuella`, usa `item_perfil_opcion_switch.xml` en vez de `item_perfil_opcion.xml`): activa/desactiva la huella para esta cuenta — ver sección 6.2 para el detalle completo del flujo con `BiometricKeyManager`.
- Opciones (Notificaciones, Historial Médico, Cuidador de Apoyo): por ahora son solo visuales — al tocarlas muestran un aviso de "próximamente", no están conectadas a ninguna función real todavía.

---

## 9. Sistema de diseño (tema oscuro)

Todo el look & feel sigue `BRAND.md`: paleta oscura (`bg_app #0A3429`, tarjetas `bg_card #104739`, acento `primary #22997C`), tipografía mínima de 14sp/18sp y áreas táctiles de 60dp mínimo (pensado para adultos mayores, con textos grandes y alto contraste aunque el fondo sea oscuro). Los `drawable` reutilizables (`bg_card.xml`, `bg_card_selected.xml`, `bg_pill.xml`, `bg_circle_primary.xml`, etc.) evitan repetir estilos en cada pantalla.

---

## 10. Cómo compilar y ejecutar

```bash
./gradlew :app:assembleDebug     # compila el APK de depuración
./gradlew :app:installDebug      # lo instala en un emulador/dispositivo conectado
```

## 11. Limitaciones actuales (para tener en cuenta si preguntan)

- No hay backend: todos los datos son locales al dispositivo (si desinstalas la app, se pierden).
- Las opciones de Perfil (Notificaciones, Historial, Cuidador) son solo visuales.
- Los médicos y especialidades son un catálogo fijo en el código, no se pueden agregar nuevos desde la app.
- No hay lógica para marcar automáticamente una cita como "Pasada" cuando su fecha ya venció (el estado se fija una sola vez al crearla, como `PROXIMA`).
- **Límite real de la huella por usuario**: Android no expone una forma de saber *cuál* dedo enrolado tocó el sensor — solo confirma "hay una huella válida en este equipo". La app liga el ingreso biométrico a la cuenta escrita en el campo de cédula y a si esa cuenta activó su propio interruptor de huella (con una clave de Keystore por cédula, ver sección 6.2), pero **no puede impedir** que dos cuentas distintas, registradas en el mismo teléfono, ambas con la huella activada, entren usando el mismo dedo físico — esa distinción no existe en la plataforma.
- Al subir `AppDatabase` de `version = 1` a `2` se usó `fallbackToDestructiveMigration(dropAllTables = true)` en vez de una migración manual: cualquier dato de prueba que ya estuviera instalado en un emulador/dispositivo se perdió con esta actualización.
