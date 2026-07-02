# Salud Contigo (versión XML) — Guía para Claude

## Regla principal de diseño

Antes de modificar cualquier pantalla, layout o archivo de tema, lee `BRAND.md` y aplica las reglas definidas ahí. Este proyecto tiene un manual de marca estricto pensado para adultos mayores (65+).

## Stack

- Android + **Vistas (XML)** con el editor visual de arrastrar componentes (Layout Editor)
- Kotlin + ViewBinding
- Material Components (Material 3)
- Una `Activity` por pantalla, cada una con su layout en `res/layout/`

> Esta es la versión basada en XML del proyecto. La versión original con Jetpack Compose está en `ProyectoAppMoviles/`.

## Archivos clave de diseño

| Archivo | Propósito |
|---|---|
| `res/values/colors.xml` | Colores de la paleta — deben coincidir con `BRAND.md` |
| `res/values/dimens.xml` | Tamaños táctiles, radios y escala tipográfica |
| `res/values/themes.xml` | Tema Material 3 |
| `res/values/strings.xml` | Todos los textos de la UI (sin jerga técnica) |
| `res/layout/activity_*.xml` | Pantallas (se abren en la vista Design) |

## Pantallas

| Activity | Layout | Navega a |
|---|---|---|
| `LoginActivity` (inicio) | `activity_login.xml` | Home / Registro |
| `RegistroActivity` | `activity_registro.xml` | Home / Login |
| `HomeActivity` | `activity_home.xml` | Cita |
| `CitaActivity` | `activity_cita.xml` | Home |

## Restricciones de diseño (resumen rápido)

- Área táctil mínima: **60dp** (`@dimen/touch_min`)
- Texto body mínimo: **18sp** (`@dimen/texto_body`)
- Color primario: **#00A680** (`@color/primary`)
- Texto principal: **#212121** (`@color/text_primary`)
- Border radius de tarjetas/botones: **16–20dp**
- Sin jerga técnica en los textos de la UI
