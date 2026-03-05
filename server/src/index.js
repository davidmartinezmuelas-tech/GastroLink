const express = require("express");
const rateLimit = require("express-rate-limit");
const OpenAI = require("openai");

const PORT = Number(process.env.PORT || 3000);
const MAX_REQUEST_SIZE = process.env.MAX_REQUEST_SIZE || "20kb";
const RATE_LIMIT_WINDOW_MS = Number(process.env.RATE_LIMIT_WINDOW_MS || 60_000);
const RATE_LIMIT_MAX_REQUESTS = Number(process.env.RATE_LIMIT_MAX_REQUESTS || 30);
const OPENAI_MODEL = process.env.OPENAI_MODEL || "gpt-4.1-mini";
const TRUST_PROXY = (process.env.TRUST_PROXY || "false").toLowerCase() === "true";
const AI_PROXY_TOKEN = String(process.env.AI_PROXY_TOKEN || "").trim();

const ALLOWED_METHODS = ["GET", "POST", "OPTIONS"];
const ALLOWED_HEADERS = ["Content-Type", "Authorization", "X-API-KEY"];

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY
});

const app = express();
app.disable("x-powered-by");

if (TRUST_PROXY) {
  app.set("trust proxy", 1);
}

app.use(createCorsMiddleware());
app.use(express.json({ limit: MAX_REQUEST_SIZE }));

const limiter = rateLimit({
  windowMs: RATE_LIMIT_WINDOW_MS,
  limit: RATE_LIMIT_MAX_REQUESTS,
  standardHeaders: true,
  legacyHeaders: false,
  statusCode: 429,
  message: {
    error: "Demasiadas solicitudes. Intentalo de nuevo en unos segundos."
  },
  handler: (_req, res) => {
    res.status(429).json({
      error: "Demasiadas solicitudes. Intentalo de nuevo en unos segundos."
    });
  }
});

app.use("/ai", limiter);

app.get("/health", (_req, res) => {
  res.status(200).json({ status: "ok" });
});

app.post("/ai/recommendation", requireProxyAuth, async (req, res) => {
  if (!process.env.OPENAI_API_KEY) {
    return res.status(500).json({
      error: "Configuracion incompleta del servicio de IA."
    });
  }

  const validation = sanitizeAndValidateRequest(req.body);
  if (!validation.ok) {
    return res.status(400).json({ error: validation.error });
  }

  const safeRequest = validation.data;

  try {
    const prompt = buildPrompt(safeRequest);

    const completion = await openai.chat.completions.create({
      model: OPENAI_MODEL,
      temperature: 0.2,
      max_tokens: 140,
      messages: [
        {
          role: "system",
          content:
            "Eres un asistente nutricional para GastroLink. Responde en espanol con recomendaciones claras, neutrales y accionables. Devuelve como maximo dos frases."
        },
        {
          role: "user",
          content: prompt
        }
      ]
    });

    const rawText = completion.choices?.[0]?.message?.content || "";
    const recommendationText = toTwoSentences(rawText);

    if (!recommendationText) {
      return res.status(502).json({
        error: "No se pudo generar una recomendacion valida en este momento."
      });
    }

    return res.status(200).json({
      recommendationText,
      model: completion.model || OPENAI_MODEL,
      requestId: completion.id || null
    });
  } catch (_error) {
    return res.status(502).json({
      error: "Servicio de recomendaciones no disponible temporalmente."
    });
  }
});

app.use((req, res) => {
  res.status(404).json({ error: `Ruta no encontrada: ${req.method} ${req.path}` });
});

app.use((err, _req, res, _next) => {
  if (err && err.type === "entity.too.large") {
    return res.status(413).json({
      error: "El cuerpo de la solicitud supera el tamano maximo permitido."
    });
  }

  return res.status(500).json({ error: "Error interno del servidor." });
});

app.listen(PORT, () => {
  console.log(`Proxy IA de GastroLink escuchando en puerto ${PORT}`);
});

