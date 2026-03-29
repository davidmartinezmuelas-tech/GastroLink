package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.Participant

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: (String, String?) -> Unit,
    onDecrease: (String, String?) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = item.dish.name, style = MaterialTheme.typography.titleMedium)
            val itemKcal = item.dish.kcal * item.qty
            val itemP = item.dish.proteinG * item.qty
            val itemC = item.dish.carbsG * item.qty
            val itemF = item.dish.fatG * item.qty
            Text(
                text = "$itemKcal kcal · P ${itemP}g · C ${itemC}g · G ${itemF}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onDecrease(item.dish.id, item.participantId) },
                        modifier = Modifier.width(44.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "−")
                    }
                    Text(
                        text = "${item.qty}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                    FilledTonalButton(
                        onClick = { onIncrease(item.dish.id, item.participantId) },
                        modifier = Modifier.width(44.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "+")
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    orderMode: OrderMode?,
    items: List<CartItem>,
    participants: List<Participant>,
    onIncrease: (String, String?) -> Unit,
    onDecrease: (String, String?) -> Unit,
    onGoToSummary: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val totalKcal = items.sumOf { it.dish.kcal * it.qty }
    val totalProtein = items.sumOf { it.dish.proteinG * it.qty }
    val totalCarbs = items.sumOf { it.dish.carbsG * it.qty }
    val totalFat = items.sumOf { it.dish.fatG * it.qty }

    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Carrito",
                onBack = onBack,
                onSettings = onOpenSettings
            )
        },
        bottomBar = {
            Column {
                if (items.isNotEmpty()) {
                    HorizontalDivider()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Total nutricional",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$totalKcal kcal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = "P ${totalProtein}g", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "C ${totalCarbs}g", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "G ${totalFat}g", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onGoToSummary, enabled = items.isNotEmpty()) {
                        Text(text = "Ir a resumen")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "El carrito está vacío", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Añade platos desde el menú para continuar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (orderMode == OrderMode.GROUP) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                participants.forEach { participant ->
                    val participantItems = items.filter { it.participantId == participant.id }
                    if (participantItems.isNotEmpty()) {
                        item(key = "header_${participant.id}") {
                            Text(
                                text = participant.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        items(participantItems, key = { "${it.dish.id}_${it.participantId}" }) { item ->
                            CartItemCard(item = item, onIncrease = onIncrease, onDecrease = onDecrease)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { "${it.dish.id}_${it.participantId}" }) { item ->
                    CartItemCard(item = item, onIncrease = onIncrease, onDecrease = onDecrease)
                }
            }
        }
    }
}
