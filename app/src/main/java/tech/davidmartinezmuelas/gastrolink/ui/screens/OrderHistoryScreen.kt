package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
            GastroTopBar(
                title = "Historial de pedidos",
                onBack = onBack,
                actions = {
                    TextButton(onClick = { showExportDialog.value = true }) { Text(text = "Exportar") }
                    TextButton(onClick = onOpenStats) { Text(text = "Estadísticas") }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            !errorMessage.isNullOrBlank() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Todavía no hay pedidos guardados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
        HistoryExportDialog(
            onExportJson = {
                showExportDialog.value = false
                scope.launch {
                    handleHistoryExport(HistoryExportFormat.JSON, onExportHistory, context, snackbarHostState)
                }
            },
            onExportCsv = {
                showExportDialog.value = false
                scope.launch {
                    handleHistoryExport(HistoryExportFormat.CSV, onExportHistory, context, snackbarHostState)
                }
            },
            onDismiss = { showExportDialog.value = false }
        )
    }
}

@Composable
private fun HistoryExportDialog(
    onExportJson: () -> Unit,
    onExportCsv: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Exportar historial") },
        text = { Text(text = "Elige el formato de exportación") },
        confirmButton = { TextButton(onClick = onExportJson) { Text(text = "JSON") } },
        dismissButton = { TextButton(onClick = onExportCsv) { Text(text = "CSV") } }
    )
}

private suspend fun handleHistoryExport(
    format: HistoryExportFormat,
    onExportHistory: suspend (HistoryExportFormat) -> HistoryExportResult,
    context: android.content.Context,
    snackbarHostState: SnackbarHostState
) {
    when (val result = onExportHistory(format)) {
        is HistoryExportResult.Success -> {
            val shared = ExportShareHelper.shareExportFile(context, result.payload)
            if (!shared) snackbarHostState.showSnackbar("No se pudo compartir el archivo")
        }
        HistoryExportResult.EmptyHistory -> snackbarHostState.showSnackbar("No hay historial para exportar")
        HistoryExportResult.Error -> snackbarHostState.showSnackbar("No se pudo exportar el historial")
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
