# GastroLink

[![Android CI](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/android.yml/badge.svg)](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/android.yml)
[![Build Debug APK](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/build-debug.yml/badge.svg)](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/build-debug.yml)
[![Nightly Debug Release](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/nightly.yml/badge.svg)](https://github.com/davidmartinezmuelas-tech/GastroLink/actions/workflows/nightly.yml)

GastroLink es una app Android (Kotlin + Compose) para pedidos de comida con apoyo nutricional, diseñada para operar en modo local con flujo guiado y preparada para evolucionar a servicios remotos.

## Estado actual del proyecto

Funcionalidades implementadas hoy:

- Modos de pedido: `Solitario` y `Grupo`.
- Modos nutricionales: `Sin datos` y `Con datos` (habilitado por premium demo).
- Perfiles nutricionales:
	- Solitario: perfil completo.
	- Grupo: perfil ligero por participante.
- Carrito por participante en modo grupo.
- Recomendaciones nutricionales:
	- Reglas locales.
	- Opcion IA (beta) via proxy, con fallback local automatico.
- Persistencia con Room:
	- Confirmacion de pedido.
	- Historial.
	- Detalle de pedido.
	- Estadisticas nutricionales.
- Entitlements Free/Premium Demo centralizados.
- Privacidad y control de datos:
	- Borrado de datos locales.
	- Exportacion de historial en JSON/CSV.
- CI/CD:
	- CI principal en push/PR.
	- Build manual de APK debug.
	- Release nightly de APK debug.

## Flujo real de pantallas

Flujo base desde el arranque:

1. `StartModeScreen`.
2. `NutritionModeScreen`.
3. `ProfileScreen` (si aplica modo con datos).
4. `BranchScreen`.
5. `MenuScreen`.
6. `CartScreen`.
7. `SummaryScreen`.

Pantallas transversales:

- `SettingsScreen`.
- `PlansScreen`.
- `OrderHistoryScreen`.
- `OrderDetailScreen`.
- `StatsScreen`.

## Probar la app

### Sin Android Studio (APK nightly)

Puedes descargar la build nightly desde:

- `https://github.com/davidmartinezmuelas-tech/GastroLink/releases/tag/nightly`

Guia rapida de QR:

- `docs/QR.md`

### Con Android Studio

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Usar JDK 17.
4. Sincronizar Gradle.
5. Ejecutar `app` en `debug`.

Comandos utiles en local:

```bash
./gradlew detekt
./gradlew test
./gradlew :app:assembleDebug
```

## IA (demo) con proxy

La app no llama directamente a OpenAI. Usa un proxy en `server/`.

Pasos:

1. Levantar proxy:

```bash
cd server
npm i
npm run dev
```

2. Configurar variables del proxy (`server/.env.example` como base):

- `OPENAI_API_KEY`
- `AI_PROXY_TOKEN`
- `ALLOWED_ORIGINS`
- `PORT`

3. Configurar app en debug/demo:

- `AI_BASE_URL` via `BuildConfig` (debug).
- `AI_PROXY_TOKEN` via entorno de build debug (`AI_PROXY_TOKEN`).
- No incluir secretos reales en `release`.

Comportamiento ante fallo IA:

- Se aplica fallback automatico a reglas locales.

## Workflows

| Workflow | Archivo | Disparador | Proposito |
| --- | --- | --- | --- |
| Android CI | `.github/workflows/android.yml` | `push`, `pull_request` | CI principal: detekt + test + assembleDebug |
| Build Debug APK | `.github/workflows/build-debug.yml` | `workflow_dispatch` | Build manual y artifact descargable |
| Nightly Debug Release | `.github/workflows/nightly.yml` | `schedule`, `workflow_dispatch` | Publicacion/actualizacion del release `nightly` |

## Privacidad y datos

- Documentacion: `docs/privacy.md`
- La app guarda datos locales en Room y DataStore para funcionalidad offline.
- El envio a IA se hace por proxy y con datos minimos.

## Estructura del repositorio

```text
GastroLink/
├── .github/workflows/
│   ├── android.yml
│   ├── build-debug.yml
│   └── nightly.yml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/tech/davidmartinezmuelas/gastrolink/
│       │   ├── data/
│       │   ├── domain/
│       │   ├── model/
│       │   └── ui/
│       └── res/
├── config/detekt/detekt.yml
├── docs/
│   ├── decisions/
│   ├── privacy.md
│   └── QR.md
├── server/
│   ├── src/index.js
│   ├── README.md
│   └── .env.example
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## Checklist de release

Antes de una release formal:

1. Revisar `versionName` y `versionCode`.
2. Actualizar changelog/notas.
3. Verificar CI verde (`android.yml`).
4. Generar artifact/release (manual o nightly segun caso).
5. Probar instalacion en dispositivo real.

## FUTURO (roadmap)

Elementos planificados para fases siguientes:

- Sincronizacion multi-dispositivo con backend.
- Entitlement premium real con Play Billing/backend.
- Mejoras de recomendacion IA con politicas de privacidad mas avanzadas.
- Telemetria de calidad (sin datos sensibles) para mejorar UX.