function sanitizeAndValidateRequest(body) {
  if (!body || typeof body !== "object" || Array.isArray(body)) {
    return { ok: false, error: "Payload invalido." };
  }

  const orderMode = sanitizeText(body.orderMode, 20);
  const nutritionMode = sanitizeText(body.nutritionMode, 30);

  if (!orderMode || !nutritionMode) {
    return { ok: false, error: "orderMode y nutritionMode son obligatorios." };
  }

  if (!["SOLO", "GROUP"].includes(orderMode)) {
    return { ok: false, error: "orderMode no valido." };
  }

  if (!["WITH_PROFILE", "WITHOUT_PROFILE"].includes(nutritionMode)) {
    return { ok: false, error: "nutritionMode no valido." };
  }

  const totals = sanitizeTotals(body.totals);
  if (!totals) {
    return { ok: false, error: "totals no es valido." };
  }

  const dishes = sanitizeDishes(body.dishes);
  if (!dishes) {
    return { ok: false, error: "dishes no es valido." };
  }

  const profile = sanitizeProfile(body.profile);
  if (body.profile !== undefined && profile === null) {
    return { ok: false, error: "profile no es valido." };
  }

  return {
    ok: true,
    data: {
      orderMode,
      nutritionMode,
      totals,
      dishes,
      profile
    }
  };
}

function sanitizeTotals(input) {
  if (!input || typeof input !== "object" || Array.isArray(input)) {
    return null;
  }

  const kcal = toSafeNumber(input.kcal);
  const proteinG = toSafeNumber(input.proteinG);
  const carbsG = toSafeNumber(input.carbsG);
  const fatG = toSafeNumber(input.fatG);

  const values = [kcal, proteinG, carbsG, fatG];
  if (values.some((value) => value === null)) {
    return null;
  }

  return {
    kcal,
    proteinG,
    carbsG,
    fatG
  };
}

function sanitizeDishes(input) {
  if (!Array.isArray(input) || input.length === 0 || input.length > 50) {
    return null;
  }

  const dishes = [];

  for (const item of input) {
    if (!item || typeof item !== "object" || Array.isArray(item)) {
      return null;
    }

    const name = sanitizeText(item.name, 80);
    const qty = toSafeNumber(item.qty);
    const kcal = toSafeNumber(item.kcal);
    const proteinG = toSafeNumber(item.proteinG);
    const carbsG = toSafeNumber(item.carbsG);
    const fatG = toSafeNumber(item.fatG);

    if (!name || [qty, kcal, proteinG, carbsG, fatG].some((v) => v === null)) {
      return null;
    }

    dishes.push({ name, qty, kcal, proteinG, carbsG, fatG });
  }

  return dishes;
}

function sanitizeProfile(input) {
  if (input === undefined || input === null) {
    return null;
  }

  if (typeof input !== "object" || Array.isArray(input)) {
    return null;
  }

  const type = sanitizeText(input.type, 30);
  if (!type) {
    return null;
  }

  const summary = sanitizeJsonValue(input.summary, 0);

  return {
    type,
    summary
  };
}

function sanitizeJsonValue(value, depth) {
  if (depth > 4) {
    return null;
  }

  if (value === null || value === undefined) {
    return null;
  }

  if (typeof value === "string") {
    return sanitizeText(value, 120);
  }

  if (typeof value === "number") {
    return toSafeNumber(value);
  }

  if (typeof value === "boolean") {
    return value;
  }

  if (Array.isArray(value)) {
    return value.slice(0, 20).map((item) => sanitizeJsonValue(item, depth + 1));
  }

  if (typeof value === "object") {
    const output = {};
    const entries = Object.entries(value).slice(0, 30);

    for (const [rawKey, rawValue] of entries) {
      const key = sanitizeText(rawKey, 40);
      if (!key) {
        continue;
      }
      output[key] = sanitizeJsonValue(rawValue, depth + 1);
    }

    return output;
  }

  return null;
}

function toSafeNumber(value) {
  const numberValue = Number(value);
  if (!Number.isFinite(numberValue)) {
    return null;
  }

  if (numberValue < 0 || numberValue > 100000) {
    return null;
  }

  return Math.round(numberValue * 100) / 100;
}

