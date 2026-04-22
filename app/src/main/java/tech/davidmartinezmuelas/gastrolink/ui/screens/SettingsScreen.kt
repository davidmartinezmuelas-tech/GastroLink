package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.ui.BuildInfo
import tech.davidmartinezmuelas.gastrolink.ui.DataWipeResult
import tech.davidmartinezmuelas.gastrolink.ui.ExportShareHelper
import tech.davidmartinezmuelas.gastrolink.ui.HistoryExportFormat
import tech.davidmartinezmuelas.gastrolink.ui.HistoryExportResult
import tech.davidmartinezmuelas.gastrolink.ui.components.SectionHeader
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing

@Composable
fun SettingsScreen(
    isPremiumDemoEnabled: Boolean,
    canUseAiRecommendations: Boolean,
    nutritionMode: NutritionMode?,
    useAiRecommendations: Boolean,
    buildInfo: BuildInfo,
    onTogglePremiumDemo: (Boolean) -> Unit,
    onToggleUseAiRecommendations: (Boolean) -> Unit,
    onOpenPlans: () -> Unit,
    onExportHistory: suspend (HistoryExportFormat) -> HistoryExportResult,
    onDeleteAllData: suspend () -> DataWipeResult,
    onOpenHistory: () -> Unit,
    onNavigateStart: () -> Unit,
    onBack: () -> Unit
) {
    val canShowAiToggle = canUseAiRecommendations && nutritionMode == NutritionMode.WITH_PROFILE
    val snackbarHostState = remember { SnackbarHostState() }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showExportDialog = remember { mutableStateOf(false) }
    val showPremiumEnableDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GastroTopBar(
                title = "Ajustes",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GastroSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
        ) {
            // Plan card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremiumDemoEnabled) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPremiumDemoEnabled) "Premium Demo" else "Plan Free",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isPremiumDemoEnabled) {
                                "Todas las funciones disponibles"
                            } else {
                                "Sin perfil nutricional ni recomendaciones"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onOpenPlans) {
                        Text(text = "Ver planes")
                    }
                }
            }

            // Section: Plan y acceso
            SectionHeader(title = "Plan y acceso")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Premium Demo",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Activa perfiles nutricionales, recomendaciones y estadísticas avanzadas. Sin coste real.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isPremiumDemoEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showPremiumEnableDialog.value = true
                            } else {
                                onTogglePremiumDemo(false)
                            }
                        },
                        modifier = Modifier.padding(start = GastroSpacing.sm)
                    )
                }
            }

            // Section: Navegación
            SectionHeader(title = "Navegación")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(GastroSpacing.md)) {
                    OutlinedButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = "Historial y estadísticas")
                    }
                }
            }

            // Section: Recomendaciones IA (conditional)
            if (canShowAiToggle) {
                SectionHeader(title = "Recomendaciones IA")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(GastroSpacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Usar IA (beta)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Las recomendaciones se generan via servidor externo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = useAiRecommendations,
                            onCheckedChange = onToggleUseAiRecommendations,
                            modifier = Modifier.padding(start = GastroSpacing.sm)
                        )
                    }
                }
            }

            // Section: Privacidad
            SectionHeader(title = "Privacidad")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                ) {
                    OutlinedButton(
                        onClick = { showExportDialog.value = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = "Exportar historial")
                    }
                    Button(
                        onClick = { showDeleteDialog.value = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(text = "Borrar todos mis datos")
                    }
                }
            }

            // Section: Acerca de
            SectionHeader(title = "Acerca de")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.xs)
                ) {
                    Text(
                        text = "Versión ${buildInfo.versionName} (${buildInfo.versionCode})",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Git: ${buildInfo.gitSha}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Build: ${buildInfo.buildTime}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(GastroSpacing.xl))
        }
    }

    if (showPremiumEnableDialog.value) {
        PremiumEnableDialog(
            onConfirm = { showPremiumEnableDialog.value = false; onTogglePremiumDemo(true) },
            onDismiss = { showPremiumEnableDialog.value = false }
        )
    }

    if (showDeleteDialog.value) {
        DeleteDataDialog(
            onConfirm = {
                showDeleteDialog.value = false
                scope.launch {
                    val result = onDeleteAllData()
                    snackbarHostState.showSnackbar(result.message)
                    if (result.success) onNavigateStart()
                }
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }

    if (showExportDialog.value) {
        ExportDialog(
            onExportJson = {
                showExportDialog.value = false
                scope.launch {
                    handleExport(HistoryExportFormat.JSON, onExportHistory, context, snackbarHostState)
                }
            },
            onExportCsv = {
                showExportDialog.value = false
                scope.launch {
                    handleExport(HistoryExportFormat.CSV, onExportHistory, context, snackbarHostState)
                }
            },
            onDismiss = { showExportDialog.value = false }
        )
    }
}

@Composable
private fun PremiumEnableDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Activar Premium Demo") },
        text = {
            Text(
                text = "Esto activará el modo Premium para explorar todas las funciones avanzadas:\n\n" +
                    "• Perfiles nutricionales personalizados\n" +
                    "• Recomendaciones basadas en tu objetivo\n" +
                    "• Recomendaciones IA (beta)\n" +
                    "• Estadísticas avanzadas\n\n" +
                    "No implica ningún pago real."
            )
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text(text = "Activar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancelar") } }
    )
}

@Composable
private fun DeleteDataDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Borrar todos los datos") },
        text = {
            Text(
                text = "Esta acción eliminará permanentemente:\n\n" +
                    "• Todo el historial de pedidos\n" +
                    "• Perfiles nutricionales guardados\n" +
                    "• Preferencias y ajustes\n\n" +
                    "No se puede deshacer."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Borrar definitivamente", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancelar") } }
    )
}

@Composable
private fun ExportDialog(onExportJson: () -> Unit, onExportCsv: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Exportar historial") },
        text = { Text(text = "Elige el formato de exportación") },
        confirmButton = { TextButton(onClick = onExportJson) { Text(text = "JSON") } },
        dismissButton = { TextButton(onClick = onExportCsv) { Text(text = "CSV") } }
    )
}

private suspend fun handleExport(
    format: HistoryExportFormat,
    onExportHistory: suspend (HistoryExportFormat) -> HistoryExportResult,
    context: android.content.Context,
    snackbarHostState: SnackbarHostState
) {
    when (val exportResult = onExportHistory(format)) {
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
