# Salud Contigo — Manual de Marca

**Producto:** App Móvil Android
**Usuarios objetivo:** Adultos mayores (65+ años)
**Equipo:** Grupo 4 — Fabián Simbaña, Brayan Ortiz, Lenin Reyes, Ozzy Loachamín, Jorge Bósquez

---

## Filosofía de Diseño

El diseño busca reducir el estrés tecnológico y evitar errores. Tres principios rectores:

| Principio | Descripción |
|---|---|
| **Un paso a la vez** | Mostrar solo la información estrictamente necesaria en cada pantalla. Reducir carga cognitiva. |
| **Alta Legibilidad** | Textos grandes, contrastes altos y botones masivos para superar problemas visuales y motrices. |
| **Seguridad Continua** | Lenguaje natural y confirmaciones detalladas antes de agendar para dar tranquilidad al usuario. |

> **Nota de versión:** a partir de julio 2026 la app adopta un **tema oscuro** como nueva identidad visual (ver mockup en `contexto/`). Se mantienen los tres principios de arriba: los tamaños de texto y áreas táctiles del mockup se ajustaron hacia arriba para no sacrificar legibilidad en usuarios 65+.

---

## Sistema de Color

### Paleta Principal (tema oscuro)

| Token | HEX | Uso |
|---|---|---|
| `BgDarkest` | `#061D18` | Fondo de pantallas de estado especial (splash, éxito) |
| `BgApp` | `#0A3429` | Fondo principal de todas las pantallas |
| `BgCard` | `#104739` | Tarjetas, filas de lista, inputs de calendario/horarios sin seleccionar |
| `BgCardSelected` | `#1B5B4A` | Tarjetas/opciones seleccionadas (especialidad, día, horario activos) |
| `BgInput` | `#08201A` | Fondo de campos de texto |
| `Primary` | `#22997C` | Color principal de marca — botones primarios, ítem activo de navegación |
| `PrimaryHover` | `#2CBDA9` | Estados pressed/hover de botones primarios |
| `Accent` | `#00F2C2` | Acentos puntuales (línea de progreso, ícono de marca) — uso decorativo, no en texto de cuerpo |
| `TextPrimary` | `#FFFFFF` | Todo texto principal sobre fondo oscuro |
| `TextSecondary` | `#A3B8B5` | Subtítulos, descripciones, placeholders, metadatos |
| `Danger` | `#FF4B4B` | Estados de error, ícono de Cardiología |
| `Border` | `#1F4A3D` | Bordes sutiles de tarjetas e inputs |

### Colores de acento por especialidad médica

Sobre fondo oscuro se usa un círculo de ícono con fondo translúcido del color de acento; el texto siempre va en `TextPrimary` (`#FFFFFF`) para máxima legibilidad.

| Especialidad | Acento (ícono) |
|---|---|
| Cardiología | `#FF4B4B` (rojo, `Danger`) |
| Medicina General | `#00F2C2` (`Accent`) |
| Neumología | `#22997C` (`Primary`) |
| Neurología | `#B48CE0` (morado) |
| Otras / Default | `#A3B8B5` (`TextSecondary`) |

---

## Tipografía

**Fuente:** Roboto (sistema Android) — equivalente a "Plus Jakarta Sans" del mockup, no disponible nativamente en Android.

| Rol | Tamaño | Peso | Uso |
|---|---|---|---|
| H1 / Título de pantalla | 24sp | Bold (700) | "Salud Contigo", "Selecciona Especialidad" |
| H2 / Subtítulo | 20sp | Medium (500) | "¿Qué deseas hacer?", nombres de doctores |
| Body / Texto general | 18sp | Regular (400) | Fechas, descripciones, instrucciones |
| Label pequeño | 14sp | Regular (400) | Etiquetas de campos, metadatos, badges de estado |

> **Regla:** Nunca usar texto menor a 14sp. El tamaño mínimo de cuerpo de texto para el usuario final es 18sp. El mockup original usa textos de hasta 10–13px para metadatos; en la app Android estos se redondean a 14sp como mínimo.

---

## Componentes Interactivos

### Botones

