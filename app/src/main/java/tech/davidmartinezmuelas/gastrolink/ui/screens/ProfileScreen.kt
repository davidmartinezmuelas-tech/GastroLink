package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.ActivityLevel
import tech.davidmartinezmuelas.gastrolink.model.Goal
import tech.davidmartinezmuelas.gastrolink.model.GroupNutritionProfile
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.Participant
import tech.davidmartinezmuelas.gastrolink.model.SavedProfile
import tech.davidmartinezmuelas.gastrolink.model.Sex
import tech.davidmartinezmuelas.gastrolink.model.SoloNutritionProfile
import tech.davidmartinezmuelas.gastrolink.ui.components.SectionHeader
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroSpacing

// =============================================================================
// Helpers de etiquetas
// =============================================================================

private fun sexLabel(sex: Sex): String = when (sex) {
    Sex.MALE   -> "Hombre"
    Sex.FEMALE -> "Mujer"
    Sex.OTHER  -> "Otro"
}

private fun goalLabel(goal: Goal): String = when (goal) {
    Goal.MAINTAIN     -> "Mantener"
    Goal.LOSE_WEIGHT  -> "Perder peso"
    Goal.GAIN_MUSCLE  -> "Ganar músculo"
}

private fun activityLabel(level: ActivityLevel): String = when (level) {
    ActivityLevel.LOW    -> "Baja"
    ActivityLevel.MEDIUM -> "Media"
    ActivityLevel.HIGH   -> "Alta"
}

// =============================================================================
// Validación de campos numéricos
// =============================================================================

private fun ageError(value: String): String? {
    if (value.isBlank()) return null
    val n = value.toIntOrNull() ?: return "Solo números"
    return if (n !in 1..120) "Edad entre 1 y 120" else null
}

private fun heightError(value: String): String? {
    if (value.isBlank()) return null
    val n = value.toIntOrNull() ?: return "Solo números"
    return if (n !in 50..250) "Altura entre 50 y 250 cm" else null
}

private fun weightError(value: String): String? {
    if (value.isBlank()) return null
    val n = value.toFloatOrNull() ?: return "Solo números"
    return if (n !in 20f..400f) "Peso entre 20 y 400 kg" else null
}

// =============================================================================
// ProfileScreen
// =============================================================================

