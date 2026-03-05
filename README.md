# GastroLink

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
