# Guía de Contribución

Gracias por contribuir a GastroLink.
Este documento define el flujo base para ramas, commits y pull requests.

## Flujo de ramas

- Rama principal: `main`.
- Crear ramas nuevas desde `main` usando el patrón: `feature/<descripcion-corta>`.

Ejemplos:

- `feature/branch-selector`
- `feature/menu-list`
- `feature/cart-nutrition-summary`

## Commits

Se utiliza la convención Conventional Commits.

Formato:

```text
<tipo>(<scope opcional>): <descripcion>
```

Tipos frecuentes:

- `feat`: nueva funcionalidad.
- `fix`: corrección de errores.
- `docs`: cambios de documentación.
- `test`: pruebas.
- `chore`: tareas de mantenimiento.

Ejemplos:

- `feat(cart): agregar total de macronutrientes`
- `fix(menu): evitar duplicados en listado`
- `docs(adr): documentar decision de arquitectura`

## Pull Requests

Pasos recomendados:

1. Actualizar tu rama con los cambios recientes de `main`.
2. Verificar que compila y que tests relevantes pasan localmente.
3. Abrir PR con título claro y descripción breve del alcance.
4. Referenciar issue/tarea si aplica.

## Checklist de PR

- [ ] La rama sigue el patrón `feature/*` (o equivalente acordado).
- [ ] Los commits siguen Conventional Commits.
- [ ] No incluye cambios no relacionados.
- [ ] Documentación actualizada si hubo cambios de comportamiento.
- [ ] Tests añadidos/actualizados cuando aplica.
- [ ] CI en verde (cuando el wrapper de Gradle esté disponible).
