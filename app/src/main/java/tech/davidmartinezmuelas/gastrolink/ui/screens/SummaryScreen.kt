package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.domain.RecommendationSource
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals
import tech.davidmartinezmuelas.gastrolink.model.Participant

@Composable
fun SummaryScreen(
    nutritionMode: NutritionMode,
    totals: NutritionTotals,
    recommendations: List<String>,
    recommendationSource: RecommendationSource,
    isRecommendationLoading: Boolean,
    participants: List<Participant>,
    totalsByParticipant: Map<String, NutritionTotals>,
    isSavingOrder: Boolean,
    onConfirmOrder: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val participantNames = participants.associateBy({ it.id }, { it.name })

    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Resumen del pedido",
                onBack = onBack,
                onSettings = onOpenSettings
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Totales nutricionales",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        // Grid 2×2 de macros
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NutritionStat(
                                label = "Calorías",
                                value = "${totals.kcal}",
                                unit = "kcal",
                                highlight = true
                            )
                            NutritionStat(label = "Proteína", value = "${totals.proteinG}", unit = "g")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NutritionStat(label = "Carbohidratos", value = "${totals.carbsG}", unit = "g")
                            NutritionStat(label = "Grasa", value = "${totals.fatG}", unit = "g")
                        }
                    }
                }
            }

            if (totalsByParticipant.isNotEmpty()) {
                item {
                    Text(
                        text = "Por participante",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val orderedEntries = participants
                    .mapNotNull { p -> totalsByParticipant[p.id]?.let { p.id to it } }
                    .ifEmpty { totalsByParticipant.entries.map { it.key to it.value } }

                items(orderedEntries, key = { it.first }) { entry ->
                    val label = participantNames[entry.first] ?: entry.first
                    val participantTotals = entry.second
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, style = MaterialTheme.typography.titleSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "${participantTotals.kcal} kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = "P ${participantTotals.proteinG}g", style = MaterialTheme.typography.bodySmall)
                                Text(text = "C ${participantTotals.carbsG}g", style = MaterialTheme.typography.bodySmall)
                                Text(text = "G ${participantTotals.fatG}g", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            if (nutritionMode == NutritionMode.WITH_PROFILE) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recomendaciones",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                val sourceLabel = when (recommendationSource) {
                                    RecommendationSource.AI -> "IA"
                                    RecommendationSource.LOCAL_RULES -> "Reglas locales"
                                    RecommendationSource.NONE -> null
                                }
                                if (sourceLabel != null) {
                                    Text(
                                        text = sourceLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            HorizontalDivider()
                            when {
                                isRecommendationLoading -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator()
                                        Text(text = "Generando recomendación...")
                                    }
                                }
                                recommendationSource == RecommendationSource.NONE -> {
                                    Text(
                                        text = "Sin recomendación disponible para este perfil",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                recommendations.isEmpty() -> {
                                    Text(
                                        text = "No se generaron sugerencias para este pedido",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                else -> {
                                    recommendations.forEach { message ->
                                        Text(
                                            text = "• $message",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onConfirmOrder, enabled = !isSavingOrder) {
                        Text(text = if (isSavingOrder) "Guardando..." else "Confirmar pedido")
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionStat(
    label: String,
    value: String,
    unit: String,
    highlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = if (highlight) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}
