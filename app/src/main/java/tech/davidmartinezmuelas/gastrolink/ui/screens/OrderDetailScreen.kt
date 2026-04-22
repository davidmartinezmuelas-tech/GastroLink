package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import tech.davidmartinezmuelas.gastrolink.ui.OrderDetailUi
import tech.davidmartinezmuelas.gastrolink.ui.components.NutritionStatGrid
import tech.davidmartinezmuelas.gastrolink.ui.components.SectionHeader
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing
import tech.davidmartinezmuelas.gastrolink.ui.theme.PillShape

@Composable
fun OrderDetailScreen(
    detail: OrderDetailUi?,
    onDeleteOrder: (String) -> Unit,
    onBack: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showRawJson by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GastroTopBar(
                title = "Detalle de pedido",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        if (detail == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(GastroSpacing.md),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay detalle disponible",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = GastroSpacing.md,
                vertical = GastroSpacing.md
            ),
            verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
        ) {
            // Section: Información del pedido
            item {
                SectionHeader(title = "Información del pedido")
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sucursal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = detail.branchName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Modo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = detail.orderMode,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nivel nutricional",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = detail.nutritionMode,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Section: Perfil nutricional (conditional)
            if (detail.profileType != null || detail.profileSummaryLines.isNotEmpty()) {
                item {
                    SectionHeader(title = "Perfil nutricional")
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
                            if (detail.profileSummaryLines.isNotEmpty()) {
                                detail.profileSummaryLines.forEach { line ->
                                    Text(
                                        text = "• $line",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            } else if (detail.profileType != null) {
                                Text(
                                    text = "Perfil guardado (sin resumen)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!detail.profileParseable) {
                                Text(
                                    text = "Perfil guardado (no parseable)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (detail.canShowRawProfileJson && !detail.rawProfileJson.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)
                                ) {
                                    Button(
                                        onClick = { showRawJson = !showRawJson },
                                        shape = MaterialTheme.shapes.large
                                    ) {
                                        Text(text = if (showRawJson) "Ocultar JSON" else "Mostrar JSON")
                                    }
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(detail.rawProfileJson))
                                        },
                                        shape = MaterialTheme.shapes.large
                                    ) {
                                        Text(text = "Copiar JSON")
                                    }
                                }
                                if (showRawJson) {
                                    SelectionContainer {
                                        Text(
                                            text = detail.rawProfileJson,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section: Participantes (conditional)
            if (detail.participants.isNotEmpty()) {
                item {
                    SectionHeader(title = "Participantes")
                    Column(verticalArrangement = Arrangement.spacedBy(GastroSpacing.xs)) {
                        detail.participants.forEach { participant ->
                            Text(
                                text = "• ${participant.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Section: Platos header
            item {
                SectionHeader(title = "Platos")
            }

            // Dish items
            items(
                detail.items,
                key = { "${it.dishName}_${it.participantName}_${it.quantity}" }
            ) { item ->
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
                                text = item.dishName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (!item.participantName.isNullOrBlank()) {
                                Text(
                                    text = item.participantName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            shape = PillShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "×${item.quantity}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = GastroSpacing.sm,
                                    vertical = GastroSpacing.xs
                                )
                            )
                        }
                    }
                }
            }

            // Section: Totales nutricionales
            item {
                SectionHeader(title = "Totales nutricionales")
                NutritionStatGrid(
                    kcal = detail.totals.kcal,
                    proteinG = detail.totals.proteinG,
                    carbsG = detail.totals.carbsG,
                    fatG = detail.totals.fatG
                )
            }

            // Delete button + spacer
            item {
                Button(
                    onClick = { onDeleteOrder(detail.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(text = "Eliminar pedido")
                }
                Spacer(modifier = Modifier.height(GastroSpacing.xl))
            }
        }
    }
}
