package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.SavedProfile
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing

@Composable
fun StartModeScreen(
    selectedMode: OrderMode?,
    savedProfiles: List<SavedProfile> = emptyList(),
    hasProfileData: Boolean = false,
    onSelectSolo: () -> Unit,
    onSelectGroup: () -> Unit,
    onOpenSettings: () -> Unit,
    onManageProfiles: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            GastroTopBar(
                title = "GastroLink",
                onSettings = onOpenSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = GastroSpacing.md, vertical = GastroSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
        ) {
            // Branding block
            Column(verticalArrangement = Arrangement.spacedBy(GastroSpacing.xs)) {
                Text(
                    text = "Bienvenido a GastroLink",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Tu asistente de pedidos inteligente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ModeCard(
                icon = Icons.Filled.Person,
                title = "Solitario",
                description = "Pedido individual con carrito personal y seguimiento nutricional.",
                isSelected = selectedMode == OrderMode.SOLO,
                buttonLabel = if (selectedMode == OrderMode.SOLO) "Seleccionado" else "Elegir",
                onClick = onSelectSolo
            )

            ModeCard(
                icon = Icons.Filled.Person,
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
private fun ModeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        Column(
            modifier = Modifier.padding(GastroSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(text = buttonLabel)
            }
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
        shape = MaterialTheme.shapes.large,
        color = if (hasSomething) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = GastroSpacing.md, vertical = GastroSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
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
                    style = MaterialTheme.typography.labelMedium,
                    color = if (hasSomething) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
