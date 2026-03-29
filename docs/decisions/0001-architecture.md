# ADR 0001: Arquitectura base para MVP y evolucion

## Estado

Aceptado

## Contexto

GastroLink nace con foco en MVP, tiempos cortos de entrega y necesidad de crecer sin reescrituras grandes.
El proyecto requiere separar reglas de negocio, UI y acceso a datos desde el inicio para mantener testabilidad y escalabilidad.

## Decisión

Se adopta una arquitectura MVVM con separación Clean-ish en tres capas:

- `ui`: pantallas Compose, estados de UI y ViewModels.
- `domain`: casos de uso y modelos de negocio.
- `data`: repositorios, fuentes locales/remotas y mapeos.

Motivación:

- MVVM encaja de forma natural con Jetpack Compose y el ciclo de vida Android.
- La separación `ui/domain/data` reduce acoplamiento y permite evolucionar la infraestructura sin romper la lógica de negocio.
- Facilita pruebas unitarias en `domain` y pruebas de integración en `data`.

## Estrategia Offline-First

Estado actual:

- Carga de catalogo local desde `assets/sample_data`.
- Persistencia local con Room para historial, detalle y estadisticas.
- DataStore para ajustes ligeros (por ejemplo, flags de experiencia).

Motivacion de incluir Room:

- Necesidad de mantener trazabilidad local de pedidos.
- Necesidad de calculo de estadisticas historicas sin dependencia de red.

Evolucion prevista:

- Cuando exista backend, mantener Room como cache local y fuente offline.
- Incorporar sincronizacion por repositorio (local-first con reconciliacion diferida).

## Evolución hacia Backend e IA

Cuando el producto avance a fase conectada:

- Backend: se añadirá una fuente remota en `data` (API REST/GraphQL) manteniendo intactos los casos de uso de `domain`.
- IA: se integrará como servicio adicional para recomendaciones (por perfil nutricional, historial o contexto), consumido por casos de uso específicos en `domain`.

## Consecuencias

Positivas:

- Mejor mantenibilidad desde etapas tempranas.
- Menor riesgo de regresiones al cambiar infraestructura.
- Camino claro para escalar a múltiples fuentes de datos.

Costes:

- Mayor estructura inicial que un enfoque monolítico.
- Necesidad de disciplina de equipo para respetar límites entre capas.
