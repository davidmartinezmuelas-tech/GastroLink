package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.SavedProfile

@Composable
fun StartModeScreen(
    selectedMode: OrderMode?,
    savedProfiles: List<SavedProfile> = emptyList(),
    hasProfileData: Boolean = false,
    onSelectSolo: () -> Unit,
    onSelectGroup: () -> Unit,
    onOpenSettings: () -> Unit,
    onManageProfiles: () -> Unit = {},
    onOpenChat: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            GastroTopBar(
                title = "GastroLink",
                onSettings = onOpenSettings
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenChat,
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                text = { Text("Asistente IA") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "¿Cómo vas a pedir hoy?",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Elige el modo de pedido para comenzar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ModeCard(
                title = "Solitario",
                description = "Pedido individual con carrito personal y seguimiento nutricional.",
                isSelected = selectedMode == OrderMode.SOLO,
                buttonLabel = if (selectedMode == OrderMode.SOLO) "Seleccionado" else "Elegir",
                onClick = onSelectSolo
            )

            ModeCard(
                title = "En grupo",
                description = "Gestiona participantes, asigna cada plato y compara totales.",
                isSelected = selectedMode == OrderMode.GROUP,
                buttonLabel = if (selectedMode == OrderMode.GROUP) "Seleccionado" else "Elegir",
                onClick = onSelectGroup
            )

            ProfileBanner(
                savedProfiles = savedProfiles,
                hasProfileData = hasProfileData,
                onManageProfiles = onManageProfiles
            )
        }
    }
}

@Composable
private fun ProfileBanner(
    savedProfiles: List<SavedProfile>,
    hasProfileData: Boolean,
    onManageProfiles: () -> Unit
) {
    val hasSomething = savedProfiles.isNotEmpty() || hasProfileData

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (hasSomething) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (hasSomething) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        savedProfiles.isNotEmpty() ->
                            "${savedProfiles.size} perfil${if (savedProfiles.size != 1) "es" else ""} guardado${if (savedProfiles.size != 1) "s" else ""}"
                        hasProfileData -> "Perfil nutricional configurado"
                        else -> "Sin perfil nutricional"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (hasSomething) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = if (hasSomething) "Toca para gestionar" else "Añade uno para recomendaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onManageProfiles) {
                Text(
                    text = if (hasSomething) "Gestionar" else "Añadir",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onClick) {
                Text(text = buttonLabel)
            }
        }
    }
}
