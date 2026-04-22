package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.domain.RecommendationSource
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals
import tech.davidmartinezmuelas.gastrolink.model.Participant
import tech.davidmartinezmuelas.gastrolink.ui.components.KcalChip
import tech.davidmartinezmuelas.gastrolink.ui.components.MacroPillsRow
import tech.davidmartinezmuelas.gastrolink.ui.components.NutritionStatGrid
import tech.davidmartinezmuelas.gastrolink.ui.components.SectionHeader
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing
import tech.davidmartinezmuelas.gastrolink.ui.theme.PillShape

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
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = GastroSpacing.md,
                vertical = GastroSpacing.md
            ),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.lg)
        ) {
            item {
                SectionHeader(title = "Totales nutricionales")
            }
            item {
                NutritionStatGrid(
                    kcal = totals.kcal,
                    proteinG = totals.proteinG,
                    carbsG = totals.carbsG,
                    fatG = totals.fatG
                )
            }

            if (totalsByParticipant.isNotEmpty()) {
                item {
                    SectionHeader(title = "Por participante")
                }

                val orderedEntries = participants
                    .mapNotNull { p -> totalsByParticipant[p.id]?.let { p.id to it } }
                    .ifEmpty { totalsByParticipant.entries.map { it.key to it.value } }

                items(orderedEntries, key = { it.first }) { (id, pTotals) ->
                    val label = participantNames[id] ?: id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(GastroSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                KcalChip(kcal = pTotals.kcal)
                                MacroPillsRow(
                                    proteinG = pTotals.proteinG,
                                    carbsG = pTotals.carbsG,
                                    fatG = pTotals.fatG
                                )
                            }
                        }
                    }
                }
            }

            if (nutritionMode == NutritionMode.WITH_PROFILE) {
                item {
                    SectionHeader(title = "Recomendaciones")
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(GastroSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                        ) {
                            val sourceLabel = when (recommendationSource) {
                                RecommendationSource.AI -> "IA"
                                RecommendationSource.LOCAL_RULES -> "Reglas locales"
                                RecommendationSource.NONE -> null
                            }
                            if (sourceLabel != null) {
                                Surface(
                                    shape = PillShape,
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = sourceLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            when {
                                isRecommendationLoading -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(GastroSpacing.md),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Generando recomendación...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                recommendationSource == RecommendationSource.NONE ||
                                recommendations.isEmpty() -> {
                                    Text(
                                        text = "No se generaron sugerencias para este pedido",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                else -> {
                                    recommendations.forEach { message ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = message,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(GastroSpacing.sm))
                Button(
                    onClick = onConfirmOrder,
                    enabled = !isSavingOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isSavingOrder) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Confirmar pedido",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(GastroSpacing.xl))
            }
        }
    }
}