@Composable
fun ProfileScreen(
    orderMode: OrderMode?,
    soloProfile: SoloNutritionProfile,
    savedProfiles: List<SavedProfile> = emptyList(),
    profileResetKey: Int = 0,
    isStandalone: Boolean = false,
    participants: List<Participant>,
    groupProfiles: Map<String, GroupNutritionProfile>,
    onSaveSoloProfile: (
        age: String,
        sex: Sex?,
        heightCm: String,
        weightKg: String,
        goal: Goal?,
        activityLevel: ActivityLevel?,
        allergies: String
    ) -> Unit,
    onSaveProfileAs: (name: String) -> Unit = {},
    onLoadSavedProfile: (String) -> Unit = {},
    onDeleteSavedProfile: (String) -> Unit = {},
    onAddParticipant: () -> Unit,
    onRemoveParticipant: (String) -> Unit,
    onRenameParticipant: (String, String) -> Unit,
    onUpdateGroupProfile: (String, String, String, Goal?) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val canContinue = orderMode != OrderMode.GROUP || participants.isNotEmpty()

    Scaffold(
        topBar = {
            GastroTopBar(
                title = if (isStandalone) "Mis perfiles" else "Perfil nutricional",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        when (orderMode) {
            OrderMode.SOLO -> SoloProfileContent(
                modifier          = Modifier.padding(innerPadding),
                currentProfile    = soloProfile,
                savedProfiles     = savedProfiles,
                profileResetKey   = profileResetKey,
                isStandalone      = isStandalone,
                onSaveSoloProfile = onSaveSoloProfile,
                onSaveProfileAs   = onSaveProfileAs,
                onLoadSavedProfile   = onLoadSavedProfile,
                onDeleteSavedProfile = onDeleteSavedProfile,
                onContinue        = onContinue,
                onBack            = onBack,
                canContinue       = canContinue
            )
            OrderMode.GROUP -> GroupProfileContent(
                modifier              = Modifier.padding(innerPadding),
                participants          = participants,
                groupProfiles         = groupProfiles,
                canContinue           = canContinue,
                onAddParticipant      = onAddParticipant,
                onRemoveParticipant   = onRemoveParticipant,
                onRenameParticipant   = onRenameParticipant,
                onUpdateGroupProfile  = onUpdateGroupProfile,
                onContinue            = onContinue
            )
            null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(GastroSpacing.md),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Selecciona un modo de pedido antes de continuar")
            }
        }
    }
}

// =============================================================================
// SoloProfileContent
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoloProfileContent(
    modifier: Modifier,
    currentProfile: SoloNutritionProfile,
    savedProfiles: List<SavedProfile>,
    profileResetKey: Int,
    isStandalone: Boolean,
    onSaveSoloProfile: (String, Sex?, String, String, Goal?, ActivityLevel?, String) -> Unit,
    onSaveProfileAs: (name: String) -> Unit,
    onLoadSavedProfile: (String) -> Unit,
    onDeleteSavedProfile: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    canContinue: Boolean
) {
    var age        by remember(profileResetKey) { mutableStateOf(currentProfile.age?.toString().orEmpty()) }
    var height     by remember(profileResetKey) { mutableStateOf(currentProfile.heightCm?.toString().orEmpty()) }
    var weight     by remember(profileResetKey) { mutableStateOf(currentProfile.weightKg?.toString().orEmpty()) }
    var allergies  by remember(profileResetKey) { mutableStateOf(currentProfile.allergies) }
    var selectedSex      by remember(profileResetKey) { mutableStateOf(currentProfile.sex) }
    var selectedGoal     by remember(profileResetKey) { mutableStateOf(currentProfile.goal) }
    var selectedActivity by remember(profileResetKey) { mutableStateOf(currentProfile.activityLevel) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveDialogName by remember { mutableStateOf("") }

    fun save() = onSaveSoloProfile(age, selectedSex, height, weight, selectedGoal, selectedActivity, allergies)

    // Errores de validación
    val ageErr    = ageError(age)
    val heightErr = heightError(height)
    val weightErr = weightError(weight)
    val hasErrors = listOf(ageErr, heightErr, weightErr).any { it != null }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Guardar perfil") },
            text = {
                OutlinedTextField(
                    value = saveDialogName,
                    onValueChange = { saveDialogName = it },
                    label = { Text("Nombre del perfil") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProfileAs(saveDialogName)
                        saveDialogName = ""
                        showSaveDialog = false
                    },
                    enabled = saveDialogName.isNotBlank()
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Cancelar") }
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = GastroSpacing.md,
            vertical   = GastroSpacing.md
        ),
        verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
    ) {

        // ── Perfiles guardados ──────────────────────────────────────────
        if (savedProfiles.isNotEmpty()) {
            item {
                SectionHeader(title = "Perfiles guardados")
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)) {
                    items(savedProfiles, key = { it.id }) { saved ->
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(start = GastroSpacing.md, end = GastroSpacing.sm, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = saved.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                TextButton(onClick = { onLoadSavedProfile(saved.id) }) {
                                    Text("Usar", style = MaterialTheme.typography.labelMedium)
                                }
                                IconButton(onClick = { onDeleteSavedProfile(saved.id) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Eliminar perfil",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Datos personales ────────────────────────────────────────────
        item { SectionHeader(title = "Datos personales") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
                ) {
                    OutlinedTextField(
                        value       = age,
                        onValueChange = { age = it; save() },
                        modifier    = Modifier.fillMaxWidth(),
                        label       = { Text("Edad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine  = true,
                        isError     = ageErr != null,
                        supportingText = ageErr?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    LabeledDropdown(
                        label     = "Sexo",
                        options   = Sex.entries,
                        selected  = selectedSex,
                        labelFor  = ::sexLabel,
                        onSelect  = { selectedSex = it; save() }
                    )
                    OutlinedTextField(
                        value       = height,
                        onValueChange = { height = it; save() },
                        modifier    = Modifier.fillMaxWidth(),
                        label       = { Text("Altura (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine  = true,
                        isError     = heightErr != null,
                        supportingText = heightErr?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    OutlinedTextField(
                        value       = weight,
                        onValueChange = { weight = it; save() },
                        modifier    = Modifier.fillMaxWidth(),
                        label       = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine  = true,
                        isError     = weightErr != null,
                        supportingText = weightErr?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            }
        }

        // ── Objetivo y actividad ────────────────────────────────────────
        item { SectionHeader(title = "Objetivo y actividad") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
                ) {
                    LabeledDropdown(
                        label     = "Objetivo",
                        options   = Goal.entries,
                        selected  = selectedGoal,
                        labelFor  = ::goalLabel,
                        onSelect  = { selectedGoal = it; save() }
                    )
                    LabeledDropdown(
                        label     = "Nivel de actividad",
                        options   = ActivityLevel.entries,
                        selected  = selectedActivity,
                        labelFor  = ::activityLabel,
                        onSelect  = { selectedActivity = it; save() }
                    )
                }
            }
        }

        // ── Restricciones ───────────────────────────────────────────────
        item { SectionHeader(title = "Restricciones alimentarias") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                OutlinedTextField(
                    value       = allergies,
                    onValueChange = { allergies = it; save() },
                    modifier    = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    label       = { Text("Alergias (opcional)") },
                    placeholder = { Text("Ej: gluten, lactosa, frutos secos") },
                    singleLine  = true
                )
            }
        }

        // ── Guardar perfil ──────────────────────────────────────────────
        item {
            OutlinedButton(
                onClick  = { showSaveDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape    = MaterialTheme.shapes.large
            ) {
                Text("Guardar perfil actual")
            }
        }

        // ── CTA ─────────────────────────────────────────────────────────
        item {
            Button(
                onClick  = if (isStandalone) onBack else onContinue,
                enabled  = canContinue && !hasErrors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = MaterialTheme.shapes.large,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text       = if (isStandalone) "Listo" else "Continuar",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(GastroSpacing.xl))
        }
    }
}

// =============================================================================
// LabeledDropdown
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> LabeledDropdown(
    label: String,
    options: List<T>,
    selected: T?,
    labelFor: (T) -> String,
    onSelect: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded          = expanded,
        onExpandedChange  = { expanded = it },
        modifier          = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value         = selected?.let { labelFor(it) } ?: "",
            onValueChange = {},
            readOnly      = true,
            label         = { Text(label) },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier      = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded          = expanded,
            onDismissRequest  = { expanded = false }
        ) {
            DropdownMenuItem(
                text    = { Text("— Sin especificar —", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                onClick = { onSelect(null); expanded = false }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(labelFor(option)) },
                    onClick = { onSelect(option); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// =============================================================================
// GroupProfileContent
// =============================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupProfileContent(
    modifier: Modifier,
    participants: List<Participant>,
    groupProfiles: Map<String, GroupNutritionProfile>,
    canContinue: Boolean,
    onAddParticipant: () -> Unit,
    onRemoveParticipant: (String) -> Unit,
    onRenameParticipant: (String, String) -> Unit,
    onUpdateGroupProfile: (String, String, String, Goal?) -> Unit,
    onContinue: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = GastroSpacing.md,
            vertical   = GastroSpacing.md
        ),
        verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
    ) {
        // ── Cabecera ────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = "Participantes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text  = "${participants.size} participante${if (participants.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onAddParticipant,
                    shape   = MaterialTheme.shapes.large
                ) { Text("Añadir") }
            }
        }

        // ── Tarjetas de participante ────────────────────────────────────
        items(participants, key = { it.id }) { participant ->
            val profile = groupProfiles[participant.id] ?: GroupNutritionProfile(participant.id)
            var name         by remember(participant.id) { mutableStateOf(participant.name) }
            var allergies    by remember(participant.id) { mutableStateOf(profile.allergies) }
            var preferences  by remember(participant.id) { mutableStateOf(profile.preferences) }
            var selectedGoal by remember(participant.id) { mutableStateOf(profile.generalGoal) }

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = MaterialTheme.shapes.large,
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GastroSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(GastroSpacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value         = name,
                            onValueChange = { name = it; onRenameParticipant(participant.id, it) },
                            label         = { Text("Nombre") },
                            modifier      = Modifier.weight(1f),
                            singleLine    = true,
                            isError       = name.isBlank(),
                            supportingText = if (name.isBlank()) ({ Text("El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error) }) else null
                        )
                        if (participants.size > 1) {
                            IconButton(
                                onClick  = { onRemoveParticipant(participant.id) },
                                modifier = Modifier.padding(start = GastroSpacing.sm)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Eliminar participante",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Text(
                        text  = "Objetivo (opcional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(GastroSpacing.sm)) {
                        Goal.entries.forEach { goal ->
                            FilterChip(
                                selected = selectedGoal == goal,
                                onClick  = {
                                    selectedGoal = if (selectedGoal == goal) null else goal
                                    onUpdateGroupProfile(participant.id, allergies, preferences, selectedGoal)
                                },
                                label = { Text(goalLabel(goal)) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value         = allergies,
                        onValueChange = { allergies = it; onUpdateGroupProfile(participant.id, allergies, preferences, selectedGoal) },
                        label         = { Text("Alergias (opcional)") },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true
                    )
                    OutlinedTextField(
                        value         = preferences,
                        onValueChange = { preferences = it; onUpdateGroupProfile(participant.id, allergies, preferences, selectedGoal) },
                        label         = { Text("Preferencias (opcional)") },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true
                    )
                }
            }
        }

        // ── CTA ─────────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(GastroSpacing.sm))
            Button(
                onClick  = onContinue,
                enabled  = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = MaterialTheme.shapes.large,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text       = "Continuar",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(GastroSpacing.xl))
        }
    }
}
