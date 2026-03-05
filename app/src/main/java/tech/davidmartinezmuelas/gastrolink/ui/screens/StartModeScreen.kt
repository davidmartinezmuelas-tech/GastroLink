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
import tech.davidmartinezmuelas.gastrolink.model.OrderMode

@Composable
fun StartModeScreen(
    selectedMode: OrderMode?,
    onSelectSolo: () -> Unit,
    onSelectGroup: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "GastroLink") },
                actions = {
                    TextButton(onClick = onOpenSettings) {
                        Text(text = "Ajustes")
                    }
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
            Text(
                text = "Modo de pedido",
                style = MaterialTheme.typography.titleLarge
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Solitario", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Pedido individual con carrito personal")
                    Button(onClick = onSelectSolo) {
                        Text(text = if (selectedMode == OrderMode.SOLO) "Seleccionado" else "Elegir")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "En grupo", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Gestiona participantes y asigna cada plato")
                    Button(onClick = onSelectGroup) {
                        Text(text = if (selectedMode == OrderMode.GROUP) "Seleccionado" else "Elegir")
                    }
                }
            }
        }
    }
}
