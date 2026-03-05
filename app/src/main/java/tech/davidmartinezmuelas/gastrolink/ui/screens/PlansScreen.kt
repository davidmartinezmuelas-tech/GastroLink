package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun PlansScreen(
    isPremiumEnabled: Boolean,
    isDebugBuild: Boolean,
    onActivatePremiumDemo: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Planes") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "Atras") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Comparativa Free vs Premium", style = MaterialTheme.typography.titleLarge)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Plan Free", style = MaterialTheme.typography.titleMedium)
                    Text(text = "- Info nutricional del plato")
                    Text(text = "- Totales del carrito")
                    Text(text = "- Flujo sin perfil")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Plan Premium", style = MaterialTheme.typography.titleMedium)
                    Text(text = "- Perfiles nutricionales")
                    Text(text = "- Reglas nutricionales avanzadas")
                    Text(text = "- Recomendaciones IA (beta)")
                    Text(text = "- Estadisticas avanzadas")
                }
            }

            if (isPremiumEnabled) {
                Text(text = "Estado actual: Premium Demo activo")
            } else {
                Text(text = "Estado actual: Free")
            }

            Button(
                onClick = onActivatePremiumDemo,
                enabled = !isPremiumEnabled
            ) {
                Text(text = if (isPremiumEnabled) "Demo Premium ya activa" else "Activar Demo Premium")
            }

            Text(
                text = if (isDebugBuild) {
                    "Modo presentacion: la activacion es solo demo y no implica pago real."
                } else {
                    "Demo de presentacion: no hay pagos reales en esta fase."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
