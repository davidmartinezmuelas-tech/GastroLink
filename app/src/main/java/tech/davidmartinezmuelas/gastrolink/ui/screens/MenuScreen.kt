package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.Dish

@Composable
fun MenuScreen(
    selectedBranchName: String?,
    dishes: List<Dish>,
    onAddDish: (Dish) -> Unit,
    onGoToCart: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = selectedBranchName ?: "Menu") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Atras")
                    }
                },
                actions = {
                    TextButton(onClick = onGoToCart) {
                        Text(text = "Carrito")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dishes, key = { it.id }) { dish ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = dish.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${dish.kcal} kcal | P ${dish.proteinG}g | C ${dish.carbsG}g | G ${dish.fatG}g",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = { onAddDish(dish) }) {
                            Text(text = "Anadir")
                        }
                    }
                }
            }
        }
    }
}
