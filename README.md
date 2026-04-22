# GastroLink

App Android para pedidos de comida con seguimiento nutricional y asistente IA. Desarrollada en Kotlin + Jetpack Compose como Proyecto de Fin de Ciclo de 2 DAM.

---

## ¿Qué es GastroLink?

GastroLink permite gestionar pedidos de comida de forma individual o en grupo, mostrando la información nutricional de cada plato en tiempo real. Incorpora perfiles nutricionales personalizados, recomendaciones automáticas y un asistente IA conversacional para ayudarte a elegir qué comer según tu estado de ánimo y objetivos.

---

## Funcionalidades principales

### Modos de pedido
- **Solitario** — carrito individual con seguimiento nutricional completo
- **Grupo** — participantes independientes, cada uno con su propio carrito y perfil

### Información nutricional
- Kcal, proteína, carbohidratos y grasa por plato
- Totales del carrito en tiempo real (NutritionStatGrid animado)
- Distribución de macros por contribución calórica (StatsScreen)
- Promedio nutricional por pedido en el historial

### Perfiles nutricionales *(requiere Premium Demo)*
- Perfil completo en modo solitario: edad, sexo, altura, peso, objetivo, actividad, alergias
- Perfil ligero por participante en modo grupo: objetivo, alergias, preferencias
- Guardar y cargar perfiles con nombre
- Validación de campos en tiempo real (rango válido de edad/altura/peso)

### Recomendaciones *(requiere Premium Demo)*
- **Reglas locales** — siempre disponibles, sin internet
- **IA via proxy** (beta) — modelo Llama 3.3 70B a través de servidor en Render.com, con fallback automático a reglas locales si falla

### Asistente IA (chat)
- Botón flotante global en todas las pantallas (excepto carrito y chat)
- Chat conversacional: pregunta qué te apetece y el asistente recomienda platos del menú según tu perfil y estado de ánimo
- Historial de conversación por sesión, con botón "Limpiar"

### Historial y estadísticas
- Todos los pedidos confirmados se guardan localmente con Room
- Detalle completo de cada pedido (platos, participantes, totales nutricionales, perfil usado)
- Estadísticas: promedios por pedido, distribución de macros, plato más pedido
- Exportar historial en JSON o CSV
- Eliminar pedidos individuales o todos los datos

### Ajustes
- Activar/desactivar Premium Demo
- Activar/desactivar recomendaciones IA
- Exportar historial
- Borrar todos los datos locales

---

## Flujo de pantallas

```
StartModeScreen (elegir Solo / Grupo)
    └─► NutritionModeScreen (sin perfil / con perfil)
            ├─► BranchScreen (elegir sucursal)  ← sin perfil
            └─► ProfileScreen → BranchScreen    ← con perfil
                      └─► MenuScreen
                              └─► CartScreen
                                      └─► SummaryScreen
                                              └─► OrderHistoryScreen
```

**Pantallas transversales** (accesibles desde cualquier punto):
- `SettingsScreen` — ajustes globales
- `PlansScreen` — comparativa Free / Premium Demo
- `OrderHistoryScreen` → `OrderDetailScreen` — historial de pedidos
- `StatsScreen` — estadísticas nutricionales
- `ChatScreen` — asistente IA (botón flotante global)

---

## Estado actual y Guía para el Equipo (Auditoría Técnica)

El proyecto se encuentra en una etapa madura, estructurado como un **monorepo** (App Android + Proxy Node.js). A continuación, los puntos clave de la arquitectura actual para los integrantes del equipo:

> [!TIP]
> **Puntos clave de Arquitectura y Flujo de Trabajo:**
> - **Arquitectura:** El proyecto utiliza **Clean Architecture** dividida en `ui`, `domain`, `data` y `model`. La lógica de negocio debe residir estrictamente en `domain` (Ej. `NutritionCalculator`).
> - **Estado Global:** Mantenemos un Single Source of Truth (`AppUiState`) gestionado a través de un único `AppViewModel`.
> - **Testing:** Disponemos de tests unitarios robustos en la capa de `domain` y tests de Room en `androidTest`. Asegúrate de escribir tests para tus nuevos Use Cases.
> - **Linting y Calidad de Código:** Usamos **Detekt** para el linting de Kotlin. Antes de hacer commit, asegúrate de correr `./gradlew detekt` para respetar las reglas fijadas en `/config/detekt/detekt.yml`.
> - **Entorno IA:** El servidor proxy hacia Groq reside en Render. Recordad que la primera petición puede tener tiempos de "arranque en frío" de hasta 50 segundos.

### Próximos pasos a considerar (Mejoras técnicas):
- **Inyección de Dependencias (DI):** Evaluar la incorporación de Hilt o Koin para escalar fluidamente, eliminando la instanciación manual del `AppViewModel`.
- **Modularización de views:** Evaluar fragmentar el `AppUiState` único en múltiples estados según el flujo si la funcionalidad de la app crece.

