package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.ActivityLevel
import tech.davidmartinezmuelas.gastrolink.model.Goal
import tech.davidmartinezmuelas.gastrolink.model.GroupNutritionProfile
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.Participant
import tech.davidmartinezmuelas.gastrolink.model.Sex
import tech.davidmartinezmuelas.gastrolink.model.SoloNutritionProfile

@Composable
fun ProfileScreen(
    orderMode: OrderMode?,
    soloProfile: SoloNutritionProfile,
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
    onAddParticipant: () -> Unit,
    onRemoveParticipant: (String) -> Unit,
    onRenameParticipant: (String, String) -> Unit,
    onUpdateGroupProfile: (String, String, String, Goal?) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Perfil nutricional") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "Atras") }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onContinue) {
                    Text(text = "Continuar")
                }
            }
        }
    ) { innerPadding ->
        when (orderMode) {
            OrderMode.SOLO -> {
                SoloProfileContent(
                    modifier = Modifier.padding(innerPadding),
                    currentProfile = soloProfile,
                    onSaveSoloProfile = onSaveSoloProfile
                )
            }
            OrderMode.GROUP -> {
                GroupProfileContent(
                    modifier = Modifier.padding(innerPadding),
                    participants = participants,
                    groupProfiles = groupProfiles,
                    onAddParticipant = onAddParticipant,
                    onRemoveParticipant = onRemoveParticipant,
                    onRenameParticipant = onRenameParticipant,
                    onUpdateGroupProfile = onUpdateGroupProfile
                )
            }
            null -> {
                Column(
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
}

@Composable
private fun SoloProfileContent(
    modifier: Modifier,
    currentProfile: SoloNutritionProfile,
    onSaveSoloProfile: (
        age: String,
        sex: Sex?,
        heightCm: String,
        weightKg: String,
        goal: Goal?,
        activityLevel: ActivityLevel?,
        allergies: String
    ) -> Unit
) {
    var age by remember { mutableStateOf(currentProfile.age?.toString().orEmpty()) }
    var sexText by remember { mutableStateOf(currentProfile.sex?.name.orEmpty()) }
    var height by remember { mutableStateOf(currentProfile.heightCm?.toString().orEmpty()) }
    var weight by remember { mutableStateOf(currentProfile.weightKg?.toString().orEmpty()) }
    var goalText by remember { mutableStateOf(currentProfile.goal?.name.orEmpty()) }
    var activityText by remember { mutableStateOf(currentProfile.activityLevel?.name.orEmpty()) }
    var allergies by remember { mutableStateOf(currentProfile.allergies) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Perfil completo opcional",
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Edad") }
            )
        }
        item {
            OutlinedTextField(
                value = sexText,
                onValueChange = {
                    sexText = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Sexo (FEMALE/MALE/OTHER)") }
            )
        }
        item {
            OutlinedTextField(
                value = height,
                onValueChange = {
                    height = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Altura cm") }
            )
        }
        item {
            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Peso kg") }
            )
        }
        item {
            OutlinedTextField(
                value = goalText,
                onValueChange = {
                    goalText = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Objetivo (MAINTAIN/LOSE_WEIGHT/GAIN_MUSCLE)") }
            )
        }
        item {
            OutlinedTextField(
                value = activityText,
                onValueChange = {
                    activityText = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Actividad (LOW/MEDIUM/HIGH)") }
            )
        }
        item {
            OutlinedTextField(
                value = allergies,
                onValueChange = {
                    allergies = it
                    onSaveSoloProfile(
                        age,
                        parseSex(sexText),
                        height,
                        weight,
                        parseGoal(goalText),
                        parseActivity(activityText),
                        allergies
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Alergias") }
            )
        }
    }
}

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
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Perfil ligero por participante",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onAddParticipant) {
                    Text(text = "Anadir")
                }
            }
        }

        items(participants, key = { it.id }) { participant ->
            val profile = groupProfiles[participant.id] ?: GroupNutritionProfile(participant.id)
            var name by remember(participant.id) { mutableStateOf(participant.name) }
            var allergies by remember(participant.id) { mutableStateOf(profile.allergies) }
            var preferences by remember(participant.id) { mutableStateOf(profile.preferences) }
            var goalText by remember(participant.id) { mutableStateOf(profile.generalGoal?.name.orEmpty()) }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            onRenameParticipant(participant.id, it)
                        },
                        label = { Text(text = "Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = {
                            allergies = it
                            onUpdateGroupProfile(participant.id, allergies, preferences, parseGoal(goalText))
                        },
                        label = { Text(text = "Alergias") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = preferences,
                        onValueChange = {
                            preferences = it
                            onUpdateGroupProfile(participant.id, allergies, preferences, parseGoal(goalText))
                        },
                        label = { Text(text = "Preferencias") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = {
                            goalText = it
                            onUpdateGroupProfile(participant.id, allergies, preferences, parseGoal(goalText))
                        },
                        label = { Text(text = "Objetivo general") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (participants.size > 1) {
                        TextButton(onClick = { onRemoveParticipant(participant.id) }) {
                            Text(text = "Eliminar participante")
                        }
                    }
                }
            }
        }
    }
}

private fun parseSex(raw: String): Sex? {
    return runCatching { Sex.valueOf(raw.trim().uppercase()) }.getOrNull()
}

private fun parseGoal(raw: String): Goal? {
    return runCatching { Goal.valueOf(raw.trim().uppercase()) }.getOrNull()
}

private fun parseActivity(raw: String): ActivityLevel? {
    return runCatching { ActivityLevel.valueOf(raw.trim().uppercase()) }.getOrNull()
}
