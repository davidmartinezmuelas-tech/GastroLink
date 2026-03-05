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
import tech.davidmartinezmuelas.gastrolink.ui.StatsUi

@Composable
fun StatsScreen(
    stats: StatsUi,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Estadisticas") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "Atras") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Resumen nutricional", style = MaterialTheme.typography.titleLarge)
            Text(text = "Calorias promedio: ${"%.1f".format(stats.averageCaloriesPerOrder)}")
            Text(text = "Proteina promedio: ${"%.1f".format(stats.averageProtein)} g")
            Text(text = "Carbohidratos promedio: ${"%.1f".format(stats.averageCarbs)} g")
            Text(text = "Grasas promedio: ${"%.1f".format(stats.averageFat)} g")
            Text(text = "Plato mas pedido: ${stats.mostOrderedDishName}")
        }
    }
}
