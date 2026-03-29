# Hoja de ruta TFG — GastroLink

Orden de prioridad: de mayor impacto a menor. Cada paso es independiente y se puede hacer por separado.

---

## PASO 1 — Desplegar el servidor IA en Railway ⭐ MÁXIMO IMPACTO

**Estado:** El servidor ya está programado en `/server/`. Solo falta desplegarlo.
**Tiempo estimado:** 20-30 minutos.

El servidor es un proxy Node.js/Express que recibe el pedido de la app,
llama a la API de OpenAI (gpt-4.1-mini) y devuelve la recomendación nutricional.

### 1.1 Crear cuenta en Railway
- Ir a https://railway.app → Sign up con GitHub

### 1.2 Desplegar el servidor
```
1. New Project → Deploy from GitHub repo
2. Seleccionar el repositorio GastroLink
3. Railway detecta el package.json en /server → configurar root directory = "server"
4. Add variables de entorno:
   - OPENAI_API_KEY  = sk-... (tu clave de OpenAI)
   - AI_PROXY_TOKEN  = cualquier string secreto, ej: "gastrolink-secret-2025"
   - NODE_ENV        = production
   - TRUST_PROXY     = true
5. Deploy → Railway da una URL tipo: https://gastrolink-xxx.railway.app
```

### 1.3 Conectar la app al servidor
En `app/build.gradle.kts`, cambiar:
```kotlin
// debug block
buildConfigField("String", "AI_BASE_URL", "\"https://TU-URL.railway.app\"")
buildConfigField("String", "AI_PROXY_TOKEN", "\"$aiProxyTokenDebug\"")
```

Y en tu máquina, en PowerShell (solo una vez):
```powershell
[System.Environment]::SetEnvironmentVariable("AI_PROXY_TOKEN", "gastrolink-secret-2025", "User")
```
Reiniciar Android Studio y compilar.

### 1.4 Activar en la app
- Ajustes → activar Premium Demo → activar "Recomendaciones IA"
- Añadir platos al carrito con perfil nutricional activado
- En el resumen del pedido aparecerá la recomendación de la IA

### 1.5 Verificar que funciona
```
curl -X POST https://TU-URL.railway.app/ai/recommendation \
  -H "Authorization: Bearer gastrolink-secret-2025" \
  -H "Content-Type: application/json" \
  -d '{"orderMode":"SOLO","nutritionMode":"WITH_PROFILE","totals":{"kcal":500,"proteinG":30,"carbsG":50,"fatG":20},"dishes":[{"name":"Salmon","qty":1,"kcal":500,"proteinG":30,"carbsG":50,"fatG":20}]}'
```

---

## PASO 2 — Tests unitarios básicos ⭐

**Estado:** Infraestructura lista (junit, coroutines-test en build.gradle). 0 tests escritos.
**Tiempo estimado:** 1-2 horas.
**Carpeta:** `app/src/test/java/tech/davidmartinezmuelas/gastrolink/`

### Tests a escribir (por orden de facilidad)

#### A) NutritionCalculator — puro, sin dependencias
```kotlin
class NutritionCalculatorTest {
    @Test
    fun `calcular totales de carrito vacio devuelve ceros`()
    @Test
    fun `calcular totales suma kcal correctamente con cantidades`()
    @Test
    fun `calcular TDEE devuelve null si faltan datos del perfil`()
    @Test
    fun `calcular TDEE hombre 30 años activo devuelve valor razonable`()
}
```

#### B) RecommendationEngineLocal — lógica de reglas
```kotlin
class RecommendationEngineLocalTest {
    @Test
    fun `sin perfil nutricional devuelve lista vacia`()
    @Test
    fun `pedido con muchas calorias genera aviso`()
    @Test
    fun `pedido equilibrado devuelve mensaje positivo`()
}
```

#### C) UpdateSoloProfile — lógica del ViewModel
```kotlin
class AppViewModelTest {
    @Test
    fun `updateSoloProfile convierte strings a tipos correctos`()
    @Test
    fun `updateSoloProfile ignora edad no numerica`()
}
```

