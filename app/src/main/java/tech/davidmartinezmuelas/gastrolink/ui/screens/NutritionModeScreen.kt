package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.SavedProfile
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing
import tech.davidmartinezmuelas.gastrolink.ui.theme.NutritionCalories
import tech.davidmartinezmuelas.gastrolink.ui.theme.PillShape

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
                .padding(GastroSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
        ) {
            Text(
                text = "¿Con qué nivel de detalle nutricional quieres pedir?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Card 1: Sin datos de perfil
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(GastroSpacing.xs)) {
                            Text(
                                text = "Sin datos de perfil",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Muestra kcal y macros por plato y totales del carrito.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = onChooseWithoutProfile,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = "Continuar")
                    }
                }
            }

            // Card 2: Con perfil nutricional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremiumEnabled) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (isPremiumEnabled) {
                                NutritionCalories
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(GastroSpacing.xs)) {
                            Text(
                                text = "Con perfil nutricional",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Recibe recomendaciones personalizadas según tu objetivo y perfil.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!isPremiumEnabled) {
                                Surface(
                                    shape = PillShape,
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = "Requiere Premium",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(
                                            horizontal = GastroSpacing.sm,
                                            vertical = GastroSpacing.xs
                                        )
                                    )
                                }
                            }
                        }
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
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
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
                            horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
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
                            horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
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
