package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.ui.BuildInfo

@Composable
fun SettingsScreen(
    isPremiumDemoEnabled: Boolean,
    nutritionMode: NutritionMode?,
    useAiRecommendations: Boolean,
    buildInfo: BuildInfo,
    onTogglePremiumDemo: (Boolean) -> Unit,
    onToggleUseAiRecommendations: (Boolean) -> Unit,
    onOpenHistory: () -> Unit,
    onBack: () -> Unit
) {
    val canShowAiToggle = isPremiumDemoEnabled && nutritionMode == NutritionMode.WITH_PROFILE

    Scaffold(
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
                text = "Acerca de",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "Version: ${buildInfo.versionName}")
            Text(text = "Version code: ${buildInfo.versionCode}")
            Text(text = "Git SHA: ${buildInfo.gitSha}")
            Text(text = "Build time (UTC): ${buildInfo.buildTime}")
        }
    }
}
