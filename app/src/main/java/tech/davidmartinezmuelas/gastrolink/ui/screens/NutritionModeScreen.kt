package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.SavedProfile

@Composable
fun NutritionModeScreen(
    isPremiumEnabled: Boolean,
    savedProfiles: List<SavedProfile> = emptyList(),
    onChooseWithoutProfile: () -> Unit,
    onChooseWithProfile: () -> Unit,
    onSelectProfile: (String) -> Unit = {},
    onContinueWithoutProfile: () -> Unit,
    onOpenPlans: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val showPremiumDialog = remember { mutableStateOf(false) }
    val showProfilePicker = remember { mutableStateOf(false) }
    var selectedProfileId = remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Nivel nutricional",
                onBack = onBack,
                onSettings = onOpenSettings
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
            Text(
                text = "¿Con qué nivel de detalle nutricional quieres pedir?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Sin datos de perfil", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Muestra kcal y macros por plato y totales del carrito.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                    Text(
                        text = "Con perfil nutricional",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Recibe recomendaciones personalizadas según tu objetivo y perfil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isPremiumEnabled) {
                        Text(
                            text = "Requiere plan Premium",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            if (!isPremiumEnabled) {
                                showPremiumDialog.value = true
                            } else if (savedProfiles.isNotEmpty()) {
                                selectedProfileId.value = null
                                showProfilePicker.value = true
                            } else {
                                onChooseWithProfile()
                            }
                        }
                    ) {
                        Text(text = "Continuar")
                    }
                }
            }
        }
    }

    // Dialog: plan premium requerido
    if (showPremiumDialog.value) {
        AlertDialog(
            onDismissRequest = { showPremiumDialog.value = false },
            title = { Text(text = "Función Premium") },
            text = {
                Text(
                    text = "El perfil nutricional y las recomendaciones personalizadas " +
                        "están disponibles en el plan Premium.\n\n" +
                        "Puedes activar el modo demo gratuito desde la pantalla de Planes."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog.value = false
                        onOpenPlans()
                    }
                ) {
                    Text(text = "Ver planes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog.value = false
                        onContinueWithoutProfile()
                    }
                ) {
                    Text(text = "Continuar sin perfil")
                }
            }
        )
    }

    // Dialog: selector de perfil
    if (showProfilePicker.value) {
        AlertDialog(
            onDismissRequest = { showProfilePicker.value = false },
            title = { Text("¿Qué perfil usar?") },
            text = {
                LazyColumn {
                    items(savedProfiles, key = { it.id }) { profile ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedProfileId.value = profile.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedProfileId.value == profile.id,
                                onClick = { selectedProfileId.value = profile.id }
                            )
                            Column {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                val summary = listOfNotNull(
                                    profile.profile.age?.let { "$it años" },
                                    profile.profile.weightKg?.let { "$it kg" }
                                ).joinToString(" · ")
                                if (summary.isNotEmpty()) {
                                    Text(
                                        text = summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedProfileId.value = "new" }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedProfileId.value == "new",
                                onClick = { selectedProfileId.value = "new" }
                            )
                            Text(
                                text = "Nuevo perfil",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = selectedProfileId.value
                        showProfilePicker.value = false
                        if (id == "new" || id == null) {
                            onChooseWithProfile()
                        } else {
                            onSelectProfile(id)
                            onChooseWithProfile()
                        }
                    },
                    enabled = selectedProfileId.value != null
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfilePicker.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
