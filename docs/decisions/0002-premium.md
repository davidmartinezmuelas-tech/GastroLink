# 0002 - Entitlements Free/Premium centralizados sin pagos reales

## Estado

Aceptada

## Contexto

GastroLink necesita una base de planes Free/Premium para presentacion y evolucion futura, pero en esta fase no se implementan pagos reales.

Riesgos detectados antes del cambio:

- Gating disperso en pantalla/ViewModel.
- Dificultad para migrar a Play Billing o backend en el futuro.
- Inconsistencias entre toggle de demo y capacidades efectivas.

## Decision

Se introduce una capa central de entitlement:

- `EntitlementRepository` (interfaz): fuente unica de estado Free/Premium.
- `DataStoreEntitlementRepository`: implementacion actual con DataStore (`premium_demo_enabled`).
- `EntitlementUseCase`: reglas de negocio de habilitacion:
  - `isPremiumEnabled()`
  - `canUseNutritionWithProfile()`
  - `canUseAiRecommendations()`

Ademas:

- Se crea `PlansScreen` para comunicar diferencias Free vs Premium.
- La activacion de `Premium Demo` es solo para presentacion, sin pago.
- El gating de nutricion con perfil y recomendaciones IA usa el caso de uso central.

## Consecuencias

Positivas:

- Menor acoplamiento de la UI a la fuente de entitlement.
- Camino claro para sustituir DataStore por Play Billing/backend sin rehacer pantallas.
- Reglas consistentes para capacidades premium.

Limitaciones:

- No hay compra real ni validacion remota de suscripcion en esta fase.
- El modo demo premium no representa un estado comercial definitivo.

## Alternativas consideradas

1. Mantener gating en ViewModel/pantallas: descartado por deuda tecnica y riesgo de incoherencia.
2. Integrar Play Billing ahora: descartado por alcance y complejidad para esta iteracion.
