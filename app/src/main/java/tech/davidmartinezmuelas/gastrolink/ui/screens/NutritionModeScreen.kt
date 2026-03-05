package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NutritionModeScreen(
    isPremiumEnabled: Boolean,
    onChooseWithoutProfile: () -> Unit,
    onChooseWithProfile: () -> Unit,
    onContinueWithoutProfile: () -> Unit,
    onEnablePremiumDemo: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val showPremiumDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Nivel nutricional") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "Atras") }
                },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text(text = "Ajustes") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Sin datos", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Muestra kcal/macros por plato y totales del carrito")
                    Button(onClick = onChooseWithoutProfile) {
                        Text(text = "Continuar")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Con datos", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Perfil nutricional y recomendaciones locales")
                    Button(
                        onClick = {
                            if (isPremiumEnabled) {
                                onChooseWithProfile()
                            } else {
                                showPremiumDialog.value = true
                            }
                        }
                    ) {
                        Text(text = "Continuar")
                    }
                }
            }
        }
    }

    if (showPremiumDialog.value) {
        AlertDialog(
            onDismissRequest = { showPremiumDialog.value = false },
            title = { Text(text = "Disponible en Premium") },
            text = {
                Text(
                    text = "El modo con datos requiere premium. Puedes activar demo premium desde ajustes o seguir sin datos."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEnablePremiumDemo()
                        showPremiumDialog.value = false
                        onChooseWithProfile()
                    }
                ) {
                    Text(text = "Activar demo premium")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog.value = false
                        onContinueWithoutProfile()
                    }
                ) {
                    Text(text = "Continuar sin datos")
                }
            }
        )
    }
}
