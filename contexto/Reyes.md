# Prompt para Asistente de IA (Claude en Android Studio)

**Rol y Contexto:**
Actúa como mi Desarrollador Senior de Android (Kotlin) emparejado (Pair Programmer). Tienes acceso a los archivos de este proyecto ("Salud Contigo"). Te he adjunto un mockup (imagen) con el diseño final de 8 pantallas. 
Actualmente, el proyecto ya cuenta con la lógica funcional hasta la pantalla 3, pero la interfaz gráfica no coincide con este nuevo diseño.

**Objetivo Principal:**
Necesito que transformemos este proyecto para que la interfaz sea un clon exacto del mockup adjunto y agreguemos persistencia local con Room y autenticación con hardware.

**Plan de Acción Requerido (Por favor, confirma este plan antes de empezar a escribir código):**

### Paso 1: Configuración inicial y Dependencias
* Revisa mi archivo `build.gradle.kts` (app).
* Añade (o actualiza) las dependencias necesarias para: `androidx.biometric:biometric`, Room (con soporte para Coroutines/Flow) y Navigation Component.
* Extrae la paleta de colores del mockup (fondo `#0A3429`, tarjetas `#104739`, botones/acentos `#22997C`) y actualiza mi archivo de colores y el tema principal de la app.

### Paso 2: Capa de Datos (Room)
* Crea un paquete `data/local` o similar según mi estructura.
* Genera la entidad `UserEntity` (id, nombre, cédula/teléfono, edad, EPS) para gestionar la sesión.
* Genera la entidad `AppointmentEntity` (id_cita, nombre_doctor, especialidad, fecha, hora, modalidad, estado [Programada/Pasada]).
* Crea los DAOs (`UserDao`, `AppointmentDao`), la clase `AppDatabase` y el Repositorio correspondiente.

### Paso 3: Autenticación Biométrica (Pantalla 1)
* Revisa el código actual de la Pantalla 1 (Ingreso). Refactoriza su UI para que coincida con el mockup.
* Implementa `BiometricManager` y `BiometricPrompt` en el botón de huella/FaceID.
* Conecta el inicio de sesión exitoso biométrico con el rescate del usuario activo desde Room.

### Paso 4: Refactorización y Creación de UI (Pantallas 2 a 8)
* Refactoriza las pantallas 2 y 3 actuales para igualar el nuevo diseño.
* Crea un `SharedViewModel` para manejar el flujo de Agendar Cita (Especialidad -> Fecha/Hora -> Confirmación).
* Crea las pantallas 4 (Fecha y Hora), 5 (Confirmación) y 6 (Éxito). Al dar "Confirmar Cita" en la pantalla 5, inserta la cita en Room usando el repositorio creado en el Paso 2.
* Crea la pantalla 7 (Mis Citas) leyendo los datos de Room como un `Flow` para que se actualice en tiempo real, dividiendo entre "Próximas" y "Pasadas".
* Crea la pantalla 8 (Perfil) rescatando los datos del `UserEntity`.
* Asegúrate de que la navegación inferior (Bottom Navigation) funcione correctamente entre Inicio, Citas y Perfil.

---

**Instrucciones de Ejecución:**
No apliques todos los cambios a la vez. Confírmame que entiendes el alcance y los requerimientos, y luego **avancemos paso a paso**. Dime qué archivos vas a modificar o crear en el Paso 1, espero tu código/aplicación de cambios, y luego pasamos al Paso 2.