---

## Cómo ejecutar la app

### Requisitos
- Android Studio Hedgehog o superior
- JDK 17
- Dispositivo o emulador con API 26+

### Pasos
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/davidmartinezmuelas-tech/GastroLink.git
   ```
2. Abrir la carpeta en Android Studio
3. Esperar a que sincronice Gradle (puede tardar 1-2 min la primera vez)
4. Pulsar **Run** (▶) o ejecutar:
   ```bash
   ./gradlew installDebug
   ```

### Generar APK para instalar en móvil
```bash
./gradlew assembleDebug
```
El APK queda en `app/build/outputs/apk/debug/app-debug.apk`. Pásalo al móvil e instálalo (necesitas activar "Instalar apps de origen desconocido" en ajustes del dispositivo).

---

## Activar Premium Demo

La mayoría de funciones nutricionales están detrás de un flag Premium Demo que **no implica ningún pago**. Es solo para simular un sistema de entitlements en el PFC.

Para activarlo: `Ajustes → Premium Demo → activar el switch`

O desde: `Ajustes → Ver planes → Activar Premium Demo (gratis)`

---

## Servidor IA (proxy)

El asistente IA y las recomendaciones no llaman directamente a la API de IA. Pasan por un servidor proxy desplegado en **Render.com** que gestiona las claves de API de forma segura.

**URL del servidor:** `https://gastrolink-onjy.onrender.com`

> ⚠️ El servidor usa el plan gratuito de Render, que hiberna tras 15 min de inactividad. La primera petición puede tardar hasta 30-50 segundos en "despertar". Las siguientes son rápidas.

### Endpoints del proxy
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/health` | Estado del servidor |
| POST | `/ai/recommend` | Recomendaciones nutricionales (usado en SummaryScreen) |
| POST | `/ai/chat` | Chat con el asistente IA (usado en ChatScreen) |

### Levantar el servidor en local (opcional)
Si quieres correrlo tú mismo:
```bash
cd server
cp .env.example .env
# Editar .env con tu GROQ_API_KEY y AI_PROXY_TOKEN
npm install
npm run dev
```
Variables necesarias en `.env`:
- `GROQ_API_KEY` — clave de [console.groq.com](https://console.groq.com) (gratuita)
- `AI_PROXY_TOKEN` — token que la app usa para autenticarse con el proxy
- `ALLOWED_ORIGINS` — orígenes permitidos por CORS
- `PORT` — puerto (por defecto 3000)

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM + Clean (ui / domain / data) |
| Estado | `StateFlow` + `AppUiState` como single source of truth |
| Persistencia local | Room 2.6 (KSP) + DataStore |
| Navegación | Navigation Compose 2.8 con transiciones slide |
| Imágenes | Coil Compose 2.6 |
| Red | Ktor Client |
| IA | Groq API (llama-3.3-70b-versatile) via proxy Node.js |
| Servidor | Node.js + Express, desplegado en Render.com |

---

## Estructura del repositorio

```
GastroLink/
├── app/
│   ├── build.gradle.kts
│   └── src/main/java/tech/davidmartinezmuelas/gastrolink/
│       ├── data/           # Implementaciones (Room, DataStore, Ktor, IA)
│       ├── domain/         # Casos de uso e interfaces
│       ├── model/          # Modelos de datos
│       └── ui/
│           ├── components/ # GastroComponents reutilizables (KcalChip, NutritionStatGrid…)
│           ├── navigation/ # AppNavGraph + AppRoute
│           ├── screens/    # Una pantalla por archivo
│           └── theme/      # Color, Type, Theme, GastroSpacing, PillShape
├── server/
│   ├── src/index.js        # Proxy IA (Express + Groq SDK)
│   ├── .env.example
│   └── README.md
├── docs/
│   ├── decisions/          # ADRs y decisiones de diseño
│   └── privacy.md
├── build.gradle.kts
└── README.md
```

---

## Sistema de diseño

La app usa un design system propio llamado **GastroLink Premium**:

- **Color primario:** Verde bosque `#1A5C3C` (TopBar, botones CTA)
- **Fondo:** Casi blanco `#F4F8F2`, superficies blanco puro
- **Colores nutricionales semánticos:**
  - 🟡 Calorías — ámbar `#F59E0B`
  - 🟢 Proteína — esmeralda `#10B981`
  - 🟠 Carbohidratos — naranja `#F97316`
  - 🟣 Grasa — violeta `#8B5CF6`
- **GastroSpacing:** grid de 8dp (xs=4, sm=8, md=16, lg=24, xl=32)
- **Formas:** esquinas grandes (16-24dp) en todas las cards
- **Componentes reutilizables:** `KcalChip`, `MacroPillsRow`, `NutritionStatGrid`, `MacroDistributionBar`, `EmptyState`, `LoadingState`, `SectionHeader`, `QuantityControl`
