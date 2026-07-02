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

---

## Sistema de Color

### Paleta Principal

| Token | HEX | Uso |
|---|---|---|
| `Primary` | `#00A680` | Color principal de la marca, botones primarios, íconos activos |
| `PrimaryDark` | `#007D60` | Variante oscura, footer, hover states |
| `PrimaryLight` | `#E0F2F1` | Fondos suaves, degradados de portada |
| `BackgroundMain` | `#FFFFFF` | Fondo de pantallas y tarjetas |
| `BackgroundAlt` | `#F4F7F6` | Fondo alternativo, inputs, secciones grises |
| `TextPrimary` | `#212121` | Todo texto principal |
| `TextSecondary` | `#757575` | Subtítulos, descripciones, placeholders |
| `Success` | `#4CAF50` | Confirmaciones, estados exitosos |
| `SuccessLight` | `#E8F5E9` | Fondos de mensajes de éxito |
| `Border` | `#E0E0E0` | Bordes de tarjetas, inputs, divisores |

### Colores de Especialidades Médicas

Fondos pastel para el grid de especialidades. El texto siempre va en `#212121` para máxima legibilidad.

| Especialidad | HEX |
|---|---|
| Cardiología | `#FFEBEE` |
| Medicina General | `#E3F2FD` |
| Neumología | `#E8F5E9` |
| Neurología | `#F3E5F5` |
| Gastroenterología | `#FFF3E0` |
| Otorrinolaringología | `#FFF8E1` |
| Pediatría | `#E0F7FA` |
| Otras / Default | `#F5F5F5` |

---

## Tipografía

**Fuente:** Inter o Roboto (sistema Android)

| Rol | Tamaño | Peso | Uso |
|---|---|---|---|
| H1 / Título de pantalla | 24sp | Bold (700) | "Elige tu médico", "Confirma tu cita" |
| H2 / Subtítulo | 20sp | Medium (500) | "Mis médicos frecuentes" |
| Body / Texto general | 18sp | Regular (400) | Fechas, descripciones, instrucciones |
| Label pequeño | 14sp | Regular (400) | Etiquetas de campos, metadatos |

> **Regla:** Nunca usar texto menor a 14sp. El tamaño mínimo de cuerpo de texto para el usuario final es 18sp.

---

## Componentes Interactivos

### Botones

| Tipo | Fondo | Texto | Sombra |
|---|---|---|---|
| Primario | `#00A680` | Blanco, UPPERCASE, 600 | `rgba(0,166,128,0.25)` elevation 8dp |
| Secundario | `#FFFFFF` | `#212121`, normal, 500 | Borde `#E0E0E0` 2dp |

- **Altura mínima:** 60dp (área táctil)
- **Border radius:** 16dp (botones normales), 20dp (botones grandes de home)
- **Padding horizontal:** 32dp
- **Tipografía:** 18sp, Font Weight 600

### Inputs / Campos de Texto

- Fondo: `#F4F7F6`
- Borde: 2dp solid `#E0E0E0`
- Border radius: 16dp
- Padding: 20dp
- Tipografía: 18sp

### Tarjetas (Cards)

- Fondo: `#FFFFFF`
- Border radius: 16–20dp
- Sombra: `rgba(0,0,0,0.08)` elevation 4dp
- Borde superior de acento (en tarjetas de valores): 6dp solid `#00A680`

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

- **Bottom Navigation Bar** con 3 ítems: Inicio, Citas, Perfil
- Ítem activo en color `#00A680`, ícono + label
- Ítem inactivo en `#757575`
- Íconos grandes (~24dp), labels visibles siempre

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

| Pantalla | Descripción |
|---|---|
| **Login** | Campo de cédula/teléfono + botón huella/Face ID. Mínimo elementos. |
| **Home** | Saludo personalizado + dos botones grandes: "AGENDAR NUEVA CITA" y "VER MIS CITAS" |
| **Selección de especialidad** | Grid de especialidades con colores pastel, ícono grande y nombre |
| **Selección de médico / fecha** | Lista de médicos con foto, nombre, especialidad; selector de fecha/hora prominente |
| **Confirmación de cita** | Resumen detallado con médico, fecha y hora. Pregunta directa: "¿Es correcta esta información?" + botones SÍ/NO |
| **Mis Citas** | Listado de citas próximas y pasadas |
