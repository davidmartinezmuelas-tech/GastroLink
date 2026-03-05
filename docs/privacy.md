# Privacidad en GastroLink

## Datos que se guardan localmente

GastroLink guarda datos en el dispositivo del usuario para funcionamiento offline y continuidad:

- Historial de pedidos (Room).
- Participantes por pedido (Room).
- Perfil nutricional guardado por pedido (Room).
- Preferencias de la app (DataStore), por ejemplo:
  - uso de recomendaciones IA,
  - ajustes de demo/personalizacion de flujo.

No se almacenan credenciales del usuario final ni datos de pago.

## Datos enviados al proxy IA

Cuando el usuario activa recomendaciones IA y el modo lo permite, la app envia al proxy solo datos nutricionales necesarios para generar la recomendacion:

- Totales nutricionales del pedido.
- Lista de platos con cantidades y macros.
- Contexto de modo de pedido y modo nutricional.
- Perfil nutricional:
  - en modo solo: campos de perfil necesarios para recomendacion,
  - en modo grupo: perfil ligero por participante.

No se envian secretos del dispositivo ni la API key de OpenAI desde el cliente.

## Garantias del proxy

El proxy IA aplica medidas basicas de seguridad:

- No expone `OPENAI_API_KEY` al cliente.
- Autenticacion por token de proxy.
- CORS restrictivo por origen permitido.
- Rate limit por IP.
- Limite de tamano de request.
- Respuestas genericas en error de proveedor IA.
- No logging de payloads sensibles.

## Control de datos por parte del usuario

La app permite al usuario:

- **Borrar todos sus datos** desde `Ajustes > Privacidad`.
  - Elimina historial, perfiles, participantes y ajustes locales.
- **Exportar historial** en JSON o CSV desde:
  - `Ajustes > Exportar historial`, o
  - `Historial > Exportar`.

Formato de exportacion:

- JSON: estructura por pedido con sus platos y totales.
- CSV: una fila por plato, incluyendo columnas de contexto del pedido.

El archivo se comparte mediante selector de apps del sistema (Drive, correo, mensajeria, etc.).

## Nota de terminologia

En interfaz de usuario se usan los terminos `Sin datos` y `Con datos`.
En implementacion interna pueden aparecer identificadores tecnicos como `WITHOUT_PROFILE` y `WITH_PROFILE`.

## Aviso

La informacion nutricional y las recomendaciones de GastroLink son orientativas y no sustituyen consejo medico profesional.
