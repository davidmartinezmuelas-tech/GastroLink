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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (isStandalone) {
                    Button(onClick = onBack) { Text(text = "Listo") }
                } else {
                    Button(onClick = onContinue, enabled = canContinue) {
                        Text(text = "Continuar")
                    }
                }
            }
        }
    ) { innerPadding ->
        when (orderMode) {
            OrderMode.SOLO -> SoloProfileContent(
                modifier = Modifier.padding(innerPadding),
                currentProfile = soloProfile,
                savedProfiles = savedProfiles,
                profileResetKey = profileResetKey,
                onSaveSoloProfile = onSaveSoloProfile,
                onSaveProfileAs = onSaveProfileAs,
                onLoadSavedProfile = onLoadSavedProfile,
                onDeleteSavedProfile = onDeleteSavedProfile
            )
            OrderMode.GROUP -> GroupProfileContent(
                modifier = Modifier.padding(innerPadding),
                participants = participants,
                groupProfiles = groupProfiles,
                onAddParticipant = onAddParticipant,
                onRemoveParticipant = onRemoveParticipant,
                onRenameParticipant = onRenameParticipant,
                onUpdateGroupProfile = onUpdateGroupProfile
            )
            null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Selecciona un modo de pedido antes de continuar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoloProfileContent(
    modifier: Modifier,
    currentProfile: SoloNutritionProfile,
    savedProfiles: List<SavedProfile>,
    profileResetKey: Int,
    onSaveSoloProfile: (
        age: String,
        sex: Sex?,
        heightCm: String,
        weightKg: String,
        goal: Goal?,
        activityLevel: ActivityLevel?,
        allergies: String
    ) -> Unit,
    onSaveProfileAs: (name: String) -> Unit,
    onLoadSavedProfile: (String) -> Unit,
    onDeleteSavedProfile: (String) -> Unit
) {
    // profileResetKey ensures these reset when a saved profile is loaded
    var age by remember(profileResetKey) { mutableStateOf(currentProfile.age?.toString().orEmpty()) }
    var height by remember(profileResetKey) { mutableStateOf(currentProfile.heightCm?.toString().orEmpty()) }
    var weight by remember(profileResetKey) { mutableStateOf(currentProfile.weightKg?.toString().orEmpty()) }
    var allergies by remember(profileResetKey) { mutableStateOf(currentProfile.allergies) }
    var selectedSex by remember(profileResetKey) { mutableStateOf(currentProfile.sex) }
    var selectedGoal by remember(profileResetKey) { mutableStateOf(currentProfile.goal) }
    var selectedActivity by remember(profileResetKey) { mutableStateOf(currentProfile.activityLevel) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveDialogName by remember { mutableStateOf("") }

    fun save() = onSaveSoloProfile(age, selectedSex, height, weight, selectedGoal, selectedActivity, allergies)

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
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Perfiles guardados ──────────────────────────────────────────
        if (savedProfiles.isNotEmpty()) {
            item {
                Text(
                    text = "Perfiles guardados",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(savedProfiles, key = { it.id }) { saved ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = saved.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                TextButton(onClick = { onLoadSavedProfile(saved.id) }) {
                                    Text("Usar")
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

        // ── Cabecera + botón guardar ────────────────────────────────────
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Perfil nutricional", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Cuanto más completo, más precisas las recomendaciones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            OutlinedButton(
                onClick = { showSaveDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar perfil actual")
            }
        }

        // ── Campos del perfil ───────────────────────────────────────────
        item {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it; save() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        item {
            LabeledDropdown(
                label = "Sexo",
                options = Sex.entries,
                selected = selectedSex,
                labelFor = ::sexLabel,
                onSelect = { selectedSex = it; save() }
            )
        }
        item {
            OutlinedTextField(
                value = height,
                onValueChange = { height = it; save() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Altura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; save() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        item {
            LabeledDropdown(
                label = "Objetivo",
                options = Goal.entries,
                selected = selectedGoal,
                labelFor = ::goalLabel,
                onSelect = { selectedGoal = it; save() }
            )
        }
        item {
            LabeledDropdown(
                label = "Nivel de actividad",
                options = ActivityLevel.entries,
                selected = selectedActivity,
                labelFor = ::activityLabel,
                onSelect = { selectedActivity = it; save() }
            )
        }
        item {
            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it; save() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Alergias (opcional)") },
                singleLine = true
            )
        }
    }
}

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
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected?.let { labelFor(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "— Sin especificar —", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                onClick = { onSelect(null); expanded = false }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = labelFor(option)) },
                    onClick = { onSelect(option); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupProfileContent(
    modifier: Modifier,
    participants: List<Participant>,
    groupProfiles: Map<String, GroupNutritionProfile>,
    onAddParticipant: () -> Unit,
    onRemoveParticipant: (String) -> Unit,
    onRenameParticipant: (String, String) -> Unit,
    onUpdateGroupProfile: (String, String, String, Goal?) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Participantes", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${participants.size} participante${if (participants.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = onAddParticipant) { Text(text = "Añadir") }
            }
        }

        items(participants, key = { it.id }) { participant ->
            val profile = groupProfiles[participant.id] ?: GroupNutritionProfile(participant.id)
            var name by remember(participant.id) { mutableStateOf(participant.name) }
            var allergies by remember(participant.id) { mutableStateOf(profile.allergies) }
            var preferences by remember(participant.id) { mutableStateOf(profile.preferences) }
            var selectedGoal by remember(participant.id) { mutableStateOf(profile.generalGoal) }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; onRenameParticipant(participant.id, it) },
                            label = { Text(text = "Nombre") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        if (participants.size > 1) {
                            TextButton(
                                onClick = { onRemoveParticipant(participant.id) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(text = "Eliminar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Objetivo (opcional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Goal.entries.forEach { goal ->
                            FilterChip(
                                selected = selectedGoal == goal,
                                onClick = {
                                    selectedGoal = if (selectedGoal == goal) null else goal
                                    onUpdateGroupProfile(participant.id, allergies, preferences, selectedGoal)
                                },
                                label = { Text(text = goalLabel(goal)) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = {
                            allergies = it
                            onUpdateGroupProfile(participant.id, allergies, preferences, selectedGoal)
                        },
                        label = { Text(text = "Alergias (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = preferences,
                        onValueChange = {
                            preferences = it
                            onUpdateGroupProfile(participant.id, allergies, preferences, selectedGoal)
                        },
                        label = { Text(text = "Preferencias (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

private fun sexLabel(sex: Sex): String = when (sex) {
    Sex.MALE -> "Hombre"
    Sex.FEMALE -> "Mujer"
    Sex.OTHER -> "Otro"
}

private fun goalLabel(goal: Goal): String = when (goal) {
    Goal.MAINTAIN -> "Mantener"
    Goal.LOSE_WEIGHT -> "Perder peso"
    Goal.GAIN_MUSCLE -> "Ganar músculo"
}

private fun activityLabel(level: ActivityLevel): String = when (level) {
    ActivityLevel.LOW -> "Baja"
    ActivityLevel.MEDIUM -> "Media"
    ActivityLevel.HIGH -> "Alta"
}