| Tipo | Fondo | Texto | Sombra |
|---|---|---|---|
| Primario | `#22997C` | Blanco, 600 | `rgba(0,0,0,0.4)` elevation 8dp |
| Secundario / Outline | `#104739` | Blanco, normal, 500 | Borde `#1F4A3D` 2dp |

- **Altura mínima:** 60dp (área táctil)
- **Border radius:** 16dp (botones normales), 20dp (botones grandes de home)
- **Padding horizontal:** 32dp
- **Tipografía:** 18sp, Font Weight 600

### Inputs / Campos de Texto

- Fondo: `#08201A`
- Borde: 2dp solid `#1F4A3D`
- Border radius: 16dp
- Padding: 20dp
- Tipografía: 18sp, texto blanco, placeholder `TextSecondary`

### Tarjetas (Cards)

- Fondo: `#104739` (seleccionada: `#1B5B4A`)
- Border radius: 16–20dp
- Sombra: `rgba(0,0,0,0.4)` elevation 4dp
- Borde de acento cuando aplica (tarjetas de especialidad/estado): 2dp solid `Primary` o color de acento de la especialidad

### Área táctil mínima

**60dp × 60dp** en todos los elementos interactivos. Sin excepción.

---

## Espaciado y Layout

| Contexto | Valor |
|---|---|
| Padding de pantalla (horizontal) | 16–24dp |
| Gap entre tarjetas en grid | 16–24dp |
| Separación entre secciones | 32–48dp |
| Margen entre ícono y texto en botón | 12dp |

---

## Navegación

- **Bottom Navigation Bar** con 3 ítems: Inicio, Citas, Perfil — visible en Inicio, Especialidad, Fecha/Hora, Confirmación, Mis Citas y Perfil.
- No se muestra en Login/Registro ni en la pantalla de Éxito (confirmación final de cita).
- Ítem activo en color `Primary` (`#22997C`), ícono + label.
- Ítem inactivo en `TextSecondary` (`#A3B8B5`).
- Íconos grandes (~24dp), labels visibles siempre.

---

## Tono de Comunicación

La voz de la app es **empática, directa y sin jerga técnica**.

### Así habla Salud Contigo
- "¡Cita agendada con éxito! Te enviaremos un recordatorio a tu teléfono."
- "¿Es correcta esta información?"
- "Ingresa con tu Huella Digital / Face ID."
- "¡Hola, [nombre]! ¿Qué necesitas hoy?"

### Así NO habla
- "El registro ha sido insertado en la base de datos."
- "Por favor, verifique el input del formulario anterior."
- "Autenticación biométrica requerida."
- "Error 404 — recurso no encontrado."

**Regla:** Si el mensaje suena a software o sistema, reescríbelo en lenguaje humano y positivo.

---

## Estructura de Pantallas

| # | Pantalla | Descripción |
|---|---|---|
| 1 | **Ingreso** | Logo + campo único de cédula/teléfono + botón "Ingresar" + Huella/Face ID. Sin bottom nav. |
| 2 | **Inicio** | Saludo personalizado, tarjeta de próxima cita, dos tarjetas de acción grandes ("Agendar Cita", "Mis Citas"), stats rápidas. |
| 3 | **Selecciona Especialidad** | Paso 1 de 3. Grid de especialidades con ícono circular de acento y nombre del médico asignado. |
| 4 | **Fecha y Hora** | Paso 2 de 3. Calendario mensual + grid de horarios disponibles. |
| 5 | **Confirmar Cita** | Paso 3 de 3. Resumen detallado (médico, fecha, hora, modalidad, duración) + botón "Confirmar Cita". |
| 6 | **Éxito** | Confirmación visual (check), resumen de la cita creada, botón "Volver al Inicio". Sin bottom nav. |
| 7 | **Mis Citas** | Tabs Próximas / Pasadas, listado vertical de citas con badge de estado. |
| 8 | **Mi Perfil** | Datos del usuario (nombre, cédula, edad, EPS), stats (citas totales, este año, próxima cita), lista de opciones (Notificaciones, Historial Médico, Cuidador de Apoyo). |
