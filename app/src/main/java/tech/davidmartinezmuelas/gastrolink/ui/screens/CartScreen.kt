package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.Participant

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
    val participantNames = participants.associateBy({ it.id }, { it.name })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Carrito") },
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
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onGoToSummary,
                    enabled = items.isNotEmpty()
                ) {
                    Text(text = "Ir a resumen")
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "El carrito esta vacio", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.dish.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = item.dish.name, style = MaterialTheme.typography.titleMedium)
                            if (orderMode == OrderMode.GROUP && !item.participantId.isNullOrBlank()) {
                                Text(
                                    text = "Participante: ${participantNames[item.participantId] ?: item.participantId}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Qty: ${item.qty}")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { onDecrease(item.dish.id, item.participantId) }) {
                                        Text(text = "-")
                                    }
                                    Button(onClick = { onIncrease(item.dish.id, item.participantId) }) {
                                        Text(text = "+")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
