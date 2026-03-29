package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Elige tu plan",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Compara las funcionalidades disponibles en cada plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Free plan card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (!isPremiumEnabled) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Free", style = MaterialTheme.typography.titleLarge)
                        if (!isPremiumEnabled) {
                            Text(
                                text = "Plan actual",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider()
                    PlanFeature(included = true, text = "Información nutricional por plato")
                    PlanFeature(included = true, text = "Totales del carrito (kcal, proteína, carbos, grasa)")
                    PlanFeature(included = true, text = "Historial de pedidos")
                    PlanFeature(included = false, text = "Perfiles nutricionales personalizados")
                    PlanFeature(included = false, text = "Recomendaciones basadas en perfil")
                    PlanFeature(included = false, text = "Recomendaciones IA (beta)")
                    PlanFeature(included = false, text = "Estadísticas avanzadas")
                }
            }

            // Premium plan card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremiumEnabled) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Premium", style = MaterialTheme.typography.titleLarge)
                        if (isPremiumEnabled) {
                            Text(
                                text = "Plan actual",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider()
                    PlanFeature(included = true, text = "Todo lo incluido en Free")
                    PlanFeature(included = true, text = "Perfiles nutricionales personalizados")
                    PlanFeature(included = true, text = "Recomendaciones basadas en perfil y objetivo")
                    PlanFeature(included = true, text = "Recomendaciones IA (beta)")
                    PlanFeature(included = true, text = "Estadísticas avanzadas de historial")
                }
            }

            Button(
                onClick = onActivatePremiumDemo,
                enabled = !isPremiumEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isPremiumEnabled) "Premium Demo activo" else "Activar Premium Demo")
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
        }
    }
}

@Composable
private fun PlanFeature(included: Boolean, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (included) "+" else "–",
            style = MaterialTheme.typography.bodyMedium,
            color = if (included) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (included) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
