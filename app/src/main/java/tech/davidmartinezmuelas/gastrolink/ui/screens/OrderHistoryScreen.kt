package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import tech.davidmartinezmuelas.gastrolink.ui.ExportShareHelper
import tech.davidmartinezmuelas.gastrolink.ui.HistoryExportFormat
import tech.davidmartinezmuelas.gastrolink.ui.HistoryExportResult
import tech.davidmartinezmuelas.gastrolink.ui.OrderHistoryItemUi

@Composable
fun OrderHistoryScreen(
    isLoading: Boolean,
    errorMessage: String?,
    orders: List<OrderHistoryItemUi>,
    onRefresh: () -> Unit,
    onExportHistory: suspend (HistoryExportFormat) -> HistoryExportResult,
    onViewDetails: (String) -> Unit,
    onOpenStats: () -> Unit,
    onBack: () -> Unit
) {
    val showExportDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Historial de pedidos") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "Atras") }
                },
                actions = {
                    TextButton(onClick = { showExportDialog.value = true }) { Text(text = "Exportar") }
                    TextButton(onClick = onOpenStats) { Text(text = "Estadisticas") }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Cargando historial...")
                }
            }
            !errorMessage.isNullOrBlank() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = errorMessage)
                    Button(onClick = onRefresh) { Text(text = "Reintentar") }
                }
            }
            orders.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Todavia no hay pedidos guardados")
                    Button(onClick = onRefresh) { Text(text = "Actualizar") }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = formatDate(order.createdAt),
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(text = "Sucursal: ${order.branchName}")
                                Text(text = "Platos: ${order.dishCount}")
                                Text(text = "kcal totales: ${order.totalCalories}")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(onClick = { onViewDetails(order.id) }) {
                                        Text(text = "Ver detalles")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog.value) {
        AlertDialog(
            onDismissRequest = { showExportDialog.value = false },
            title = { Text(text = "Exportar historial") },
            text = { Text(text = "Elige el formato de exportacion") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExportDialog.value = false
                        scope.launch {
                            when (val exportResult = onExportHistory(HistoryExportFormat.JSON)) {
                                is HistoryExportResult.Success -> {
                                    val shared = ExportShareHelper.shareExportFile(context, exportResult.payload)
                                    if (!shared) {
                                        snackbarHostState.showSnackbar("No se pudo compartir el archivo")
                                    }
                                }

                                HistoryExportResult.EmptyHistory -> {
                                    snackbarHostState.showSnackbar("No hay historial para exportar")
                                }

                                HistoryExportResult.Error -> {
                                    snackbarHostState.showSnackbar("No se pudo exportar el historial")
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "JSON")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExportDialog.value = false
                        scope.launch {
                            when (val exportResult = onExportHistory(HistoryExportFormat.CSV)) {
                                is HistoryExportResult.Success -> {
                                    val shared = ExportShareHelper.shareExportFile(context, exportResult.payload)
                                    if (!shared) {
                                        snackbarHostState.showSnackbar("No se pudo compartir el archivo")
                                    }
                                }

                                HistoryExportResult.EmptyHistory -> {
                                    snackbarHostState.showSnackbar("No hay historial para exportar")
                                }

                                HistoryExportResult.Error -> {
                                    snackbarHostState.showSnackbar("No se pudo exportar el historial")
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "CSV")
                }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
