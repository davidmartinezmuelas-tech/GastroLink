package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing
import tech.davidmartinezmuelas.gastrolink.ui.theme.NutritionCalories
import tech.davidmartinezmuelas.gastrolink.ui.theme.PillShape

@Composable
fun PlansScreen(
    isPremiumEnabled: Boolean,
    isDebugBuild: Boolean,
    onActivatePremiumDemo: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Planes",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GastroSpacing.md, vertical = GastroSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
        ) {
            Text(
                text = "Elige tu plan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Compara las funcionalidades disponibles en cada plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ── Plan Free ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = if (!isPremiumEnabled) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (!isPremiumEnabled) {
                    androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                },
                elevation = CardDefaults.cardElevation(defaultElevation = if (!isPremiumEnabled) 0.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Free",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (!isPremiumEnabled) {
                            Surface(shape = PillShape, color = MaterialTheme.colorScheme.secondary) {
                                Text(
                                    text = "Plan actual",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(horizontal = GastroSpacing.sm, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    PlanFeature(included = true,  text = "Información nutricional por plato")
                    PlanFeature(included = true,  text = "Totales del carrito (kcal, proteína, carbos, grasa)")
                    PlanFeature(included = true,  text = "Historial de pedidos")
                    PlanFeature(included = false, text = "Perfiles nutricionales personalizados")
                    PlanFeature(included = false, text = "Recomendaciones basadas en perfil")
                    PlanFeature(included = false, text = "Recomendaciones IA (beta)")
                    PlanFeature(included = false, text = "Estadísticas avanzadas")
                }
            }

            // ── Plan Premium ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremiumEnabled) {
                        NutritionCalories.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (isPremiumEnabled) {
                    androidx.compose.foundation.BorderStroke(1.5.dp, NutritionCalories)
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                },
                elevation = CardDefaults.cardElevation(defaultElevation = if (isPremiumEnabled) 0.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = NutritionCalories,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Premium",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isPremiumEnabled) NutritionCalories
                                        else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (isPremiumEnabled) {
                            Surface(shape = PillShape, color = NutritionCalories) {
                                Text(
                                    text = "Plan actual",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(horizontal = GastroSpacing.sm, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    PlanFeature(included = true, text = "Todo lo incluido en Free")
                    PlanFeature(included = true, text = "Perfiles nutricionales personalizados")
                    PlanFeature(included = true, text = "Recomendaciones basadas en perfil y objetivo")
                    PlanFeature(included = true, text = "Recomendaciones IA (beta)")
                    PlanFeature(included = true, text = "Estadísticas avanzadas de historial")
                }
            }

            // ── CTA ─────────────────────────────────────────────────────
            Button(
                onClick  = onActivatePremiumDemo,
                enabled  = !isPremiumEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = MaterialTheme.shapes.large,
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = NutritionCalories,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (isPremiumEnabled) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = GastroSpacing.sm)
                            .size(18.dp)
                    )
                }
                Text(
                    text       = if (isPremiumEnabled) "Premium Demo activo" else "Activar Premium Demo (gratis)",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (isDebugBuild) {
                    "Modo presentación: la activación es solo demo y no implica pago real."
                } else {
                    "Demo de presentación: no hay pagos reales en esta fase del proyecto."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(GastroSpacing.xl))
        }
    }
}

@Composable
private fun PlanFeature(included: Boolean, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (included) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text  = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (included) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