function sanitizeText(value, maxLength) {
  if (typeof value !== "string") {
    return "";
  }

  const cleaned = value
    .replace(/[\u0000-\u001F\u007F]/g, " ")
    .replace(/[<>`]/g, "")
    .replace(/\s+/g, " ")
    .trim();

  if (!cleaned) {
    return "";
  }

  return cleaned.slice(0, maxLength);
}

function buildPrompt(request) {
  const dishLines = request.dishes
    .map((dish, index) => {
      return `${index + 1}. ${dish.name} x${dish.qty} (kcal ${dish.kcal}, P ${dish.proteinG}g, C ${dish.carbsG}g, G ${dish.fatG}g)`;
    })
    .join("\n");

  const profileLine = request.profile
    ? `Perfil (${request.profile.type}): ${JSON.stringify(request.profile.summary)}`
    : "Perfil: no disponible";

  return [
    "Genera una recomendacion nutricional breve para este pedido.",
    "Limite estricto: maximo 2 frases.",
    `Modo pedido: ${request.orderMode}`,
    `Modo nutricional: ${request.nutritionMode}`,
    `Totales: kcal ${request.totals.kcal}, P ${request.totals.proteinG}g, C ${request.totals.carbsG}g, G ${request.totals.fatG}g`,
    profileLine,
    "Platos:",
    dishLines
  ].join("\n");
}

function toTwoSentences(text) {
  const safeText = sanitizeText(String(text || ""), 400);
  if (!safeText) {
    return "";
  }

  const parts = safeText
    .split(/(?<=[.!?])\s+/)
    .map((part) => part.trim())
    .filter(Boolean);

  const selected = (parts.length > 0 ? parts : [safeText]).slice(0, 2);
  const normalized = selected
    .join(" ")
    .replace(/\s+/g, " ")
    .trim();

  return normalized.slice(0, 280);
}

function requireProxyAuth(req, res, next) {
  const tokenFromAuthHeader = extractBearerToken(req.headers.authorization);
  const tokenFromApiKeyHeader = sanitizeText(String(req.headers["x-api-key"] || ""), 200);
  const providedToken = tokenFromAuthHeader || tokenFromApiKeyHeader;

  if (!AI_PROXY_TOKEN || !providedToken || providedToken !== AI_PROXY_TOKEN) {
    return res.status(401).json({ error: "No autorizado." });
  }

  return next();
}

function extractBearerToken(authorizationHeader) {
  if (!authorizationHeader || typeof authorizationHeader !== "string") {
    return "";
  }

  const [scheme, token] = authorizationHeader.trim().split(/\s+/, 2);
  if (!scheme || !token || scheme.toLowerCase() !== "bearer") {
    return "";
  }

  return sanitizeText(token, 200);
}

function createCorsMiddleware() {
  const allowedOrigins = resolveAllowedOrigins();

  return (req, res, next) => {
    const origin = req.headers.origin;

    if (origin) {
      if (!isCorsRouteAllowed(req.method, req.path)) {
        return res.status(403).json({ error: "Ruta no habilitada para CORS." });
      }

      if (!allowedOrigins.has(origin)) {
        return res.status(403).json({ error: "Origen no permitido." });
      }

      res.setHeader("Access-Control-Allow-Origin", origin);
      res.setHeader("Vary", "Origin");
      res.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS.join(", "));
      res.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS.join(", "));
      res.setHeader("Access-Control-Max-Age", "600");
    }

    if (req.method === "OPTIONS") {
      return res.status(204).send();
    }

    return next();
  };
}

function resolveAllowedOrigins() {
  const configuredOrigins = String(process.env.ALLOWED_ORIGINS || "")
    .split(",")
    .map((origin) => origin.trim())
    .filter(Boolean);

  if (configuredOrigins.length > 0) {
    return new Set(configuredOrigins);
  }

  const isDevelopment = (process.env.NODE_ENV || "development") !== "production";
  if (!isDevelopment) {
    return new Set();
  }

  return new Set([
    "http://localhost:3000",
    "http://localhost:5173",
    "http://127.0.0.1:3000",
    "http://127.0.0.1:5173"
  ]);
}

function isCorsRouteAllowed(method, path) {
  if (path === "/health" && (method === "GET" || method === "OPTIONS")) {
    return true;
  }

  if (path === "/ai/recommendation" && (method === "POST" || method === "OPTIONS")) {
    return true;
  }

  return false;
}
