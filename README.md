# GastroLink

[![Android CI](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/android.yml/badge.svg)](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/android.yml)
[![Build Debug APK](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/build-debug.yml/badge.svg)](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/build-debug.yml)
[![Nightly Debug Release](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/nightly.yml/badge.svg)](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/nightly.yml)

Aplicación Android (Kotlin) enfocada en una experiencia de pedido guiada por datos nutricionales.
El MVP permite seleccionar sucursal, explorar menú, gestionar carrito y visualizar un resumen nutricional antes de confirmar.

## MVP

Funcionalidades iniciales planificadas:

- Selección de sucursal.
- Listado de menú por sucursal.
- Carrito con altas, bajas y ajuste de cantidades.
- Resumen nutricional acumulado del carrito (kcal, proteínas, carbohidratos y grasas).

## Stack Tecnológico

- Android nativo.
- Kotlin.
- Jetpack Compose (recomendado para UI declarativa).
- MVVM + separación `ui/domain/data` (Clean-ish).
- Room (opcional en fase inicial, previsto para persistencia local).

## Cómo Ejecutar (Android Studio)

Estado actual del repositorio: proyecto Android creado con módulo único `:app`, Kotlin, Compose y Gradle Kotlin DSL.

Pasos para ejecutar:

1. Abrir Android Studio y seleccionar `Open` sobre este repositorio.
2. Esperar a la sincronización de Gradle (se usará `gradlew`).
3. Verificar JDK 17 en la configuración del proyecto.
4. Ejecutar configuración `app` en modo `debug` sobre emulador/dispositivo.

Ejecución por línea de comandos (opcional):

```bash
./gradlew test
./gradlew assembleDebug
```

Calidad de código local:

```bash
./gradlew detekt
```

## CI/CD

Proposito de workflows:

- `android.yml`: CI de calidad (detekt + tests + assembleDebug en cada push/PR).
- `build-debug.yml`: build manual bajo demanda para descargar APK de debug.
- `nightly.yml`: distribucion nocturna del APK debug como release `nightly`.

## Privacidad

- Documentacion de privacidad: `docs/privacy.md`
- La app permite borrar todos los datos locales y exportar historial en JSON/CSV.

### Build manual (workflow_dispatch)

Workflow: `Build Debug APK` (`.github/workflows/build-debug.yml`).

Pasos para lanzarlo manualmente en GitHub:

1. Ir a `Actions` en el repositorio.
2. Seleccionar workflow `Build Debug APK`.
3. Pulsar `Run workflow`.
4. Descargar el artifact `app-debug-apk` al finalizar.

### Nightly release (programado + manual)

Workflow: `Nightly Debug Release` (`.github/workflows/nightly.yml`).

- Se ejecuta diariamente y tambien se puede lanzar con `Run workflow`.
- Publica/actualiza siempre el Release con tag `nightly`.
- El APK debug (`app-debug.apk`) se reemplaza en cada ejecucion.

Enlace estable del release nightly:

- `https://github.com/davidmartinezmuelas-tech/GastroLink/releases/tag/nightly`

QR para descarga nightly:

- Ver `docs/QR.md`.

## Flujo de Pantallas (MVP)

Navegación implementada en Compose Navigation:

1. `BranchScreen`: lista de sucursales.
2. Al seleccionar sucursal se guarda en estado compartido y se navega a `MenuScreen`.
3. `MenuScreen`: lista de platos y botón `Anadir` para el carrito; acceso a `CartScreen` desde top bar.
4. `CartScreen`: ajuste de cantidades con `+` y `-`; si qty llega a 0, el item se elimina; botón hacia `SummaryScreen`.
5. `SummaryScreen`: totales nutricionales (kcal, proteína, carbs, grasa) + recomendaciones simples.

## Convención de Commits

Se adopta [Conventional Commits](https://www.conventionalcommits.org/).

Formato:

```text
<tipo>(<scope opcional>): <descripción corta>
```

Ejemplos:

- `feat(menu): mostrar listado de platos por sucursal`
- `feat(cart): calcular macros totales del carrito`
- `fix(branch): corregir filtro por ciudad`
- `docs(readme): añadir roadmap de iteraciones`
- `chore(ci): preparar workflow android`

## Estructura del Repositorio

```text
GastroLink/
├── .github/
│   └── workflows/
│       └── android.yml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/tech/davidmartinezmuelas/gastrolink/
│       └── res/values/
├── assets/
│   └── sample_data/
│       ├── branches.json
│       └── dishes.json
├── config/
│   └── detekt/
│       └── detekt.yml
├── docs/
│   ├── decisions/
│   │   └── 0001-architecture.md
│   ├── diagrams/
│   │   └── .gitkeep
│   └── screenshots/
│       └── .gitkeep
├── CONTRIBUTING.md
├── build.gradle.kts
├── gradle.properties
├── gradle/wrapper/
├── gradlew
├── gradlew.bat
└── README.md
```

## Roadmap

### Iteración 1

- Inicialización del proyecto Android + módulos base (`ui/domain/data`).
- Flujo de selección de sucursal.
- Carga de menú desde `assets/sample_data`.

### Iteración 2

- Gestión completa de carrito.
- Cálculo de resumen nutricional en tiempo real.
- Pruebas unitarias de casos de uso principales.

### Iteración 3

- Persistencia local con Room (offline-first real).
- Endpoints backend iniciales para menú/sucursales.
- Preparación para recomendaciones con IA (fase posterior).
