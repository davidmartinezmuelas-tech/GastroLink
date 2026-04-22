package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.ui.StatsUi
import tech.davidmartinezmuelas.gastrolink.ui.components.EmptyState
import tech.davidmartinezmuelas.gastrolink.ui.components.MacroDistributionBar
import tech.davidmartinezmuelas.gastrolink.ui.components.NutritionStatGrid
import tech.davidmartinezmuelas.gastrolink.ui.components.SectionHeader
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing
import tech.davidmartinezmuelas.gastrolink.ui.theme.NutritionCalories
import tech.davidmartinezmuelas.gastrolink.ui.theme.NutritionCarbs
import tech.davidmartinezmuelas.gastrolink.ui.theme.NutritionFat
import tech.davidmartinezmuelas.gastrolink.ui.theme.NutritionProtein

@Composable
fun StatsScreen(
    stats: StatsUi,
    onBack: () -> Unit
) {
    val hasData = stats.averageCaloriesPerOrder > 0.0

    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Estadísticas",
                onBack = onBack
            )
        }
    ) { innerPadding ->

        if (!hasData) {
            EmptyState(
                icon     = Icons.Filled.Star,
                title    = "Sin datos aún",
                subtitle = "Confirma pedidos para ver tus estadísticas nutricionales aquí",
                modifier = Modifier.padding(innerPadding)
            )
            return@Scaffold
        }

        // Cálculo distribución de macros (contribución calórica)
        val protCal  = stats.averageProtein * 4.0
        val carbsCal = stats.averageCarbs   * 4.0
        val fatCal   = stats.averageFat     * 9.0
        val totalMacroCal = protCal + carbsCal + fatCal

        val protPct  = if (totalMacroCal > 0) (protCal  / totalMacroCal).toFloat() else 0f
        val carbsPct = if (totalMacroCal > 0) (carbsCal / totalMacroCal).toFloat() else 0f
        val fatPct   = if (totalMacroCal > 0) (fatCal   / totalMacroCal).toFloat() else 0f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GastroSpacing.md, vertical = GastroSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.lg)
        ) {

            // ── Promedio por pedido ────────────────────────────────────────
            SectionHeader(title = "Promedio por pedido")

            NutritionStatGrid(
                kcal     = stats.averageCaloriesPerOrder.toInt(),
                proteinG = stats.averageProtein.toInt(),
                carbsG   = stats.averageCarbs.toInt(),
                fatG     = stats.averageFat.toInt()
            )

            // ── Distribución de macros ─────────────────────────────────────
            SectionHeader(title = "Distribución de macros")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = MaterialTheme.shapes.large,
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
                ) {
                    Text(
                        text  = "Contribución calórica de cada macro",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MacroDistributionBar(
                        label      = "Proteína",
                        percentage = protPct,
                        color      = NutritionProtein
                    )
                    MacroDistributionBar(
                        label      = "Carbohidratos",
                        percentage = carbsPct,
                        color      = NutritionCarbs
                    )
                    MacroDistributionBar(
                        label      = "Grasa",
                        percentage = fatPct,
                        color      = NutritionFat
                    )
                }
            }

            // ── Plato más pedido ───────────────────────────────────────────
            if (stats.mostOrderedDishName.isNotBlank() &&
                stats.mostOrderedDishName != "No disponible"
            ) {
                SectionHeader(title = "Plato más pedido")

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = MaterialTheme.shapes.large,
                    colors    = CardDefaults.cardColors(
                        containerColor = NutritionCalories.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(GastroSpacing.md),
                        verticalAlignment   = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(GastroSpacing.md)
                    ) {
                        // Trofeo
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(NutritionCalories.copy(alpha = 0.18f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint     = NutritionCalories,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text  = "Tu favorito",
                                style = MaterialTheme.typography.labelSmall,
                                color = NutritionCalories,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text  = stats.mostOrderedDishName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(GastroSpacing.xl))
        }
    }
}
