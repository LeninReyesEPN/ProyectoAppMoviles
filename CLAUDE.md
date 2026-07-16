# Salud Contigo (versión XML) — Guía para Claude

## Regla principal de diseño

Antes de modificar cualquier pantalla, layout o archivo de tema, lee `BRAND.md` y aplica las reglas definidas ahí. Este proyecto tiene un manual de marca estricto pensado para adultos mayores (65+). Desde julio 2026 la marca usa **tema oscuro** (ver `BRAND.md` y el mockup en `contexto/`).

## Stack

- Android + **Vistas (XML)** con el editor visual de arrastrar componentes (Layout Editor)
- Kotlin + ViewBinding
- Material Components (Material 3)
- Room (persistencia local), Navigation Component, `androidx.biometric`
- **Login/Registro** son Activities independientes (no llevan bottom nav).
- **Todo lo demás** (Inicio, flujo de reserva, Mis Citas, Perfil) vive dentro de una sola `MainActivity` que hospeda un `NavHostFragment` + `BottomNavigationView`; cada pantalla es un `Fragment`, no una Activity.

> Esta es la versión basada en XML del proyecto. La versión original con Jetpack Compose está en `ProyectoAppMoviles/`.

## Archivos clave de diseño

| Archivo | Propósito |
|---|---|
| `res/values/colors.xml` | Colores de la paleta — deben coincidir con `BRAND.md` |
| `res/values/dimens.xml` | Tamaños táctiles, radios y escala tipográfica |
| `res/values/themes.xml` | Tema Material 3 (oscuro) |
| `res/values/strings.xml` | Todos los textos de la UI (sin jerga técnica) |
| `res/navigation/nav_graph.xml` | Grafo de navegación de `MainActivity` |
| `res/layout/activity_*.xml` | Pantallas fuera del nav graph (Login, Registro, Main) |
| `res/layout/fragment_*.xml` | Pantallas dentro de `MainActivity` |

## Pantallas

| Pantalla | Tipo | Layout | Navega a |
|---|---|---|---|
| `LoginActivity` (inicio) | Activity | `activity_login.xml` | `MainActivity` / `RegistroActivity` |
| `RegistroActivity` | Activity | `activity_registro.xml` | `LoginActivity` |
| `MainActivity` | Activity (host) | `activity_main.xml` | contiene el `NavHostFragment` |
| `HomeFragment` | Fragment | `fragment_home.xml` | Especialidad / Mis Citas |
| `EspecialidadFragment` | Fragment | `fragment_especialidad.xml` | Fecha y Hora |
| `FechaHoraFragment` | Fragment | `fragment_fecha_hora.xml` | Confirmación |
| `ConfirmacionFragment` | Fragment | `fragment_confirmacion.xml` | Éxito |
| `ExitoFragment` | Fragment (sin bottom nav) | `fragment_exito.xml` | Inicio |
| `MisCitasFragment` | Fragment | `fragment_mis_citas.xml` | — |
| `PerfilFragment` | Fragment | `fragment_perfil.xml` | — |

## Capa de datos

- `data/local`: `UserEntity`, `AppointmentEntity`, `UserDao`, `AppointmentDao`, `AppDatabase` (Room).
- `data/repository`: `UserRepository`, `AppointmentRepository` — únicos puntos de acceso a Room desde la UI.
- `Sesion.kt`: solo guarda un puntero (cédula) al usuario con sesión activa en `SharedPreferences`; los datos reales viven en Room.

## Restricciones de diseño (resumen rápido)

- Área táctil mínima: **60dp** (`@dimen/touch_min`)
- Texto body mínimo: **18sp** (`@dimen/texto_body`)
- Color primario: **#22997C** (`@color/primary`)
- Fondo de pantalla: **#0A3429** (`@color/bg_app`)
- Texto principal: **#FFFFFF** (`@color/text_primary`)
- Border radius de tarjetas/botones: **16–20dp**
- Sin jerga técnica en los textos de la UI
