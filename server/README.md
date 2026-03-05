# Proxy IA de GastroLink (Node.js)

Este servicio actua como proxy entre la app y OpenAI.
Su objetivo es evitar exponer `OPENAI_API_KEY` en el cliente Android.

## Que hace

- Expone `POST /ai/recommendation`.
- Recibe un `AiRecommendationRequest`.
- Llama a OpenAI en servidor.
- Devuelve `AiRecommendationResponse` con `recommendationText` (maximo 2 frases).
- Aplica limites de tamano de request y rate limit por IP.
- No persiste payloads.

## Seguridad aplicada

- API key de OpenAI solo en servidor (`OPENAI_API_KEY`).
- Autenticacion simple obligatoria:
  - `Authorization: Bearer <AI_PROXY_TOKEN>`
  - o `X-API-KEY: <AI_PROXY_TOKEN>`
- CORS restrictivo (por defecto deny).
- Rate limit por IP con respuesta `429`.
- Respuesta generica `502` si OpenAI falla.
- Sanitizacion y truncado de entradas/salida.

## Variables de entorno

Usa `.env.example` como referencia:

- `OPENAI_API_KEY=`
- `OPENAI_MODEL=gpt-4.1-mini`
- `AI_PROXY_TOKEN=`
- `ALLOWED_ORIGINS=http://localhost:8080,http://localhost:5173`
- `PORT=3000`
- `MAX_REQUEST_SIZE=20kb`
- `RATE_LIMIT_WINDOW_MS=60000`
- `RATE_LIMIT_MAX_REQUESTS=30`
- `TRUST_PROXY=false`

Si `ALLOWED_ORIGINS` esta vacio, en desarrollo solo se permite localhost/127.0.0.1.
En produccion, si no configuras `ALLOWED_ORIGINS`, no se permite ningun origen web.

## Arranque local

```bash
cd server
npm i
npm run dev
```

Para modo normal:

```bash
npm start
```

## Endpoints

### GET /health

Respuesta:

```json
{
  "status": "ok"
}
```

### POST /ai/recommendation

Headers requeridos:

- `Content-Type: application/json`
- `Authorization: Bearer <AI_PROXY_TOKEN>` o `X-API-KEY: <AI_PROXY_TOKEN>`

Request de ejemplo:

```json
{
  "orderMode": "SOLO",
  "nutritionMode": "WITH_PROFILE",
  "totals": {
    "kcal": 980,
    "proteinG": 42,
    "carbsG": 105,
    "fatG": 28
  },
  "dishes": [
    {
      "name": "Bowl de salmon",
      "qty": 1,
      "kcal": 520,
      "proteinG": 31,
      "carbsG": 42,
      "fatG": 19
    }
  ],
  "profile": {
    "type": "SOLO",
    "summary": {
      "goal": "MAINTAIN",
      "allergies": ""
    }
  }
}
```

Response de ejemplo:

```json
{
  "recommendationText": "Ajusta ligeramente el total calorico con una guarnicion mas ligera. Mantienes un buen aporte proteico para tu objetivo.",
  "model": "gpt-4.1-mini",
  "requestId": "chatcmpl_xxx"
}
```

## Probar con curl

```bash
curl -X POST "http://localhost:3000/ai/recommendation" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{
    "orderMode":"SOLO",
    "nutritionMode":"WITH_PROFILE",
    "totals":{"kcal":980,"proteinG":42,"carbsG":105,"fatG":28},
    "dishes":[{"name":"Bowl de salmon","qty":1,"kcal":520,"proteinG":31,"carbsG":42,"fatG":19}],
    "profile":{"type":"SOLO","summary":{"goal":"MAINTAIN"}}
  }'
```

## Notas de seguridad

- No incluyas secretos reales en el cliente.
- No se deben loggear payloads ni tokens.
- Usa HTTPS en produccion.
- Ajusta `RATE_LIMIT_*` y `ALLOWED_ORIGINS` segun entorno.
