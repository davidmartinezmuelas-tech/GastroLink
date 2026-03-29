package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.ui.OrderDetailUi

@Composable
fun OrderDetailScreen(
    detail: OrderDetailUi?,
    onDeleteOrder: (String) -> Unit,
    onBack: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showRawJson by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Detalle de pedido",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        if (detail == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay detalle disponible",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(text = "Sucursal: ${detail.branchName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Modo de pedido: ${detail.orderMode}")
                Text(text = "Nivel nutricional: ${detail.nutritionMode}")
            }

            item {
                Text(text = "Perfil guardado", style = MaterialTheme.typography.titleMedium)
                Text(text = "Tipo: ${detail.profileType ?: "No disponible"}")

                if (detail.profileSummaryLines.isNotEmpty()) {
                    detail.profileSummaryLines.forEach { line ->
                        Text(text = "- $line")
                    }
                } else if (detail.profileType != null) {
                    Text(text = "Perfil guardado (sin resumen)")
                } else {
                    Text(text = "No hay perfil asociado al pedido")
                }

                if (!detail.profileParseable) {
                    Text(text = "Perfil guardado (no parseable)")
                }

                if (detail.canShowRawProfileJson && !detail.rawProfileJson.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { showRawJson = !showRawJson }) {
                            Text(text = if (showRawJson) "Ocultar JSON" else "Mostrar JSON")
                        }
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(detail.rawProfileJson))
                            }
                        ) {
                            Text(text = "Copiar JSON")
                        }
                    }
                    if (showRawJson) {
                        SelectionContainer {
                            Text(text = detail.rawProfileJson)
                        }
                    }
                }
            }

            if (detail.participants.isNotEmpty()) {
                item {
                    Text(text = "Participantes", style = MaterialTheme.typography.titleMedium)
                }
                items(detail.participants, key = { it.id }) { participant ->
                    Text(text = "- ${participant.name}")
                }
            }

            item {
                Text(text = "Platos", style = MaterialTheme.typography.titleMedium)
            }
            items(detail.items, key = { "${it.dishName}_${it.participantName}_${it.quantity}" }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = item.dishName)
                        Text(text = "Cantidad: ${item.quantity}")
                        if (!item.participantName.isNullOrBlank()) {
                            Text(text = "Asignado a: ${item.participantName}")
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Totales", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Calorías: ${detail.totals.kcal} kcal")
                        Text(text = "Proteína: ${detail.totals.proteinG} g")
                        Text(text = "Carbohidratos: ${detail.totals.carbsG} g")
                        Text(text = "Grasa: ${detail.totals.fatG} g")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { onDeleteOrder(detail.id) }) {
                        Text(text = "Eliminar pedido")
                    }
                }
            }
        }
    }
}
