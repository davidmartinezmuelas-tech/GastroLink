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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
            TopAppBar(
                title = { Text(text = "Resumen") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Atras")
                    }
                },
                actions = {
                    TextButton(onClick = onOpenSettings) {
                        Text(text = "Ajustes")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(text = "Totales", style = MaterialTheme.typography.titleLarge)
                Text(text = "kcal: ${totals.kcal}")
                Text(text = "protein: ${totals.proteinG} g")
                Text(text = "carbs: ${totals.carbsG} g")
                Text(text = "fat: ${totals.fatG} g")
            }

            if (totalsByParticipant.isNotEmpty()) {
                item {
                    Text(
                        text = "Totales por participante",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(totalsByParticipant.entries.toList(), key = { it.key }) { entry ->
                    val label = participantNames[entry.key] ?: entry.key
                    val participantTotals = entry.value
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = label, style = MaterialTheme.typography.titleSmall)
                            Text(text = "kcal ${participantTotals.kcal} | P ${participantTotals.proteinG} | C ${participantTotals.carbsG} | G ${participantTotals.fatG}")
                        }
                    }
                }
            }

            if (nutritionMode == NutritionMode.WITH_PROFILE) {
                item {
                    Text(
                        text = when (recommendationSource) {
                            RecommendationSource.AI -> "Recomendacion (IA)"
                            RecommendationSource.LOCAL_RULES -> "Recomendacion (Reglas locales)"
                            RecommendationSource.NONE -> "Recomendacion"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                if (isRecommendationLoading) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator()
                            Text(text = "Generando recomendacion...")
                        }
                    }
                } else {
                    items(recommendations) { message ->
                        Text(text = "- $message")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onConfirmOrder,
                        enabled = !isSavingOrder
                    ) {
                        Text(text = if (isSavingOrder) "Guardando..." else "Confirmar pedido")
                    }
                }
            }
        }
    }
}
