package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals

@Composable
fun SummaryScreen(
    totals: NutritionTotals,
    recommendations: List<String>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Resumen") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Atras")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Totales", style = MaterialTheme.typography.titleLarge)
            Text(text = "kcal: ${totals.kcal}")
            Text(text = "protein: ${totals.proteinG} g")
            Text(text = "carbs: ${totals.carbsG} g")
            Text(text = "fat: ${totals.fatG} g")

            Text(
                text = "Recomendaciones",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            recommendations.forEach { message ->
                Text(text = "- $message")
            }
        }
    }
}