---

## PASO 3 — Pantalla de onboarding (primera vez)

**Estado:** No existe.
**Tiempo estimado:** 2-3 horas.

Mostrar 3 slides la primera vez que se abre la app:
1. "Pide con inteligencia" — descripción general
2. "Sigue tus macros" — nutrición y perfiles
3. "En grupo o en solitario" — modos de pedido

### Implementación
- Guardar `onboarding_completed` en DataStore (añadir a SettingsRepository)
- Nueva ruta `AppRoute.ONBOARDING`
- Cambiar `startDestination` del NavHost: si no completado → ONBOARDING, si sí → START_MODE
- Usar `HorizontalPager` de `androidx.compose.foundation.pager`
- Añadir dependencia: `implementation("androidx.compose.foundation:foundation")`

---

## PASO 4 — Snackbars para errores y confirmaciones

**Estado:** Los errores se muestran como texto en pantalla. Algunas acciones no tienen feedback.
**Tiempo estimado:** 1-2 horas.

### Casos a cubrir
- Error al cargar catálogo → Snackbar con botón "Reintentar"
- Error al guardar pedido → Snackbar de error
- Pedido confirmado correctamente → Snackbar de éxito
- Perfil guardado → Snackbar "Perfil guardado"
- Datos eliminados → Snackbar de confirmación

### Implementación
- Añadir `snackbarHostState: SnackbarHostState` al Scaffold principal en `MainActivity`
- Añadir `snackbarMessage: String?` a `AppUiState`
- Añadir `fun clearSnackbar()` al ViewModel
- Observar el mensaje en NavGraph y lanzar el snackbar

---

## PASO 5 — Verificar dark mode

**Estado:** El tema M3 soporta dark mode automáticamente pero no se ha probado.
**Tiempo estimado:** 30 minutos.

- En el emulador: Ajustes → Pantalla → Tema oscuro → activar
- Revisar que todos los colores son legibles (texto sobre fondo, iconos)
- Si algo no se ve bien, ajustar en `ui/theme/Theme.kt` el `darkColorScheme`
- Añadir un screenshot del modo oscuro en la memoria del TFG

---

## PASO 6 — Mejorar RecommendationEngineLocal

**Estado:** Funciona pero las reglas son básicas (4 umbrales fijos).
**Tiempo estimado:** 1 hora.

### Mejoras posibles
- Calcular el porcentaje de calorías respecto al TDEE del usuario (ya existe `NutritionCalculator.calculateTdee`)
- Mostrar mensaje personalizado con el nombre del objetivo (Mantener/Perder/Ganar músculo)
- Detectar alergias en el perfil y avisar si algún plato podría contenerlas
- Añadir regla de ratio proteína/peso corporal para objetivo "Ganar músculo"

---

## Para la memoria del TFG

### Secciones recomendadas
1. **Introducción y motivación** — por qué una app de nutrición para grupos
2. **Análisis de requisitos** — funcionales y no funcionales
3. **Diseño de la arquitectura** — diagrama MVVM, flujo de datos con StateFlow
4. **Tecnologías utilizadas** — justificar Compose vs XML, KSP vs kapt, Room vs SQLite puro
5. **La integración de IA** — patrón proxy, por qué no llamar directo (seguridad de la API key), fallback local
6. **Problemas encontrados y soluciones** — bug SQLite/Windows, Java 25 incompatible con KGP
7. **Pruebas** — tests unitarios, pruebas manuales en emulador
8. **Conclusiones y trabajo futuro**

### Decisiones técnicas documentadas (ya en /docs/decisions/)
Revisar si están actualizadas y añadir las nuevas (KSP, perfiles persistentes, proxy IA).

---

## Orden sugerido para empezar

```
PASO 1 → PASO 2 → PASO 3 → PASO 4 → PASO 5 → PASO 6
  IA    →  Tests  → Onboard → Snackb → Dark  → Engine
 30min  →  2h    →   3h    →   2h   →  30m  →   1h
```

**Total estimado: ~9 horas de trabajo.**
