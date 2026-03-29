package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.Dish
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.Participant

@Composable
private fun kcalChipColors(kcal: Int): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when {
        kcal < 400 -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        kcal < 600 -> scheme.secondaryContainer to scheme.onSecondaryContainer
        else -> scheme.errorContainer to scheme.onErrorContainer
    }
}

@Composable
private fun DishCard(dish: Dish, qtyInCart: Int, onAdd: () -> Unit, onViewDetail: () -> Unit) {
    val (chipBg, chipFg) = kcalChipColors(dish.kcal)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onViewDetail),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (!dish.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = dish.imageUrl,
                contentDescription = dish.name,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
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
                    text = dish.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = "${dish.kcal} kcal",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = chipBg,
                        labelColor = chipFg
                    ),
                    border = null
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (qtyInCart > 0) {
                    Text(
                        text = "×$qtyInCart en carrito",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Ver ingredientes y detalle →",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = onAdd) {
                    Text(text = "+ Añadir")
                }
            }
        }
    }
}

@Composable
private fun DishDetailSheet(dish: Dish, onDismiss: () -> Unit) {
    val (chipBg, chipFg) = kcalChipColors(dish.kcal)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            if (!dish.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = dish.imageUrl,
                    contentDescription = dish.name,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dish.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(text = "${dish.kcal} kcal") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = chipBg,
                            labelColor = chipFg
                        ),
                        border = null
                    )
                }
                HorizontalDivider()
                Text(
                    text = "Macronutrientes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Proteína" to "${dish.proteinG}g",
                        "Carbos" to "${dish.carbsG}g",
                        "Grasa" to "${dish.fatG}g"
                    ).forEach { (label, value) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(text = value, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                val ingredientList = dish.ingredients.orEmpty()
                if (ingredientList.isNotEmpty()) {
                    HorizontalDivider()
                    Text(
                        text = "Ingredientes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ingredientList.forEach { ingredient ->
                            Text(
                                text = "• $ingredient",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuScreen(
    orderMode: OrderMode?,
    selectedBranchName: String?,
    dishes: List<Dish>,
    cartItems: List<CartItem>,
    participants: List<Participant>,
    selectedParticipantId: String?,
    onSelectParticipant: (String) -> Unit,
    onAddDish: (Dish, String?) -> Unit,
    onGoToCart: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val totalInCart = cartItems.sumOf { it.qty }
    var detailDish by remember { mutableStateOf<Dish?>(null) }

    Scaffold(
        topBar = {
            GastroTopBar(
                title = selectedBranchName ?: "Menú",
                onBack = onBack,
                onSettings = onOpenSettings,
                actions = {
                    TextButton(onClick = onGoToCart) {
                        Text(
                            text = if (totalInCart > 0) "Carrito ($totalInCart)" else "Carrito",
                            color = if (totalInCart > 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (dishes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No hay platos disponibles",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Comprueba la conexión o selecciona otra sucursal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (orderMode == OrderMode.GROUP && participants.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Asignar platos a",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    participants.forEach { participant ->
                                        FilterChip(
                                            selected = participant.id == selectedParticipantId,
                                            onClick = { onSelectParticipant(participant.id) },
                                            label = { Text(text = participant.name) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                items(dishes, key = { it.id }) { dish ->
                    val qtyInCart = if (orderMode == OrderMode.GROUP) {
                        cartItems
                            .filter { it.dish.id == dish.id && it.participantId == selectedParticipantId }
                            .sumOf { it.qty }
                    } else {
                        cartItems.filter { it.dish.id == dish.id }.sumOf { it.qty }
                    }
                    DishCard(
                        dish = dish,
                        qtyInCart = qtyInCart,
                        onAdd = { onAddDish(dish, selectedParticipantId) },
                        onViewDetail = { detailDish = dish }
                    )
                }
            }
        }
    }

    detailDish?.let { dish ->
        DishDetailSheet(dish = dish, onDismiss = { detailDish = null })
    }
}
