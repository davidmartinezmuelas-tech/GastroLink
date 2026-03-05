package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import kotlinx.coroutines.launch
import tech.davidmartinezmuelas.gastrolink.ui.DataWipeResult
import tech.davidmartinezmuelas.gastrolink.ui.ExportShareHelper
import tech.davidmartinezmuelas.gastrolink.ui.HistoryExportFormat
import tech.davidmartinezmuelas.gastrolink.ui.HistoryExportResult
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.ui.BuildInfo

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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Ajustes") },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Entitlements",
                style = MaterialTheme.typography.titleLarge
            )

            Text(text = "Demo Premium")
            Switch(
                checked = isPremiumDemoEnabled,
                onCheckedChange = onTogglePremiumDemo
            )

            Text(
                text = if (isPremiumDemoEnabled) {
                    "Estado actual: PREMIUM_DEMO"
                } else {
                    "Estado actual: FREE"
                }
            )

            Button(onClick = onOpenHistory) {
                Text(text = "Historial y estadisticas")
            }

            Button(onClick = onOpenPlans) {
                Text(text = "Planes")
            }

            Button(onClick = { showExportDialog.value = true }) {
                Text(text = "Exportar historial")
            }

            if (canShowAiToggle) {
                Text(
                    text = "Recomendaciones",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Usar IA para recomendaciones (beta)")
                Switch(
                    checked = useAiRecommendations,
                    onCheckedChange = onToggleUseAiRecommendations
                )
            }

            Text(
                text = "Privacidad",
                style = MaterialTheme.typography.titleLarge
            )

            Button(onClick = { showDeleteDialog.value = true }) {
                Text(text = "Borrar todos mis datos")
            }

            Text(
                text = "Acerca de",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "Version: ${buildInfo.versionName}")
            Text(text = "Version code: ${buildInfo.versionCode}")
            Text(text = "Git SHA: ${buildInfo.gitSha}")
            Text(text = "Build time (UTC): ${buildInfo.buildTime}")
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = "Confirmar borrado") },
            text = {
                Text(text = "Esto eliminara historial, perfiles, participantes y ajustes.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        scope.launch {
                            val result = onDeleteAllData()
                            snackbarHostState.showSnackbar(result.message)
                            if (result.success) {
                                onNavigateStart()
                            }
                        }
                    }
                ) {
                    Text(text = "Borrar definitivamente")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
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
                            handleExport(
                                format = HistoryExportFormat.JSON,
                                onExportHistory = onExportHistory,
                                context = context,
                                snackbarHostState = snackbarHostState
                            )
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
                            handleExport(
                                format = HistoryExportFormat.CSV,
                                onExportHistory = onExportHistory,
                                context = context,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                ) {
                    Text(text = "CSV")
                }
            }
        )
    }
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
